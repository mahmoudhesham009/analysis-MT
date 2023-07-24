package gov.uspto.pe2e.cpc.wms.migration.analysis.adapter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import gov.uspto.pe2e.cpc.wms.migration.analysis.constant.Constant;
import gov.uspto.pe2e.cpc.wms.migration.analysis.repository.mysql.MigrationJob;
import gov.uspto.pe2e.cpc.wms.migration.analysis.repository.mysql.MigrationJobRepoSpringImpl;
import gov.uspto.pe2e.cpc.wms.migration.analysis.repository.mysql.ProcessDeployment;
import gov.uspto.pe2e.cpc.wms.migration.analysis.repository.mysql.ProcessDeploymentRepoSpringImpl;
import gov.uspto.pe2e.cpc.wms.migration.analysis.repository.mysql.ValidationLogs;
import gov.uspto.pe2e.cpc.wms.migration.analysis.repository.mysql.ValidationLogsRepository;
import gov.uspto.pe2e.cpc.wms.migration.analysis.service.MigrationServices;
import gov.uspto.pe2e.cpc.wms.migration.analysis.service.MigrationServicesImplTest;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.ChildShape;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.HistoricTasksReqPojo;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.HistoricTasksResPojo;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.HttpServiceImpl;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.MessageResPojo;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.Outgoing;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.ProcessDataPojo;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.ProcessInstanceData;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.TaskTimeLine;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.TasksHistory;
import gov.uspto.pe2e.cpc.wms.migration.analysis.workflow.Workflow;
import lombok.val;

@RestController
@RequestMapping("/validate")
public class ValidationController {
	
	@Autowired
	ProcessDeploymentRepoSpringImpl deploymentRepoSpringImpl;
	
	@Autowired
	MigrationServicesImplTest migrationServices;
	
	@Autowired
	HttpServiceImpl httpService;
	
	@Autowired
	MigrationJobRepoSpringImpl migrationJobRepo;
	
	@GetMapping("/{jobId}")
	MessageResPojo validateJob(@PathVariable Integer jobId) throws URISyntaxException, JsonProcessingException, JSONException {
		
		List<ValidationLogs> logs=valLogsRepo.findByJobId(jobId);
		valLogsRepo.deleteAll(logs);
		
		MigrationJob job=migrationJobRepo.findById(jobId).get();
		
		
		Map<String,Object> body=new HashMap<>();
		body.put("includeProcessVariables",true);

		
		if(job.isSingleProcess()) {
			body.put("processBusinessKey", job.getProcessName());
		}else {
			body.put("startedAfter", job.getFromDate());
			body.put("startedBefore", job.getToDate());

		}
		
		//ObjectMapper objectMapper = new ObjectMapper();
		//Object bodyObj=objectMapper.convertValue(fromValue, ×)
		
		Object obj=httpService.postReq("enterprise/historic-process-instances/query", body, Object.class);
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(obj));
		JSONArray dataArray = jsonObject.getJSONArray("data");
		
		List<String> invalidProcess=new ArrayList<>();
		for(Object d: dataArray) {
			
			JSONObject process=(JSONObject) d;
			String result=validate(process.getString("id"),process.getString("processDefinitionDeploymentId"));
			if(!result.equals("")) {
				invalidProcess.add(process.getString("id"));
				String projectNum="";
				
				
				Object objVar=httpService.getReq("enterprise/process-instances/"+process.getString("id"), Object.class);
				JSONObject objVariable = new JSONObject(objectMapper.writeValueAsString(objVar));
				JSONArray valArray = objVariable.getJSONArray("variables");
				
				//JSONArray valArray = process.getJSONArray("variables");
				
				for(Object v: valArray) {
					JSONObject variable=(JSONObject) v;
					if(variable.getString("name").equals("PROPOSAL_ALIAS")) {
						projectNum=variable.getString("value");
						break;
					}
				}
				
				logTheError(jobId,process.getString("id"),process.getString("name"),projectNum,result);
			}
		}
		job.setValidProcess(true);
		migrationJobRepo.save(job);
		return invalidProcess.size()==0?new MessageResPojo("All proesses validated sucessfully",200):new MessageResPojo(invalidProcess);
	}
	
	
	@Autowired
	ValidationLogsRepository valLogsRepo;
	private void logTheError(Integer jobId, String processId, String processName, String projectNum, String result) {
		// TODO Auto-generated method stub
		ValidationLogs log=new ValidationLogs();
		log.setJobId(jobId);
		log.setProcessId(processId);
		log.setProcessName(processName);
		log.setDetails(result);
		log.setProjectNum(projectNum);
		valLogsRepo.save(log);
		
	}


	String validate(String insId, String deploymentId) throws JsonProcessingException, URISyntaxException {
		System.out.println(insId);
		String result="";
		
		
		List<TaskTimeLine> ttl= getProcessTimeline(insId).getTaskTimeLine();
		
		String json=getDeployment(deploymentId);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		//objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
		Workflow workflow = objectMapper.readValue(json, Workflow.class);
		
		
		boolean isComp=true;
		
		for (TaskTimeLine taskTimeLine : ttl) {
			if(taskTimeLine.getState().equals("running")) isComp=false;
		}
		
		Set<String> isVisited=new HashSet<>();
		for (int i = 0; i <= ttl.size(); i++) {
			
			if(i==ttl.size()&&!isComp)
				continue;
			
			isVisited.clear();
			String refSt= (i==0)?findStartRefId(workflow):findRefIdByKey(workflow,ttl.get(i-1).getTaskDefKey());
			String refEnd= (i==ttl.size())?findEndRefId(workflow):findRefIdByKey(workflow,ttl.get(i).getTaskDefKey());
			if(!pathSearch(workflow,refSt,refEnd,isVisited)){				
				result+= "Can't find path between "+ ((i!=0)?ttl.get(i-1).getTaskDefKey():"Start event")+" and "+((i!=ttl.size())?ttl.get(i).getTaskDefKey():"EndEvent")+". <br>";
			}
		}
		System.out.println("done");
		return result;
	}

	private String findEndRefId(Workflow wf) {
		for (ChildShape i : wf.getChildShapes()) {
			if(i.getStencil().getId().equals("EndNoneEvent")) return i.getResourceId();
		}
		return  null;
	}


	public TasksHistory getProcessTimeline(String proInstId) {
        HistoricTasksResPojo runningHistory = null;
        try {
            runningHistory = httpService.postReq("enterprise/historic-tasks/query",
                    new HistoricTasksReqPojo(proInstId, "created-asc", false, Integer.MAX_VALUE), HistoricTasksResPojo.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        List<TaskTimeLine> timeLineRunning = runningHistory.getData().stream().map(m -> new TaskTimeLine(m, "running"))
                .collect(Collectors .toList());

        HistoricTasksResPojo completedHistory = null;
        try {
            completedHistory = httpService.postReq("enterprise/historic-tasks/query",
                    new HistoricTasksReqPojo(proInstId, "created-asc", true, Integer.MAX_VALUE), HistoricTasksResPojo.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        List<TaskTimeLine> timeLineCompleted = completedHistory.getData().stream()
                .map(m -> new TaskTimeLine(m, "completed")).collect(Collectors.toList());

        List<TaskTimeLine> timeLine = new ArrayList<>();
        timeLine.addAll(timeLineCompleted);
        timeLine.addAll(timeLineRunning);

        

        return new TasksHistory(timeLine);
    }


	private String getDeployment(String string) {
		// TODO Auto-generated method stub
		ProcessDeployment m=deploymentRepoSpringImpl.findDeploymentObjectByDeploymentId(string);
		return m.getDeploymentObject();
	}


	private String findRefIdByKey(Workflow wf, String taskDefKey) {
		for (ChildShape i : wf.getChildShapes()) {
			if(i.getProperties().getOverrideid().equals(taskDefKey)) return i.getResourceId();
		}
		return  null;
	}

	private String findStartRefId(Workflow wf) {
		for (ChildShape i : wf.getChildShapes()) {
			if(i.getStencil().getId().equals("StartNoneEvent")) return i.getResourceId();
		}
		return  null;
	}

	
	boolean pathSearch(Workflow wf, String start, String end,Set<String> isVisited){
		ChildShape s=findNodeByRefId(wf,start);

		for (Outgoing out:s.getOutgoing()){
			if (isVisited.contains(out.getResourceId())) return false;
			isVisited.add(out.getResourceId());
			ChildShape next=findNodeByRefId(wf, out.getResourceId());
			if(next.getStencil().getId().equals("UserTask")||next.getStencil().getId().equals("EndNoneEvent")){
				if(next.getResourceId().equals(end)){
					return true;
				}
			}else{
				if(pathSearch(wf, out.getResourceId(), end,isVisited))
					return true;
			}
		}
		return false;
	}

	ChildShape findNodeByRefId(Workflow wf, String refId){

		for (ChildShape i : wf.getChildShapes()) {
			if(i.getResourceId().equals(refId)) return i;
		}
		return  null;
	}
	

}
