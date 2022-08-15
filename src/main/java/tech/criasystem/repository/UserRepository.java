package tech.criasystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.criasystem.model.UserLogin;

@Repository
public interface UserRepository extends JpaRepository<UserLogin, Long> {

	UserLogin findByUsername(String username);
}
