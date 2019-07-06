import java.util.ArrayList;


public class Minimax {
	
	public static int evaluationCount = 0;//buoc di tinh duoc
	private Board board;
	private static final int winScore = 100000000;//diem thang
	
	
	public Minimax(Board board) {
		this.board = board;
	}
	
	public static int getWinScore() {
		return winScore;
	}
	//tinh diem AI gap bn lan nguoi choi
	public static double evaluateBoardForWhite(Board board, boolean blacksTurn) {
		evaluationCount++;
		
		
		double blackScore = getScore(board, true, blacksTurn);
		double whiteScore = getScore(board, false, blacksTurn);
		
		if(blackScore == 0) blackScore = 1.0;
		
		return whiteScore / blackScore;
		
	}
	//ham lay diem
	public static int getScore(Board board, boolean forBlack, boolean blacksTurn) {
		
		
		int[][] boardMatrix = board.getBoardMatrix();
		return evaluateHorizontal(boardMatrix, forBlack, blacksTurn) +
				evaluateVertical(boardMatrix, forBlack, blacksTurn) +
				evaluateDiagonal(boardMatrix, forBlack, blacksTurn);
	}
	//tinh toan buoc di AI
	public int[] calculateNextMove(int depth) {
		board.thinkingStarted();
		int[] move = new int[2];
		long startTime = System.currentTimeMillis();
		// buoc di ket thuc game
		Object[] bestMove = searchWinningMove(board); 
		if(bestMove != null ) {
			System.out.println("Finisher!");
			move[0] = (Integer)(bestMove[1]);
			move[1] = (Integer)(bestMove[2]);
			
		} else {
			// buoc di tot nhat tai thoi diem do
			bestMove = minimaxSearchAB(depth, board, true, -1.0, getWinScore());
			if(bestMove[1] == null) {
				move = null;
			} else {
				move[0] = (Integer)(bestMove[1]);
				move[1] = (Integer)(bestMove[2]);
			}
		}
		System.out.println("Cases calculated: " + evaluationCount + " Calculation time: " + (System.currentTimeMillis() - startTime) + " ms");
		board.thinkingFinished();
		
		evaluationCount=0;
		
		return move;
		
		
	}
	
	
	/*
	 * alpha : Best AI Move (Max)
	 * beta : Best Player Move (Min)
	 * returns: {score, move[0], move[1]}
	 * */
	private static Object[] minimaxSearchAB(int depth, Board board, boolean max, double alpha, double beta) {
		if(depth == 0) {
			
			Object[] x = {evaluateBoardForWhite(board, !max), null, null};
			return x;
		}
		
		ArrayList<int[]> allPossibleMoves = board.generateMoves();
		
		if(allPossibleMoves.size() == 0) {
			
			Object[] x = {evaluateBoardForWhite(board, !max), null, null};
			return x;
		}
		
		Object[] bestMove = new Object[3];
		
		
		if(max) {
			bestMove[0] = -1.0;
			// tat ca cac vi tri danh duoc
			for(int[] move : allPossibleMoves) {
				// tao ban co an
				Board dummyBoard = new Board(board);
				// danh thu tren ban co an
				dummyBoard.addStoneNoGUI(move[1], move[0], false);
				
				// goi ham minimax cho buoc tiep theo de tim diem thap nhat cua nguoi choi
				Object[] tempMove = minimaxSearchAB(depth-1, dummyBoard, !max, alpha, beta);
				
				// cap nhat alpha max
				if((Double)(tempMove[0]) > alpha) {
					alpha = (Double)(tempMove[0]);
				}
				// cat tia beta min
				if((Double)(tempMove[0]) >= beta) {
					return tempMove;
				}
				if((Double)tempMove[0] > (Double)bestMove[0]) {
					bestMove = tempMove;
					bestMove[1] = move[0];
					bestMove[2] = move[1];
				}
			}
			
		}
		//neu chua dat duoc diem max tai vi tri hien tai
		else {
			bestMove[0] = 100000000.0;
			bestMove[1] = allPossibleMoves.get(0)[0];
			bestMove[2] = allPossibleMoves.get(0)[1];
			for(int[] move : allPossibleMoves) {
				Board dummyBoard = new Board(board);
				dummyBoard.addStoneNoGUI(move[1], move[0], true);
				
				Object[] tempMove = minimaxSearchAB(depth-1, dummyBoard, !max, alpha, beta);
				
				// cap nhat alpha max
				if(((Double)tempMove[0]) < beta) {
					beta = (Double)(tempMove[0]);
				}
				// cat tia beta min
				if((Double)(tempMove[0]) <= alpha) {
					return tempMove;
				}
				if((Double)tempMove[0] < (Double)bestMove[0]) {
					bestMove = tempMove;
					bestMove[1] = move[0];
					bestMove[2] = move[1];
				}
			}
		}
		return bestMove;
	}
	
	private static Object[] searchWinningMove(Board board) {
		ArrayList<int[]> allPossibleMoves = board.generateMoves();
		Object[] winningMove = new Object[3];
		
		// nhung buoc di duoc
		for(int[] move : allPossibleMoves) {
			evaluationCount++;
			// ban co an
			Board dummyBoard = new Board(board);
			// danh thu tren ban co an
			dummyBoard.addStoneNoGUI(move[1], move[0], false);
			
			// quan AI thang khi danh thu
			if(getScore(dummyBoard,false,false) >= winScore) {
				winningMove[1] = move[0];
				winningMove[2] = move[1];
				return winningMove;
			}
		}
		return null;
		
	}
	//hang ngang
	public static int evaluateHorizontal(int[][] boardMatrix, boolean forBlack, boolean playersTurn ) {
		
		int consecutive = 0;
		int blocks = 2;
		int score = 0;
		
		for(int i=0; i<boardMatrix.length; i++) {
			for(int j=0; j<boardMatrix[0].length; j++) {
				if(boardMatrix[i][j] == (forBlack ? 2 : 1)) {
					consecutive++;
				}
				else if(boardMatrix[i][j] == 0) {
					if(consecutive > 0) {
						blocks--;
						score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
						consecutive = 0;
						blocks = 1;
					}
					else {
						blocks = 1;
					}
				}
				else if(consecutive > 0) {
					score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
					consecutive = 0;
					blocks = 2;
				}
				else {
					blocks = 2;
				}
			}
			if(consecutive > 0) {
				score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
				
			}
			consecutive = 0;
			blocks = 2;
			
		}
		return score;
	}
	//hang doc
	public static  int evaluateVertical(int[][] boardMatrix, boolean forBlack, boolean playersTurn ) {
		
		int consecutive = 0;
		int blocks = 2;
		int score = 0;
		
		for(int j=0; j<boardMatrix[0].length; j++) {
			for(int i=0; i<boardMatrix.length; i++) {
				if(boardMatrix[i][j] == (forBlack ? 2 : 1)) {
					consecutive++;
				}
				else if(boardMatrix[i][j] == 0) {
					if(consecutive > 0) {
						blocks--;
						score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
						consecutive = 0;
						blocks = 1;
					}
					else {
						blocks = 1;
					}
				}
				else if(consecutive > 0) {
					score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
					consecutive = 0;
					blocks = 2;
				}
				else {
					blocks = 2;
				}
			}
			if(consecutive > 0) {
				score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
				
			}
			consecutive = 0;
			blocks = 2;
			
		}
		return score;
	}
	//hang cheo
	public static  int evaluateDiagonal(int[][] boardMatrix, boolean forBlack, boolean playersTurn ) {
		
		int consecutive = 0;
		int blocks = 2;
		int score = 0;
		// cheo len
		for (int k = 0; k <= 2 * (boardMatrix.length - 1); k++) {
		    int iStart = Math.max(0, k - boardMatrix.length + 1);
		    int iEnd = Math.min(boardMatrix.length - 1, k);
		    for (int i = iStart; i <= iEnd; ++i) {
		        int j = k - i;
		        
		        if(boardMatrix[i][j] == (forBlack ? 2 : 1)) {
					consecutive++;
				}
				else if(boardMatrix[i][j] == 0) {
					if(consecutive > 0) {
						blocks--;
						score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
						consecutive = 0;
						blocks = 1;
					}
					else {
						blocks = 1;
					}
				}
				else if(consecutive > 0) {
					score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
					consecutive = 0;
					blocks = 2;
				}
				else {
					blocks = 2;
				}
		        
		    }
		    if(consecutive > 0) {
				score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
				
			}
			consecutive = 0;
			blocks = 2;
		}
		// cheo xuong
		for (int k = 1-boardMatrix.length; k < boardMatrix.length; k++) {
		    int iStart = Math.max(0, k);
		    int iEnd = Math.min(boardMatrix.length + k - 1, boardMatrix.length-1);
		    for (int i = iStart; i <= iEnd; ++i) {
		        int j = i - k;
		        
		        if(boardMatrix[i][j] == (forBlack ? 2 : 1)) {
					consecutive++;
				}
				else if(boardMatrix[i][j] == 0) {
					if(consecutive > 0) {
						blocks--;
						score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
						consecutive = 0;
						blocks = 1;
					}
					else {
						blocks = 1;
					}
				}
				else if(consecutive > 0) {
					score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
					consecutive = 0;
					blocks = 2;
				}
				else {
					blocks = 2;
				}
		        
		    }
		    if(consecutive > 0) {
				score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
				
			}
			consecutive = 0;
			blocks = 2;
		}
		return score;
	}
	public static  int getConsecutiveSetScore(int count, int blocks, boolean currentTurn) {
		final int winGuarantee = 1000000;
		if(blocks == 2 && count < 5) return 0;
		switch(count) {
		case 5: {
			return winScore;
		}
		case 4: {
			if(currentTurn) return winGuarantee;
			else {
				if(blocks == 0) return winGuarantee/4;
				else return 200;
			}
		}
		case 3: {
			if(blocks == 0) {
				if(currentTurn) return 50000;
				else return 200;
			}
			else {
				if(currentTurn) return 10;
				else return 5;
			}
		}
		case 2: {
			if(blocks == 0) {
				if(currentTurn) return 7;
				else return 5;
			}
			else {
				return 3;
			}
		}
		case 1: {
			return 1;
		}
		}
		return winScore*2;
	}
}