package web;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPServerListener extends Thread {
    
    private final Logger LOGGER = LoggerFactory.getLogger(HTTPServerListener.class);
    
    static final File WEB_ROOT = new File("resources/webapp");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    
    private Socket connect = null;
    
    public HTTPServerListener(Socket socket) {
        this.connect = socket;
    }
    
    @Override
    public void run() {
        LOGGER.info("Connection opened.");
        
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        
        String fileRequested = null;
        
        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());
            
            String input = in.readLine();
            if( input == null ) {
                return;
            }
            LOGGER.info(String.format("Request: %s", input));
            
            // we parse the request with a string tokenizer
            StringTokenizer parse = new StringTokenizer(input);

            String method = parse.nextToken().toUpperCase();
            fileRequested = parse.nextToken().toLowerCase();
            
            // we support only GET and HEAD methods, we check
            if (!"GET".equals(method)  && !"HEAD".equals(method)) {
                LOGGER.debug("501 Not Implemented : " + method + " method.");
                
                // we return the not supported file to the client
                File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
                int fileLength = (int) file.length();
                String contentMimeType = "text/html";
                //read content to return to client
                byte[] fileData = readFileData(file, fileLength);
                    
                // we send HTTP Headers with data to client
                out.println("HTTP/1.1 501 Not Implemented");
                out.println("Server: Java HTTP Server from SSaurel : 1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + contentMimeType);
                out.println("Content-length: " + fileLength);
                out.println(); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer
                // file
                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
                
            } else {
                // GET or HEAD method
                if (fileRequested.endsWith("/")) {
                    fileRequested += DEFAULT_FILE;
                }
                
                LOGGER.debug(String.format("%s%s", WEB_ROOT, fileRequested));
                
                File file = new File(WEB_ROOT, fileRequested);
                int fileLength = (int) file.length();
                String content = getContentType(fileRequested);
                
                if ("GET".equals(method)) { // GET method so we return content
                    byte[] fileData = readFileData(file, fileLength);
                    
                    // send HTTP Headers
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Date: " + new Date());
                    out.println("Content-type: " + content);
                    out.println("Content-length: " + fileLength);
                    out.println(); // blank line between headers and content, very important !
                    out.flush(); // flush character output stream buffer
                    
                    dataOut.write(fileData, 0, fileLength);
                    dataOut.flush();
                }
                
                LOGGER.debug("File " + fileRequested + " of type " + content + " returned");
            }
            
        } catch (FileNotFoundException fnfe) {
            try {
                fileNotFound(out, dataOut, fileRequested);
            } catch (IOException ioe) {
                LOGGER.error("Error with file not found exception : " + ioe.getMessage());
            }
            
        } catch (IOException ioe) {
            LOGGER.error("Server error : " + ioe);
        } finally {

            LOGGER.debug(out.toString());
            
            try {
                in.close();
                out.close();
                dataOut.close();
                connect.close(); // we close socket connection
            } catch (Exception e) {
                LOGGER.error("Error closing stream : " + e.getMessage());
            } 

            LOGGER.info("Connection closed.");
        }
        
        
    }
    
    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];
        
        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null) 
                fileIn.close();
        }
        
        return fileData;
    }
    
    // return supported MIME Types
    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
            return "text/html;charset=utf8;";
        else
            return "text/plain;charset=utf8;";
    }
    
    private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
        File file = new File(WEB_ROOT, FILE_NOT_FOUND);
        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, fileLength);
        
        out.println("HTTP/1.1 404 File Not Found");
        out.println("Server: Java HTTP Server from SSaurel : 1.0");
        out.println("Date: " + new Date());
        out.println("Content-type: " + content);
        out.println("Content-length: " + fileLength);
        out.println(); // blank line between headers and content, very important !
        out.flush(); // flush character output stream buffer
        
        dataOut.write(fileData, 0, fileLength);
        dataOut.flush();

        LOGGER.debug("File " + fileRequested + " not found");
    }
}