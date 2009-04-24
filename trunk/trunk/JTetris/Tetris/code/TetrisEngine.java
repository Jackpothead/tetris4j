package code;

import java.awt.Color;
import java.util.*;

import javax.swing.JOptionPane;

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
	Block[][] activeBlock;
	
	
	/**Variables with infomation about the active block.*/
	int activeBlockType, activeBlockX,
	    activeBlockY, activeBlockRot;
	
	
	Color activeBlockColor = null;
	
	
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
				newblock();
				
				while(true)
				{
					long timeelapsedsincelaststep = 
						System.currentTimeMillis() - laststep;
					
					//Break loop if game isn't even playing.
					if(!(tetris.state == GameState.PLAYING))
						continue;
					
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
		
		if(activeBlock==null)return;//necessary NPE checking.
		
		Block[][] lastblock = copy2D(activeBlock);
		int lastrot = activeBlockRot;
		
		//Next rotation in array.
		if(activeBlockRot == blockdef[activeBlockType].length-1)
		{
			activeBlockRot = 0;
		}
		else activeBlockRot++;
		
		activeBlock = toBlock2D(
				blockdef[activeBlockType][activeBlockRot]);
		tetris.sound.sfx(Sounds.ROTATE);
		
		//Failsafe revert.
		if(!copy()){
			activeBlock = lastblock;
			activeBlockRot = lastrot;
		}
	}
	
	/**Called when slam key (SPACE) is pressed.*/
	public synchronized void keyslam()
	{
		laststep = System.currentTimeMillis();
		
		//This will game over pretty damn fast!
		if(activeBlock == null)newblock();
		
		while(true)
		{
			activeBlockY++;
		
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
	
	
	/**Done the current block; plays the FALL sound and changes
	 * <br>all active blocks to filled.*/
	public synchronized void donecurrent()
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
		
		checkforclears(0,null);//Moving this here.
		
		activeBlock = null;
	}

	/**Called when Game Over (Blocks stacked so high that copy() fails)*/
	public synchronized void gameover()
	{
		//pause the game first.
		tetris.state = GameState.GAMEOVER;
		
		pImportant("Game Over");
		
		//die sound.
		tetris.sound.sfx(Sounds.DIE);
		
		//wait for input.
		JOptionPane.showInternalMessageDialog(tetris,
				"Game Over! Press OK to play again!");
		
		//reset.
		tetris.score=0;
		tetris.lines=0;
		for(int i = 0;i < tetris.blocks.length;i++)
		{
			for(int j = 0;j < tetris.blocks[0].length;j++)
			{
				tetris.blocks[i][j] = new Block(BlockState.EMPTY);
			}
		}
		activeBlock = null;
		tetris.state = GameState.PLAYING;
		newblock();
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
		Block[][] buffer = copy2D(tetris.blocks);
		
		if(activeBlock==null)
			return false;//Early NullPointerException failsafe
		
		//Check if any blocks already have a block under them.
		//If yes, immediately return.
		for(int i = 0;i < 4;i++)
		{
			for(int r = 0;r < 4;r++)
			{
				if(activeBlock[r][i].getState() == BlockState.ACTIVE
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
				if(activeBlock[i][r].getState() == BlockState.ACTIVE)
				{
					buffer[x+r][y+i].setState(BlockState.ACTIVE);
					
					//facepalm.
					buffer[x+r][y+i].setColor(activeBlockColor);
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
		if(activeBlock == null)
		{//step() gives you a random block if none is available.
			tetris.score++;
			newblock();
			
//			//Gameover bug fix
//			if(tetris.state != GameState.GAMEOVER)
//				copy();
//			else tetris.state = GameState.PLAYING;
			
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
	private synchronized void newblock()
	{
		//Generate random block.
		int x = blockdef.length;
		int retx = rdm.nextInt(x);
		
		int y = blockdef[retx].length;
		int rety = rdm.nextInt(y);
		
		activeBlockType=retx;
		activeBlockRot=rety;
		
		activeBlock = toBlock2D(blockdef[retx][rety]);
		
		activeBlockX = tetris.width/2 -2;
		activeBlockY = -3;
		
		Color bcolor = Block.colors
		[rdm.nextInt(Block.colors.length)];
		activeBlockColor = bcolor;
		
		//Don't even try if there's any blocks in the first row.
		for(int i = 0;i < tetris.blocks.length;i++)
		{
			if(tetris.blocks[i][0].getState().equals(BlockState.FILLED))
				gameover();
		}
		
		
		//Fill the block with their colors first.
		for(int i = 0;i < activeBlock.length;i++)
		{
			for(int k = 0;k < activeBlock[i].length;k++)
			{
				if(activeBlock[i][k].getState()==BlockState.ACTIVE)
					activeBlock[i][k].setColor(activeBlockColor);
			}
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

}
