package tech.criasystem.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tech.criasystem.authentication.JwtTokenUtil;
import tech.criasystem.authentication.UsuarioService;
import tech.criasystem.dto.res.UsuariotResDTO;
import tech.criasystem.model.Usuario;

@RestController
@RequestMapping("/api/login")
public class LoginController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
    public ResponseEntity<UsuariotResDTO> post(@Valid @RequestBody Usuario usuario){
		try{
			if(usuarioService.autenticacao(usuario)){
				String jwtToken = new JwtTokenUtil().generateToken(usuario);
                return new ResponseEntity<UsuariotResDTO>(new UsuariotResDTO(new Usuario("","",jwtToken)),HttpStatus.OK);
	            }
	            else
	                return new ResponseEntity<UsuariotResDTO>(HttpStatus.UNAUTHORIZED);
	        }
	        catch(Exception ex)
	        {
	        	return new ResponseEntity<UsuariotResDTO>(HttpStatus.BAD_REQUEST);
	        } 
	   }
}
