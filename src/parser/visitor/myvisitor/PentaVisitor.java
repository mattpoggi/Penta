package parser.visitor.myvisitor;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.JTextArea;

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
 * Visitor for semantic check.
 * Main checks:
 * <ul>
 * <li> Declaration of known instrument. </li>
 * <li> Declaration of strophes (variables) and their usage. </li>
 * <li> Respect of beat time (if declared). </li>
 * </ul>
 * @author Matteo Poggi
 */
public class PentaVisitor implements IVoidVisitor {

	private JTextArea console;
	private String errorMsg;
	private boolean error;
	
	private boolean inEssay;
	private boolean success;
	
	private String sheet;
	private HashMap<String,Sequence>strophes;
	private String currentStrophe;
	
	private MidiPlayer player;
	
	private double beat;
	private int currentBeat;
	private int speed;
	private int signCount;
	private int lastDuration;
	
	
	/**
	 * Visitor's constructor, accepting an output console for debugging.
	 * @param console
	 * 		JTextArea where to write output.
	 * @throws MidiUnavailableException 
	 */
	public PentaVisitor(JTextArea console) throws MidiUnavailableException {
		this.console = console;
		this.error=false;
		this.errorMsg="";
		if (console!=null) this.console.setText("");
		this.strophes= new HashMap<String, Sequence>();
		this.setPlayer(new MidiPlayer(0));
		this.setInEssay(false);
		this.setSuccess(false);
		this.beat=0;
		this.currentBeat=0;
		this.speed=0;
	}

	/**
	 * Getter for last semantic error occurred.
	 * @return
	 * 		String with last semantic error occurred.
	 */
	public String getErrorMsg() {
		return this.errorMsg;
	}

	/**
	 * Getter checking for semantic errors.
	 * 
	 * @return
	 * 		true if semantic errors occured.
	 */
	public boolean checkError() {
		return this.error;
	}



	@Override
	public void visit(NodeList n) {
		
		for (final Iterator<INode> e = n.elements(); e.hasNext();) {
			if(checkError()) return; 
			else error=false;
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
		n.f1.accept(this);
		this.sheet=n.f1.tokenImage;
		n.f2.accept(this);
		n.f3.accept(this);
		// Declarations() potrebbe dare errore (Strumento non riconosciuto, strofe diverse con stesso nome)
		if(checkError())	return;
		n.f4.accept(this);
		// Essay() potrebbe dare errore (nome già esistente, uso di strofe non esistenti)
		if(checkError())	return;
		n.f5.accept(this);
		consolePrint("Compilation Succesfull!");
		this.setSuccess(true);
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
		if(checkError()) return;
		n.f1.accept(this);
		if(checkError())	return;
		n.f2.accept(this);
		if(checkError()) return;
		n.f3.accept(this);
		if(checkError()) return;
		n.f4.accept(this);
	}
	
	/** BeatDeclaration()
	 *   
	 * f0-> BEAT
	 * f1-> =
	 * f2-> < DIGIT >
	 * f3-> "/"
	 * f4-> < SEMIBREVE > | < MINIM > | < CROTCHET > | < QUAVER > | < SEMIQUAVER > | < DEMISEMIQUAVER > | < HEMIDEMISEMIQUAVER >
	 * f5-> ";"
	 */
	@Override
	public void visit(BeatDeclaration n) {
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		n.f5.accept(this);
		this.beat= Double.parseDouble(n.f2.tokenImage)/Double.parseDouble(((NodeToken)n.f4.choice).tokenImage.replace("(", "").replace(")",""));
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
	}
	
	/** SpeedDeclaration()
	 *   
	 * f0-> SPEED
	 * f1-> =
	 * f2-> < INTEGER >
	 */
	@Override
	public void visit(SpeedDeclaration n) {
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		this.beat*=Integer.parseInt(n.f2.tokenImage);
		this.speed=Integer.parseInt(n.f2.tokenImage);
	}

	/** InstrumentDeclaration()
	 *   
	 * f0-> INSTRUMENT
	 * f1-> =
	 * f2-> < ID >
	 */
	@Override
	public void visit(InstrumentDeclaration n) {
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		boolean found=false;
		for (int i=0 ; i< this.player.getInstruments().length; i++)
		{
			if(this.player.getInstruments()[i].toString().toUpperCase().contains(n.f2.tokenImage.toUpperCase()))
				{
					found=true;
				}
		}
		if(!found)
		{
			consolePrint("Unknown instrument!");
			this.error=true;
			return;
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
		if(this.strophes.containsKey(n.f1.tokenImage.toUpperCase()))
		{
			consolePrint("A strophe with that name already exists!");
			this.error=true;
			return;
		}
		if( this.sheet.toUpperCase().equals(n.f1.tokenImage.toUpperCase()))
		{
			consolePrint("Can not call a strophe with sheet's name!");
			this.error=true;
			return;
		}
		this.currentStrophe=n.f1.tokenImage;
		n.f2.accept(this);
		n.f3.accept(this);
		// Una sequenza potrebbe avere errori semantici? Si, uso di variabili non ancora dichiarate/variabile stessa/essay/sheet!
		if(checkError())	return;
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
		if( !this.sheet.toUpperCase().equals(n.f1.tokenImage.toUpperCase()))
		{
			this.errorMsg="Essay's name must be the same of sheet!";
			this.error=true;
		}
		if(checkError()) {
			consolePrint("ERROR: "+errorMsg);
			return;
		} else error = false;
		this.currentStrophe=n.f1.tokenImage;
		n.f2.accept(this);
		n.f3.accept(this);
		// Una sequenza potrebbe avere errori semantici? Si, uso di variabili non dichiarate/variabile stessa/essay/sheet!
		if(checkError())	return;
		n.f4.accept(this);
	}

	/** Sequence()
	 *   
	 * f0-> Notes()
	 */
	@Override
	public void visit(Sequence n) {
		n.f0.accept(this);
		// Una linea potrebbe avere errori semantici? Si, uso di variabili non dichiarate/variabile stessa/essay/sheet!
		if(checkError())	return;
	}
	
	/** Notes()	
	 *   
	 * f0-> Beat() | < ID >
	 */
	@Override
	public void visit(Notes n) {
		n.f0.accept(this);
		if(n.f0.choice instanceof NodeToken && ((NodeToken)(n.f0.choice)).tokenImage.toUpperCase().equals(this.currentStrophe.toUpperCase()))
		{
			consolePrint("A strophe or essay can not recursively call himself!");
			this.error=true;
			return;
		}
		if(n.f0.choice instanceof NodeToken && !this.strophes.containsKey(((NodeToken)(n.f0.choice)).tokenImage.toUpperCase()))
		{
			consolePrint("Strophe "+((NodeToken)(n.f0.choice)).tokenImage+" used but not declared!");
			this.error=true;
			return;
		}

		if(n.f0.choice instanceof NodeToken)
		{
			Sequence strophe=this.strophes.get(((NodeToken)n.f0.choice).tokenImage.toUpperCase());
			if(strophe !=null) strophe.accept(this);
			return;
		}
	}
	
	
	/** Beat()
	 *   
	 * f0-> Sign()
	 * f1-> "|"
	 */
	@Override
	public void visit(Beat n) {
		n.f0.accept(this);
		// Un simbolo potrebbe avere errori semantici? Si, variabile non dichiarata/essay/sheet!
		if(checkError()) return;
		n.f1.accept(this);
		if(this.currentBeat!=this.beat && this.isInEssay() && this.beat!=0)
		{
			consolePrint("Invalid beat value in note sequence! ( declared value is "+this.beat+", current is "+this.currentBeat+")");
			this.error=true;
			return;
		}
		this.currentBeat=0;
	}

	/** Sign()
	 *   
	 * f0-> Group() | Single() 
	 */
	@Override
	public void visit(Sign n) {
		this.signCount=0;
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
		//this.currentDuration=0;
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		
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
		n.f0.accept(this);
		if(n.f0.choice instanceof NodeToken) this.signCount++;
		n.f1.accept(this);
		
	}

	/** Note()	
	 *   
	 * f0-> < SEMITUNE >  
	 * f1-> < TUNE >
	 * f2-> < SCALE >
	 */	
	@Override
	public void visit(Note n) {
		n.f0.accept(this);
		n.f1.accept(this);
		this.signCount++;
		
	}

	/** Duration()	
	 *   
	 * f0-> (Flagged() | Unflagged())  
	 * f1-> < DOT >
	 * f2-> Extend()
	 */	
	@Override
	public void visit(Duration n) {
		//this.currentDuration=0;
		n.f0.accept(this);
		n.f1.accept(this);
		if(n.f1.present()) this.currentBeat+=this.lastDuration*0.5;
		n.f2.accept(this);
		if(checkError()) return;
	}
	
	/** Extend()	
	 *  
	 * f0-> +  
	 * f1-> (Flagged() | Unflagged())
	 * f2-> .
	 */	
	@Override
	public void visit(Extend n) {
		n.f0.accept(this);
		if(n.f0.present())
		{
			if(this.currentBeat!=this.beat && this.isInEssay() && this.beat!=0)
			{
				consolePrint("Invalid beat value in note sequence! ( declared value is "+this.beat+", current is "+this.currentBeat+")");
				this.error=true;
				return;
			}
			this.currentBeat=0;
		}
		n.f1.accept(this);
		n.f2.accept(this);
		if(n.f2.present()) this.currentBeat+=this.lastDuration*0.5;
	}

	/** Flagged()	
	 *   
	 * f0-> < QUAVER > | < SEMIQUAVER >  | < DEMISEMIQUAVER >  | < HEMIDEMISEMIQUAVER >
	 */	
	@Override
	public void visit(Flagged n) {
		n.f0.accept(this);
		this.currentBeat+=(this.speed/Integer.parseInt(((NodeToken)n.f0.choice).tokenImage.replace("(", "").replace(")","")))*this.signCount;
		this.lastDuration=(this.speed/Integer.parseInt(((NodeToken)n.f0.choice).tokenImage.replace("(", "").replace(")","")))*this.signCount;
	}

	/** Unflagged()	
	 *   
	 * f0-> < SEMIBREVE > | < MINIM > | < CROTCHET >
	 */	
	@Override
	public void visit(Unflagged n) {
		n.f0.accept(this);		
		this.currentBeat+=(this.speed/Integer.parseInt(((NodeToken)n.f0.choice).tokenImage.replace("(", "").replace(")","")))*this.signCount;
		this.lastDuration=(this.speed/Integer.parseInt(((NodeToken)n.f0.choice).tokenImage.replace("(", "").replace(")","")))*this.signCount;
	}
	
	
	// Utils
	

	
	/**
	 * Utility method to print on output console.
	 * 
	 * @param msg
	 * 		String to append on console.
	 */
	public void consolePrint(String msg){
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
	 * Getter for compilation result.
	 * 
	 * @return
	 * 		true if no error occured.
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Setter for compilation result.
	 * @param success
	 * 		true if no error occured.
	 */
	public void setSuccess(boolean success) {
		this.success = success;
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

}