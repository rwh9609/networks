import java.io.*;
import java.net.*;


public class Talk {
	
	public static Boolean isServer = false;
	public static Boolean isClient = false;
	public static Boolean isAuto = false;
	public static Boolean isHelp = false;
	public static Boolean portNumberArgument = false;
	public static int portNumber = 12987;
	public static String host = "localhost";
	
	public static void main(String[] args) { 
		
		// command line argument processor
		if (args.length == 0) {
			isHelp = true;
			// run in help mode
		}
		for (String arg: args) {
			if (arg.equals("-h")) {
				System.out.println("Client mode enabled. Will try to set up as client.");
				isClient = true;
				continue;
			} else if (arg.equals("-s")) {
				System.out.println("Server mode enabled. Will try to set up as server.");
				isServer = true;
				continue;
			} else if (arg.equals("-a")) {
				isAuto = true;
				System.out.println("Auto-mode enabled. Will try to set up as server or client.");
				continue;
			} else if (arg.equals("-help")){
				isHelp = true;
			} else if (arg.equals("-p")) {
				portNumberArgument = true;
				continue;
			}
			
			// since the logic above "continues" once an argument matches
			// a specific character sequence {-h, -s, -a, -help, and -p}
			// if the isClient or isAuto Boolean flags are true (and a port number
			// is not the next argument), then the following argument MUST be the host 
			// (if invalid, it fails and defers to help).
			if ( (isClient == true || isAuto == true) && portNumberArgument == false ) {
				host = arg;
			}
			
			// processes the port number argument
			if (portNumberArgument == true) {
				// validate port number 
				try {
				portNumber = Integer.parseInt(arg);
				} catch(NumberFormatException e) {
					System.out.println("Invalid port number");
					System.exit(-1);
				} 
				if (portNumber < 1500 || portNumber > 15000) {
					System.out.println("port number = " + portNumber);
					portNumber = 12987;
				}
				portNumberArgument = false;
				continue;
			}
			
			// if the code reaches this point, then there are extraneous arguments
			// or something didn't pass the checker.
			// run the help method.
			help();
		}
		
		// this tells this specific program
		// how to run based on the argument passed
		// Client, Server, Auto, Help
		if (isClient == true) {
			client(portNumber);	
		} else if (isServer == true) {
			server(portNumber);
		} else if (isAuto == true) {
			server(portNumber);
		} else {
			help();
		}
	}
	
	public static void server(int portNumber) {
		
		System.out.println("Starting TalkServer");
		BufferedReader in=null;
		int serverPortNumber=portNumber;
		String message=null;
		Socket client=null;
		ServerSocket server=null;
		String serverName=host;	
		
		try {
			server= new ServerSocket(serverPortNumber);
			System.out.println("Server listening on port "+serverPortNumber);
		} catch (IOException e) {
			System.out.println("Could not listen on port "+serverPortNumber);
			if(isAuto == true) {
				client(serverPortNumber);
			} else {
				System.exit(-1);
			}
			
		}
		
		try {
			client=server.accept();
			System.out.println("Server accepted connection from "+client.getInetAddress());
		} catch (IOException e) {
			System.out.println("Accept failed on port "+ serverPortNumber);
			System.exit(-1);
		}
		
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			System.out.println("Couldn't get an inputStream from the client");
			System.exit(-1);
		}
		
		try{

	       	PrintWriter out = new PrintWriter(client.getOutputStream(), true);
	       	BufferedReader inputBuffer =new BufferedReader(new InputStreamReader(System.in));
	       	
			while(true){
				
				if (in.ready()) {
					
				message=in.readLine();
				System.out.println("[remote host] " + message);
				
				}
				
				if (inputBuffer.ready()){
					
					message = inputBuffer.readLine();
					
					if (message.contains("STATUS") ) {
						System.out.println("STATUS: Host = " + host + " Port Number = " + portNumber);
						continue;
					}
					
					out.println(message);
				}
			} 
		} catch (IOException e) {
			System.out.println("Read failed");
			System.exit(-1);
		}
	}
	
	public static void client(int portNumber) {
		
		System.out.println("Starting TalkClient");
		String serverName=host;	
		int serverPortNumber=portNumber;	
		String message=null;    
		
		try {       
			Socket socket = new Socket(serverName, serverPortNumber);
	       	BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
	       	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	       	BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    
	       	while(true) {
	       		
	       		if (in.ready()) {
	       			
	       			message = in.readLine();
	       			
					if (message.contains("STATUS") ) {
						System.out.println("STATUS: Host = " + host + " Port Number = " + portNumber);
						continue;
					}
					
	       			out.println(message);
	       		} 
	       		if (inputBuffer.ready()) {
	       			
	       			message = inputBuffer.readLine();	   
	       			System.out.println("[remote host] " + message);
	       			
	       		}
	       	}
	       	
	     } catch (UnknownHostException e) {
	       System.out.println("Unknown host:"+serverName);
	       System.exit(1);
	     } catch  (IOException e) {
	       System.out.println("No I/O");
	       System.exit(1);
	     }
	}
	
	public static void help() {
		
		System.out.println(
				"java Talk -a\n"
				+ "    --- runs in auto-mode\n"
				+ "    --- can specify a host name or IP\n"
				+ "    --- can also specify a specific port number\n"
				+ "java Talk -s\n"
				+ "    --- runs as server\n"
				+ "java Talk -h\n"
				+ "    --- runs as client\n");
		System.exit(1);
		
	}
}
