package practice.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * client socket
 */
public class ClientSocket {

    public static void main(String[] args) {
        try {
            //클라이언트 소켓 생성 server => accept()
            Socket socket = new Socket("127.0.0.1", 9999);
            System.out.println("connection success!");

            //전송할 메시지 입력 스케너
            Scanner scanner = new Scanner(System.in);
            System.out.print("write your message > ");
            String message = scanner.nextLine();

            //메시지 전송 스트림
            OutputStream out = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeUTF(message);

            //돌아온 메시지 받는 스트림
            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            System.out.println("receive : " + dis.readUTF());

            //리소스 해제
            dos.close();
            out.close();
            dis.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
