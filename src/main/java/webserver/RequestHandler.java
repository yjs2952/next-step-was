package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(WebServer.class);
    private Socket connection;

    public RequestHandler(Socket connection){
        this.connection = connection;
    }

    public void run(){
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String requestUrl = getRequestUrl(br.readLine());

            List<HttpRequestUtils.Pair> headers = getHeaders(br);

            if (requestUrl != null && requestUrl.contains("/user/create")) {
                Map<String, String> paramsMap = HttpRequestUtils.parseQueryString(getParams(requestUrl));
                User user = new User(paramsMap.get("userId"), paramsMap.get("password"), paramsMap.get("name"), paramsMap.get("email"));
                System.out.println(user.toString());
                DataBase.addUser(user);

                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(br.readLine());

                    if (line.equals("")) {
                        break;
                    }
                }
            }

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp" + requestUrl).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException ie) {
            log.error(ie.getMessage());
        }
    }

    private String getParams(String requestUrl) {
        int index = requestUrl.indexOf("?");
        return requestUrl.substring(index + 1);
    }

    private String getRequestPath(String requestUrl) {
        int index = requestUrl.indexOf("?");
        return requestUrl.substring(0, index);
    }

    private List<HttpRequestUtils.Pair> getHeaders(BufferedReader br) throws IOException {
        List<HttpRequestUtils.Pair> headers = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
            headers.add(HttpRequestUtils.parseHeader(line));
            if (line.equals("")) {
                break;
            }
        }
        return headers;
    }

    private String getRequestUrl(String line) {
        String requestUrl = null;
        String[] tokens = line.split(" ");
        requestUrl = tokens[1];
        return requestUrl;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8 \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
