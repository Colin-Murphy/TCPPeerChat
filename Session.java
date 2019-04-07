import java.util.ArrayList;
import java.lang.IllegalArgumentException;
import java.io.IOException;

import java.net.*;
import java.io.*;

import org.json.JSONObject;
import org.json.JSONArray;

public class Session extends Thread {
	//There is always a session, until you try to join someone it's a local session
	public boolean joined = false;

	public ArrayList<Peer> peers;

	public ChatUI ui = null;
	private int port = 8080;

	//Users information
	public String name;
	public int zip;
	public int age;

	private ServerSocket server = null;
	private Socket socket = null;

	//Restictions
	int name_max_length = 32;


	public Session(String name, int zip, int age) throws IllegalArgumentException {
		setUserName(name);
		setAge(age);
		setZip(zip);
		peers = new ArrayList<Peer>();

	}

	public void run() {

		//System.out.println("Runnnig");

		while (1>0) {
			if (server != null) {
				try {
					Socket sock = server.accept();
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
					BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

					Peer p = new Peer(sock, in, out, this, false, false);
					p.start();
					peers.add(p);

				}

				catch (Exception e) {
					//e.printStackTrace();
				}

			}

			
			else {
				System.out.print("");
			}
			
		}

	}

	public void showUsers() {
		System.out.println("[" + name + " " + age + " " + zip);
		for (Peer p: peers) {
			System.out.println("[" + p.name + " " + p.age + " " + p.zip +"]");
		}
	}

	public void leave() {
		JSONObject message = new JSONObject();
		message.put("type", "leave");
		for (Peer p:peers) {
			try {
				p.deliver(message.toString());
				p.in.close();
				p.out.close();
				p.sock.close();
				p.joined = false;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		peers = new ArrayList<Peer>();
		try {
			server.close();
			server = null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		joined = false;
	}

	public JSONArray peersExcluding(Peer exclude) {
		JSONArray peersResp = new JSONArray();

		for (Peer p:peers) {
			//Dont tell a peer about themself
			if (p != exclude) {
				peersResp.put(p.getIP());
			}

		}

		return peersResp;
	}

	/*
		Starts a server listening on the specified port.
		Sessions run method will accept incomming connections
	*/
	public void joinPort(int port) throws IOException {
		this.server = new ServerSocket(port);
		this.port = port;
		joined = true;
		try {
			this.start();
		}


		catch (Exception e) {

		}
	}

	/*
		Connect to a peer
		ip: the ip to connect to
		discover: Whether the peer needs to discover the network
	*/
	public void joinPeer(String ip, boolean discover) throws IOException {


		Socket sock = new Socket(ip, port);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

		Peer p = new Peer(sock, in, out, this, true, discover);
		p.start();

		peers.add(p);

	}

	public void joinPortAndIP(int port, String ip) throws IOException {
		joinPort(port);
		joinPeer(ip, true);
	}

	public void joinIP(String ip) throws IOException {
		joinPort(port);
		joinPeer(ip, true);
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

	/**
		Accepts raw keyboard input from user
		Escapes message and inserts into correct format
		Delivers to each peer
	*/
	public void sendMessage(String message) {
		//Format message

		//Format the message as json and escape the text
		JSONObject m = new JSONObject();
		m.put("type", "message");
		m.put("message", message);

		//Deliver to all peers
		for (Peer p: peers) {
			p.deliver(m.toString());
		}
	}

}