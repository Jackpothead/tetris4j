package code;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;

import static code.ProjectConstants.*;

/**<p>TetrisPanel is the panel that contains the (main)
 * <br>panels AKA. core. This also holds most of the objects
 * <br>needed to render the game on a JDesktopPane.*/
public class TetrisPanel extends JDesktopPane
{
	
	//---------------BEGIN PUBLIC VARIABLES---------------//
	
	
	/**Size of Tetris window, in pixels.*/
	public Dimension bounds;
	
	/**Width and height of the grid, counted in number
	 * <br>of blocks.*/
	public int width=12, height=20;
	
	/**Dimensions (Width and height) of each square. Squares in
	 * <br>Tetris must be the same height and width.*/
	public int squaredim=25;
	
	/**Color of an empty block.*/
	public Color blockemptycolor;
	
	/**Color of a filled block.*/
	public Color blockfullcolor;
	
	/**Color of an active block. Is currently always the same
	 * <br>color as the full block.*/
	public Color blockactivecolor;
	
	/**DBlock array representation of the gamefield. Blocks are<br>
	 * counted X first starting from the top left: blocks[5][3]<br>
	 * would be a block 5 left and 3 down from (0,0).*/
	public Block[][] blocks;
	
	/**Score (UNUSED)*/
	public int score = 0;
	
	/**Level (UNUSED)*/
	public int level = 0;
	
	/**Lines cleared (UNUSED)*/
	public int lines = 0;
	
	/**Maximum time allowed per step in milliseconds.*/
	public int steptime = 550;
	
	/**Game mode (UNUSED)*/
	public String mode = "CLASSIC";
	
	/**Public reference to the TetrisEngine object.*/
	public TetrisEngine gameengine;
	
	/**Current state of the game (PLAYING, PAUSED, etc.)*/
	public GameState state;
	
	/**Reference to the static SoundManager object.*/
	public SoundManager sound;
	
	/**Background image used for the game.*/
	public Image bg = null;
	
	/**<p>Public TetrisPanel constructor.*/
	public TetrisPanel()
	{
		//Bounds changed to be thus:
		bounds = new Dimension(squaredim*width,squaredim*height);
		
		//(not so) awesome color choices.
		blockemptycolor = new Color(184,245,184,204);
		blockfullcolor = new Color(202,51,51,241);
		blockactivecolor = blockfullcolor;//for now.
		
		//Initialize a DBlock array and set all its contents
		// to DBlock.EMPTY.
		blocks = new Block[width][height];
		for(int t1 = 0;t1 < blocks.length;t1++)
		{
			for(int t2 = 0;t2 < blocks[t1].length;t2++)
			{
				blocks[t1][t2] = new Block(BlockState.EMPTY);
			}
		}
		
		//MAY CHANGE: Set the game to PLAYING asap.
		state = GameState.PLAYING;
		
		sound = SoundManager.getSoundManager();
		sound.music(SoundManager.Sounds.TETRIS_THEME);
		
		//This is the bg-image.
		try
		{
			bg = Toolkit.getDefaultToolkit()
			.createImage(getResURL("/image/backlayer.jpg"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//Animation loop. Updates every 20 milliseconds (50 fps).
		new Thread(){
			public void run()
			{
				while(state == GameState.PLAYING)
				{
					try{Thread.sleep(20);}catch(Throwable t){}
					repaint();
				}
			}
		}.start();
		
		//I should add a KeyManager for this.
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent ke)
			{
				if(state == GameState.PLAYING)
				{
    				if(ke.getKeyCode() == KeyEvent.VK_LEFT)
    					TetrisPanel.this.gameengine.keyleft();
    				if(ke.getKeyCode() == KeyEvent.VK_RIGHT)
    					TetrisPanel.this.gameengine.keyright();
    				if(ke.getKeyCode() == KeyEvent.VK_DOWN)
    					TetrisPanel.this.gameengine.keydown();
    				if(ke.getKeyCode() == KeyEvent.VK_UP
    					||ke.getKeyCode() == KeyEvent.VK_Z)
    					TetrisPanel.this.gameengine.keyrotate();
				}
			}
		});
		
		//Focus when clicked.
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me)
			{
				TetrisPanel.this.requestFocusInWindow();
			}});
		
		//Whatever it takes to get mouse focus in a JFrame -.-
		new Thread(){
			
    		public void run(){
    			while(!TetrisPanel.this.isFocusOwner())
    			{
    				TetrisPanel.this.requestFocusInWindow();
    			}
    		}
    			
		}.start();
		
		setFocusable(true);
		
		//Initialize the TetrisEngine object.
		gameengine = new TetrisEngine(this);
		
		for(int i = 0;i < blocks.length;i++)
		{
			for(int j = 19;j > 5;j--)
				blocks[i][j] = new Block(BlockState.FILLED);
		}
		
	}
	
	/**<p>Paints this component, called with repaint().*/
	public void paintComponent(Graphics g)
	{
		//Necessary mostly because this is a JDesktopPane and
		//not a JPanel.
		super.paintComponent(g);
		
		//Background.
		g.drawImage(bg, 0, 0, this);
		
		//The coordinates of the top left corner of the game board.
		int cornerx = (getWidth() - bounds.width) / 2;
		int cornery = (getHeight() - bounds.height) / 2;
		
		//Create a border;
		g.setColor(Color.BLACK);
		g.drawRect(cornerx-1,cornery-1,
				bounds.width+2,bounds.height+2);
		
		//Loop and draw all the blocks.
		for(int c1 = 0;c1 < blocks.length;c1++)
		{
		for(int c2 = 0;c2 < blocks[c1].length;c2++)
		{
			//Reset the color for each block:
			switch(blocks[c1][c2].state)
			{
			case FILLED:
				g.setColor(blockfullcolor);
				break;
			case EMPTY:
				g.setColor(blockemptycolor);
				break;
			case ACTIVE:
				g.setColor(blockactivecolor);
				break;
			default://Whaaht?
				break;
			}
			
			g.fillRect(cornerx+c1*squaredim,
					cornery+c2*squaredim, squaredim, squaredim);
			
			//Draw square borders.
			g.setColor(Color.DARK_GRAY);
			g.drawRect(cornerx+c1*squaredim,
				cornery+c2*squaredim, squaredim, squaredim);
		}
		}
	}
}
