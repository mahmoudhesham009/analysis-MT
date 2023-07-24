package gov.uspto.pe2e.cpc.wms.migration.analysis.repository.mysql;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessDeploymentRepoSpringImpl extends JpaRepository<ProcessDeployment, Integer> {

	Optional<ProcessDeployment> findByDeploymentId(String deploymentId);

	@Query(value="select * from PROCESS_DEPLOYMENT where DEPLOYMENT_ID=:deploymentId",nativeQuery= true)
	ProcessDeployment findDeploymentObjectByDeploymentId(String deploymentId);

}
