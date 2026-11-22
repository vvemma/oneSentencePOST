package edu.causwict.restapi.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.causwict.restapi.entity.Post;
import edu.causwict.restapi.repository.InMemoryPostRepository;

@Service
public class PostService {

	private final InMemoryPostRepository postRepository;

	public PostService(InMemoryPostRepository postRepository) { // 생성자 주입
		this.postRepository = postRepository;
	}

	private static final int MAX_TITLE_LENGTH = 30;

	public Post create(String title, String content) {
		validateTitleFormat(title);
		if (postRepository.existsByTitle(title)) {
			throw duplicateTitle();
		}

		Post post = new Post(null, title, content);
		return postRepository.save(post);
	}

	private ResponseStatusException duplicateTitle() {
		return new ResponseStatusException(HttpStatus.CONFLICT, "이미 같은 제목의 게시글이 있습니다.");
	}

	private void validateTitleFormat(String title) {
		if (title == null || title.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 비어 있을 수 없습니다.");
		}
		if (title.codePointCount(0, title.length()) > MAX_TITLE_LENGTH) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				"제목은 " + MAX_TITLE_LENGTH + "자를 넘을 수 없습니다.");
		}
	}

	public List<Post> findAll() {
		return postRepository.findAll().stream()
			.sorted(Comparator.comparing(Post::getId))
			.toList();
	}

	public Post update(Long id, String title, String content) {
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다. id=" + id));

		validateTitleFormat(title);
		if (postRepository.existsByTitleAndIdNot(title, id)) {
			throw duplicateTitle();
		}

		post.setTitle(title);
		post.setContent(content);
		return postRepository.save(post);
	}
}