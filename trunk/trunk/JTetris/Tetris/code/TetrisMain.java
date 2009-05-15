package code;

import static code.ProjectConstants.*;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.midi.*;
import javax.swing.*;

import code.SoundManager.Sounds;


// GOD.. I'm starting to HATE Subversive..
public class TetrisMain
{
	
	//Main, for testing purposes.
	public static void main(String... args)
	{
		try{
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}catch(Throwable t){}
		
		//Better exception catching.
		Thread.setDefaultUncaughtExceptionHandler(
				new Thread.UncaughtExceptionHandler(){

					public void uncaughtException(Thread t, Throwable e)
					{
						JOptionPane.showMessageDialog(null,
	"Sarah got stuck in a tree while " +
	"Wen li was being pissed off by " +//Don't ask.
	"Jason\nand the principal threw an exception.\n\n"+
						"Fatal exception in thread: " + t.getName()
						+ "\nException type: " + e.getClass().getName()
						+ "\nReason given: " + e.getMessage()
						+ "\n\n"+formatStackTrace(e.getStackTrace()));
						System.exit(1);
					}
				});
		
		//Bugfix.
		SwingUtilities.invokeLater(new Runnable(){
		public void run(){
		
		JFrame window = new JFrame();
		
		if(STARTFS)
		{
			window.setUndecorated(true);
		}
		
		window.setTitle("JTetris");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800, 600);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		
		
		TetrisPanel tframe = new TetrisPanel();
		
		tframe.setPreferredSize(new Dimension(800,600));
		window.setContentPane(tframe);
		
		if(STARTFS)
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
		
		tframe.engine.startengine();
		window.setVisible(true);
		SwingUtilities.updateComponentTreeUI(window);
		
		}});
	}
}
