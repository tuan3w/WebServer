import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerDemo {
	private File root;
	private int port;
	private ServerSocket serverSock;
	public ServerDemo(String docRoot, int port) throws IOException{
		this.root = new File(docRoot);
		if (!this.root.isDirectory() || !this.root.exists())
			throw new IllegalArgumentException("Invalid root path");
		this.port = port;
		serverSock = new ServerSocket(this.port);
	}

	public void run() {
		while(true){
			try {
				//System.out.println("new Connection...");
				Socket sock = serverSock.accept();
				new ServerHandler(root.getAbsolutePath(),sock).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				try {
					serverSock.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}

		}
	}
	
	//start
	public static void main(String[] args) throws IOException{
		ServerDemo server;
		if (args.length == 1){
			server = new ServerDemo(".", Integer.parseInt(args[0]));
			
		}
		else if (args.length == 2 ){
			server = new ServerDemo(args[0], Integer.parseInt(args[1]));
		}
		else
			server = new ServerDemo(".", 8080);
		System.out.println("Server start...");
		server.run();
	}
}
