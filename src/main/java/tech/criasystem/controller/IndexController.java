package tech.criasystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.criasystem.service.LoadTestDataService;

@RestController
public class IndexController {

	@Autowired
	private LoadTestDataService loadTestDataService;

	@GetMapping("/index")
	public String index() {
		return "Deu Certo";
	}

	@PostMapping("/api/private/load-test-data")
	public void loadPosts() {
		this.loadTestDataService.loadPosts();
	}

	@DeleteMapping("/api/private/delete-test-data")
	public void deletePosts() {
		this.loadTestDataService.deletePosts();
	}
}
