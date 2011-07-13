import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;


public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server server = new Server();
		server.init();
		server.start();
	}
	
	///////
	
	public int port=7778;
	
	public LinkedList<Acter> clients=new LinkedList<Acter>();
	
	public Server(){		
	}
	
	public void init(){
		
	}
	
	public void start(){
		System.out.println("server start listening port...");
		
		try{
			ServerSocket server = new ServerSocket();
			server.bind(new InetSocketAddress(port), 100);
			
			Socket client=null;
			while(true){
				client = server.accept();
				Acter a = new Acter(this,client);
				clients.add(a);
				new Thread(a).start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void onMessage(Acter sender, String msg, String value){
		if(msg.equals("SYS_CAST")){
			for(Iterator<Acter> it=clients.iterator();it.hasNext();){
				Acter a = (Acter)it.next();
				a.send(value);
			}
			
		}
		if(msg.equals("ACTER_CHAT")){
			for(Iterator<Acter> it=clients.iterator();it.hasNext();){
				Acter a = (Acter)it.next();
				a.send(sender.userName + "หตฃบ" +value);
			}
		}
	}
	
	public void removeClient(String userName){
		for(Iterator<Acter> it=clients.iterator();it.hasNext();){
			Acter a = (Acter)it.next();
			if(a.userName == userName){
				clients.remove(a);
				break;
			}
		}
	}
}
