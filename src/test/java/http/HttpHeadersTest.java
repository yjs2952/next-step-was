package http;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

class HttpHeadersTest {
	@Test
	void add() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Connection: keep-alive");
		assertEquals("keep-alive", headers.getHeader("Connection"));
	}
}
