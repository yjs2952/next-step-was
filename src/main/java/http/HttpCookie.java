package http;

import util.HttpRequestUtils;

import java.util.Map;

public class HttpCookie {
    private Map<String, String> cookies;

    public HttpCookie(String cookieValue) {
        this.cookies = HttpRequestUtils.parseCookies(cookieValue);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
