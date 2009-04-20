package tetris.core;

import java.util.*;

import tetris.core.SoundManager.Sounds;
import static tetris.core.ProjectConstants.*;

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
        		{0,0,1,0},
    			{1,1,1,0},
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
	
	int stepcount = 0;//Hey best to have this aswell..
	
	
	/**Public constructor.
	 * @param p TetrisPanel.*/
	public TetrisEngine(TetrisPanel p)
	{
		//Initialize objects.
		tetris = p;
		rdm = new Random();
		
		//Start game thread.
		new Thread(){
			public void run()
			{
				while(true)
				{
					
					long timeelapsedsincelaststep = 
						System.currentTimeMillis() - laststep;
					
					try{
						//Safer than sleeping for more.
						Thread.sleep(20);
					}catch(Exception e){}
					
					//Break loop if game isn't even playing.
					if(!(tetris.state == GameState.PLAYING))
						continue;
					
					if(activeBlock == null)
					{
						randomBlock();
					}
					
					if(timeelapsedsincelaststep > tetris.steptime)
						step();
				}
			}
		}.start();
		
		randomBlock();
	}
	
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
	
	public synchronized void donecurrent()
	{	
		tetris.sound.sfx(Sounds.FALL);
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

	public synchronized void gameover()
	{
		pImportant("Game Over");
		tetris.sound.sfx(Sounds.DIE);
		tetris.state = GameState.STARTSCREEN;
	}

	/**Copies the position of the active block into<br>
	 * the abstract block grid. Returns false if a block<br>
	 * already exists under it, true otherwise.<br>*/
	public synchronized boolean copy()
	{
		try{
		int x = activeBlockX;
		int y = activeBlockY;
		byte[][] t = activeBlock;
		DBlock[][] buffer = tetris.blocks;
		
		if(t==null)return false;//Early NullPointerException failsafe
		
		//Check if any blocks already have a block under them.
		//If yes, immediately return.
		for(int i = 0;i < 4;i++)
		{
			for(int r = 0;r < 4;r++)
			{
					if(activeBlock[r][i] == 1
						&&buffer[x+i][y+r]==DBlock.FILLED)
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
				if(buffer[i][r] == DBlock.ACTIVE)
					buffer[i][r] = DBlock.EMPTY;
			}
		}
		
		//Then fill in with the new position.
		for(int i = 0;i < 4;i++)
		{
			for(int r = 0;r < 4;r++)
			{
				if(t[r][i]==1)
					buffer[x+i][y+r] = t[r][i]==1?
							DBlock.ACTIVE:DBlock.EMPTY;
			}
		}
		
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
		if(DEBUG)
			System.out.println("STEP: " + ++stepcount);
		laststep = System.currentTimeMillis();
		
		//move 1 down.
		activeBlockY++;
		
		if(!copy())
			donecurrent();
		
		checkforclears(0,null);
		
	}
	
	
	private synchronized void
		checkforclears(int alreadycleared, DBlock[][] b)
	{
		if(b==null)
			b = tetris.blocks;
		int whichline = -1;
		int old = alreadycleared;
		
		ML:
		for(int i = b[0].length-1;i>=0;i--)
		{
			for(int y = 0;y < b.length;y++)
			{
				if(b[y][i]!=DBlock.FILLED)continue ML;
			}
			
			alreadycleared++;
			whichline = i;
			break ML;
		}
		
		if(alreadycleared>old)
		{
			for(int i = whichline;i>0;i--)
			{
				for(int y = 0;y < b.length;y++)
				{
					b[y][i] = b[y][i-1];
				}
			}
			
			checkforclears(alreadycleared,b);
		}
		else if(alreadycleared>0)
		{
			pImportant("Cleared: " + alreadycleared + " line(s).");
			if(alreadycleared==4)tetris.sound.sfx(Sounds.TETRIS);
			else tetris.sound.sfx(Sounds.TETRIS);
		}
		
		tetris.blocks = b;
	}
	
	
	/**Generates a random block , in a random rotation.*/
	private synchronized void randomBlock()
	{
		int x = blockdef.length;
		int retx = rdm.nextInt(x);
		
		int y = blockdef[retx].length;
		int rety = rdm.nextInt(y);
		
		activeBlockType=retx;
		activeBlockRot=rety;
		
		activeBlock = blockdef[retx][rety];
		
		activeBlockX = tetris.width/2 -2;
		activeBlockY = -3;
		
		do{
			if(activeBlockY > 0)
			{
				gameover();
				break;
			}
			activeBlockY++;
		}while(!copy());
	}
}
