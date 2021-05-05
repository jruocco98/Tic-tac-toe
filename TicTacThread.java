import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TicTacThread implements Runnable {
	
	private int player;
	private Socket s;
	private TicTacGame game;
	
	private PrintWriter out;
	private Scanner in;
	private boolean stop;
	
	public TicTacThread(int player, Socket s, TicTacGame game) {
		this.player = player;
		this.s = s;
		this.game = game;
		stop = false;
	}
	
	@Override
	public void run() {
		
		String msg;
		
		try {
			in = new Scanner(s.getInputStream());
			out  = new PrintWriter(s.getOutputStream());
			
			msg = in.nextLine();
			
			if(msg.equals("Hello")) {
				out.println("Hello you are player " + player + ".");
				out.flush();
			}
			
			while(stop != true) {
				stop = getMove();				
			}
			game.closeGame();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		finally {
			
			in.close();
			out.close();
		}

	}

	private boolean getMove() {
		String msg = "";
		if(in.hasNextLine()) {
			msg = in.nextLine();		
		}
		else {
			return true;
		}
		
		String errorType;
		String[] tokens = msg.split(" ");
		int playerNum;
		int row;
		int col;
		
		try {
			
			errorType = "Illegal player number";
			playerNum = Integer.parseInt(tokens[1]);
			
			errorType = "Illegal Board Position";
			row = Integer.parseInt(tokens[2]);
			col = Integer.parseInt(tokens[3]);
		}
		catch (NumberFormatException e)
		{
			//IllegalMove
			//send errortype
			playerNum = 0;
			row = 0;
		    col = 0;		   
		}
		
		String result = game.makeMove(playerNum, row, col);	
		
		if(result.equals("Game Over") || result.equals("Draw")) {
			return true;
		}
		
		while(result.matches("Position [0-2] [0-2] is taken, try again")) {
			msg = in.nextLine();
			tokens = msg.split(" ");
			
			try {
				
				errorType = "Illegal player number";
				playerNum = Integer.parseInt(tokens[1]);
				
				errorType = "Illegal Board Position";
				row = Integer.parseInt(tokens[2]);
				col = Integer.parseInt(tokens[3]);
			}
			catch (NumberFormatException e)
			{
				//IllegalMove
				//send errortype
				playerNum = 0;
				row = 0;
			    col = 0;		   
			}
			result = game.makeMove(playerNum, row, col);
		}
		return false;
	}
	
}
