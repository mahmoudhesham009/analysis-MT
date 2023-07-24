package gov.uspto.pe2e.cpc.wms.migration.analysis.workflow;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class HistoricTasksResPojo {
    private Integer size;
    private Integer total;
    private Integer start;
    private List<Task> data;

    public HistoricTasksResPojo(List<Task> data){
        this.data = data;
    }

}
