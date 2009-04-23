package code;

import java.util.*;

import code.SoundManager.Sounds;

import static code.ProjectConstants.*;

/**This class calculates most of the block positions,<br>
 * rotations, etc, although the TetrisPanel object<br>
 * still keeps track of the concrete block coordinates.<br><br>
 * This class will change variables in the TetrisPanel class.*/
public class TetrisEngine
{
	
	
	
	
	//---------------VARIABLES--------------//
	
	/**Bunch of hardcoded blocks and their rotations.*/
	public static final byte[][][][] blockdef =
	{
    	{
    		//0 = I block.
        	{
        		{0,0,0,0},
        		{0,0,0,0},
        		{1,1,1,1},
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
    			{0,0,0,0},
    			{0,1,1,0},
    			{0,1,1,0},
    			{0,0,0,0}
    		}
        },
        {
        	//2 = L block
        	{
        		{0,0,0,0},
        		{0,1,0,0},
    			{0,1,0,0},
    			{0,1,1,0}
        	},
        	{
        		{0,0,0,0},
        		{0,0,0,0},
    			{0,0,1,0},
    			{1,1,1,0}
        	},
        	{
        		{0,0,0,0},
        		{1,1,0,0},
    			{0,1,0,0},
    			{0,1,0,0}
        	},
        	{
        		{0,0,0,0},
        		{1,1,1,0},
    			{1,0,0,0},
    			{0,0,0,0}
        	}
        },
        {
        	//3 = J block
        	{
        		{0,0,0,0},
        		{0,0,1,0},
    			{0,0,1,0},
    			{0,1,1,0}
        	},
        	{
        		{0,0,0,0},
        		{0,0,0,0},
    			{1,1,1,0},
    			{0,0,1,0}
        	},
        	{
        		{0,0,0,0},
        		{0,1,1,0},
    			{0,1,0,0},
    			{0,1,0,0}
        	},
        	{
        		{0,0,0,0},
        		{0,0,0,0},
    			{1,0,0,0},
    			{1,1,1,0}
        	}
        },
        {
        	//4 = T block
        	{
        		{0,0,0,0},
        		{0,1,0,0},
        		{1,1,1,0},
        		{0,0,0,0}
        	},
        	{
        		{0,0,0,0},
        		{0,1,0,0},
        		{0,1,1,0},
        		{0,1,0,0}
        	},
        	{
        		{0,0,0,0},
        		{0,0,0,0},
        		{1,1,1,0},
        		{0,1,0,0}
        	},
        	{
        		{0,0,0,0},
        		{0,1,0,0},
        		{1,1,0,0},
        		{0,1,0,0}
        	}
        },
        {
        	//5 = S block
        	{
        		{0,0,0,0},
        		{0,1,1,0},
        		{1,1,0,0},
        		{0,0,0,0}
        	},
        	{
        		{0,0,0,0},
        		{0,1,0,0},
        		{0,1,1,0},
        		{0,0,1,0}
        	}
        },
        {
        	//6 = Z block
        	{
        		{0,0,0,0},
        		{0,1,1,0},
        		{0,0,1,1},
        		{0,0,0,0}
        	},
        	{
        		{0,0,0,0},
        		{0,0,1,0},
        		{0,1,1,0},
        		{0,1,0,0}
        	}
        }
    };
	
	/**Reference to the TetrisPanel containing this object;*/
	TetrisPanel tetris;
	
	
	/**Random object used to generate new blocks.*/
	Random rdm;
	
	
	/**Primitive representation of active block.*/
	byte[][] activeBlock;
	
	
	/**Variables with infomation about the active block.*/
	int activeBlockType, activeBlockX,
	    activeBlockY, activeBlockRot;
	
	
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
				randomblock();
				
				while(true)
				{
					long timeelapsedsincelaststep = 
						System.currentTimeMillis() - laststep;
					
					//Break loop if game isn't even playing.
					if(!(tetris.state == GameState.PLAYING))
						continue;
					
					try{
						//Safer than sleeping for more.
						Thread.sleep(20);
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
		
		activeBlockX ++;
		
		//Failsafe: Revert XPosition.
		if(!copy())activeBlockX --;
		
	}
	
	/**Called when the LEFT key is pressed.*/
	public synchronized void keyleft()
	{
		if(DEBUG)System.out.println("LEFT.");
		
		activeBlockX --;
		
		//Failsafe: Revert XPosition.
		if(!copy())activeBlockX ++;
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
		
		byte[][] lastblock = activeBlock;
		int lastrot = activeBlockRot;
		
		//Next rotation in array.
		if(activeBlockRot == blockdef[activeBlockType].length-1)
		{
			activeBlockRot = 0;
		}
		else activeBlockRot++;
		
		activeBlock = blockdef[activeBlockType][activeBlockRot];
		tetris.sound.sfx(Sounds.ROTATE);
		
		//Failsafe revert.
		if(!copy()){
			activeBlock = lastblock;
			activeBlockRot = lastrot;
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
	
	
	/**Done the current block; plays the FALL sound and changes
	 * <br>all active blocks to filled.*/
	public synchronized void donecurrent()
	{	
		tetris.sound.sfx(Sounds.FALL);
		for(int i = 0;i < tetris.blocks.length;i++)
		{
			for(int r = 0;r < tetris.blocks[i].length;r++)
			{
				if(tetris.blocks[i][r].state == BlockState.ACTIVE)
					tetris.blocks[i][r].state = BlockState.FILLED;
			}
		}
		
		checkforclears(0,null);//Moving this here.
		
		activeBlock = null;
	}

	/**Called when Game Over (Blocks stacked so high that copy() fails)*/
	public synchronized void gameover()
	{
		pImportant("Game Over");
		tetris.sound.sfx(Sounds.DIE);
		tetris.state = GameState.STARTSCREEN;
	}

	/**Copies the position of the active block into<br>
	 * the abstract block grid. Returns false if a block<br>
	 * already exists under it, true otherwise.<br>
	 * 
	 * <br>This method isn't very efficient.*/
	public synchronized boolean copy()
	{
		try{
		int x = activeBlockX;
		int y = activeBlockY;
		byte[][] t = activeBlock;
		Block[][] buffer = tetris.blocks;
		
		if(t==null)return false;//Early NullPointerException failsafe
		
		//Check if any blocks already have a block under them.
		//If yes, immediately return.
		for(int i = 0;i < 4;i++)
		{
			for(int r = 0;r < 4;r++)
			{
				if(activeBlock[r][i] == 1
					&&buffer[x+i][y+r].state==BlockState.FILLED)
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
				if(buffer[i][r].state == BlockState.ACTIVE)
					buffer[i][r].state = BlockState.EMPTY;
			}
		}
		
		//Then fill in blocks from the new position.
		for(int i = 0;i < 4;i++)
		{
			for(int r = 0;r < 4;r++)
			{
				if(activeBlock[i][r] == 1)
				{
					buffer[x+r][y+i].state = BlockState.ACTIVE;
				}
			}
		}
		
		//Nothing threw an exception; now copy the buffer.
		tetris.blocks = buffer;
		
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
		if(activeBlock == null)
		{//step() gives you a random block if none is available.
			randomblock();
			copy();
			return;
		}
		
		if(DEBUG)
			System.out.println("STEP: " + ++stepcount);
		laststep = System.currentTimeMillis();
		
		//move 1 down.
		activeBlockY++;
		
		if(!copy())
			donecurrent();
		
	}
	
	
	/**As expected this function checks whether there are any clears.
	 * <br>Uses recursion if more than one line can be cleared.*/
	private synchronized void
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
				if(b[y][i].state!=BlockState.FILLED)continue ML;
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
			//No new lines were cleared.
			pImportant("Cleared: " + alreadycleared + " line(s).");
			if(alreadycleared>=4)tetris.sound.sfx(Sounds.TETRIS);
			else tetris.sound.sfx(Sounds.CLEAR);
			
			tetris.lines += alreadycleared;
		}
		
		tetris.blocks = b;
	}
	
	
	/**Generates a random block , in a random rotation.*/
	private synchronized void randomblock()
	{
		//Generate random block.
		int x = blockdef.length;
		int retx = rdm.nextInt(x);
		
		int y = blockdef[retx].length;
		int rety = rdm.nextInt(y);
		
		activeBlockType=retx;
		activeBlockRot=rety;
		
		activeBlock = blockdef[retx][rety];
		
		activeBlockX = tetris.width/2 -2;
		activeBlockY = -3;
		
		//Don't even try if there's any blocks in the first row.
		for(int i = 0;i < tetris.blocks.length;i++)
		{
			if(tetris.blocks[i][0].state==BlockState.FILLED)
				gameover();
		}
		
		//Attempts to generate the block as high up as possible.
		do{
			if(activeBlockY > 0)
			{
				gameover();
				break;
			}
			activeBlockY++;
		}while(!copy());
		
		System.out.println(activeBlockX + " , " + activeBlockY);
	}
}
