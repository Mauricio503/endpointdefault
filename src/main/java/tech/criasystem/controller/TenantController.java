package tech.criasystem.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.criasystem.dto.TenantDTO;
import tech.criasystem.dto.res.ResponseMesageDTO;
import tech.criasystem.model.Tenant;
import tech.criasystem.service.SchemaDDLService;

@RestController
@RequestMapping("/api/tenant")
public class TenantController {

	@Autowired
	private SchemaDDLService schemaDDLService;

	@PostMapping("")
    public ResponseEntity<ResponseMesageDTO> register(@Valid @RequestBody TenantDTO dto){
		Tenant tenant = new Tenant();
		tenant.dtoToModel(dto);
		try {
			if(schemaDDLService.createSchema(tenant)) {
				 return new ResponseEntity<ResponseMesageDTO>(new ResponseMesageDTO(),HttpStatus.OK);
			}else {
				return new ResponseEntity<ResponseMesageDTO>(new ResponseMesageDTO("Já tem um Schema criado"),
						HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (Exception e) {
			e.getStackTrace();
	        return new ResponseEntity<ResponseMesageDTO>(new ResponseMesageDTO(e.getMessage()),HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping
	public ResponseEntity<ResponseMesageDTO> drop(@Valid @RequestBody TenantDTO dto){
		Tenant tenant = new Tenant();
		tenant.dtoToModel(dto);
		try {
			if(schemaDDLService.deleteSchema(tenant)) {
				 return new ResponseEntity<ResponseMesageDTO>(new ResponseMesageDTO(),HttpStatus.OK);
			}else {
				return new ResponseEntity<ResponseMesageDTO>(new ResponseMesageDTO("Este Schema não existe"),
						HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (Exception e) {
			e.getStackTrace();
	        return new ResponseEntity<ResponseMesageDTO>(new ResponseMesageDTO(e.getMessage()),HttpStatus.BAD_REQUEST);
		}
	}
}
