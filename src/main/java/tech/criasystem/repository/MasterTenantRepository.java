package tech.criasystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.criasystem.model.MasterTenant;

@Repository
public interface MasterTenantRepository extends JpaRepository<MasterTenant, Integer> {
    
	MasterTenant findById(Long clientId);
}
