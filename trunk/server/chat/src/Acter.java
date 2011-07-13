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
	//玩家当前的状态：0：为初始化，需要提供名字；1：聊天状态
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
			
			send("请输入您的大名！");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void destroy(){
		//关闭socket
		if(server!=null){
			
			if(userName!=""){
				server.removeClient(userName);
			}
			server=null;	
		}
		
		close();
		
		runEnabled=false;
	}
	
	public void close(){
		state = 0;
		try{

			if(s!=null){
				s.close();
				s=null;
			}
		} catch (IOException e) {
			System.out.println("ActerThread close error :: "+e);
			//e.printStackTrace();
		}finally{

		}
	}
	
	@Override
	public void run() {
		try{
			while(runEnabled){
				if(s.isClosed()) destroy();
				
				String command = getCommand();
				System.out.println("command:"+command);
				
				if(state == 0){
					userName = command;
					state = 1;
					send("您的名字是："+userName);
					//同时需要通知所有Acter有人进来了
					server.onMessage(this,"SYS_CAST",userName+" 进来了");
					continue;
				}
				if(state == 1){
					//玩家开始说话
					server.onMessage(this,"ACTER_CHAT",command);
					continue;
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
