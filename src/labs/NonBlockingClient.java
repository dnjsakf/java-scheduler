package labs;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Scanner;

class Receive implements Runnable {
    private CharsetDecoder decoder = null;

    public void run() {
        Charset charset = Charset.forName("UTF-8");
        decoder = charset.newDecoder();
        try {
            while (true) {
                NonBlockingClient.selector.select();
                Iterator<SelectionKey> iterator = NonBlockingClient.selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if(key.isReadable())
                        read(key);

                    iterator.remove();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void read(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

        try {
            byteBuffer.flip();
            String data = decoder.decode(byteBuffer).toString();
            if( data != null && !"".equals(data)) {
                System.out.println("Receive Message - " + data);
            }
            NonBlockingClient.clearBuffer(byteBuffer);
        }
        catch (IOException ex){
            try {
                socketChannel.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

public class NonBlockingClient {
    static Selector selector = null;
    private SocketChannel socketChannel = null;

    public void startServer() throws IOException {
        initServer();
        Receive receive = new Receive();
        new Thread(receive).start();
        startWriter();
    }

    private void initServer() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 3000));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_WRITE);
    }

    private void startWriter() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        Scanner scanner = null;
        try {
            while (true) {
                scanner = new Scanner(System.in);
                String message = scanner.next();
                byteBuffer.clear();
                byteBuffer.put(message.getBytes());
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            scanner.close();
            clearBuffer(byteBuffer);
        }
    }

    static void clearBuffer(ByteBuffer buffer) {
        if (buffer != null) {
            buffer.clear();
        }
    }
    
    public static void main(String[] args) {
        NonBlockingClient client = new NonBlockingClient();
        try {
            client.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
