package code;

import static code.ProjectConstants.sleep_;
import static code.ProjectConstants.GameState;

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
	public static final int waittime = 100;
	
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
	
	/*This can calculate the best possible fit for it, given the current
	 * state the blocks are in.*/
	static BlockPosition computeBestFit(TetrisEngine currentstate){
		BlockPosition ret = new BlockPosition();
		ret.bx = 4;
		ret.rot = 0;
		return ret;
	}
	
}

//groups together variables.
class BlockPosition{
	int bx, rot;
}
