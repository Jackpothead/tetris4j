package code;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.*;

import static code.ProjectConstants.*;

/*TetrisPanel is the panel that contains the (main)
 * panels AKA. core. This also holds most of the objects
 * needed to render the game on a JDesktopPane.*/
public class TetrisPanel extends JPanel
{
	
	//---------------BEGIN PUBLIC VARIABLES---------------//
	
	/*Public reference to the TetrisEngine object.*/
	public TetrisEngine engine;
	
	/*Reference to the static SoundManager object.*/
	public SoundManager sound;
	
	/*Background image used for the game.*/
	public Image bg = null;
	
	/*Foreground image.*/
	public Image fg = null;
	
	/*Is it being controlled by a human or ai?*/
	public boolean isHumanControlled = true;
	
	/*AI object controlling the game.*/
	public TetrisAI controller = null;
	
	/*Public TetrisPanel constructor.*/
	public TetrisPanel()
	{
		//Initialize the TetrisEngine object.
		engine = new TetrisEngine(this);
		
		sound = SoundManager.getSoundManager();
		
		//This is the bg-image.
		try
		{
			bg = ImageIO.read
				(getResURL("/image/background.png"));
			fg = ImageIO.read
				(getResURL("/image/backlayer.png"));
			
			// Actually, the background is the actual background plus
			// the meta image.
			Image meta = ImageIO.read
				(getResURL("/image/metalayer.png"));
			Graphics g = bg.getGraphics();
			g.drawImage(meta, 0, 0, null);
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Cannot load image.");
		}
		
		//Animation loop. Updates every 40 milliseconds (25 fps).
		new Thread(){
			public void run()
			{
				while(true)
				{
					sleep_(40);
					repaint();
				}
			}
		}.start();
		
		//I should add a KeyManager for this.
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent ke)
			{	
				synchronized(engine)
				{
    				if(isHumanControlled)
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
    					if(controller != null && !controller.isrunning)
    						controller.send_ready();
    					if(engine.state==GameState.PAUSED)
    						engine.state = GameState.PLAYING;
    					else engine.state = GameState.PAUSED;
    				}
				}
			}
		});
		
		//Focus when clicked.
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me)
			{
				TetrisPanel.this.requestFocusInWindow();
			}});
		
		setFocusable(true);
		engine.state = GameState.PAUSED;
		
		sound.music(SoundManager.Sounds.TETRIS_THEME);
		
		if(!isHumanControlled)
			controller = new TetrisAI(this);
	}
	
	/*Paints this component, called with repaint().*/
	public void paintComponent(Graphics g)
	{
		//Necessary mostly because this is a JDesktopPane and
		//not a JPanel.
		super.paintComponent(g);
		
		//Draw: background, then main, then foreground.
		g.drawImage(bg, 0, 0, this);
		engine.draw(g);
		g.drawImage(fg, 0, 0, this);
	
	}
}
