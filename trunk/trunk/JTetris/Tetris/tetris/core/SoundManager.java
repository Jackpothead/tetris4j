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
			
			try
			{
				midiseq = MidiSystem.getSequencer();
			}
			/*
			catch (MidiUnavailableException e)
			{
				e.printStackTrace();
				System.out.println("Cannot initiate MIDI device..");
				try
				{
					midiseq = MidiSystem.getSequencer(false);
				} catch (Exception e1)
				{
					//Really screwed up now!
					e1.printStackTrace();
				}
			}*/
			catch(Exception e){e.printStackTrace();}
			
			try{
				midiseq = MidiSystem.getSequencer();
				midiseq.open();
				midiseq.setSequence(MidiSystem.getSequence(sound1));
				midiseq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
				midiseq.start();
			}catch(Exception e){e.printStackTrace();}
					
		}
	}
	
	public static enum Sounds{
		TETRIS_THEME;
	}
}
