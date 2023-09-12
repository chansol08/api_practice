package practice.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * multiCatting practice
 */
public class MultiChatServer {
    HashMap<String, OutputStream> clients;

    public MultiChatServer() {
        clients = new HashMap<>();
        Collections.synchronizedMap(clients); //동기화
    }

    /**
     * serverSocket 생성
     */
    public void start() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("start server...");

            while (true) {
                socket = serverSocket.accept(); //클라이언트 정보를 담은 소켓 생성
                System.out.println(socket.getInetAddress() + ":" + socket.getPort() + " connect!");

                ServerReceiver thread = new ServerReceiver(socket); //스레드 생성
                thread.start(); //run()
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  broadcasting 동작 메서드
     * @param message
     */
    void sendToAll(String message) {
        Iterator iterator = clients.keySet().iterator(); //HashMap 정보를 iterator 객체에 저장

        while (iterator.hasNext()) { //루프를 돌며 메시지 전송
            try {
                DataOutputStream out = (DataOutputStream) clients.get(iterator.next());
                out.writeUTF(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * disconnect 메서드
     * @param in
     * @param out
     * @param socket
     */
    public void disconnect(InputStream in,OutputStream out, Socket socket) {
        try {
            if (socket != null) { //매개 변수로 넘어온 리소스 해제
                in.close();
                out.close();
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MultiChatServer().start();
    }

    /**
     * inner class
     */
    class ServerReceiver extends Thread {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        ServerReceiver(Socket socket) {
            this.socket = socket;

            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String name = "";

            try {
                name = in.readUTF();

                if (clients.get(name) != null) { //같은 이름의 사용자 존재
                    out.writeUTF("#aleady exist name : " + name);
                    out.writeUTF("#please reconnect by other name");
                    disconnect(in, out, socket);
                    socket = null;
                    System.out.println(socket.getInetAddress() + ":" + socket.getPort() + " disconnect!");
                } else { //정상 동작
                    sendToAll("#" + name + "join!");
                    clients.put(name, out);

                    while (in != null) { //읽은 드린 메시지 뿌리는 동작
                        sendToAll(in.readUTF());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    sendToAll("#" + name + "exit!");
                    System.out.println(socket.getInetAddress() + ":" + socket.getPort() + "disconnect!");
                    disconnect(in, out, socket);
                    clients.remove(name);
                }
            }
        }
    }
}
