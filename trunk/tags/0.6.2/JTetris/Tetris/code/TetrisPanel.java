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
	
	/**Public reference to the TetrisEngine object.*/
	public TetrisEngine engine;
	
	/**Reference to the static SoundManager object.*/
	public SoundManager sound;
	
	/**Background image used for the game.*/
	public Image bg = null;
	
	/**<p>Public TetrisPanel constructor.*/
	public TetrisPanel()
	{
		//Initialize the TetrisEngine object.
		engine = new TetrisEngine(this);
		
		//MAY CHANGE: Set the game to PLAYING asap.
		engine.state = GameState.PLAYING;
		
		sound = SoundManager.getSoundManager();
		
		//This is the bg-image.
		try
		{
			bg = Toolkit.getDefaultToolkit()
			.getImage(getResURL("/image/backlayer.jpg"));
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
				if(engine.state == GameState.PLAYING)
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
				if(ke.getKeyCode() == KeyEvent.VK_SHIFT)
				{
					if(engine.state==GameState.PAUSED)
						engine.state = GameState.PLAYING;
					else engine.state = GameState.PAUSED;
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
		engine.state = GameState.PAUSED;
		
		sound.music(SoundManager.Sounds.TETRIS_THEME);
		
	}
	
	/**<p>Paints this component, called with repaint().*/
	public void paintComponent(Graphics g)
	{
		//Necessary mostly because this is a JDesktopPane and
		//not a JPanel.
		super.paintComponent(g);
		
		engine.draw(g, bg);
	
	}
}