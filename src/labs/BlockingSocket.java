package labs;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class BlockingSocket {
    public static void main(String[] args)  throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(true); // 블로킹 방식
        serverSocketChannel.bind(new InetSocketAddress(3000)); // 포트 12345를 열고

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept(); // 이 부분에서 연결이 될때딱지 블로킹
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
            System.out.println("Connected : " + inetSocketAddress.getHostName());

            Charset charset = Charset.forName("UTF-8");

            // Client로부터 글자 받기
            ByteBuffer byteBuffer = ByteBuffer.allocate(100);
            socketChannel.read(byteBuffer); // 클라이언트로부터 데이터 읽기
            byteBuffer.flip();
            System.out.println("Received Data : " + charset.decode(byteBuffer).toString());

            // "Hello Client"란 글자 보내기
            byteBuffer = charset.encode("Hello Client");
            socketChannel.write(byteBuffer);

            System.out.println("Sending Success");
        }
    }
}
