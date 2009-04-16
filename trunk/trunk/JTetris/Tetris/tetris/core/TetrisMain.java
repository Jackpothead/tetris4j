package tetris.core;

import static tetris.core.ProjectConstants.DEBUG;

import java.awt.*;
import java.io.IOException;

import javax.sound.midi.*;
import javax.swing.*;

public class TetrisMain
{
	//Main, for testing purposes.
	public static void main(String... args)
	{
		try{
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}catch(Throwable t){}
		
		
		//Guess what this does!
		boolean fullscreen = true;
		
		
		
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
		}
		SwingUtilities.updateComponentTreeUI(window);
		
		Sequence sq;
		
		try
		{
			sq = MidiSystem.getSequence(
					TetrisMain.class.getClass()
					.getResourceAsStream("/sound/tetris.midi"));
			
			Sequencer sqncr = MidiSystem.getSequencer();
			sqncr.setSequence(sq);
			sqncr.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			sqncr.open();
			sqncr.start();
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
