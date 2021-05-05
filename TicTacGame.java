import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicTacGame {
	
	private int[][] board;
	private Lock lock;
	private final int ROW = 3;
	private final int COL = 3;
	
	private PrintWriter outP1;
	private PrintWriter outP2;
	private int playerTurn;
	private Condition notTurn;
	private boolean gameOver;
	
	public TicTacGame(Socket player1, Socket player2) {
		
		board = new int[ROW][COL];
		lock = new ReentrantLock();
		notTurn = lock.newCondition();
		gameOver = false;
		playerTurn = 1;
		for(int i = 0; i < ROW; i++) {
			for(int j = 0; j < COL; j++) {
				board[i][j] = -1;
			}
		}
		
		try {
			outP1 = new PrintWriter(player1.getOutputStream());
			outP2 = new PrintWriter(player2.getOutputStream());
			
		} catch (IOException e) {

			e.printStackTrace();
		}
			
	}
	
	public String makeMove(int player, int row, int col) {
					
		lock.lock();
		if(gameOver == true) {
			return "Game Over";
		}
		//if it is not this threads turn release lock and wait for signal
		if(playerTurn != player) {	

			try {
				notTurn.await();
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}	
		} 

		//if move is illegal tell thread to ask for another move
		if(!checkLegal(row, col)){
			String msg = "";
			
			if(player == 1) {
				outP1.println("Position " + row + " " + col + " is taken, try again");
				outP1.flush();
			}
			if(player == 2) {
				outP2.println("Position " + row + " " + col + " is taken, try again");
				outP2.flush();
			}
			return "Position " + row + " " + col + " is taken, try again";
		}


		//move is legal, notify both clients
		board[row][col] = player;	

		outP1.println("Player " + player + " has chosen " + row + " " + col);
		outP1.flush();
		outP2.println("Player " + player + " has chosen " + row + " " + col);
		outP2.flush();
		
		System.out.println("Player " + player + " has chosen " + row + " " + col);
		
		if(checkWinner(player)) {
			
			outP1.println("Player " + player + " won!!!");
			outP1.flush();
			outP2.println("Player " + player + " won!!!");
			outP2.flush();
			gameOver = true;
			return "Game Over";
		}
		if(checkDraw()) {
			outP1.println("The game is a draw.");
			outP1.flush();
			outP2.println("The game is a draw.");
			outP2.flush();
			gameOver = true;
			return "Game Over";
		}
		
		//switch turn once move is complete
		if(player == 1) 
			playerTurn = 2;		
		else
			playerTurn = 1;
		
		//signal other thread its their turn and unlock
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		

		
		notTurn.signalAll();
		lock.unlock();
		
		return "Player " + player + " has chosen " + row + " " + col;
	}
	
	public boolean checkLegal(int row, int col) {
		
		if(board[row][col] == -1)
			return true;
		else
			return false;
	}
	
	private boolean checkWinner(int player) {
		
		//check rows
		for(int i = 0; i < ROW; i++) {
			if(board[i][0] != -1 && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
				return true;
			}
		}
		//check cols
		for(int j = 0; j < COL; j++) {
			
			if(board[0][j] != -1 && board[0][j] == board[1][j] && board[0][j] == board[2][j]) {
				return true;
			}
		}
		//check diagonal
		if(board[0][0] != -1 && board[0][0] == board[1][1] &&  board[0][0] == board[2][2]) {
			return true;
		}
		if(board[2][0] != -1 && board[2][0] == board[1][1] && board[2][0] == board[0][2]) {
			return true;
		}
		
		return false;
	}
	private boolean checkDraw() {
		
		for(int i = 0; i < ROW; i++) {
			for(int j = 0; j < COL; j++) {
				if (board[i][j] == -1) 
					return false;
				
			}
		}
		return true;
	}
	public void closeGame() {

		
		outP1.close();
		outP2.close();		
	}
}
