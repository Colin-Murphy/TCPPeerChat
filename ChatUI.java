import java.util.Scanner;

public class ChatUI extends Thread {

	public Session s = null;
	

	public void run() {
		Scanner sc = new Scanner(System.in);
		while (sc.hasNext()) {
			String in = sc.nextLine();
			if (in.length() > 0) {
				//Check the first character to see if the user might have typed a command
				String fc = in.substring(0,1);

				//Whether or not the user typed a command
				boolean command = false;
				//Text may be a command
				if (fc.equals("/")) {
					String[] words = in.split(" ");

					try {
						//Find out which command it is and run it
						switch(words[0]) {
							case "/join":
								if (!s.joined) {
									try {
										// /join [-p port] ip
										if (words.length >= 4) {
											int port = Integer.parseInt(words[2]);
											String ip = words[3];
											s.joinPortAndIP(port, ip);
											command = true;
										}
										// /join ip
										else if (words.length == 2) {
											s.joinIP(words[1]);
											command = true;
										}
									}
									catch (Exception e) {
										System.out.println("[join failure - 2");
										//Stop listening on the port in case it was the ip that failed
										s.leave();
									}
								}
								else {
									System.out.println("[join failure - 1");
									command = true;
								}
								break;
							case "/leave":
								s.leave();
								//New copy of the session that hasn't been started yet
								command = true;
								break;
							case "/who":
								command = true;
								s.showUsers();
								break;
							case "/zip":
								System.out.println("Zip");
								break;
							case "/age":
								System.out.println("Age");
								break;
							case "/exit":
								s.leave();
								System.exit(0);
								System.out.println("Exit");
								break;
						}
					}
					catch (Exception e) {
						System.out.println("error");
						e.printStackTrace();
					}
				}

				if (!command) {
					s.sendMessage(in);
				}
			}
		}
	}

	public void write(String message, Peer peer) {
		System.out.println(message);
	}

	
}