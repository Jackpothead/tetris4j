package code;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.*;

import code.SoundManager.Sounds;

import static code.ProjectConstants.*;

/**This class calculates most of the block positions,<br>
 * rotations, etc, although the TetrisPanel object<br>
 * still keeps track of the concrete block coordinates.<br><br>
 * This class will change variables in the TetrisPanel class.*/
public class TetrisEngine
{
	
	
	//---------------VARIABLES--------------//
	
	/**Bunch of hardcoded blocks and their rotations.
	 * Code them high up in the array so that when you
	 * get a new one it appears in the highest spot 
	 * possible.*/
	public static final byte[][][][] blockdef =
	{
    	{
    		//0 = I block.
        	{
        		{1,1,1,1},
        		{0,0,0,0},
        		{0,0,0,0},
        		{0,0,0,0}
        	},
        	{
        		{0,1,0,0},
        		{0,1,0,0},
        		{0,1,0,0},
        		{0,1,0,0}
        	}
        },
        {
        	//1 = O block
    		{
    			{0,1,1,0},
    			{0,1,1,0},
    			{0,0,0,0},
    			{0,0,0,0}
    		}
        },
        {
        	//2 = L block
        	{
        		{0,1,0,0},
        		{0,1,0,0},
    			{0,1,1,0},
    			{0,0,0,0}
        	},
        	{
        		{0,0,1,0},
        		{1,1,1,0},
    			{0,0,0,0},
    			{0,0,0,0}
        	},
        	{
        		{1,1,0,0},
        		{0,1,0,0},
    			{0,1,0,0},
    			{0,0,0,0}
        	},
        	{
        		{1,1,1,0},
        		{1,0,0,0},
    			{0,0,0,0},
    			{0,0,0,0}
        	}
        },
        {
        	//3 = J block
        	{
        		{0,0,1,0},
        		{0,0,1,0},
    			{0,1,1,0},
    			{0,0,0,0}
        	},
        	{
        		{1,1,1,0},
        		{0,0,1,0},
    			{0,0,0,0},
    			{0,0,0,0}
        	},
        	{
        		{0,1,1,0},
        		{0,1,0,0},
    			{0,1,0,0},
    			{0,0,0,0}
        	},
        	{
        		{1,0,0,0},
        		{1,1,1,0},
    			{0,0,0,0},
    			{0,0,0,0}
        	}
        },
        {
        	//4 = T block
        	{
        		{0,1,0,0},
        		{1,1,1,0},
        		{0,0,0,0},
        		{0,0,0,0}
        	},
        	{
        		{0,1,0,0},
        		{0,1,1,0},
        		{0,1,0,0},
        		{0,0,0,0}
        	},
        	{
        		{1,1,1,0},
        		{0,1,0,0},
        		{0,0,0,0},
        		{0,0,0,0}
        	},
        	{
        		{0,1,0,0},
        		{1,1,0,0},
        		{0,1,0,0},
        		{0,0,0,0}
        	}
        },
        {
        	//5 = S block
        	{
        		{0,1,1,0},
        		{1,1,0,0},
        		{0,0,0,0},
        		{0,0,0,0}
        	},
        	{
        		{0,1,0,0},
        		{0,1,1,0},
        		{0,0,1,0},
        		{0,0,0,0}
        	}
        },
        {
        	//6 = Z block
        	{
        		{0,1,1,0},
        		{0,0,1,1},
        		{0,0,0,0},
        		{0,0,0,0}
        	},
        	{
        		{0,0,1,0},
        		{0,1,1,0},
        		{0,1,0,0},
        		{0,0,0,0}
        	}
        }
    };
	
	
	/**Reference to the TetrisPanel containing this object;*/
	TetrisPanel tetris;
	
	
	/**Random object used to generate new blocks.*/
	Random rdm;
	
	
	/**Primitive representation of active block.*/
	Tetromino activeblock;
	
	
	/**Next block.*/
	Tetromino nextblock = null;
	
	
	/**Time of previous step.*/
	long laststep = System.currentTimeMillis();
	
	
	/**Not really needed, just a counter for steps.*/
	int stepcount = 0;
	
	
	/**Thread to run for the game.*/
	Thread gamethread;
	
	
	/**Public constructor. Remember to call startengine()
	 * <br>or else this won't do anything!
	 * @param p TetrisPanel.*/
	public TetrisEngine(TetrisPanel p)
	{
		//Initialize objects.
		tetris = p;
		rdm = new Random();
		
		//Initialize game thread.
		gamethread = new Thread(){
			public void run()
			{
				//this fixes a bug.
				//newblock();
				
				while(true)
				{
					//Break loop if game isn't even playing.
					if(!(tetris.state == GameState.PLAYING))
						continue;
					
					long timeelapsedsincelaststep = 
						System.currentTimeMillis() - laststep;
					
					try{
						//Safer than sleeping for more.
						Thread.sleep(10);
					}catch(Exception e){}
					
					if(timeelapsedsincelaststep > tetris.steptime)
						step();
				}
			}
		};
	}
	
	
	
	
	//---------------FUNCTIONS---------------//
	
	/**Called when the RIGHT key is pressed.*/
	public synchronized void keyright()
	{
		if(DEBUG)System.out.println("RIGHT.");
		
		activeblock.x++;
		
		//Failsafe: Revert XPosition.
		if(!copy())activeblock.x--;
		
	}
	
	/**Called when the LEFT key is pressed.*/
	public synchronized void keyleft()
	{
		if(DEBUG)System.out.println("LEFT.");
		
		activeblock.x--;
		
		//Failsafe: Revert XPosition.
		if(!copy())activeblock.x++;
	}
	
	/**Called when the DOWN key is pressed.*/
	public synchronized void keydown()
	{
		if(DEBUG)System.out.println("DOWN.");
		step();
	}
	
	/**Called when rotate key is called (Z or UP)*/
	public synchronized void keyrotate()
	{
		if(DEBUG)System.out.println("ROTATED.");
		
		if(activeblock.array==null)return;//necessary NPE checking.
		
		Block[][] lastblock = copy2D(activeblock.array);
		int lastrot = activeblock.rot;
		
		//Next rotation in array.
		if(activeblock.rot == blockdef[activeblock.type].length-1)
		{
			activeblock.rot = 0;
		}
		else activeblock.rot++;
		
		activeblock.array = toBlock2D(
				blockdef[activeblock.type][activeblock.rot]);
		tetris.sound.sfx(Sounds.ROTATE);
		
		//Failsafe revert.
		if(!copy()){
			activeblock.array = lastblock;
			activeblock.rot = lastrot;
		}
	}
	
	/**Called when slam key (SPACE) is pressed.*/
	public synchronized void keyslam()
	{
		laststep = System.currentTimeMillis();
		
		//This will game over pretty damn fast!
		if(activeblock.array == null)newblock();
		
		while(true)
		{
			activeblock.y++;
		
			if(!copy())
			{
				donecurrent();
				return;
			}
		}
	}
	
	//I'm bored so here's an ASCII rendering of TETRIS..
	///////////////////////////////////////////////////////////////////
	//                                                               //
	//  ///////////   ////////  //////////  /////     //   ///////   //
	//      //       //            //      //   //   //   //         //
	//     //       ////////      //      ///////   //   ////////    //
	//    //       //            //      //  //    //         //     //
	//   //       ////////      //      ///  //   //   ////////      //
	//                                                               //
	///////////////////////////////////////////////////////////////////
	
	
	/**Should be called AFTER swing initialization. This is so
	 * <br>the first block doesn't appear halfway down the screen.*/
	public void startengine()
	{
		if(!gamethread.isAlive())gamethread.start();
	}
	
	/**Resets the blocks but keeps everything else.*/
	public void clear()
	{
		for(int i = 0;i < tetris.blocks.length;i++)
		{
			for(int j = 0;j < tetris.blocks[i].length;j++)
			{
				tetris.blocks[i][j] = new Block(BlockState.EMPTY);
			}
		}
	}
	
	/**Fully resets everything.*/
	public void reset()
	{
		tetris.score=0;
		tetris.lines=0;
		clear();
		activeblock.array = null;
	}
	
	
	/**Done the current block; plays the FALL sound and changes
	 * <br>all active blocks to filled.*/
	private void donecurrent()
	{	
		tetris.sound.sfx(Sounds.FALL);
		for(int i = 0;i < tetris.blocks.length;i++)
		{
			for(int r = 0;r < tetris.blocks[i].length;r++)
			{
				if(tetris.blocks[i][r].getState() == BlockState.ACTIVE)
					tetris.blocks[i][r].setState(BlockState.FILLED);
			}
		}
		
		checkforclears();//Moving this here.
		
		activeblock.array = null;
	}

	/**Called when Game Over (Blocks stacked so high that copy() fails)*/
	private void gameover()
	{
		//Check first.
		if(tetris.state == GameState.GAMEOVER)
			return;
		
		//Return immediately.
		new Thread(){public void run(){
			//pause the game first.
			tetris.state = GameState.GAMEOVER;
			
			pImportant("Game Over");
			
			//die sound.
			tetris.sound.sfx(Sounds.DIE);
			
			String disp = 	"            \n"+
			"    xxxx    \n"+
			"   x    x   \n"+
			"  x      x  \n"+
			" x xx  xx x \n"+
			" x        x \n"+
			" x   x    x \n"+
			" x        x \n"+
			" x  xxx   x \n"+
			" x x   x  x \n"+
			" x       x  \n"+
			"  xx   xx   \n"+
			"    xxx     \n"+
			"x          x\n"+
			" xx      xx \n"+
			"   xx  xx   \n"+
			"     xx     \n"+
			"   xx  xx   \n"+
			" xx      xx \n"+
			"x          x\n";

			//Must do this before reset.
            Block[][] gameover = 
            	strToBlocks(disp, tetris.width, tetris.height).clone();
            tetris.blocks = gameover;
            
            long timebefore = System.currentTimeMillis();
            
            //Pause loop. Capped at 5 seconds.
            while(tetris.state == GameState.GAMEOVER
            		&& System.currentTimeMillis()-timebefore < 5000)
            {
            	try{Thread.sleep(20);}catch(Exception e){}
            }
			
			//reset.
			reset();
			tetris.state = GameState.PAUSED;
			
			//Important?
			clear();
			
		}}.start();
		
	}

	/**Copies the position of the active block into<br>
	 * the abstract block grid. Returns false if a block<br>
	 * already exists under it, true otherwise.<br>
	 * 
	 * <br>This method isn't very efficient.*/
	private boolean copy()
	{
		try{
		int x = activeblock.x;
		int y = activeblock.y;
		Block[][] buffer = copy2D(tetris.blocks);
		
		if(activeblock.array==null)
			return false;//Early NullPointerException failsafe
		
		//Check if any blocks already have a block under them.
		//If yes, immediately return.
		for(int i = 0;i < 4;i++)
		{
			for(int r = 0;r < 4;r++)
			{
				if(activeblock.array[r][i].getState() == BlockState.ACTIVE
					&&buffer[x+i][y+r].getState() == BlockState.FILLED)
				{
					return false;
				}
			}
		}
		
		//First remove all active blocks.
		for(int i = 0;i < buffer.length;i++)
		{
			for(int r = 0;r < buffer[i].length;r++)
			{
				if(buffer[i][r].getState() == BlockState.ACTIVE)
				{
					buffer[i][r].setState(BlockState.EMPTY);
					buffer[i][r].setColor(Block.emptycolor);
				}
			}
		}
		
		//Then fill in blocks from the new position.
		for(int i = 0;i < 4;i++)
		{
			for(int r = 0;r < 4;r++)
			{
				if(activeblock.array[i][r].getState() == BlockState.ACTIVE)
				{
					buffer[x+r][y+i].setState(BlockState.ACTIVE);
					
					//facepalm.
					buffer[x+r][y+i].setColor(activeblock.color);
				}
			}
		}
		
		//Nothing threw an exception; now copy the buffer.
		tetris.blocks = copy2D(buffer);
		
		}catch(ArrayIndexOutOfBoundsException e)
		{return false;}//Noob bounds detection.
					//Exceptions are supposedly slow but
					//performance isn't really an issue
					//here.
		
		return true;
	}

	/**Steps into the next phase if possible.*/
	private synchronized void step()
	{
		if(activeblock == null)
		{//step() gives you a random block if none is available.
			newblock();
			
			return;
		}
		
		if(DEBUG)
			System.out.println("STEP: " + ++stepcount);
		laststep = System.currentTimeMillis();
		
		//move 1 down.
		activeblock.y++;
		
		if(!copy())
			donecurrent();
		
	}
	
	
	/**Runs the checkforclears() on a seperate thread. Also performs
	 * <br>the fade out effect.*/
	private void checkforclears()
	{
		new Thread(){
			public void run()
			{
				//Some copy/pasting here! =)
				ArrayList<Block> fadeblocks = new ArrayList<Block>();
				
				loop:
				for(int i = tetris.blocks[0].length-1;i>=0;i--)
				{
					//check for unfilled blocks.
					for(int y = 0;y < tetris.blocks.length;y++)
					{
						if(!tetris.blocks[y][i].getState()
								.equals(BlockState.FILLED))
						continue loop;
					}
					
					//passed; now add blocks.
					for(int u = 0;u < tetris.blocks.length;u++)
					{
						fadeblocks.add(tetris.blocks[u][i]);
					}
				}
				
				long before = System.currentTimeMillis();
				int approxloops = tetris.fadetime/20;
				
				tetris.state = GameState.BUSY;
				
				//Fade loop: works by object referencing
				while(System.currentTimeMillis() - before 
						< tetris.fadetime)
				{
					if(fadeblocks.size()==0)break;//Lol yea.
					
					//This is a linear fade algorithm.
					for(Block b : fadeblocks)
					{
						//Not the best color algorithm, but works most of
						//the time.
						
						//New fading algorithm. Only changes the ALPHA value
						//and leaves the rgb.
						Color bcol = b.getColor();
						int R = bcol.getRed();
						int G = bcol.getGreen();
						int B = bcol.getBlue();
						int AL = bcol.getAlpha();
						
						int fade = (AL-Block.emptycolor.getAlpha()) /approxloops;
						
						if(AL>0)
							AL-=fade;
						
						if(AL < 0) //Occasionally crashes without this.
							AL = 0;
						
						Color newc = new Color(R,G,B,AL);
						b.setColor(newc);
					}
					
					try{
						Thread.sleep(20);
					}catch(Exception e){}
				}
				
				tetris.state = GameState.PLAYING;
				
				//Now actually remove the blocks.
				checkforclears(0,null);
				newblock();
			}
		}.start();
	}
	
	
	/**As expected this function checks whether there are any clears.
	 * <br>Uses recursion if more than one line can be cleared.
	 * <br>Don't run this on the EDT!*/
	private void
		checkforclears(int alreadycleared, Block[][] b)
	{
		if(b==null)
			b = tetris.blocks;
		int whichline = -1;
		int old = alreadycleared;
		
		//Loops to find any row that has every block filled.
		// If one block is not filled, the loop breaks.
		ML:
		for(int i = b[0].length-1;i>=0;i--)
		{
			for(int y = 0;y < b.length;y++)
			{
				if(!b[y][i].getState().equals(BlockState.FILLED))
					continue ML;
			}
			
			alreadycleared++;
			whichline = i;
			break ML;
		}
		
		//If this recursive step produced more clears:
		if(alreadycleared>old)
		{
			for(int i = whichline;i>0;i--)
			{//Iterate and copy the state of the block on top of itself
				//to its location.
				for(int y = 0;y < b.length;y++)
				{
					b[y][i] = b[y][i-1];
				}
			}
			
			//Recursion step! Necessary if you want to clear more than
			//1 line at a time!
			checkforclears(alreadycleared,b);
		}
		else if(alreadycleared>0)
		{
			switch(alreadycleared)
			{
			case 1:
				tetris.score += 45;
				break;
			case 2:
				tetris.score += 105;
				break;
			case 3:
				tetris.score += 350;
				break;
			case 4:
				tetris.score += 1250;
				break;
			}
			
			//No new lines were cleared.
			pImportant("Cleared: " + alreadycleared + " line(s).");
			if(alreadycleared>=4)tetris.sound.sfx(Sounds.TETRIS);
			else tetris.sound.sfx(Sounds.CLEAR);
			
			tetris.lines += alreadycleared;
		}
		
		tetris.blocks = b;
	}
	
	
	/**Generates a random block , in a random rotation.*/
	private void newblock()
	{
		// Check:
		if(nextblock == null)
			nextblock = getRandBlock();
		
		//Next block becomes this block.
		activeblock = nextblock.clone();
		
		//Generate random block.
		nextblock = getRandBlock();
		
		if(!copy()){
			gameover();
		}
		
		//Bonus?
		tetris.score++;
	}
	
	/**Create and return a random block.*/
	private Tetromino getRandBlock()
	{
		Tetromino ret = new Tetromino();
		int x = blockdef.length;
		int rnd1 = rdm.nextInt(x);
		
		int y = blockdef[rnd1].length;
		int rnd2 = rdm.nextInt(y);
		
		ret.type=rnd1;
		ret.rot=rnd2;
		
		ret.array = toBlock2D(blockdef[rnd1][rnd2]);
		
		ret.x = tetris.width/2 -2;
		ret.y = 0;
		
		Color bcolor = Block.colors[rnd1];
		ret.color = bcolor;
		
		//Fill the block with their colors first.
		for(int i = 0;i < ret.array.length;i++)
		{
			for(int k = 0;k < ret.array[i].length;k++)
			{
				if(ret.array[i][k].getState()==BlockState.ACTIVE)
					ret.array[i][k].setColor(ret.color);
			}
		}
		return ret;
	}
	
	/**Copies an array, but runs in n^2 time.*/
	private static Block[][] copy2D(Block[][] in)
	{
		Block[][] ret = new Block[in.length][in[0].length];
		for(int i = 0;i < in.length;i++)
		{
			for(int j = 0;j < in[0].length;j++)
			{
				ret[i][j] = in[i][j].clone();
			}
		}
		return ret;
	}
	
	/**Function to convert byte[][] to Block[][]*/
	private static Block[][] toBlock2D(byte[][] b)
	{
		if(b == null)return null;         
		
		Block[][] ret = new Block[b.length][b[0].length];
		
		for(int i = 0;i < b.length;i++)
		{
			for(int j = 0;j < b[0].length;j++)
			{
				switch(b[i][j])
				{
				case 1:
					ret[i][j] = new Block(BlockState.ACTIVE);
					break;
				default:
					ret[i][j] = new Block(BlockState.EMPTY);
				}
			}
		}
		return ret;
	}
	
	
	/**Function to convert Block[][] to byte[][]*/
	private static byte[][] toByte2D(Block[][] b)
	{
		if(b == null)return null;
		
		byte[][] ret = new byte[b.length][b[0].length];
		
		for(int i = 0;i < b.length;i++)
		{
			for(int j = 0;j < b[0].length;j++)
			{
				ret[i][j] = b[i][j].toByte();
			}
		}
		
		return ret;
	}
	
	
	/**Return a Block[][], from a String.*/
	private static Block[][] strToBlocks(String disp, int a, int b)
	{
		Block[][] ret = new Block[a][b];
		String[] ts = disp.split("\n");
		for(int i = 0;i < ret[0].length;i++)
		{
			for(int y = 0;y < ret.length;y++)
			{
				if(ts[i].charAt(y)=='x')
				{
					ret[y][i] = new Block(BlockState.FILLED);
					ret[y][i].setColor(new Color(0,0,0,127));
				}
				else ret[y][i] = new Block(BlockState.EMPTY);
			}
		}
		return ret;
	}

}
