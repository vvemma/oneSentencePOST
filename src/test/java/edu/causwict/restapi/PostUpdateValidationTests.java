package edu.causwict.restapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PostUpdateValidationTests {

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() throws Exception {
		// id=1 "첫 번째 글", id=2 "두 번째 글"
		mockMvc.perform(post("/api/posts").contentType(MediaType.APPLICATION_JSON)
			.content("{\"title\":\"첫 번째 글\",\"content\":\"내용\"}")).andExpect(status().isOk());
		mockMvc.perform(post("/api/posts").contentType(MediaType.APPLICATION_JSON)
			.content("{\"title\":\"두 번째 글\",\"content\":\"내용\"}")).andExpect(status().isOk());
	}

	private ResultActions updatePost(long id, String json) throws Exception {
		return mockMvc.perform(put("/api/posts/" + id)
			.contentType(MediaType.APPLICATION_JSON)
			.content(json));
	}

	@Test
	void 새_제목으로_수정된다() throws Exception {
		updatePost(1, "{\"title\":\"바뀐 제목\",\"content\":\"내용\"}").andExpect(status().isOk());
	}

	@Test
	void 자기_제목을_그대로_두고_내용만_수정된다() throws Exception {
		updatePost(1, "{\"title\":\"첫 번째 글\",\"content\":\"새 내용\"}").andExpect(status().isOk());
	}

	@Test
	void 제목이_없으면_400() throws Exception {
		updatePost(1, "{\"content\":\"내용\"}").andExpect(status().isBadRequest());
	}

	@Test
	void 제목이_공백이면_400() throws Exception {
		updatePost(1, "{\"title\":\" \",\"content\":\"내용\"}").andExpect(status().isBadRequest());
	}

	@Test
	void 제목이_31자면_400() throws Exception {
		updatePost(1, "{\"title\":\"" + "가".repeat(31) + "\",\"content\":\"내용\"}").andExpect(status().isBadRequest());
	}

	@Test
	void 다른_글과_같은_제목이면_409() throws Exception {
		updatePost(1, "{\"title\":\"두 번째 글\",\"content\":\"내용\"}").andExpect(status().isConflict());
	}

	@Test
	void 없는_글이면_404() throws Exception {
		updatePost(99, "{\"title\":\"아무 제목\",\"content\":\"내용\"}").andExpect(status().isNotFound());
	}
}
