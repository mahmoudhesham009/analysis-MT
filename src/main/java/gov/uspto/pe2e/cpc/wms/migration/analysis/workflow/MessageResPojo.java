package gov.uspto.pe2e.cpc.wms.migration.analysis.workflow;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResPojo {
	String message;
	Integer status;
	public MessageResPojo(List<String> invalidProcess) {
		message="These processes are invalid: <br>";
		invalidProcess.forEach(i->{
			message+=i+"<br>";
		});
		status=400;
	}

	
}

