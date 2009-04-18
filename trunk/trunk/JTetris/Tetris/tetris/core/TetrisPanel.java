package tetris.core;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

import javax.swing.*;

import static tetris.core.ProjectConstants.*;

/**<p>TetrisPanel is the panel that contains the (main)
 * <br>panels AKA. core.*/
public class TetrisPanel extends JDesktopPane
{
	
	public Dimension bounds;//Size of Tetris window.
	
	public int squaredim;//Dimensions of individual square
						//(must be same width and height).
	
	public Color blockemptycolor;//Color of a block that's empty.
	
	public Color blockfullcolor;//Color of a block that's filled.
	
	public Color blockactivecolor;
	
	public Color theme1;//background for now.
	
	public DBlock[][] blocks;//Boolean representation of the blocks,
					//counted X first and starting from the top
					//left corner. blocks[5][3] would be the block
					//5 left of (0,0) and 3 down.
	
	public int score = 0;//Score.
	public int level = 0;//Level.
	public int lines = 0;//Lines cleared.
	public int steptime = 550;//Max milliseconds per step.
	public String mode = "CLASSIC";
	
	public TetrisEngine gameengine;
	public GameState state;
	public SoundManager sfx;
	
	/**<p>Public TetrisPanel constructor.*/
	public TetrisPanel()
	{
		squaredim = 30;
		//Defaulted to be 10x16. May be changed.
		bounds = new Dimension(squaredim*10,squaredim*16);
		
		blocks = new DBlock[10][16];
		
		//(not so) awesome color choices.
		blockemptycolor = new Color(255,204,153);
		blockfullcolor = new Color(143,31,255);
		//blockactivecolor = new Color(31,143,255);
		blockactivecolor = blockfullcolor;//for now
		theme1 = new Color(153,153,255);
		
		//empty squares.
		for(int t1 = 0;t1 < blocks.length;t1++)
		{
			for(int t2 = 0;t2 < blocks[t1].length;t2++)
			{
				blocks[t1][t2] = DBlock.EMPTY;
			}
		}
		
		state = GameState.PLAYING;
		
		gameengine = new TetrisEngine(this);
		
		sfx = new SoundManager();
		sfx.music(SoundManager.Sounds.TETRIS_THEME);
		
		//Animation loop.
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
		
		//Whatever it takes to get mouse focus in a JPanel -.-
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me)
			{
				TetrisPanel.this.requestFocusInWindow();
			}
		});
		
		setFocusable(true);
		requestFocusInWindow();
		
	}
	
	/**<p>Paints this component, called with repaint().*/
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.setColor(theme1);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		int cornerx = (getWidth() - bounds.width) / 2;
		int cornery = (getHeight() - bounds.height) / 2;
		
		if(DEBUG)
		//Create a border;
		g.drawRect(cornerx,cornery,bounds.width,bounds.height);
		else
			g.drawRect(cornerx-1,cornery-1,
				bounds.width+1,bounds.height+1);
		
		//Loop and draw all the blocks.
		for(int c1 = 0;c1 < blocks.length;c1++)
		{
		for(int c2 = 0;c2 < blocks[c1].length;c2++)
		{
			if(blocks[c1][c2]==DBlock.FILLED)
			{
				g.setColor(blockfullcolor);
			}
			if(blocks[c1][c2]==DBlock.ACTIVE)
			{
				g.setColor(blockactivecolor);
			}
			if(blocks[c1][c2]==DBlock.EMPTY)
			{
				g.setColor(blockemptycolor);
			}
			g.fillRect(cornerx+c1*squaredim,
					cornery+c2*squaredim, squaredim, squaredim);
			
			if(DEBUG)
			{
				//Show square info.
				g.setColor(Color.BLACK);
				g.drawString(c1 + ":" + c2,
						cornerx+c1*squaredim+2,
						cornery+c2*squaredim+10);
			}
			
			if(DEBUG)
			{
				//explicitly draw squares.
				g.setColor(Color.BLACK);
				g.drawRect(cornerx+c1*squaredim,
					cornery+c2*squaredim, squaredim, squaredim);
			}
		}
		}
	}
}
