
package gov.uspto.pe2e.cpc.wms.migration.analysis.workflow;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "resourceId"
})
@Generated("jsonschema2pojo")
@Data
public class Target {

    @JsonProperty("resourceId")
    private String resourceId;

}
