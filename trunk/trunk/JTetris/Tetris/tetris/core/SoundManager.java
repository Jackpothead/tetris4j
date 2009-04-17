package tetris.core;

import java.io.*;

import javax.sound.midi.*;

import static tetris.core.ProjectConstants.*;

public class SoundManager
{
	private Sequencer midiseq;
	private InputStream sound1;
	
	public SoundManager(){
		try
		{
			sound1 = getResStream("/sound/tetris.midi");
			sound1.mark(0);
		} catch (Exception e)
		{
			e.printStackTrace();
		} 
	}
	
	public synchronized void music(Sounds s)
	{
		if(s==null)
		{
			midiseq.close();
			return;
		}
		
		else if(s == Sounds.TETRIS_THEME)
		{
			new Thread(){
				public void run(){
    		
    		while(true)
			{
				if(midiseq==null || !midiseq.isRunning())
				{
					try
					{
						if(midiseq!=null)
							midiseq.close();
						midiseq = MidiSystem.getSequencer();
						
						sound1.reset();//Oooops.
						
						midiseq.setSequence(sound1);
						midiseq.open();
						midiseq.start();
					}
					catch (MidiUnavailableException e)
		    		{
		    			System.out.println("Cannot initiate MIDI device..");
		    			try
		    			{
		    				midiseq = MidiSystem.getSequencer(false);
		    			} catch (Exception e1)
		    			{
		    				//Really screwed up now!
		    				e1.printStackTrace();
		    			}
		    		}
					
					catch (/*AnyOther*/Exception e)
					{
						e.printStackTrace();
					}
				}
				
				try
				{
					Thread.sleep(1000);
				} catch (Exception e)
				{
				}
			}
    		
			}}.start();
		}
	}
	
	public static enum Sounds{
		TETRIS_THEME;
	}
}
