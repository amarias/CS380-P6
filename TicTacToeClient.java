import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeClient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -357392367153463029L;

	public void printBoard(byte[][] board) {

		System.out.println("Board: \n  0 1 2");
		for (int j = 0; j < board.length; j++) {
			System.out.print(j + " ");
			for (int k = 0; k < board[j].length; k++) {
				if (board[j][k] == 1)
					System.out.print("X ");
				else if (board[j][k] == 2)
					System.out.print("O ");
				else
					System.out.print("- ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {

		TicTacToeClient game = new TicTacToeClient();

		try {
			Socket socket = new Socket("codebank.xyz", 38006);
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

			System.out.print("Please enter a username: ");
			Scanner input = new Scanner(System.in);
			ConnectMessage connectMsg = new ConnectMessage(input.nextLine());
			oos.writeObject(connectMsg);

			CommandMessage newGameMsg = new CommandMessage(CommandMessage.Command.NEW_GAME);
			oos.writeObject(newGameMsg);

			System.out.println("\nPlayer is X\nComputer is O\n");
			BoardMessage boardMsg = (BoardMessage) ois.readObject();
			game.printBoard(boardMsg.getBoard());

			while (boardMsg.getStatus() == BoardMessage.Status.IN_PROGRESS) {
				System.out.println("Choose the row: ");
				byte row = input.nextByte();
				System.out.println("Choose the column: ");
				byte col = input.nextByte();
				MoveMessage moveMsg = new MoveMessage(row, col);
				oos.writeObject(moveMsg);

				System.out.println();
				boardMsg = (BoardMessage) ois.readObject();
				game.printBoard(boardMsg.getBoard());
			}

			System.out.println("\nGame Over!");

			if (boardMsg.getStatus() == BoardMessage.Status.PLAYER1_VICTORY)
				System.out.print("You Win!");
			else if (boardMsg.getStatus() == BoardMessage.Status.PLAYER2_VICTORY)
				System.out.print("You Lose!");
			else if (boardMsg.getStatus() == BoardMessage.Status.STALEMATE)
				System.out.print("It's a stalemate!");

			oos.flush();
			oos.close();
			ois.close();
			input.close();
			socket.close();
		} catch (

		IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
