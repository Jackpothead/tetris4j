package code;

import static code.ProjectConstants.sleep_;
import static code.ProjectConstants.GameState;
import java.util.*;

/*This is the default tetris playing AI. It holds a reference to
 * the tetris engines so it can send key events when necessary
 * and it knows the current block*/
public class TetrisAI
{
	private TetrisPanel panel;
	private TetrisEngine engine;
	AIThread thread;
	
	/*Is the AI running?*/
	public volatile boolean isrunning = false;
	
	/*Time (ms) AI has to wait per keypress.*/
	public static final int waittime = 1;
	
	public TetrisAI(TetrisPanel panel){
		this.panel = panel;
		engine = panel.engine;
	}
	
	public void send_ready(){
		if(isrunning)
			return;
		thread = new AIThread();
		thread.start();
		isrunning = true;
	}
	
	class AIThread extends Thread{
		public void run(){
			
			while(engine.state != GameState.GAMEOVER){
				//If it's merely paused, do nothing; if it's actually game over
				//then break loop entirely.
				if(engine.state == GameState.PLAYING){
					if(engine.activeblock == null) continue;
					
					BlockPosition temp = computeBestFit(engine);
					int elx = temp.bx;
					int erot = temp.rot;
					
					//Move it!
					movehere(elx, erot);
				}
				//safety
				sleep_(waittime);
			}
			
			isrunning = false;
		}
		
		/*Keypresses to move block to calculated position.*/
		private void movehere(int finx, int finrot){
			int st_blocksdropped = engine.blocksdropped;
			
			while(engine.activeblock.rot != finrot){
				//Rotate first so we don't get stuck in the edges.
				engine.keyrotate();
				
				//Now wait.
				sleep_(waittime);
			}
			
			while(engine.activeblock.x != finx){
				//Now nudge the block.
				if(engine.activeblock.x < finx){
					engine.keyright();
				}
				else if(engine.activeblock.x > finx){
					engine.keyleft();
				}
				
				sleep_(waittime);
			}
			
			while(engine.blocksdropped == st_blocksdropped
					&& engine.state != GameState.GAMEOVER){
				//Now move it down until it drops a new block.
				engine.keydown();
				sleep_(waittime);
			}
		}
	}

	// =============== Here be the AI code. ===============
	
	/*This can calculate the best possible fit for it, given the current
	 * state the blocks are in.*/
	static BlockPosition computeBestFit(TetrisEngine ge){

		byte[][][] allrotations = TetrisEngine.blockdef[ge.activeblock.type];
		int nrots = allrotations.length;

		// List of all the possible fits.
		List<BlockPosition> posfits = new ArrayList<BlockPosition>();

		// Loop through the rotations.
		// Here we generate all of the unique valid fits, and evaluate
		// them later.
		for(int i=0; i<nrots; i++){
			byte[][] trotation = allrotations[i];
			int free = freeSpaces(trotation);
			int freeL = free / 10;
			int freeR = free % 10;
			int minX = 0 - freeL;
			int maxX = 8 + freeR;
			// now loop through each position for a rotation.
			for(int j=minX; j<=maxX; j++){
				BlockPosition put = new BlockPosition();
				put.bx = j;
				put.rot = i;
				posfits.add(put);
			}
		}

		// now we begin the evaluation.
		// for each element in the list we have, calculate a score, and pick
		// the best.
		double[] scores = new double[posfits.size()];

		for(int i=0; i<scores.length; i++){
			scores[i] = evalPosition(ge, posfits.get(i));
		}

		//retrieve max.
		double max = Double.NEGATIVE_INFINITY;
		BlockPosition max_b = null;
		for(int i=0; i<scores.length; i++){
			if(scores[i] >= max){
				max_b = posfits.get(i);
				max = scores[i];
			}
		}

		// Return final position.
		return max_b;
	}

	static double evalPosition(TetrisEngine ge, BlockPosition p){

		byte[][] bl = ge.blockdef[ge.activeblock.type][p.rot];

		// First thing: Simulate the drop. Do this on a mock grid.
		// copying it here may seem like a waste but clearing it
		// after each iteration is too much of a hassle.

		// This copies the grid.
		byte[][] mockgrid = new byte[ge.width][ge.height];
		for(int i=0; i<ge.width; i++)
			for(int j=0; j<ge.height; j++){
				byte s = (byte) ge.blocks[i][j].getState();
				if(s==2) s=0;
				mockgrid[i][j] = s;
			}


		// Now we find the fitting height by starting from the bottom and
		// working upwards. If we're fitting a line-block on an empty
		// grid then the height would be height-1, and it can't be any
		// lower than that, so that's where we'll start.

		int h;
		for(h = ge.height-1;; h--){

			// indicator. 1: fits. 0: doesn't fit. -1: game over.
			int fit_state = 1;

			for(int i=0; i<4; i++)
				for(int j=0; j<4; j++){
					//check for bounds.


					boolean block_p = bl[j][i] == 1;

					//we have to simulate lazy evaluation in order to avoid
					//out of bounds errors.

					if(block_p){
						//still have to check for overflow. X-overflow can't
						//happen at this stage but Y-overflow can.

						if(h+j >= ge.height)
							fit_state = 0;

						else if(h+j < 0)
							fit_state = -1;

						else{
							boolean board_p = mockgrid[i+p.bx][h+j] == 1;

							// Already filled, doesn't fit.
							if(board_p)
								fit_state = 0;
						}
					}
				}

			//We don't want game over so here:
			if(fit_state==-1)
				return -99999999;
			
			//1 = found!
			if(fit_state==1)
				break;

		}

		for(int i=0; i<4; i++)
			for(int j=0; j<4; j++)
				if(bl[j][i]==1)
					mockgrid[p.bx+i][h+j] = 2;


		// Constants for score evaluation.
		final double _TOUCHING_EDGES = 1.2;
		final double _TOUCHING_WALLS = 0.5;
		final double _TOUCHING_FLOOR = 3.0;
		final double _HEIGHT = -0.2;
		final double _HOLES = -4.0;


		// Now we evaluate the resulting position.

		// Part of the evaluation algorithm is to count the number of touching sides.
		// We do this by generating all pairs and seeing how many them are touching.
		// If they add up to 3, it means one of them is from the active block and the
		// other is a normal block (ie. they're touching).

		double score = 0.0;

		//horizontal pairs
		for(int i=0; i<ge.height; i++)
			for(int j=0; j<ge.width-1; j++){
				if(j==0 && mockgrid[j][i]==2) score += _TOUCHING_WALLS;
				if(j+1==ge.width-1 && mockgrid[j+1][i]==2) score += _TOUCHING_WALLS;
				if(mockgrid[j][i] + mockgrid[j+1][i] == 3) score += _TOUCHING_EDGES;
			}

		//vertical pairs
		for(int i=0; i<ge.width; i++)
			for(int j=0; j<ge.height-1; j++){
				if(j+1==ge.height-1 && mockgrid[i][j+1]==2) score += _TOUCHING_FLOOR;
				if(mockgrid[i][j] + mockgrid[i][j+1] == 3) score += _TOUCHING_EDGES;
			}

		// Penalize height.
		for(int i=0; i<ge.width; i++)
			for(int j=0; j<ge.height; j++){
				int curheight = ge.height - j;
				if(mockgrid[i][j]>0) score += curheight * _HEIGHT;
			}

		//Penalize holes.
		for(int i=0; i<ge.width; i++) {
			boolean f = false;
			for(int j=0; j<ge.height; j++){
				if(mockgrid[i][j]>0) f = true;
				if(f && mockgrid[i][j]==0) score += _HOLES;
			}
		}

		return score;
	}


	// Takes a int array and calculates how many blocks of free spaces are there
	// on the left and right. The return value is a 2 digit integer.
	static int freeSpaces(byte[][] in){

		// It's free if all of them are zero, and their sum is zero.
		boolean c1free = in[0][0] + in[1][0] + in[2][0] + in[3][0] == 0;
		boolean c2free = in[0][1] + in[1][1] + in[2][1] + in[3][1] == 0;
		boolean c3free = in[0][2] + in[1][2] + in[2][2] + in[3][2] == 0;
		boolean c4free = in[0][3] + in[1][3] + in[2][3] + in[3][3] == 0;

		int lfree = 0;
		// Meh, I'm too lazy to code a loop for this.
		if(c1free){
			lfree++;
			if(c2free){
				lfree++;
				if(c3free){
					lfree++;
					if(c4free){
						lfree++;
		} } } }

		int rfree = 0;
		if(c4free){
			rfree++;
			if(c3free){
				rfree++;
				if(c2free){
					rfree++;
					if(c1free){
						rfree++;
		} } } }

		return lfree*10 + rfree;
	}
	
}

// No tuple support in java.
class BlockPosition{
	int bx, rot;
}
