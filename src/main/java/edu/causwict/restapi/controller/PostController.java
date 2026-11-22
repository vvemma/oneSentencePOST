package edu.causwict.restapi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.causwict.restapi.entity.Post;
import edu.causwict.restapi.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	// Create
	@PostMapping
	public Post create(@RequestBody Map<String, Object> param) {
		String title = (String) param.get("title");
		String content = (String) param.get("content");
		Post created = postService.create(title, content);

		return created;
	}

	// Read (list)
	@GetMapping
	public List<Post> list() {
		return postService.findAll();
	}

	// Update
	@PutMapping("/{id}")
	public Post update(@PathVariable Long id, @RequestBody Map<String, Object> param) {
		String title = (String) param.get("title");
		String content = (String) param.get("content");
		Post updated = postService.update(id, title, content);

		return updated;
	}
}
