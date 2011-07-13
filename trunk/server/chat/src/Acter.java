import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class Acter implements Runnable {
	private Server server;
	
	private Socket s;
	private BufferedReader in;
	private BufferedWriter out;
	
	public Boolean runEnabled=true;
	
	public String userName="";
	//��ҵ�ǰ��״̬��0��Ϊ��ʼ������Ҫ�ṩ���֣�1������״̬
	public int state=0;
	
	public Acter(Server server, Socket client){
		this.server = server;
		s = client;
		
		String clientIP = s.getRemoteSocketAddress().toString();
		//String clientIP = s.getInetAddress().getHostAddress();
		System.out.println(clientIP);
		
		try{
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			
			send("���������Ĵ�����");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void destroy(){
		
	}
	
	@Override
	public void run() {
		try{
			while(runEnabled){
				String command = getCommand();
				if(state == 0){
					userName = command;
					state = 1;
					send("���������ǣ�"+userName);
					//ͬʱ��Ҫ֪ͨ����Acter���˽�����
					server.onMessage(this,"SYS_CAST",userName+" ������");
				}
				if(state == 1){
					//��ҿ�ʼ˵��
					server.onMessage(this,"ACTER_CHAT",command);
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getCommand(){
		StringBuffer str = new StringBuffer();
		try{
			str = str.append(in.readLine());
		}catch(IOException e){}
		
		return str.toString();
	}
	
	public void send(String str){
		try{
			out.write(str);
			out.newLine();
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
			destroy();
		}
	}
}