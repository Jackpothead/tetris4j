package tetris.core;

import java.awt.*;

import javax.swing.*;

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
	public boolean[][] blocks;
	
	/**<p>Public TetrisPanel constructor.*/
	public TetrisPanel()
	{
		squaredim = 30;
		//Defaulted to be 10x16. May be changed.
		bounds = new Dimension(squaredim*10,squaredim*16);
		
		blocks = new boolean[16][10];
		
		//empty squares.
		for(boolean[] row : blocks)
			for(boolean cell : row)
				cell = false;
	}
	
	/**<p>Paints this component, called with repaint().*/
	public void paintComponent(Graphics g)
	{
		int cornerx = (getWidth() - bounds.width) / 2;
		int cornery = (getHeight() - bounds.height) / 2;
		
		//Yet to be implemented!
		g.drawRect(cornerx,cornery,bounds.width,bounds.height);
		
		
	}
}
