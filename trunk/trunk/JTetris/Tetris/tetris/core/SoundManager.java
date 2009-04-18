package tetris.core;

import java.applet.*;
import java.io.*;

import javax.sound.midi.*;

import static tetris.core.ProjectConstants.*;

/**This class loads, plays, and manages sound effects and
 * <br>music for Tetris4j. The sound URL's are hardcoded
 * <br>into this class and is loaded statically at runtime.*/
public class SoundManager
{
	
	/**This represents the list of sounds available.*/
	public static enum Sounds{
		// sound/tetris.midi
		TETRIS_THEME, 
		
		// sound/soundfall.wav
		FALL,
		
		// sound/soundrotate.wav
		ROTATE,
		
		// sound/soundclear.wav
		CLEAR, 
		
		// sound/soundtetris.wav
		TETRIS, //Need to replace this with a real sound.
		
		// sound/sounddie.wav
		DIE;	//Need to replace this with a real sound.
	}
	
	
	private Sequencer midiseq; //Midi sequencer, plays the music.
	
	private InputStream tetheme; //Tetris theme (midi-inputstream).
	
	private AudioClip sx1, sx2, sx3, sx4, sx5; //The collection of
									//sound effects used.
	
	private static SoundManager soundmanager = null;
									//Reference of the SoundManager.
	
	/**Since this class locks certain system resources, it's
	 * <br>best to only have one instance of this class. If an
	 * <br>instance of SoundManager already exists, this replaces
	 * <br>that with a new instance.*/
	public static SoundManager getSoundManager()
	{
		soundmanager = new SoundManager();
		return soundmanager;
	}
	
	//private initializer method.
	private SoundManager(){
		try
		{
			tetheme = getResStream("/sound/tetris.midi");
			sx1 = loadsound("/sound/soundfall.wav");
			sx2 = loadsound("/sound/soundrotate.wav");
			sx3 = loadsound("/sound/soundtetris.wav");
			sx4 = loadsound("/sound/soundclear.wav");
			sx5 = loadsound("/sound/sounddie.wav");
		} catch (Exception e)
		{
			e.printStackTrace();
		} 
	}
	
	/**Plays a sound. Sounds should be short because once this
	 * <br>is called again, the previous sound teminates and
	 * <br>the new sound starts.*/
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
	
	/**Plays a music track. Currently the only track
	 * <br>is the default MIDI track (theme song).*/
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
				//Sometimes throws MidiUnavailableException.
				midiseq.setSequence(MidiSystem.getSequence(tetheme));
				midiseq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
				midiseq.start();
			}catch(Exception e){e.printStackTrace();}
					
		}
		else throw new IllegalArgumentException();
	}
	
	//returns an AudioClip from a String filename.
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
