package tetris.core;

import java.applet.*;
import java.io.*;

import javax.sound.midi.*;

import static tetris.core.ProjectConstants.*;

public class SoundManager
{
	private Sequencer midiseq;
	private InputStream tetheme;
	private AudioClip sx1, sx2, sx3, sx4, sx5;
	
	public SoundManager(){
		try
		{
			tetheme = getResStream("/sound/tetris.midi");
			sx1 = Applet.newAudioClip(getResURL("/sound/soundfall.wav"));
			sx2 = Applet.newAudioClip(getResURL("/sound/soundrotate.wav"));
			sx3 = Applet.newAudioClip(getResURL("/sound/soundtetris.wav"));
			sx4 = Applet.newAudioClip(getResURL("/sound/soundclear.wav"));
			sx5 = Applet.newAudioClip(getResURL("/sound/sounddie.wav"));
		} catch (Exception e)
		{
			e.printStackTrace();
		} 
	}
	
	public synchronized void sfx(Sounds s)
	{
		switch(s)
		{
		case FALL:
			sx1.play();
			break;
		case ROTATE:
			sx2.play();
			break;
		case TETRIS:
			sx3.play();
			break;
		case CLEAR:
			sx4.play();
			break;
		case DIE:
			sx5.play();
			break;
		default:
			throw new IllegalArgumentException();
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
			
			try{
				midiseq = MidiSystem.getSequencer();
				midiseq = MidiSystem.getSequencer();
				midiseq.open();
				midiseq.setSequence(MidiSystem.getSequence(tetheme));
				midiseq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
				midiseq.start();
			}catch(Exception e){e.printStackTrace();}
					
		}
		else throw new IllegalArgumentException();
	}
	
	public static enum Sounds{
		TETRIS_THEME, FALL, ROTATE, CLEAR, TETRIS, DIE;
	}
	
	private static AudioClip loadsound(String name)
	{
		try
		{
			if(new File(getResURL(name).getFile()).exists())
				return Applet.newAudioClip(getResURL(name));
			else return null;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
