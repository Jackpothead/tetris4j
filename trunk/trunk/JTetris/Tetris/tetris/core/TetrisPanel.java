package tetris.core;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import static tetris.core.ProjectConstants.*;

/**<p>TetrisPanel is the panel that contains the (main)
 * <br>panels AKA. core.*/
public class TetrisPanel extends JDesktopPane
{
	//Main, for testing purposes.
	public static void main(String... args)
	{
		try{
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}catch(Throwable t){}
		
		
		//Guess what this does!
		boolean fullscreen = false;
		
		
		
		JFrame window = new JFrame();
		
		if(fullscreen)
		{
			window.setUndecorated(true);
		}
		
		window.setTitle("JTetris");
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800, 600);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		
		
		TetrisPanel tframe = new TetrisPanel();
		
		tframe.setPreferredSize(new Dimension(800,600));
		window.setContentPane(tframe);
		
		if(fullscreen)
		{
			GraphicsDevice dev = null;
			try{
			dev =  GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			dev.setFullScreenWindow(window);
			
			//800x600 fullscreen?
			dev.setDisplayMode(new DisplayMode
					(800,600,32,DisplayMode.REFRESH_RATE_UNKNOWN));
			}catch(Throwable t){
				//Exit fullscreen?
				dev.setFullScreenWindow(null);
				t.printStackTrace();
			}
			try{Thread.sleep(5000);}catch(Exception e){}
			
			if(DEBUG)
			{
    			dev.setFullScreenWindow(null);
    			System.exit(0);
			}
		}
	}
	
	public Dimension bounds;//Size of Tetris window.
	
	public int squaredim;//Dimensions of individual square
						//(must be same width and height).
	
	public Color blockemptycolor;//Color of a block that's empty.
	
	public Color blockfullcolor;//Color of a block that's filled.
	
	public Color theme1;//background for now.
	
	public boolean[][] blocks;//Boolean representation of the blocks,
					//counted X first and starting from the top
					//left corner. blocks[5][3] would be the block
					//5 left of (0,0) and 3 down.
	
	public int score = 0;//Score.
	public int level = 0;//Level.
	public int lines = 0;//Lines cleared.
	public int steptime = 1000;//Max milliseconds per step.
	public String mode = "CLASSIC";
	
	public TetrisEngine gameengine;
	public GameState state;
	
	/**<p>Public TetrisPanel constructor.*/
	public TetrisPanel()
	{
		squaredim = 30;
		//Defaulted to be 10x16. May be changed.
		bounds = new Dimension(squaredim*10,squaredim*16);
		
		blocks = new boolean[10][16];
		
		//(not so) awesome color choices.
		blockemptycolor = new Color(255,204,153);
		blockfullcolor = new Color(143,31,255);
		theme1 = new Color(153,153,255);
		
		//empty squares.
		for(boolean[] row : blocks)
			for(boolean cell : row)
				cell = false;
		
		state = GameState.PLAYING;
		
		gameengine = new TetrisEngine(this);
		
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
				if(ke.getKeyCode() == KeyEvent.VK_LEFT)
					TetrisPanel.this.gameengine.actionleft();
				if(ke.getKeyCode() == KeyEvent.VK_RIGHT)
					TetrisPanel.this.gameengine.actionright();
				if(ke.getKeyCode() == KeyEvent.VK_DOWN)
					TetrisPanel.this.gameengine.actiondown();
				if(ke.getKeyCode() == KeyEvent.VK_UP
					||ke.getKeyCode() == KeyEvent.VK_Z)
					TetrisPanel.this.gameengine.actionrotate();
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
		
		gameengine.activeBlockType = TetrisEngine.blocks[6][1];
		gameengine.activeBlockX=1;
		gameengine.activeBlockY=0;
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
			if(blocks[c1][c2]==true)
			{
				g.setColor(blockfullcolor);
			}
			if(blocks[c1][c2]==false)
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
