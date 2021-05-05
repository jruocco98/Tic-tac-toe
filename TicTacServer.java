import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TicTacServer {

	private final static int PORT = 8888;

	public static void main(String[] args) throws IOException {
		
		ServerSocket s = new ServerSocket(PORT);
		
		System.out.println("Waiting for players...");
		
		
		final int MAX_PLAYERS = 2;
		int currentPlayers = 0;
		Socket[] clientSocket = new Socket[MAX_PLAYERS];
		
		

		
		while(currentPlayers < MAX_PLAYERS) {
			
			clientSocket[currentPlayers] = s.accept();
			currentPlayers++;
			System.out.println(currentPlayers + " player(s) Connected.");
		}
		System.out.println("Starting Game...");
		
		TicTacGame newGame = new TicTacGame(clientSocket[0], clientSocket[1]);
		TicTacThread r1;
		TicTacThread r2;
		Thread t1;
		Thread t2;
		
		try {
			r1 = new TicTacThread(1, clientSocket[0], newGame);
			r2 = new TicTacThread(2, clientSocket[1], newGame);
			t1 = new Thread(r1);
			t2 = new Thread(r2);
			t1.start();
			t2.start();
			t1.join();
			t2.join();
		}catch(InterruptedException e) {
			
		}
	}
}
