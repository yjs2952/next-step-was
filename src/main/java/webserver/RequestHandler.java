package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

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

            // TODO: 18. 12. 11 사용자 요청에 대한 처리는 이 곳에 구현

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            String requestUrl = null;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);

                if (line.equals("")) {
                    break;
                }

                if (!line.contains(":")) {
                    String[] tokens = line.split(" ");
                    requestUrl = tokens[1];
                }
            }

            User user = null;

            if (requestUrl != null && requestUrl.contains("create?")) {
                int index = requestUrl.indexOf("?");
                String requestPath = requestUrl.substring(0, index);
                String params = requestUrl.substring(index + 1);

                Map<String, String> paramsMap = HttpRequestUtils.parseQueryString(params);

                user = new User(paramsMap.get("userId"), paramsMap.get("password"), paramsMap.get("name"), paramsMap.get("email"));

                System.out.println(user.toString());

                return;
            }



            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp" + requestUrl).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException ie) {
            log.error(ie.getMessage());
        }
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
