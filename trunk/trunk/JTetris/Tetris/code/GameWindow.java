package code;

import static code.ProjectConstants.*;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.midi.*;
import javax.swing.*;

import sun.awt.AWTAccessor;

import code.SoundManager.Sounds;


/**The game window.*/
public class GameWindow extends JFrame
{
	
	private GraphicsDevice dev;
	
	/**Create a window.*/
	public GameWindow()
	{
		super();
		setTitle("JTetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setResizable(false);
		
		
		TetrisPanel tframe = new TetrisPanel();
		
		tframe.setPreferredSize(new Dimension(800,600));
		setContentPane(tframe);
		
		try{
		dev =  GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		}catch(Throwable t){
			throw new RuntimeException("Getting screen device failed");
		}
		
		tframe.engine.startengine();
		setVisible(true);
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	
	/**Make this fullscreen.*/
	public void enterFullScreen()
	{
		try{
    		dev.setFullScreenWindow(this);
    		//800x600 fullscreen?
    		dev.setDisplayMode(new DisplayMode
    				(800,600,32,DisplayMode.REFRESH_RATE_UNKNOWN));
    		SwingUtilities.updateComponentTreeUI(this);
		
		}catch(Throwable t)
		{
			exitFullScreen();
			throw new RuntimeException("Failed fullscreen");
		}
	}
	
	/**Make this not fullscreen.*/
	public void exitFullScreen()
	{
		dev.setFullScreenWindow(null);
		SwingUtilities.updateComponentTreeUI(this);
	}
}
