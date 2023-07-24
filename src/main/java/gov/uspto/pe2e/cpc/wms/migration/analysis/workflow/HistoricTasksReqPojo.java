package gov.uspto.pe2e.cpc.wms.migration.analysis.workflow;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricTasksReqPojo {
    String processInstanceId;
    String sort;
    boolean finished;
    Integer size;

}
