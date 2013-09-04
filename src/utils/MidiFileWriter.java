package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

/**
* Utility Class used write midi files.
* 
* @author Matteo Poggi
* 
*/
public class MidiFileWriter {
	private int velocity;
	private int speed;
	private HashMap<String, Integer> notes;
	private Synthesizer synthesizer;
	private Soundbank soundbank;
	private Instrument instruments[];

	

	 static final int header[] = new int[]
		     {
		     0x4d, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06,
		     0x00, 0x00, // single track format
		     0x00, 0x01, // track
		     0x00, 0x10, // 16 ticks / quaver
		     0x4d, 0x54, 0x72, 0x6B
		     };

		  // Standard footer
		  static final int footer[] = new int[]
		     {
		     0x01, 0xFF, 0x2F, 0x00
		     };

		  // MIDI event to set time (pattern)	
		  static final int tempoEvent[] = new int[]
		     {
		     0x00, 0xFF, 0x51, 0x03,	//	Valori standard
		     0x09, 0x89, 0x68 // variable (in this example, 4M usec for a semibreve duration)
		     };

		  // MIDI event to set key signature (needed for editing applications)
		  static final int keySigEvent[] = new int[]
		     {
		     0x00, 0xFF, 0x59, 0x02,
		     0x00, // C
		     0x00  // major
		     };


		  // MIDI event to set time signature (needed for editing applications)
		  static final int timeSigEvent[] = new int[]
		     {
		     0x00, 0xFF, 0x58, 0x04,
		     0x04, // num
		     0x02, // den (2==4, power of 2)
		     0x30, // ticks per click (unused)
		     0x08  // 32th Note for quaver
		     };

		  // Events set to be written
		  protected Vector<int[]> playEvents;

		  
		  /**
			* Constructor. 
			* @throws MidiUnavailableException 
			* @param velocity
			* 		velocity value for the notes.
			* @author Matteo Poggi
			* 
			*/ 
		  public MidiFileWriter(int velocity) throws MidiUnavailableException
		  {
		    playEvents = new Vector<int[]>();
		    this.notes=new HashMap<String,Integer>();
			this.notes.put("DO",60);
			this.notes.put("RE",62);
			this.notes.put("MI",64);
			this.notes.put("FA",65);
			this.notes.put("SOL",67);
			this.notes.put("LA",69);
			this.notes.put("SI",71);
			this.synthesizer= MidiSystem.getSynthesizer();
			this.synthesizer.open();
			this.soundbank= this.synthesizer.getDefaultSoundbank();
			this.instruments= this.soundbank.getInstruments();
			this.synthesizer.loadAllInstruments(this.soundbank);
			this.velocity=velocity;
		  }


		  /**
			* Write the midi file. 
			* @param fileName
			* 		name of the file to be written.
			* @author Matteo Poggi
			* 
			*/ 
		  public void writeToFile (String filename)
		    throws IOException
		  {
		    FileOutputStream fos = new FileOutputStream (filename);
		    
		    int tempoEvent[] = this.calculateTempoEvent();
		    
		    fos.write (intArrayToByteArray (header));

		    // Calculate size of datas to be written
		    // INCLUDE footer, not INCLUDE track header

		    int size = tempoEvent.length + keySigEvent.length + timeSigEvent.length
		      + footer.length;

		    for (int i = 0; i < playEvents.size(); i++)
		      size += playEvents.elementAt(i).length;

		    // Write datas (big endian)
		    // Up to 64k datas.
		    // (Enought for Penta project) 
		    int high = size / 256;
		    int low = size - (high * 256);
		    fos.write ((byte) 0);
		    fos.write ((byte) 0);
		    fos.write ((byte) high);
		    fos.write ((byte) low);


		    // Write meta-datas
		    fos.write (intArrayToByteArray (tempoEvent));
		    fos.write (intArrayToByteArray (keySigEvent));
		    fos.write (intArrayToByteArray (timeSigEvent));

		    // Write single notes
		    for (int i = 0; i < playEvents.size(); i++)
		    {
		      fos.write (intArrayToByteArray (playEvents.elementAt(i)));
		    }

		    // write footer and close file
		    fos.write (intArrayToByteArray (footer));
		    fos.close();
		  }

		  /**
			* Calculate timing based on speed value. 
			* @author Matteo Poggi
			* 
			*/ 
		  private int[] calculateTempoEvent() {
			  String hex= Integer.toHexString(this.speed*1000/4);
			  if(hex.length()%2!=0) hex="0"+hex;
			  ArrayList<Integer> list= new ArrayList<Integer>();
			  list.add(0x00);
			  list.add(0xFF);
			  list.add(0x51);
			  list.add(0x03);
			  for(int i=0;i< hex.length()/2;i++)	list.add(Integer.parseInt(hex.substring(i*2, i*2+2),16));	
			  int[] result = new int[list.size()];
			  for(int i=0;i<list.size();i++) result[i]=list.get(i); 
			  return result;

		}


		  /**
			* Connversion from int array to byte array. 
			* @param ints
			* 		array to be converted.
			* @author Matteo Poggi
			* 
			*/ 
		  protected static byte[] intArrayToByteArray (int[] ints)
		  {
		    int l = ints.length;
		    byte[] out = new byte[ints.length];
		    for (int i = 0; i < l; i++)
		    {
		      out[i] = (byte) ints[i];
		    }
		    return out;
		  }


		  /**
			* Event rappresenting the begin of a note's sound. 
			* @param delta
			* 		delay.
			* @param note
			* 		note to be played. 
			* @author Matteo Poggi
			* 
			*/ 
		  public void noteOn (int delta,  String note)
		  {
			  int noteValue;
			  int semitune=0;
			  int scale=0;
			  if(note.contains("^")){
				  scale=12;
				  note=note.substring(0, note.length()-1);
			  }
			  if(note.contains("!")){
				  semitune=-1;
				  note=note.substring(0, note.length()-1);
			  }
			  if(note.contains("#")){
				  semitune=1;
				  note=note.substring(0, note.length()-1);
			  }
			  if(!note.equals("SILENCE")) noteValue=this.notes.get(note.replace("#", "").replace("!", "").replace("^", ""))+semitune+scale;
			  else noteValue=-1;
			  int[] data = new int[4];
			  data[0] = delta;
			  data[1] = 0x90;
			  data[2] = noteValue;
			  data[3] = velocity;
		  playEvents.add (data);
		  }


		  /**
			* Event rapresenting the end of a note's sound.
			* @param delta
			* 		delay.
			* @param note
			* 		note to be shut down. 
			* @author Matteo Poggi
			* 
			*/ 
		  public void noteOff (int delta, String note)
		  {
			  int noteValue;
			  int semitune=0;
			  int scale=0;
			  if(note.contains("^")){
				  scale=12;
				  note=note.substring(0, note.length()-1);
			  }
			  if(note.contains("!")){
				  semitune=-1;
				  note=note.substring(0, note.length()-1);
			  }
			  if(note.contains("#")){
				  semitune=1;
				  note=note.substring(0, note.length()-1);
			  }
			  if(!note.equals("SILENCE")) noteValue=this.notes.get(note.replace("#", "").replace("!", "").replace("^", ""))+semitune+scale;
			  else noteValue=-1;
			  int[] data = new int[4];
			  data[0] = delta;
			  data[1] = 0x80;
			  data[2] = noteValue;
			  data[3] = 0;
		  playEvents.add (data);
		  }


		  /**
			* Program changing, used to change instrument.
			* @param prog
			* 		program to be set.
			* @author Matteo Poggi
			* 
			*/ 
		  public void progChange (int prog)
		  {
		  int[] data = new int[3];
		  data[0] = 0;
		  data[1] = 0xC0;
		  data[2] = prog;
		  playEvents.add (data);
		  }


		  /**
			* Single note played, based on methods noteOn and noteOff.
			* @param duration
			* 		duration of the sound (delay between invocation of noteOn and noteOff).
			* @param note
			* 		note to be shut down. 
			* @author Matteo Poggi
			* 
			*/ 
		  public void noteOnOffNow (int duration, String note)
		  {
			  noteOn (0, note);
			  noteOff (duration*64/this.speed, note);
		  }


		  /**
			* Getter method, return velocity value.
			* @author Matteo Poggi
			* @return
			* 		velocity value
			*/ 
		public int getVelocity() {
			return velocity;
		}

		 /**
		* Setter method for velocity value.
		* @param velocity
		* 		value to be set
		* @author Matteo Poggi
		* 
		*/ 
		public void setVelocity(int velocity) {
			if(velocity>127) velocity=127;
			this.velocity = velocity;
		}

		/**
		* Getter method, return synthesizer object.
		* @author Matteo Poggi
		* @return	
		* 		synthesizer object
		*/ 
		public Synthesizer getSynthesizer() {
			return synthesizer;
		}

		 /**
		* Setter method for synthesizer object.
		* @param synthesizer
		* 		object to be set
		* @author Matteo Poggi
		* 
		*/ 
		public void setSynthesizer(Synthesizer synthesizer) {
			this.synthesizer = synthesizer;
		}

		/**
		* Getter method, return array of instruments.
		* @author Matteo Poggi
		* @return
		* 			array of instruments available
		*/ 
		public Instrument[] getInstruments() {
			return instruments;
		}

		 /**
		* Setter method for instruments array.
		* @param instruments
		* 		array to be set
		* @author Matteo Poggi
		* 
		*/ 
		public void setInstruments(Instrument instruments[]) {
			this.instruments = instruments;
		}

		/**
		* Getter method, return soundbank object.
		* @author Matteo Poggi
		* @return
		* 			soundbank object
		*/ 
		public Soundbank getSoundbank() {
			return soundbank;
		}

		 /**
		* Setter method for soundbank object.
		* @param soundbank
		* 		soundbank to be set
		* @author Matteo Poggi
		* 
		*/ 
		public void setSoundbank(Soundbank soundbank) {
			this.soundbank = soundbank;
		}

		/**
		* Getter method, return speed value.
		* @author Matteo Poggi
		* @return
		* 			speed value
		*/ 
		public int getSpeed() {
			return speed;
		}

		 /**
		* Setter method for speed value.
		* @param speed
		* 		value to be set
		* @author Matteo Poggi
		* 
		*/ 
		public void setSpeed(int speed) {
			this.speed = speed;
		}
	
}
