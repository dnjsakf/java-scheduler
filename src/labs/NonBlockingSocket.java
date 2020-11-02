package labs;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class NonBlockingSocket extends Thread {
    private Selector selector = null;
    private Vector<SocketChannel> room = new Vector<SocketChannel>();

    NonBlockingSocket(int port) throws IOException {
        
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));

        // 서버소켓 채널을 셀렉터에 등록한다.
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void startServer() throws Exception {
        System.out.println("Server Start");

        while (true) {
            selector.select(); //select() 메소드로 준비된 이벤트가 있는지 확인한다.

            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                if (selectionKey.isAcceptable()) {
                    accept(selectionKey);
                }
                else if (selectionKey.isReadable()) {
                    read(selectionKey);
                }

                iterator.remove();
            }
        }
    }

    private void accept(SelectionKey key) throws Exception {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();

        // 서버소켓 accept() 메소드로 서버소켓을 생성한다.
        SocketChannel socketChannel = server.accept();
        // 생성된 소켓채널을 비 블록킹과 읽기 모드로 셀렉터에 등록한다.

        if (socketChannel == null)
            return;

        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        room.add(socketChannel); // 접속자 추가
        System.out.println(socketChannel.toString() + "클라이언트가 접속했습니다.");
    }

    private void read(SelectionKey key) {
        // SelectionKey 로부터 소켓채널을 얻는다.
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024); // buffer 생성

        try {
            socketChannel.read(byteBuffer); // 클라이언트 소켓으로부터 데이터를 읽음
        }
        catch (IOException ex) {
            try {
                socketChannel.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            room.remove(socketChannel);
            ex.printStackTrace();
        }

        try {
            broadcast(byteBuffer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        byteBuffer.clear();
    }

    private void broadcast(ByteBuffer byteBuffer) throws IOException {
        byteBuffer.flip();
        Iterator<SocketChannel> iterator = room.iterator();

        while (iterator.hasNext()) {
            SocketChannel socketChannel = iterator.next();

            if (socketChannel != null) {
                socketChannel.write(byteBuffer);
                byteBuffer.rewind();
            }
        }
    }
    
    public void run() {
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main (String[] args) {
        try {
            NonBlockingSocket socket = new NonBlockingSocket(3000);
            
            socket.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
