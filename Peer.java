import java.net.*;
import java.io.*;

public class Peer extends Thread {
	private String name = null;
	private int age = 0;
	private int zip = 0;

	private Socket sock = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;

	private Session s = null;

	//Whether or not the connection to this peer was created by "me" or if they initiated it
	private boolean initiated;

	//Whether or not the peer needs to be queried for a list of peers
	private boolean discover;

	public Peer(Socket sock, BufferedReader in, BufferedWriter out, Session s, boolean initiated, boolean discover) {
		this.sock = sock;
		this.in = in;
		this.out = out;
		this.s = s;
		this.initiated = initiated;
		this.discover = discover;
	}

	public void run() {
		System.out.println("Peer now running");

		try {

			//Peer hasn't fully joined
			if (name == null) {

				String message = "Hello There!";

				//This peer initiated the connection, so it must introduce itself
				if (initiated) {
					deliver(message);
					String input = in.readLine();
					System.out.println(input);
					name = "test";
				}

				//Didn't start the connection, wait for them to introduce themselves
				else {
					String input = in.readLine();
					System.out.println(input);
					if (input.equals(message)) {
						System.out.println("Correct");
						deliver("ohai");
						name = "Test";
					}

					else {
						System.out.println("WRONG");
						System.out.println(input);
					}
				}

			}
			/*
			System.out.println("Sending message");
			out.write("Hello There!\n");
			//out.newLine();
			out.flush();
			System.out.println("Message Sent");
			*/

			boolean exit = false;

			while (!exit) {
				System.out.println("About to read");
				System.out.println(in.readLine());
				System.out.println("Read Complete");
				
			}

			System.out.println("Peer run completing");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deliver(String message) {
		try {
			System.out.println("Delivering");
			out.write(message);
			out.newLine();
			out.flush();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}