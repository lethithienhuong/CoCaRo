import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Game {
	private Board board;
	private boolean isPlayersTurn = true;
	private boolean gameFinished = false;
	private int minimaxDepth = 2;
	private boolean aiStarts = true; // AI choi truoc
	private Minimax ai;
	public static final String cacheFile = "score_cache.ser";
	private int winner; // 0: chua co ai thang, 1: AI Wins, 2: Human Wins
	
	
	public Game(Board board) {
		this.board = board;
		ai = new Minimax(board);
		winner = 0;
	}
	//bat dau game va tuong tac voi nguoi choi
	public void start() {
		// Neu AI choi truoc thi quan trang dau tien nam giua ban co
		if(aiStarts) playMove(board.getBoardSize()/2, board.getBoardSize()/2, false);
		// Sau do den luot nguoi choi
		
		// Tai lai ban co sau khi co kich chuot
		board.startListening(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				if(isPlayersTurn) {
					isPlayersTurn = false;
					// Xu ly kich chuot
					Thread mouseClickThread = new Thread(new MouseClickHandler(arg0));
					mouseClickThread.start();
				}
			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	/*
	 * 	Lay do sau cua cay minimax
	 */
	public void setAIDepth(int depth) {
		this.minimaxDepth = depth;
		
	}
	public void setAIStarts(boolean aiStarts) {
		this.aiStarts = aiStarts;
	}
	public class MouseClickHandler implements Runnable{
		MouseEvent e;
		public MouseClickHandler(MouseEvent e) {
			this.e = e;
		}
		public void run() {
			if(gameFinished) return;
			
			// Tim quan danh thuoc hang va cot nao
			 
			int posX = board.getRelativePos( e.getX() );
			int posY = board.getRelativePos( e.getY() );
			
			// Bo quan den vao hang do
			if(!playMove(posX, posY, true)) {
				// chuyen nguoi choi
				isPlayersTurn = true;
				return;
			}
			
			// Kiem tra neu la buoc cuoi thi dung
			winner = checkWinner();
			
			if(winner == 2) {
				System.out.println("Player WON!");
				board.printWinner(winner);
				gameFinished = true;
				return;
			}
			
			// tinh toan buoc di cho AI
			int[] aiMove = ai.calculateNextMove(minimaxDepth);
			
			if(aiMove == null) {
				System.out.println("No possible moves left. Game Over.");
				board.printWinner(0); // Prints "TIED!"
				gameFinished = true;
				return;
			}
			
			
			// Dua quan den vao hang da duoc danh
			playMove(aiMove[1], aiMove[0], false);
			
			System.out.println("Black: " + Minimax.getScore(board,true,true) + " White: " + Minimax.getScore(board,false,true));
			
			winner = checkWinner();
			
			if(winner == 1) {
				System.out.println("AI WON!");
				board.printWinner(winner);
				gameFinished = true;
				return;
			}
			
			if(board.generateMoves().size() == 0) {
				System.out.println("No possible moves left. Game Over.");
				board.printWinner(0); // Prints "TIED!"
				gameFinished = true;
				return;
				
			}
			
			isPlayersTurn = true;
		}
		
	}
	private int checkWinner() {
		if(Minimax.getScore(board, true, false) >= Minimax.getWinScore()) return 2;
		if(Minimax.getScore(board, false, true) >= Minimax.getWinScore()) return 1;
		return 0;
	}
	private boolean playMove(int posX, int posY, boolean black) {
		return board.addStone(posX, posY, black);
	}

}
