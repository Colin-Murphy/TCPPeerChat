import java.net.*;
import java.io.*;

public class Peer extends Thread {
	private String name;
	private int age;
	private int zip;

	private Socket sock = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;

	private Session s = null;

	public Peer(String name, int age, int zip, Socket sock, BufferedReader in, BufferedWriter out, Session s) {
		this.name = name;
		this.age = age;
		this.zip = zip;

		this.sock = sock;
		this.in = in;
		this.out = out;

		this.s = s;
	}

	public void run() {

		try {
			out.write("Hello There!\n");

			while (in.ready()) {
				System.out.println(in.readLine());
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}