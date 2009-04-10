package tetris.core;

import java.awt.*;

import javax.swing.*;
import static tetris.core.ProjectConstants.*;

/**<p>TetrisPanel is the panel that contains the (main)
 * <br>panels AKA. core.*/
public class TetrisPanel extends JPanel
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
		
		
		
		JFrame frame = new JFrame();
		
		if(fullscreen)
		{
			frame.setUndecorated(true);
		}
		
		frame.setTitle("JTetris");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		
		
		TetrisPanel tetris = new TetrisPanel();
		frame.add(tetris);
		
		if(fullscreen)
		{
			GraphicsDevice dev = null;
			try{
			dev =  GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			dev.setFullScreenWindow(frame);
			
			//800x600 fullscreen?
			dev.setDisplayMode(new DisplayMode
					(800,600,32,DisplayMode.REFRESH_RATE_UNKNOWN));
			}catch(Throwable t){
				//Exit fullscreen?
				dev.setFullScreenWindow(null);
				t.printStackTrace();
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
	
	int score = 240;//Score.
	int level = 0;//Level.
	int lines = 0;//Lines cleared.
	String mode = "CLASSIC";
	
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
		
		//used for a screenshot.
		blocks[9][15] = true;
		blocks[9][14] = true;
		blocks[8][15] = true;
		blocks[7][15] = true;
		blocks[4][14] = true;
		blocks[3][15] = true;
		blocks[4][15] = true;
		blocks[5][15] = true;
		blocks[3][8] = true;
		blocks[3][9] = true;
		blocks[2][9] = true;
		blocks[2][10] = true;
	}
	
	/**<p>Paints this component, called with repaint().*/
	public void paintComponent(Graphics g)
	{
		g.setColor(theme1);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		int cornerx = (getWidth() - bounds.width) / 2 + 120;
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
		
		g.setColor(Color.BLACK);
		
		int c1x = cornerx-300;
		int c1y = cornery;
		int c1w = 268;
		int c1h = bounds.height-3*squaredim;
		
		//body of options pane
		g.drawRect(c1x,c1y,c1w,c1h);
		
		int c1sixthY = c1h / 6;
		
		//block 1
		g.drawRect(c1x, c1y, c1w/2, 2*c1sixthY);
		
		//block 2
		g.drawRect(c1x+c1w/2,
				c1y, c1w/2, 2*c1sixthY);
		
		String score = "SCORE: " + this.score,
		level = "LEVEL: " + this.level,
		lines = "LINES: " + this.lines,
		mode = "MODE: " + this.mode;
		
		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,18));
		
		//score
		g.drawString(score,c1x+30, c1y+c1sixthY*2 + c1sixthY/2);
		
		//level
		g.drawString(level,c1x+30, c1y+c1sixthY*3 + c1sixthY/2);
		
		//lines
		g.drawString(lines,c1x+30, c1y+c1sixthY*4 + c1sixthY/2);
		
		//mode
		g.drawString(mode,c1x+30, c1y+c1sixthY*5 + c1sixthY/2);
		
		
		int c2x = c1x + 20;
		int c2y = c1y + c1h + 30;
		g.drawString("<BUTTON HERE>", c2x, c2y);
	}
}
