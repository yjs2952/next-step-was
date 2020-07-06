package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);

                if (line.equals("")) {
                    break;
                }
            }

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "Hello World! Hello JAVA! Welcome to HELL!".getBytes();
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
