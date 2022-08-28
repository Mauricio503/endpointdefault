package tech.criasystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.criasystem.model.Tenant;


@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    
	Tenant findById(Long clientId);
}
