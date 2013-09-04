package utils;

import java.util.HashMap;

import javax.sound.midi.*;

/**
* Utility Class used to play midi sounds.
* 
* @author Matteo Poggi
* 
*/
public class MidiPlayer {
	private HashMap<String, Integer> notes;
	private Synthesizer synthesizer;
	private Soundbank soundbank;
	private Instrument instruments[];
	private MidiChannel midichannels[];
	private MidiChannel channel;
	private int velocity;
	
	  /**
		* Constructor. 
		* @throws MidiUnavailableException 
		* @param velocity
		* 		velocity value for the notes.
		* @author Matteo Poggi
		* 
		*/ 
	public MidiPlayer(int velocity) throws MidiUnavailableException
	{
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
		this.midichannels=this.synthesizer.getChannels();
		this.channel=this.midichannels[0];
		this.velocity=velocity;
	}
	
	  /**
		* Select the instrument to be played. 
		* @param instrument
		* 		instrument to be selected.
		* @author Matteo Poggi
		* 
		*/ 
	public void selectInstrument(String instrument)
	{
		for(int i=0;i < this.instruments.length; i++)
		{
			if(this.instruments[i].toString().toUpperCase().contains("INSTRUMENT: "+instrument.toUpperCase()))
					{
						this.channel.programChange(this.instruments[i].getPatch().getProgram());
						break;
					}
		}
	}
	
	  /**
		* Plays a single note.
		* @throws InterruptedException 
		* @param note
		* 		note to be played.
		* @param duration
		* 		duration of the note.
		* @author Matteo Poggi
		* 
		*/ 
	public void playNote(String note, int duration) throws InterruptedException
	{
		int semitune=0;
		int scale=0;
		if(note.contains("^")){
			scale=12;
		}
		if(note.contains("!")){
			semitune=-1;
		}
		if(note.contains("#")){
			semitune=1;
		}
		if(note!="SILENCE")this.channel.noteOn(this.notes.get(note.replace("#", "").replace("!", "").replace("^", ""))+semitune+scale, this.velocity);
		Thread.sleep(duration);
		if(note!="SILENCE")this.channel.noteOff(this.notes.get(note.replace("#", "").replace("!", "").replace("^", ""))+scale+semitune);
	}
	
	/**
	* Getter method, return recognized notes.
	* @author Matteo Poggi
	* @return	
	* 		Hashmap object
	*/ 
	public HashMap<String, Integer> getNotes() {
		return notes;
	}
	
	 /**
	* Setter method for recognized notes.
	* @param notes
	* 		recognized notes
	* @author Matteo Poggi
	* 
	*/ 
	public void setNotes(HashMap<String, Integer> notes) {
		this.notes = notes;
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
	* Getter method, return soundbank object.
	* @author Matteo Poggi
	* @return	
	* 		soundbank object
	*/ 
	public Soundbank getSoundbank() {
		return soundbank;
	}
	
	 /**
	* Setter method for soundbank object.
	* @param soundbank
	* 		object to be set
	* @author Matteo Poggi
	* 
	*/ 
	public void setSoundbank(Soundbank soundbank) {
		this.soundbank = soundbank;
	}
	
	/**
	* Getter method, return array of available instruments.
	* @author Matteo Poggi
	* @return	
	* 		instruments array
	*/ 
	public Instrument[] getInstruments() {
		return instruments;
	}
	
	 /**
	* Setter method for available instruments.
	* @param instruments
	* 		array of instruments to be set
	* @author Matteo Poggi
	* 
	*/ 
	public void setInstruments(Instrument instruments[]) {
		this.instruments = instruments;
	}
	
	/**
	* Getter method, return array of midichannels.
	* @author Matteo Poggi
	* @return	
	* 		midichannels array
	*/ 
	public MidiChannel[] getMidichannels() {
		return midichannels;
	}

	 /**
	* Setter method for midichannels.
	* @param midichannels
	* 		array of midichannels to be set
	* @author Matteo Poggi
	* 
	*/ 
	public void setMidichannels(MidiChannel midichannels[]) {
		this.midichannels = midichannels;
	}

	/**
	* Getter method, return a midichannel object.
	* @author Matteo Poggi
	* @return	
	* 		midichannel object
	*/ 
	public MidiChannel getChannel() {
		return channel;
	}

	 /**
	* Setter method for channel object.
	* @param channel
	* 		object to be set
	* @author Matteo Poggi
	* 
	*/ 
	public void setChannel(MidiChannel channel) {
		this.channel = channel;
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
		this.velocity = velocity;
	}
	
	
}
