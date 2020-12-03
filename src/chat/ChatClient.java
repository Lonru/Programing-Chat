package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame{

	private static final String TAG = "ChatClient : ";
	private ChatClient chatClient = this;
	
	private static final int PORT = 10000;
	
	private JButton btnConnect,btnSend;
	private JTextField tfHost, tfChat;
	private JTextArea taChatList;
	private ScrollPane scrollPane;
	
	private JPanel topPanel,bottomPanel;
	
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private Calendar cal = Calendar.getInstance();
	
	public ChatClient() {
		init();
		setting();
		batch();
		listener();
		setVisible(true);
	}
	private void init() {
		btnConnect = new JButton("connect");
		btnSend = new JButton("send");
		tfHost = new JTextField("127.0.0.1",20);
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10,30);//row, column
		scrollPane = new ScrollPane();
		topPanel = new JPanel();
		bottomPanel = new JPanel();
	}
	private void setting() {
		setTitle("채팅 다대다 클라이언트");
		setSize(350,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); //가운대로 오게함
		taChatList.setBackground(Color.orange);
		taChatList.setForeground(Color.blue);
	}
	private void batch() {
		topPanel.add(tfHost);
		topPanel.add(btnConnect);
		bottomPanel.add(tfChat);
		bottomPanel.add(btnSend);
		scrollPane.add(taChatList);
		
		add(topPanel,BorderLayout.NORTH);
		add(scrollPane,BorderLayout.CENTER);
		add(bottomPanel,BorderLayout.SOUTH);
	}
	private void listener() {
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
	}
	private void send() {
		String chat = "{"+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+"} "+tfChat.getText();
		//1번 taChatList에 뿌리기
		taChatList.append("[내 메시지] "+chat+"\n"); //setText는 덮어씌워짐 
		//2번 서버로 전송
		writer.write(chat+"\n");
		writer.flush();
		//3번 tfChat 비우기
		tfChat.setText("");
	}
	
	private void connect() {
		String host = tfHost.getText(); //127.0...
		try {
			socket = new Socket(host,PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			ReaderThread rt = new ReaderThread();
			rt.start();
		} catch (Exception e1) {
			System.out.println(TAG+"서버 연결 에러 : "+e1.getMessage());
		}
	}
	
	class ReaderThread extends Thread{
		
		//while을 돌면서 서버로 부터 메시지를 받아서 taChatList에 뿌리기
		@Override
		public void run() {
			String input = null;
			try {
				while((input=reader.readLine())!=null) {
					taChatList.append("[상대방 메시지]"+input+"\n");
					System.out.println("서버로 부터 온메시지..? " + input);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new ChatClient();
	}
}

//인터페이스에 ALL 이나 MSG등 만들어서 
//파일 라이터 객체 만들어서 텍스트 어레이에 있는 모든 글을 getTExt로 가져와서 넣을때마다(별로) x버튼 눌러서 창을 끌 때 파일로 저장
//양쪽클라이언트 채팅내역, 파일에 적힌 내용 캡처 