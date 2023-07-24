package gov.uspto.pe2e.cpc.wms.migration.analysis.workflow;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TasksHistory {
    private List<TaskTimeLine> taskTimeLine;

}
