package chat;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {

	private static final String TAG = "ChatServer : ";
	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; // ����� Ŭ���̾�Ʈ Ŭ����(����)�� ��� �÷���

	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "Ŭ���̾�Ʈ ���� �����...");
			// ���� ������ ����
			while (true) {
				Socket socket = serverSocket.accept();
				ClientInfo clientInfo = new ClientInfo(socket); // Ŭ���̾�Ʈ ���� ���
				clientInfo.start();
				vc.add(clientInfo);
				System.out.println("Ŭ���̾�Ʈ ���� ����");
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	class ClientInfo extends Thread {

		Socket socket;
		BufferedReader reader;
		PrintWriter writer; // BufferedWriter�� �ٸ� ���� �������� �Լ��� ����

		public ClientInfo(Socket socket) {
			this.socket = socket;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
			} catch (Exception e) {
				System.out.println("���� ���� ����" + e.getMessage());
			}
		}

		// ���� : Ŭ���̾�Ʈ�� ���� ���� �޽����� ��� Ŭ���̾�Ʈ���� ������
		@Override
		public void run() {
			String input = null;
			OutputStream bs = null;
			
			try {
				while((input=reader.readLine())!=null) {
					try {
						 bs =new BufferedOutputStream(new FileOutputStream("./test.txt",true));
						 bs.write((input+"\n").getBytes());
						 bs.close();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println("Ŭ���̾�Ʈ..??? : "+input);
					for(int i=0;i<vc.size();i++) {
						if(vc.get(i) !=this) {
							vc.get(i).writer.println(input);
							vc.get(i).writer.flush();
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new ChatServer();
	}
}
