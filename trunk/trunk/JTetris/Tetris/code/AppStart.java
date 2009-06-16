package code;

import static code.ProjectConstants.formatStackTrace;

import java.awt.event.*;

import javax.swing.*;

/**Class that starts the app!*/
public class AppStart
{
	/**Errors go to console if true, otherwise go to GUI logger.*/
	public static final boolean REPORT_TO_CONSOLE = false;
	
	public static void main(String... args)
	{
		System.setErr(System.out);
		
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
						"Fatal exception in thread: " + t.getName()
						+ "\nException type: " + e.getClass().getName()
						+ "\nReason given: " + e.getMessage()
						+ "\n\n"+formatStackTrace(e.getStackTrace()));
						System.exit(1);
					}
				});
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				//Get the ball rolling!
				
				new Thread(new Runnable()
				{
					public void run()
					{
						GameWindow win = new GameWindow();
					}
				}).start();
			}
		});
	}
}
