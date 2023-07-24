package gov.uspto.pe2e.cpc.wms.migration.analysis.workflow;


import lombok.Data;

@Data
public class Task {
    private String id;
    private String name;
    private Object description;
    private Object category;
    private String created;
    private String dueDate;
    private String endDate;
    private Object duration;
    private Integer priority;
    private Object parentTaskId;
    private Object parentTaskName;
    private String processInstanceId;
    private Object processInstanceName;
    private String processDefinitionId;
    private Object processDefinitionName;
    private Object processDefinitionDescription;
    private Object processDefinitionKey;
    private Object processDefinitionCategory;
    private Integer processDefinitionVersion;
    private Object processDefinitionDeploymentId;
    private Object formKey;
    private Object processInstanceStartUserId;
    private Boolean initiatorCanCompleteTask;
    private Boolean deactivateUserTaskReassignment;
    private Boolean adhocTaskCanBeReassigned;
    private String taskDefinitionKey;
    private String executionId;
    private Boolean memberOfCandidateGroup;
    private Boolean memberOfCandidateUsers;
    private Boolean managerOfCandidateGroup;
}
