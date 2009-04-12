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
        		{1,1,1,0},
        		{0,1,0,0},
        		{0,0,0,0}
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
	
	byte[][] activeBlockType;
	int activeBlockX;
	int activeBlockY;
	
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
					
					if(timeelapsedsincelaststep > tetris.steptime)
						step();
					
					try{
						Thread.sleep(50);//Safer than sleeping for less.
					}catch(Exception e){}
				}
			}
		}.start();
	}
	
	public synchronized void actionright()
	{
		
	}
	
	public synchronized void actionleft()
	{
		
	}
	
	public synchronized void actiondown()
	{
		step();
	}
	
	public synchronized void actionrotate()
	{
		
	}
	
	/**Steps into the next phase if possible.*/
	private void step()
	{
		if(DEBUG)
			System.out.println("STEP");
		laststep = System.currentTimeMillis();
		
		//move 1 down.
		activeBlockY++;
		
		copy();
		
	}
	
	/**Rotates the specified block clockwise, by array lookup.
	 * @param blocktype Which block rotated
	 * @param rotation Current rotation
	 * @throws IllegalArgumentException blocktype or<br>
	 * rotation out of bounds.*/
	public static byte[][] rotate(int blocktype, int rotation)
	{
		if(blocktype >= blocks.length)
			throw new IllegalArgumentException();
		
		int numrotations = blocks[blocktype].length;
		if(rotation >= numrotations)
			throw new IllegalArgumentException();
		
		if(rotation == numrotations - 1)
			return blocks[blocktype][0];
		
		else return blocks[blocktype][rotation+1];
	}
	
	/**Copies the position of the active block into
	 * the abstract block grid.*/
	public void copy()
	{
		int x = activeBlockX;
		int y = activeBlockY;
		byte[][] t = activeBlockType;
		
		//LOL i'm not even sure how this works. Guess and check ftw.
		for(int i = 0;i < 4;i++)
		{
			for(int r = 0;r < 4;r++)
				tetris.blocks[x+i][y+r] = toBool(t[r][i]);
		}
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
	
	/**True if b==1, false otherwise.*/
	private static boolean toBool(byte b)
	{
		switch(b)
		{
		case 1:
			return true;
		default:
			return false;
		}
	}
}
