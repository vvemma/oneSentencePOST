package edu.causwict.restapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PostCreateValidationTests {

	@Autowired
	private MockMvc mockMvc;

	private ResultActions createPost(String json) throws Exception {
		return mockMvc.perform(post("/api/posts")
			.contentType(MediaType.APPLICATION_JSON)
			.content(json));
	}

	@Test
	void 정상_제목이면_작성된다() throws Exception {
		createPost("{\"title\":\"안녕하세요\",\"content\":\"내용\"}").andExpect(status().isOk());
	}

	@Test
	void 제목이_없으면_400() throws Exception {
		createPost("{\"content\":\"내용\"}").andExpect(status().isBadRequest());
	}

	@Test
	void 제목이_공백이면_400() throws Exception {
		createPost("{\"title\":\"   \",\"content\":\"내용\"}").andExpect(status().isBadRequest());
	}

	@Test
	void 제목이_30자면_작성된다() throws Exception {
		createPost("{\"title\":\"" + "가".repeat(30) + "\",\"content\":\"내용\"}").andExpect(status().isOk());
	}

	@Test
	void 제목이_31자면_400() throws Exception {
		createPost("{\"title\":\"" + "가".repeat(31) + "\",\"content\":\"내용\"}").andExpect(status().isBadRequest());
	}

	@Test
	void 중복_제목이면_409() throws Exception {
		createPost("{\"title\":\"같은 제목\",\"content\":\"첫 글\"}").andExpect(status().isOk());
		createPost("{\"title\":\"같은 제목\",\"content\":\"둘째 글\"}").andExpect(status().isConflict());
	}
}
