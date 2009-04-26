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
	
	/**Object representation of a tetromino.*/
	static class BlockGroup
	{
		public BlockGroup(){}
		public Block[][] array;
		public int x, y, rot, type;
		public Color color;
	}
	
	
	/**Reference to the TetrisPanel containing this object;*/
	TetrisPanel tetris;
	
	
	/**Random object used to generate new blocks.*/
	Random rdm;
	
	
	/**Primitive representation of active block.*/
	BlockGroup activeblock;
	
	
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
		
		checkforclears();//Moving this here.
		
		activeblock.array = null;
	}

	/**Called when Game Over (Blocks stacked so high that copy() fails)*/
	public synchronized void gameover()
	{
		//Return immediately.
		new Thread(){public void run(){
			//pause the game first.
			tetris.state = GameState.GAMEOVER;
			
			pImportant("Game Over");
			
			//die sound.
			tetris.sound.sfx(Sounds.DIE);
			
			try
			{
				//I have to do this.. bugfix.
				SwingUtilities.invokeAndWait(new Runnable(){
					public void run()
					{
						//wait for input.
						JOptionPane.showInternalMessageDialog(tetris,
						"Game Over: Play again?");
					}
				});
			} catch (Exception e)
			{
				throw new RuntimeException("GUI window failed.");
			}
			
			//reset.
			tetris.score=0;
			tetris.lines=0;
			for(int i = 0;i < tetris.blocks.length;i++)
			{
				for(int j = 0;j < tetris.blocks[i].length;j++)
				{
					tetris.blocks[i][j] = new Block(BlockState.EMPTY);
				}
			}
			activeblock.array = null;
			tetris.state = GameState.PLAYING;
			newblock();
			laststep = System.currentTimeMillis();
		}}.start();
		
	}

	/**Copies the position of the active block into<br>
	 * the abstract block grid. Returns false if a block<br>
	 * already exists under it, true otherwise.<br>
	 * 
	 * <br>This method isn't very efficient.*/
	public synchronized boolean copy()
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
		if(activeblock.array == null)
		{//step() gives you a random block if none is available.
			tetris.score++;
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
	private synchronized void checkforclears()
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
						fadeblocks.add(tetris.blocks[u][i]);
				}
				
				long before = System.currentTimeMillis();
				int approxloops = tetris.fadetime/20;
				
				//Fade loop: works by object referencing
				while(System.currentTimeMillis() - before 
						< tetris.fadetime)
				{
					for(Block b : fadeblocks)
					{
						Color bcol = b.getColor();
						int R = bcol.getRed();
						int G = bcol.getGreen();
						int B = bcol.getBlue();
						int AL = bcol.getAlpha();
						
						float rfade = 
							(Block.emptycolor.getRed()-R)/approxloops;
						float gfade = 
							(Block.emptycolor.getGreen()-G)/approxloops;
						float bfade = 
							(Block.emptycolor.getBlue()-B)/approxloops;
						float alfade =
							(Block.emptycolor.getAlpha()-B)/approxloops;
						
						if(R<255-rfade)
							R+=rfade;
						
						if(G<255-gfade)
							G+=gfade;
						
						if(B<255-bfade)
							B+=bfade;
						
						if(AL<255-alfade)
							AL+=alfade;
						
						Color newc = new Color(R,G,B);
						b.setColor(newc);
					}
					
					try{
						Thread.sleep(20);
					}catch(Exception e){}
				}
				
				//Now actually remove the blocks.
				checkforclears(0,null);
				newblock();
			}
		}.start();
	}
	
	
	/**As expected this function checks whether there are any clears.
	 * <br>Uses recursion if more than one line can be cleared.
	 * <br>Don't run this on the EDT!*/
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
		activeblock = new BlockGroup();
		
		int x = blockdef.length;
		int retx = rdm.nextInt(x);
		retx=0;
		
		int y = blockdef[retx].length;
		int rety = rdm.nextInt(y);
		
		activeblock.type=retx;
		activeblock.rot=rety;
		
		activeblock.array = toBlock2D(blockdef[retx][rety]);
		
		activeblock.x = tetris.width/2 -2;
		activeblock.y = 0;
		
		Color bcolor = Block.colors
		[rdm.nextInt(Block.colors.length)];
		activeblock.color = bcolor;
		
		//Don't even try if there's any blocks in the first row.
		for(int i = 0;i < tetris.blocks.length;i++)
		{
			if(tetris.blocks[i][0].getState().equals(BlockState.FILLED))
			{
				gameover();
				return;
			}
		}
		
		
		//Fill the block with their colors first.
		for(int i = 0;i < activeblock.array.length;i++)
		{
			for(int k = 0;k < activeblock.array[i].length;k++)
			{
				if(activeblock.array[i][k].getState()==BlockState.ACTIVE)
					activeblock.array[i][k].setColor(activeblock.color);
			}
		}
		
		
		if(!copy()){
			gameover();
		}
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
