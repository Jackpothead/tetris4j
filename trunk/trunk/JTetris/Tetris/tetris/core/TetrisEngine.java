package tetris.core;

import java.util.*;
import static tetris.core.ProjectConstants.*;

/**Class that works with the TetrisPanel to
 * <br>calculate the blocks, etc.*/
public class TetrisEngine
{
	
	/**Bunch of hardcoded blocks and their rotations.*/
	static final byte[][][][] blocks =
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
        		{0,1,0,0},
        		{0,1,0,0},
    			{0,1,0,0},
    			{0,1,1,0}
        	},
        	{
        		{0,0,0,0},
        		{0,0,0,1},
    			{1,1,1,1},
    			{0,0,0,0}
        	},
        	{
        		{0,1,1,0},
        		{0,0,1,0},
    			{0,0,1,0},
    			{0,0,1,0}
        	},
        	{
        		{0,0,0,0},
        		{1,1,1,1},
    			{1,0,0,0},
    			{0,0,0,0}
        	}
        },
        {
        	//3 = J block
        	{
        		{0,0,1,0},
        		{0,0,1,0},
    			{0,0,1,0},
    			{0,1,1,0}
        	},
        	{
        		{0,0,0,0},
        		{1,1,1,1},
    			{0,0,0,1},
    			{0,0,0,0}
        	},
        	{
        		{0,1,1,0},
        		{0,1,0,0},
    			{0,1,0,0},
    			{0,1,0,0}
        	},
        	{
        		{0,0,0,0},
        		{0,0,0,1},
    			{1,1,1,1},
    			{0,0,0,0}
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
	
	TetrisPanel tetris;
	Random rdm;
	
	//Primitive representation of active block.
	byte[][] activeBlock;
	int activeBlockType;
	int activeBlockX;
	int activeBlockY;
	int activeBlockRot;
	
	long laststep = System.currentTimeMillis();//Time of previous step.
	
	
	/**Public constructor.
	 * @param p TetrisPanel.*/
	public TetrisEngine(TetrisPanel p)
	{
		tetris = p;
		rdm = new Random();
		
		new Thread(){
			public void run()
			{
				while(tetris.state == GameState.PLAYING)
				{
					long timeelapsedsincelaststep = 
						System.currentTimeMillis() - laststep;
					
					try{
						Thread.sleep(50);//Safer than sleeping for less.
					}catch(Exception e){}
					
//					if(timeelapsedsincelaststep > tetris.steptime)
//						step();
				}
			}
		}.start();
	}
	
	public synchronized void actionright()
	{
		if(DEBUG)System.out.println("RIGHT.");
		activeBlockX ++;
		copy();
	}
	
	public synchronized void actionleft()
	{
		if(DEBUG)System.out.println("LEFT.");
		activeBlockX --;
		copy();
	}
	
	public synchronized void actiondown()
	{
		if(DEBUG)System.out.println("DOWN.");
		step();
	}
	
	public synchronized void actionrotate()
	{
		if(DEBUG)System.out.println("ROTATED.");
		
		if(activeBlockRot == blocks[activeBlockType].length-1)
		{
			activeBlockRot = 0;
		}
		else activeBlockRot++;
		
		activeBlock = blocks[activeBlockType][activeBlockRot];
		copy();
	}
	
	private int stepcount = 0;//Hey best to have this aswell..
	
	/**Steps into the next phase if possible.*/
	private void step()
	{
		if(DEBUG)
			System.out.println("STEP: " + ++stepcount);
		laststep = System.currentTimeMillis();
		
		//move 1 down.
			activeBlockY++;
		
		if(!copy())
			donecurrent();
		
	}
	
	public synchronized void donecurrent()
	{
		for(int i = 0;i < tetris.blocks.length;i++)
		{
			for(int r = 0;r < tetris.blocks[i].length;r++)
			{
				if(tetris.blocks[i][r] == DBlock.ACTIVE)
					tetris.blocks[i][r] = DBlock.FILLED;
			}
		}
		
		activeBlock = null;
	}

	/**Copies the position of the active block into<br>
	 * the abstract block grid. Returns false if a block<br>
	 * already exists under it, true otherwise.<br>*/
	public synchronized boolean copy()
	{
		int x = activeBlockX;
		int y = activeBlockY;
		byte[][] t = activeBlock;
		
		//Check if any blocks already have a block under them.
		for(int i = 0;i < 4;i++)
		{
			for(int r = 0;r < 4;r++)
			{
					if(activeBlock[r][i] == 1
						&&tetris.blocks[x+i][y+r]==DBlock.FILLED)
					{
						return false;
					}
			}
		}
		
		
		//First remove all active blocks.
		for(int i = 0;i < tetris.blocks.length;i++)
		{
			for(int r = 0;r < tetris.blocks[i].length;r++)
			{
				if(tetris.blocks[i][r] == DBlock.ACTIVE)
					tetris.blocks[i][r] = DBlock.EMPTY;
			}
		}
		
		//Then fill in with the new position.
		for(int i = 0;i < 4;i++)
		{
			for(int r = 0;r < 4;r++)
			{
				if(toBlock(t[r][i])==DBlock.ACTIVE)
				tetris.blocks[x+i][y+r] = toBlock(t[r][i]);
			}
		}
		
		return true;
	}
	
	/**Generates a random block , in a random rotation.*/
	public byte[][] randomBlock()
	{
		int x = blocks.length;
		int retx = rdm.nextInt(x);
		
		int y = blocks[retx].length;
		int rety = rdm.nextInt(y);
		
		return blocks[retx][rety];
		
	}
	
	/**DBlock.ACTIVE if b==1, DBlock.EMPTY otherwise.*/
	private static DBlock toBlock(byte b)
	{
		switch(b)
		{
		case 1:
			return DBlock.ACTIVE;
		default:
			return DBlock.EMPTY;
		}
	}
}
