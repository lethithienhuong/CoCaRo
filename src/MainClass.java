import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainClass {

	public static void main(String[] args) {
		// Tao MainGUI
				final int width = 760;
				final MainGUI gui = new MainGUI(width,width, "CARO");
				
				// Tao bang 19x19
				Board board = new Board(width, 19);
				
				// Tao bang game
				final Game game = new Game(board);
				
				// dua cac thanh phan bang GUI vao bang game
				gui.attachBoard(board.getGUI());
				
				// Make the frame wrap the contents and set it visible.
				gui.pack();
				gui.setVisible(true);
				
				// Bat dau game
				gui.listenGameStartButton(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						
						// them cac cai dat cho bang game
						Object[] settings = gui.fetchSettings();
						int depth = (Integer)(settings[0]);
						boolean computerStarts = (Boolean)(settings[1]);
						
						System.out.println("Depth: " + depth + " AI di truoc: " + computerStarts );
						
						// hien thi bang game
						gui.showBoard();
						
						// cai dat
						game.setAIDepth(depth);
						game.setAIStarts(computerStarts);
						
						// bat dau game
						game.start();
					}
					
				});	
	}

}
