package tech.criasystem.repository;

import tech.criasystem.model.Usuario;

public interface UsuarioRepository extends BaseRepository<Usuario, Long> {

	Usuario findByUsuario(String usuario);
}
