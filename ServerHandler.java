import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;


class ServerHandler extends Thread{
	private String root;
	private Socket sock;
	public ServerHandler(String root, Socket sock) {
		this.root = root;
		this.sock = sock;
	}
	@Override
	public void run() {
		File f = new File(root);
		if (!f.isDirectory()){
			throw new IllegalArgumentException("Invalid root path- must be directry");
		}
		if (!f.canRead() ){
			try {
				throw new Exception(" Could not read directory");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//can read now
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			PrintWriter pw = new PrintWriter(sock.getOutputStream());
			BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());
			
			//get file request
			//for example
			
//			GET /wakeupbox/index.html HTTP/1.1
//			Host: localhost
//			Connection: keep-alive
//			Cache-Control: max-age=0
//			Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
//			User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.52 Safari/537.17
//			Accept-Encoding: gzip,deflate,sdch
//			Accept-Language: en-US,en;q=0.8
//			Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3
//			If-None-Match: "462d42-387-4d405e446904f"
//			If-Modified-Since: Thu, 24 `Jan 2013 10:01:16 GMT
			
			
			//now accept only GET method
			String line = br.readLine();
			System.out.println("an request for"+ line);
			String mineType;
			if (line.startsWith("GET")){
				StringTokenizer tokens = new StringTokenizer(line);
				System.out.println("first token" + tokens);
				String method = tokens.nextToken();
				String filename = tokens.nextToken().toLowerCase();
				System.out.println("Request for " + filename);
				if (filename.equals("/html") ){
					filename = "index.html"; //return file index.html
					mineType = "text/html";
				}
				else if (filename.equals("/jpeg")){
					filename = "1.png";
					mineType = "image/png";
				}
				else if (filename.equals("/mjpeg")){
					filename = "1.mjpg";
					mineType = "video/x-motion-jpeg";
				}else{
					//not found
					pw.println("HTTP/1.0 404 File Not Found");
					pw.println();
					//close socket and exit
					pw.flush();
					bos.flush();
					//sock.close();
					return;
				}
				//else check file before send
				String path = new File(root).getCanonicalPath() + File.separator + filename;
				//create bufferedInputstream to read file
				System.out.println("Request for : " + path);
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
				byte[] buffer = new byte[10* 1024 ];
				int read = bis.read(buffer);
				
				//print header
				//demo
//				HTTP/1.1 200 OK
//				Date: Thu, 24 Jan 2013 15:58:04 GMT
//				Server: Apache/2.2.22 (Ubuntu)
//				Last-Modified: Thu, 24 Jan 2013 10:01:16 GMT
//				ETag: "462d42-387-4d405e446904f"
//				Accept-Ranges: bytes
//				Vary: Accept-Encoding
//				Content-Encoding: gzip
//				Content-Length: 498
//				Keep-Alive: timeout=5, max=100
//				Connection: Keep-Alive
//				Content-Type: text/html
				pw.write("HTTP/1.0 200 OK\r\n");
				pw.write("Date: " + new Date() + "\r\n");
				pw.write("Server: ServerDemo/0.0.1(Linux Mint)\r\n");
				pw.write("Content-Type: "+ mineType+"\r\n\r\n");
				pw.flush();
				bos.flush();
				
				//print file
				while (read != -1){
					bos.write(buffer,0, read);
					read = bis.read(buffer);
				}
				//close stream
				pw.flush();
				bos.flush();
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
		
		
	}
}
