package practice.socket;

import java.io.*;
import java.net.Socket;

/**
 * server socket
 */
public class ServerSocket {

    public static void main(String[] args) {
        //서버소켓 생성
        java.net.ServerSocket serverSocket = null;

        try {
            //임의 포트 할당
            serverSocket = new java.net.ServerSocket(9999);
            System.out.println("server ready...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                //클라이언트 정보 소켓
                Socket socket = serverSocket.accept(); //대기
                System.out.println("client connect success!");

                //클라이언트 요청 받기
                InputStream in = socket.getInputStream();
                DataInputStream dis = new DataInputStream(in); //브릿지 스트림
                String message = dis.readUTF();

                //응답 보내기
                OutputStream out = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out); //브릿지 스트림
                dos.writeUTF("[ECHO] " + message + " (from server)");

                //리소스 해제
                dos.close();
                out.close();
                dis.close();
                in.close();
                socket.close();
                System.out.println("client socket close...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
