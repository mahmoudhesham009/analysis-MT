package gov.uspto.pe2e.cpc.wms.migration.analysis.repository.mysql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ValidationLogsRepository extends JpaRepository<ValidationLogs, Integer>{

	List<ValidationLogs> findByJobId(Integer jobId);
	
}
