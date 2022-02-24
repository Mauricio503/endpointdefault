package tech.criasystem.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.criasystem.authentication.UserService;
import tech.criasystem.dto.req.UserReqDTO;
import tech.criasystem.dto.res.UserResDTO;
import tech.criasystem.model.UserLogin;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserService userService;

	@PostMapping("/register")
    public ResponseEntity<UserResDTO> register(@Valid @RequestBody UserReqDTO dto){
		UserLogin user = dto.toModel(new UserLogin());
		try{
			if(!userService.userExists(user)){
				userService.save(user);
                return new ResponseEntity<UserResDTO>(new UserResDTO(new UserLogin("","","")),HttpStatus.OK);
	            }
	            else
	                return new ResponseEntity<UserResDTO>(HttpStatus.UNAUTHORIZED);
	        }
	        catch(Exception ex)
	        {
	        	return new ResponseEntity<UserResDTO>(HttpStatus.BAD_REQUEST);
	        } 
	   }
}
