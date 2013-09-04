package parser.visitor.myvisitor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.JTextArea;

import utils.MidiFileWriter;
import utils.MidiPlayer;
import parser.PentaParser;
import parser.TokenMgrError;
import parser.syntaxtree.Beat;
import parser.syntaxtree.BeatDeclaration;
import parser.syntaxtree.Notes;
import parser.syntaxtree.SpeedDeclaration;
import parser.syntaxtree.Declarations;
import parser.syntaxtree.Duration;
import parser.syntaxtree.Essay;
import parser.syntaxtree.Extend;
import parser.syntaxtree.Flagged;
import parser.syntaxtree.Group;
import parser.syntaxtree.INode;
import parser.syntaxtree.InstrumentDeclaration;
import parser.syntaxtree.NodeChoice;
import parser.syntaxtree.NodeList;
import parser.syntaxtree.NodeListOptional;
import parser.syntaxtree.NodeOptional;
import parser.syntaxtree.NodeSequence;
import parser.syntaxtree.NodeTCF;
import parser.syntaxtree.NodeToken;
import parser.syntaxtree.Note;
import parser.syntaxtree.Others;
import parser.syntaxtree.Scope;
import parser.syntaxtree.Sequence;
import parser.syntaxtree.Sign;
import parser.syntaxtree.Single;
import parser.syntaxtree.StropheDeclaration;
import parser.syntaxtree.Unflagged;
import parser.syntaxtree.VelocityDeclaration;
import parser.visitor.IVoidVisitor;

/**
 * Visitor for midi file compiling.
 * It gives no semantic check.
 * Callable only after semantic check by PentaParseVisitor if it gives positive result.
 * While parsing, it will write music on midi file only during the essay phase (sounds declared in strophes will only be played when 
 * strophes occurs in essay!)
 * @author Matteo Poggi
 */
public class PentaMidiCompilerVisitor implements IVoidVisitor {

	private JTextArea console;
	
	private boolean inEssay;
	
	private HashMap<String,Sequence>strophes;
	
	private MidiPlayer player;
	private MidiFileWriter writer;
	private String sheet;
	private int speed;
	
	private List<String> currentOthers;
	private int currentDuration;
	private int lastDuration;	
	
	/**
	 * Visitor's constructor, accepting an output console for debugging.
	 * @param console
	 * 		JTextArea where to write output.
	 * @throws MidiUnavailableException 
	 */
	public PentaMidiCompilerVisitor(JTextArea console) throws MidiUnavailableException {
		this.console = console;
		if (console!=null) this.console.setText("");
		this.strophes= new HashMap<String, Sequence>();
		this.setPlayer(new MidiPlayer(64));
		this.setWriter(new MidiFileWriter(64));
		this.setInEssay(false);
	}



	@Override
	public void visit(NodeList n) {
		
		for (final Iterator<INode> e = n.elements(); e.hasNext();) {
			e.next().accept(this);
		}
		return;
	}

	@Override
	public void visit(NodeListOptional n) {
		if (n.present()) {
			for (final Iterator<INode> e = n.elements(); e.hasNext();) {
				e.next().accept(this);
			}
			return;
		} else
			return;
	}

	@Override
	public void visit(NodeOptional n) {
		if (n.present()) {
			//System.out.println("S“: "+n.node);
			//consolePrint(n.node.toString());
			n.node.accept(this);
			return;
		} else
			return;
	}



	/** Scope()
	 * 
	 * f0-> "SHEET"
	 * f1-> "< ID >"
	 * f2-> "{"
	 * f3-> Declarations()
	 * f4-> Essay()
	 * f5-> "}"
	 */
	@Override
	public void visit(Scope n) {
		n.f0.accept(this);
		consolePrint("Generating file "+n.f1.tokenImage+".mid ...");
		n.f1.accept(this);
		this.sheet=n.f1.tokenImage;
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		n.f5.accept(this);
		try {
			this.writer.writeToFile(this.sheet+".mid");
			consolePrint("\nCompilation Succesfull!");
		} catch (IOException e) {
			this.consolePrint(e.getMessage());
			
		}
		
	}
	
	/** Declarations()
	 * 
	 * f0-> BeatDeclaration()
	 * f1-> SpeedDeclaration()  
	 * f2-> InstrumentDeclaration()
	 * f3-> VelocityDeclaration()
	 * f4-> StropheDeclaration()
	 */
	@Override
	public void visit(Declarations n) {
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);

	}
	
	/** BeatDeclaration()
	 *   
	 * f0-> BEAT
	 * f1-> =
	 * f2-> < INTEGER >
	 * f3-> "/"
	 * f4-> Duration
	 */
	@Override
	public void visit(BeatDeclaration n) {
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
	}
	
	/** VelocityDeclaration()
	 *   
	 * f0-> VELOCITY
	 * f1-> =
	 * f2-> < INTEGER >
	 */
	@Override
	public void visit(VelocityDeclaration n) {
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		this.writer.setVelocity(Integer.parseInt(n.f2.tokenImage));
	}
	
	/** SpeedDeclarations()
	 *   
	 * f0-> SPEED
	 * f1-> =
	 * f2-> < SPEED >
	 */
	@Override
	public void visit(SpeedDeclaration n) {
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		this.speed=Integer.parseInt(n.f2.tokenImage);
		writer.setSpeed(Integer.parseInt(n.f2.tokenImage));	}

	/** InstrumentDeclarations()
	 *   
	 * f0-> INSTRUMENT
	 * f1-> =
	 * f2-> < INSTRUMENT >
	 */
	@Override
	public void visit(InstrumentDeclaration n) {
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		for (int i=0 ; i< this.writer.getInstruments().length; i++)
		{
			if(this.writer.getInstruments()[i].toString().toUpperCase().contains(n.f2.tokenImage.toUpperCase()))
				{
					this.writer.progChange(this.player.getInstruments()[i].getPatch().getProgram());
					break;
				}
		}
	}

	/** StropheDeclaration()
	 *   
	 * f0-> STROPHE
	 * f1-> < ID >
	 * f2-> "{"
	 * f3-> Sequence()
	 * f4-> "}"
	 */
	@Override
	public void visit(StropheDeclaration n) {
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		if(n.f3.present()) this.strophes.put(n.f1.tokenImage.toUpperCase(), (Sequence)n.f3.node);
		else this.strophes.put(n.f1.tokenImage.toUpperCase(), null);
		n.f4.accept(this);
	}

	/** Essay()
	 *   
	 * f0-> ESSAY
	 * f1-> < ID >
	 * f2-> "{"
	 * f3-> Sequence()
	 * f4-> "}"
	 */
	@Override
	public void visit(Essay n) {
		this.setInEssay(true);
		n.f0.accept(this);
		n.f1.accept(this);

		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
	}

	/** Sequence()
	 *   
	 * f0-> Notes()
	 */
	@Override
	public void visit(Sequence n) {
		n.f0.accept(this);
	}
	
	/** Notes()	
	 *   
	 * f0-> Beat() | < ID >
	 */
	@Override
	public void visit(Notes n) {
		n.f0.accept(this);
		if(n.f0.choice instanceof NodeToken)
		{
			Sequence strophe=this.strophes.get(((NodeToken)n.f0.choice).tokenImage);
			if(strophe!=null) strophe.accept(this);
			return;
		}
	}
	
	/** Beat()
	 *   
	 * f0-> Beat()
	 * f1-> "|"
	 */
	@Override
	public void visit(Beat n) {
		n.f0.accept(this);
		n.f1.accept(this);
	}

	/** Sign()
	 *   
	 * f0-> Group() | Single() | < ID >
	 */
	@Override
	public void visit(Sign n) {
		n.f0.accept(this);
	}

	/** Group()	
	 *   
	 * f0-> "["
	 * f1-> Note()
	 * f2-> Others()
	 * f3-> "]"
	 * f4-> Flagged()
	 */
	@Override
	public void visit(Group n) {
		this.currentDuration=0;
		this.currentOthers= new ArrayList<String>();
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		if(this.inEssay) for(String note : this.currentOthers) this.writer.noteOnOffNow(this.currentDuration, note);
		
	}
	
	/** Others()	
	 *   
	 * f0-> ","
	 * f1-> Note()
	 */
	@Override
	public void visit(Others n) {
		n.f0.accept(this);
		n.f1.accept(this);
	}

	/** Single()	
	 *   
	 * f0-> Note() | <  SILENCE >
	 * f1-> Duration()
	 */	
	@Override
	public void visit(Single n) {
		this.currentOthers= new ArrayList<String>();
		this.currentDuration=0;
		n.f0.accept(this);
		if(n.f0.choice instanceof NodeToken) this.currentOthers.add("SILENCE");
		n.f1.accept(this);
		if(this.inEssay) for(String note : this.currentOthers) this.writer.noteOnOffNow(this.currentDuration, note);
	}

	/** Note()	
	 *   
	 * f0-> < SEMITUNE >  
	 * f1-> < TUNE >
	 */	
	@Override
	public void visit(Note n) {
		String semi="";
		n.f0.accept(this);
		if(n.f0.present()) semi=((NodeToken)n.f0.node).tokenImage; 
		n.f1.accept(this);
		this.currentOthers.add(n.f1.tokenImage+semi);
	}

	/** Duration()	
	 *   
	 * f0-> (Flagged() | Unflagged())  
	 * f1-> < DOT >
	 * f2-> Extend()
	 */	
	@Override
	public void visit(Duration n) {
		n.f0.accept(this);
		n.f1.accept(this);
		if(n.f1.present()) this.currentDuration+=this.lastDuration*0.5;
		n.f2.accept(this);
	}
	
	/** Extend()	
	 *   
	 * f0-> +  
	 * f1-> (Flagged() | Unflagged())
	 * f2-> "."
	 */	
	@Override
	public void visit(Extend n) {
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		if(n.f2.present()) this.currentDuration+=this.lastDuration*0.5;
	}

	/** Flagged()	
	 *   
	 * f0-> < QUAVER > | < SEMIQUAVER >  | < DEMISEMIQUAVER >  | < HEMIDEMISEMIQUAVER >
	 */	
	@Override
	public void visit(Flagged n) {
		n.f0.accept(this);
		this.currentDuration+=this.speed/Integer.parseInt(((NodeToken)n.f0.choice).tokenImage.replace("(", "").replace(")", ""));
		this.lastDuration=this.speed/Integer.parseInt(((NodeToken)n.f0.choice).tokenImage.replace("(", "").replace(")", ""));
	}

	/** Unflagged()	
	 *   
	 * f0-> < SEMIBREVE > | < MINIM > | < CROTCHET >
	 */	
	@Override
	public void visit(Unflagged n) {
		n.f0.accept(this);		
		this.currentDuration+=this.speed/Integer.parseInt(((NodeToken)n.f0.choice).tokenImage.replace("(", "").replace(")", ""));
		this.lastDuration=this.speed/Integer.parseInt(((NodeToken)n.f0.choice).tokenImage.replace("(", "").replace(")", ""));
	}
	
	
	// Utils
	

	
	/**
	 * Utility method to print on output console.
	 * 
	 * @param msg
	 * 		String to append on console.
	 */
	private void consolePrint(String msg){
		if (console == null)
			System.out.println("" + msg);
		else console.setText(console.getText() + msg + "\n");
	}
	
	
	// Not implemented
	
	@Override
	public void visit(NodeChoice n) {
		n.accept(this);
		return;
	}
	
	@Override
	public void visit(NodeSequence n) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void visit(NodeToken n) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(NodeTCF n) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Getter for compilations phase.
	 * 
	 * @return
	 * 		true if the declaration phase ended.
	 */
	public boolean isInEssay() {
		return inEssay;
	}

	/**
	 * Setter for compilations phase.
	 * 
	 * @param inEssay
	 * 		true if the declaration phase ended.
	 */
	public void setInEssay(boolean inEssay) {
		this.inEssay = inEssay;
	}

	/**
	 * Getter for MidiPlayer.
	 * 
	 * @return
	 * 		MidiPlayer object.
	 */
	public MidiPlayer getPlayer() {
		return player;
	}

	/**
	 * Setter for MidiPlayer.
	 * 
	 * @param player
	 * 		MidiPlayer object.
	 */
	public void setPlayer(MidiPlayer player) {
		this.player = player;
	}


	/**
	 * Getter for speed value.
	 * 
	 * @return
	 * 		speed value.
	 */
	public int getSpeed() {
		return speed;
	}


	/**
	 * Setter for speed value.
	 * 
	 * @param speed
	 * 		speed value.
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}


	/**
	 * Getter for MidiFileWriter.
	 * 
	 * @return
	 * 		MidiFileWriter object.
	 */
	public MidiFileWriter getWriter() {
		return writer;
	}


	/**
	 * Getter for MidiFileWriter.
	 * 
	 * @param writer
	 * 		MidiFileWriter object.
	 */
	public void setWriter(MidiFileWriter writer) {
		this.writer = writer;
	}


	/**
	 * Getter for sheet name.
	 * 
	 * @return
	 * 		sheet name.
	 */
	public String getSheet() {
		return sheet;
	}


	/**
	 * Setter for sheet name.
	 * 
	 * @param sheet
	 * 		sheet name.
	 */
	public void setSheet(String sheet) {
		this.sheet = sheet;
	}



}