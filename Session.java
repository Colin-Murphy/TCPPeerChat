import java.util.ArrayList;
import java.lang.IllegalArgumentException;
import java.io.IOException;

import java.net.*;
import java.io.*;

public class Session extends Thread {
	//There is always a session, until you try to join someone it's a local session
	public boolean joined = false;

	private ArrayList<Peer> peers = new ArrayList<Peer>();

	public ChatUI ui = null;
	private int port = 8080;

	//Users information
	private String name;
	private int zip;
	private int age;

	private ServerSocket server = null;
	private Socket socket = null;

	//Restictions
	int name_max_length = 32;


	public Session(String name, int zip, int age) throws IllegalArgumentException {
		System.out.println("Hello");
		System.out.printf("New Session with name:%s age %d zip %d\n", name, age, zip);
		setUserName(name);
		setAge(age);
		//setZip(zip);

	}

	public void run() {

		while (1>0) {
			try {
				Socket sock = server.accept();
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			
				String message = in.readLine();
				System.out.println(message);
				System.exit(0);
			}

			catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/*
		Starts a server listening on the specified port.
		Sessions run method will accept incomming connections
	*/
	public void joinPort(int port) throws IOException {
		System.out.println("Joining Port: " + port);
		server = new ServerSocket(port);
		this.port = port;
		joined = true;
		//this.start();
	}

	/*
		Connect to a peer
	*/
	public void joinPeer(String ip) {

		try {
			Socket sock = new Socket(ip, port);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			Peer p = new Peer("Test", 100, 99999, sock, in, out, this);
			p.start();

			peers.add(p);
			System.out.println("Joining peer: " + ip);
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void joinPortAndIP(int port, String ip) throws IOException {
		joinPort(port);
		joinPeer(ip);
	}

	public void joinIP(String ip) throws IOException {
		joinPort(port);
		joinPeer(ip);
	}

	public void setUserName(String name) throws IllegalArgumentException {
		if (legalName(name)) {
			this.name = name;
		}
		else {
			throw new IllegalArgumentException();
		}

	}

	public boolean legalName(String name) {
		return name.length() <= name_max_length;
	}

	public void setAge(int age) throws IllegalArgumentException {
		if (legalAge(age)) {
			this.age = age;
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	public boolean legalAge(int age) {
		return age > 0 && age < 200;
	}



	public void setZip(int zip) {
		if (legalZip(zip)) {
			this.zip = zip;
		}
		else {
			throw new IllegalArgumentException();
		}

	}

	public boolean legalZip(int zip) {
		return zip > 0 && zip <100000;
	}

	public void sendMessage(String message) {
		System.out.println("TODO");
	}

}