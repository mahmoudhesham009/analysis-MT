package gov.uspto.pe2e.cpc.wms.migration.analysis.workflow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessInstanceData {
    private String id;
    private String name;
    private String businessKey;
    private String processDefinitionId;
    private String tenantId;
    private Date started;
    private Date ended;
    private String processDefinitionName;
    private String processDefinitionDescription;
    private String processDefinitionKey;
    private String processDefinitionCategory;
    private Integer processDefinitionVersion;
    private String processDefinitionDeploymentId;
    private Boolean graphicalNotationDefined;
    private Boolean startFormDefined;
    private Boolean suspended;
}