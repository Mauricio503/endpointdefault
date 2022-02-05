package tech.criasystem.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.criasystem.model.Post;
import tech.criasystem.repository.PostRepository;


@Service
public class LoadTestDataService {

	@Autowired
	private PostRepository postRepository;

	@Transactional
	public void loadPosts() {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			Post post = new Post("titulo", "Lorem Ipsum");
			posts.add(post);
		}
		this.postRepository.saveAll(posts);
	}

	public void deletePosts() {
		this.postRepository.deleteAll();
	}
}
