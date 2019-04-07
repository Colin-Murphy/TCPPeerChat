import java.lang.Thread.*;

public class PeerChat {
	//Holds the session that will eventually be created
	private Session s = null;
	//Chat UI
	private ChatUI ui = null;

	public PeerChat(String[] args) {
		//Expected arg length
		int argsNoPort = 3;
		int argsWithPort = 5;

		try {
			ui = new ChatUI();
			if (args.length != argsWithPort && args.length != argsNoPort) {
				System.err.println("Invalid Arguments: Exiting...");
				System.exit(1);
			}

			//Provided a port to join
			else if (args.length == argsWithPort) {
				s = new Session(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
				s.joinPort(Integer.parseInt(args[1]));
			}

			else {
				s = new Session(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));

			}

			ui.s = s;
			ui.start();
		}
		catch (Exception e) {
			System.err.println("Shit went wrong: Exiting...");
			System.exit(1);
		}

		
	}


	public static void main(String[] args) {
		new PeerChat(args);

	}
}