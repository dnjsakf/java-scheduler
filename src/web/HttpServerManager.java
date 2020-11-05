package web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;

class MainHandler implements HttpHandler {
    
    private final Logger LOGGER = LoggerFactory.getLogger(MainHandler.class);
    private final String WEBAPP_PATH = "/webapp";
    
    private void printRequestInfo(HttpExchange ex) {
        // Request Headers
        Headers reqHeaders = ex.getRequestHeaders();
        LOGGER.info("========[REQUEST INFO]");
        reqHeaders.entrySet().forEach(( data )->{
            LOGGER.info(String.format("====[HEADERS][%s] %s", data.getKey(), data.getValue().toString()));
        });
        
        // Request Method
        String reqMethod = ex.getRequestMethod();
        LOGGER.info(String.format("====[%-15s] %s", "METHOD", reqMethod));
        
        // Request URI
        URI reqURI = ex.getRequestURI();
        LOGGER.info(String.format("====[%-15s] %s", "URI", reqURI));

        // Request QueryString
        String reqQueryString = reqURI.getQuery();
        LOGGER.info(String.format("====[%-15s] %s", "QUERY_STRING", reqQueryString));
        
        // Request Principal
        HttpPrincipal principal = ex.getPrincipal();
        LOGGER.info(String.format("====[%-15s] %s", "PRINCIPAL", principal));
        LOGGER.info("===================================");
    }
    
    private void defaultBody(OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
        writer.write("<html>");
        writer.write("<head>");
        writer.write("<title>Hello, HttpServer!!!</title>");
        writer.write("</head>");
        writer.write("<body>");
        writer.write("<h5>Hello, HttpServer!!!</h5>");
        writer.write("</body>");
        writer.write("</html>");
        writer.close();
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            //printRequestInfo(ex);
            
            // Write Response Headers
            Headers headers = ex.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");

            // Write Response Body
            OutputStream respBody = ex.getResponseBody();
            InputStream contentStream = null;
            byte[] contentBtyes = null;
            int contentLength = 0;
            
            try {
                LOGGER.info("========[RESPONSE INFO]");
                
                // Set Response Content
                URI reqURI = ex.getRequestURI();
                String reqPath = reqURI.getPath();
                String contentType = "text/plain";
                
                if( "/".equals(reqPath) ) {
                    reqPath = "/index.html";
                }
                if( reqPath.endsWith(".htm") || reqPath.endsWith(".html") ) {
                    contentType = "text/html";
                }
                
                // Read Content To Stream
                contentStream = MainHandler.class.getResourceAsStream(WEBAPP_PATH+reqPath);
                contentBtyes = contentStream.readAllBytes();
                contentLength = contentBtyes.length;
                contentType = String.format("%s;%s", contentType, "charset=UTF-8");

                // Set Response Headers For Content
                headers.add("Content-Type", contentType);
                headers.add("Content-Length", String.valueOf(contentLength));
                ex.sendResponseHeaders(200, contentLength);
                
                // Set Response Content
                respBody.write(contentBtyes);
                
                contentStream.close();
                respBody.close();

                LOGGER.info(String.format("====[%-15s] %s", "Request-URI", reqPath));
                LOGGER.info(String.format("====[%-15s] %s", "Content-Type", contentType));
                LOGGER.info(String.format("====[%-15s] %d", "Content-Length", contentLength));
                LOGGER.info("===================================");
                
            } catch ( IOException e ) {
                e.printStackTrace();
                
                if( contentStream != null ) {
                    contentStream.close();
                }
                if( respBody != null ) {
                    respBody.close();
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            ex.close();
        }
    }
}

public class HttpServerManager extends Thread {
    
    private final int PORT = 8080;
    private final String ROOT_CONTEXT = "/";
    
    public void runServer() {

        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            server.createContext(ROOT_CONTEXT, new MainHandler());
            server.start();
            
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    server.stop(0);
                }
            }));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        HttpServerManager server = new HttpServerManager();
        
        server.start();
    }
    
    @Override
    public void start() {
        runServer();
    }
}
