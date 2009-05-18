package code;

import static code.ProjectConstants.formatStackTrace;

import java.awt.event.*;

import javax.swing.*;

/**Class that starts the app!*/
public class AppStart
{
	/**Errors go to console if true, otherwise go to GUI logger.*/
	public static final boolean REPORT_TO_CONSOLE = true;
	
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
						
						if(REPORT_TO_CONSOLE)
							e.printStackTrace();
						else
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
		
//		SwingUtilities.invokeLater(new Runnable(){
//			public void run()
//			{
				//Get the ball rolling!
				final GameWindow win = new GameWindow();
				
				new Thread(new Runnable()
				{
					public void run()
					{
						while(true)
						{
							try{Thread.sleep(4000);}catch(Exception e){}
							GameWindow.exitFullScreen(win);
							try{Thread.sleep(4000);}catch(Exception e){}
							GameWindow.exitFullScreen(win);
						}
					}
				}).start();
//			}
//		});
	}
}