package code;

/**This is the default tetris playing AI. It holds a reference to
 * the tetris engines so it can send key events when necessary
 * and it knows the current block*/
public class TetrisAI
{
	private TetrisPanel panel;
	private TetrisEngine engine;
	AIThread thread;
	
	/**Is the AI running?*/
	public volatile boolean isrunning = false;
	
	/**Time (ms) AI has to wait per keypress.*/
	public static final int waittime = 100;
	
	public TetrisAI(TetrisPanel panel){
		this.panel = panel;
		engine = panel.engine;
		thread = new AIThread();
	}
	
	public void send_ready(){
		thread.start();
		isrunning = true;
	}
	
	class AIThread extends Thread{
		public void run(){
			
			while(engine!=null && engine.state != GameState.GAMEOVER){
				//If it's merely paused, do nothing; if it's actually game over
				//then break loop entirely.
    			if(engine.state == GameState.PLAYING){
    				if(engine.activeblock == null) continue;
    				
    				BlockPosition temp = computeBestFit(engine);
    				int elx = temp.bx;
    				int erot = temp.rot;
    				int blx = engine.activeblock.x;
    				int brot = engine.activeblock.rot;
    				
    				//Move it!
    				movehere(blx, brot, elx, erot);
    			}
    			//safety
    			try{Thread.sleep(100);}catch(Exception e){}
			}
			
			System.out.println("Game Over!");
			isrunning = false;
		}
		
		/**Keypresses to move block to calculated position.*/
		private void movehere(int curx, int currot, int finx, int finrot){
			while(currot != finrot){
				//Rotate first so we don't get stuck in the edges.
				currot++;
				engine.keyrotate();
			}
			
			while(curx != finx){
				//Now nudge the block.
				if(curx < finx){
					engine.keyright();
					curx++;
				}
				else if(curx > finx){
					engine.keyleft();
					curx--;
				}
			}
		}
	}
	
	/**This can calculate the best possible fit for it, given the current
	 * state the blocks are in.*/
	static BlockPosition computeBestFit(TetrisEngine currentstate){
		BlockPosition ret = new BlockPosition();
		ret.bx = 0;
		ret.rot = 0;
		return ret;
	}
	
}

//groups together variables.
class BlockPosition{
	int bx, rot;
}
