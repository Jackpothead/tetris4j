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
public class TetrisPanel extends JPanel
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
	
	/**Dimensions of the squares of the next block as drawn.
	 * See squaredim.*/
	public int nextblockdim = 15;
	
	/**DBlock array representation of the gamefield. Blocks are<br>
	 * counted X first starting from the top left: blocks[5][3]<br>
	 * would be a block 5 left and 3 down from (0,0).*/
	public volatile Block[][] blocks;
	
	/**Score*/
	public int score = -1;
	
	/**Level (UNUSED)*/
	public int level = 0;
	
	/**Lines cleared (UNUSED)*/
	public int lines = 0;
	
	/**Maximum time allowed per step in milliseconds.*/
	public int steptime = 350;
	
	/**Time used to fade block that have been cleared.*/
	public int fadetime = 300;
	
	/**Game mode (UNUSED)*/
	public String mode = "CLASSIC";
	
	/**Public reference to the TetrisEngine object.*/
	public TetrisEngine engine;
	
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
			throw new RuntimeException("Cannot load image.");
		}
		
		//Animation loop. Updates every 20 milliseconds (50 fps).
		new Thread(){
			public void run()
			{
				while(true)
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
    					TetrisPanel.this.engine.keyleft();
    				if(ke.getKeyCode() == KeyEvent.VK_RIGHT)
    					TetrisPanel.this.engine.keyright();
    				if(ke.getKeyCode() == KeyEvent.VK_DOWN)
    					TetrisPanel.this.engine.keydown();
    				if(ke.getKeyCode() == KeyEvent.VK_SPACE)
    					TetrisPanel.this.engine.keyslam();
    				if(ke.getKeyCode() == KeyEvent.VK_UP
    					||ke.getKeyCode() == KeyEvent.VK_Z)
    					TetrisPanel.this.engine.keyrotate();
				}
				
				//Pause button!
				if(ke.getKeyCode() == KeyEvent.VK_P)
				{
					if(state==GameState.PLAYING)
						state = GameState.PAUSED;
					else if(state==GameState.PAUSED)
						state = GameState.PLAYING;
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
		engine = new TetrisEngine(this);
		
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
		int mainx = (getWidth() - bounds.width) / 2 + 50;
		int mainy = (getHeight() - bounds.height) / 2;
		
		//Create a border;
		g.setColor(Color.BLACK);
		g.drawRect(mainx-1,mainy-1,
				bounds.width+2,bounds.height+2);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.MONOSPACED,Font.BOLD,36));
		g.drawString(addLeadingZeroes(score,6), 145, 100);
		
		//Loop and draw all the blocks.
		for(int c1 = 0;c1 < blocks.length;c1++)
		{
    		for(int c2 = 0;c2 < blocks[c1].length;c2++)
    		{
    			// Just in case block's null, it doesn't draw as black.
    			g.setColor(Block.emptycolor);
    			g.setColor(blocks[c1][c2].getColor());
    			
    			g.fillRect(mainx+c1*squaredim,
    					mainy+c2*squaredim, squaredim, squaredim);
    			
    			//Draw square borders.
                g.setColor(new Color(32,104,183));
                g.drawRect(mainx+c1*squaredim,
                        mainy+c2*squaredim, squaredim, squaredim);
    			
    		}
		}
		
		int nextx = 100;
		int nexty = 150;
		
		//Less typing.
		Block[][] nextb = engine.nextblock.array;
		
		//Loop and draw next block.
		for(int c1 = 0;c1 < nextb.length;c1++)
		{
			for(int c2 = 0;c2 < nextb[c1].length;c2++)
			{
				Color c = nextb[c2][c1].getColor();
				
				if(!c.equals(Block.emptycolor))
				{
					g.setColor(c);
				
					g.fillRect(nextx+c1*nextblockdim,
    					nexty+c2*nextblockdim, nextblockdim, nextblockdim);
				}
				
				//The way it works, this often looks better
				//without square borders.
			}
		}
		
		
		if(state == GameState.PAUSED)
		{
    		g.setColor(Color.RED);
    		g.setFont(new Font(Font.SERIF,Font.BOLD,16));
    		String pausestring = "Paused, press P to continue";
    		g.drawString(pausestring, 
    				(getWidth() - g.getFontMetrics().stringWidth(pausestring))
    				/ 2 + 50,300);
		}
	}
}
