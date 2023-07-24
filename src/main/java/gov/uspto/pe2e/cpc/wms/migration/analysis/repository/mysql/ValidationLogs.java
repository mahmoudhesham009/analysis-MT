package gov.uspto.pe2e.cpc.wms.migration.analysis.repository.mysql;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONArray;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "validation_logs")
public class ValidationLogs {

	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name="id")
	private int id;
	
	
	@Column(name="job_Id")
	private Integer jobId;
	
	@Column(name="process_id")
	private String processId;
	
	@Column(name="process_name")
	private String processName;
	
	@Column(name="project_num")
	private String projectNum;
	
	@Column(name="details")
	private String details;
	
	
	
	
}
