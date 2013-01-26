import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
/**
 * Server Demo implemented in Java
 * @author TuanZendF
 * @version "0.0.1"
 */
import java.net.Socket;


public class ServerDemo {
	private File root;
	private int port;
	private ServerSocket serverSock;
	/**
	 * SeverDemo
	 * @param docRoot {String} root path
	 * @param port {int} port of server socket
	 */
	public ServerDemo(String docRoot, int port) throws IOException{
		this.root = new File(docRoot);
		if (!this.root.isDirectory() || !this.root.exists())
			throw new IllegalArgumentException("Invalid root path");
		this.port = port;
		serverSock = new ServerSocket(this.port);
	}
	/**
	 * Start Server
	 */
	public void run() {
		while(true){
			try {
				Socket sock = serverSock.accept();
				new ServerHandler(root.getAbsolutePath(),sock).start();
			} catch (IOException e) {
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
	
	/**
	 * Main application
	 */
	public static void main(String[] args) throws IOException{
		ServerDemo server;
		//create server with port get from parameter and root Path is current directory
		if (args.length == 1){
			server = new ServerDemo(".", Integer.parseInt(args[0]));
			
		}
		//create server with path and port
		else if (args.length == 2 ){
			server = new ServerDemo(args[0], Integer.parseInt(args[1]));
		}
		//create default server at port 8080 at current Directory
		else
			server = new ServerDemo(".", 8080);
		server.run();
	}
}
