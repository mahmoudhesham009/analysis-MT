package gov.uspto.pe2e.cpc.wms.migration.engine.repository.mysql;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.JSONArray;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "migration_job")
public class MigrationJob implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name="id")
	private int id;

	
	@Column(name="from_date")
	private String fromDate;
	
	@Column(name="to_date")
	private String toDate;

	@Lob
	@Column(name="analysis_object")
	private String analysisObject;

	@Column(name="created_date")
	private Date createdDate;
	@Column(name="modify_date")
	private Date modifyDate;
	@Column(name="status")
	private String status;
	
	@Lob
	@Column(name="analysis_object_after_save")
	private String analysisObjectAfterSave;
	
//	@Column(name="processUUID")
//	private String processName;
	
	@Column(name="single_process")
	private boolean singleProcess;
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE)
//	private int id;

	@Column(name="process_name")
	private String processName;
//	private String fromDate;
//	private String toDate;

//	@Lob
//	private String analysisObject;
	@Lob
	private String analysisObjectBeforeSave;

	@Transient
	private JSONArray test;
	
	@Column(name="valid_process")
	private Boolean validProcess;

//	private Date createdDate;
//	private Date modifyDate;

//	private String status;


}
