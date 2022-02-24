package tech.criasystem.repository;

import tech.criasystem.model.UserLogin;

public interface UserRepository extends BaseRepository<UserLogin, Long> {

	UserLogin findByUsername(String username);
}
