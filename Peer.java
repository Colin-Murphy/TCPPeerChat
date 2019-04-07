import java.net.*;
import java.io.*;

import org.json.JSONObject;
import org.json.JSONArray;

public class Peer extends Thread {
	private String name = null;
	private int age = 0;
	private int zip = 0;

	public Socket sock = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;

	private Session s = null;

	//Whether or not the connection to this peer was created by "me" or if they initiated it
	private boolean initiated;

	//Whether or not the peer needs to be queried for a list of peers
	private boolean discover;

	private boolean joined = false;

	public Peer(Socket sock, BufferedReader in, BufferedWriter out, Session s, boolean initiated, boolean discover) {
		this.sock = sock;
		this.in = in;
		this.out = out;
		this.s = s;
		this.initiated = initiated;
		this.discover = discover;
	}

	public void run() {
		try {

			//Peer hasn't fully joined
			if (!joined) {

				//This peer initiated the connection, so it must introduce itself
				if (initiated) {

					//Tell them my identity
					JSONObject message = new JSONObject();
					message.put("type", "join");
					message.put("name", s.name);
					message.put("age", s.age);
					message.put("zip", s.zip);

					deliver(message.toString());

					//Get their response
					JSONObject input = new JSONObject(in.readLine());

					try {
						setUserName(input.get("name").toString());
						setZip(Integer.parseInt(input.get("zip").toString()));
						setAge(Integer.parseInt(input.get("age").toString()));
						joined = true;
					}
					catch (Exception e) {
						e.printStackTrace();
					}

					//Send a who to this peer to learn the network
					if (discover) {
						message = new JSONObject();
						message.put("type", "who");
						deliver(message.toString());

						message = new JSONObject(in.readLine());
						JSONArray peers = message.getJSONArray("peers");

						for (int i = 0; i < peers.length(); i++) {
							s.joinPeer(peers.getString(i), false);
						}
						
					}

					System.out.println("[joined chat with " + (s.peers.size()+1) + " members]");


				}

				//Didn't start the connection, wait for them to introduce themselves
				else {
					JSONObject input = new JSONObject(in.readLine());

					try {
						setUserName(input.get("name").toString());
						setZip(Integer.parseInt(input.get("zip").toString()));
						setAge(Integer.parseInt(input.get("age").toString()));
						joined = true;
					}
					catch (Exception e) {
						e.printStackTrace();
					}

					//Tell them my identity
					JSONObject message = new JSONObject();
					message.put("type", "join-reply");
					message.put("name", s.name);
					message.put("age", s.age);
					message.put("zip", s.zip);

					deliver(message.toString());
					System.out.println("[member joined: " + name +"@" + getIP() +" " + zip + " " + age + "]");

					
				}

			}

			//Fully joined peer, read and handle their messages indefinitely
			while (joined) {
				JSONObject message = new JSONObject(in.readLine());

				String type = message.get("type").toString();

				switch (type) {
					case "message":
						System.out.println("<" + name + "> " + message.get("message").toString());
						break;
					case "leave":
						System.out.println("Leaving");
						break;
					case "who":
						System.out.println("Who");

						message = new JSONObject();
						message.put("type", "who-reply");
						message.put("peers", s.peersExcluding(this));

						deliver(message.toString());
						break;
					case "zip":
						System.out.println("Zip");
						break;
					case "age":
						System.out.println("Age");
						break;
					default:
						System.out.println(type);
				}

				
			}

			System.out.println("Peer run completing");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deliver(String message) {
		try {
			out.write(message);
			out.newLine();
			out.flush();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUserName(String name) throws IllegalArgumentException {
		if (s.legalName(name)) {
			this.name = name;
		}
		else {
			throw new IllegalArgumentException();
		}

	}

	public void setZip(int zip) throws IllegalArgumentException {
		if (s.legalZip(zip)) {
			this.zip = zip;
		}
		else {
			throw new IllegalArgumentException();
		}

	}

	public void setAge(int age) throws IllegalArgumentException {
		if (s.legalAge(age)) {
			this.age = age;
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	public String getIP() {
		String ip = sock.getRemoteSocketAddress().toString();
		ip = ip.substring(1,ip.indexOf(":"));
		return ip;
	}
}