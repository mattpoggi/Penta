package parser.visitor.myvisitor;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

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
 * Visitor for staff writing.
 * It gives no semantic check.
 * Callable only after semantic check by PentaParseVisitor if it gives positive result.
 * While parsing, it will write music only during the essay phase (sounds declared in strophes will only be written when 
 * strophes occurs in essay!)
 * @author Matteo Poggi
 */
public class PentaStaffCompilerVisitor implements IVoidVisitor {

	private JTextArea console;
		
	private HashMap<String,Sequence>strophes;
	private HashMap<String,String>notes;
	
	private int beats=1;
	private boolean inEssay=false;
	private boolean firstBeat=true;
	
	private List<String> currentNotes;
	
	/**
	 * Visitor's constructor, accepting an output console for debugging.
	 * @param console
	 * 		JTextArea where to write output.
	 * @throws MidiUnavailableException 
	 * @throws IOException 
	 * @throws FontFormatException 
	 */
	public PentaStaffCompilerVisitor(JTextArea console) throws MidiUnavailableException, FontFormatException, IOException {
		this.console = console;
		if (console!=null) this.console.setText("");
		this.strophes= new HashMap<String, Sequence>();
		this.notes= new HashMap<String, String>();
		this.notes.put("DO","rl%{");
		this.notes.put("RE","t;^}");
		this.notes.put("MI","y'&|");
		this.notes.put("FA","uz*A");
		this.notes.put("SOL","ix(S");
		this.notes.put("LA","oc)D");
		this.notes.put("SI","pv_F");
		this.notes.put("DO^","[b+G");
		this.notes.put("RE^","]nQH");
		this.notes.put("SILENCE","0987");
		this.console.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("LASSUS.TTF")).deriveFont(128f));
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
		n.f1.accept(this);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		n.f5.accept(this);
		this.consolePrint("=");
		this.alignStaf();
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
	}

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
		this.inEssay=true;
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
			strophe.accept(this);
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
		n.f1.accept(this);
		if(this.inEssay) 
		{
			this.consolePrint("-");
			//if(this.beats%4==0  ) this.consolePrint("\n`");
			//else this.consolePrint("5");
		}
	}

	/** Sign()
	 *   
	 * f0-> Group() | Single() | 
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
		this.currentNotes= new ArrayList<String>();
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
		this.currentNotes= new ArrayList<String>();
		n.f0.accept(this);
		if(n.f0.choice instanceof NodeToken) this.currentNotes.add("SILENCE");
		n.f1.accept(this);

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
		this.currentNotes.add(n.f1.tokenImage+semi);
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
		if(this.inEssay && n.f1.present()) this.consolePrint("¢");
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
		
		if(this.inEssay && n.f0.present())
		{
			this.consolePrint("-6");
		}
		else this.consolePrint("6");
		n.f1.accept(this);
		n.f2.accept(this);

	}

	/** Flagged()	
	 *   
	 * f0-> < QUAVER > | < SEMIQUAVER >  | < DEMISEMIQUAVER >  | < HEMIDEMISEMIQUAVER >
	 */	
	@Override
	public void visit(Flagged n) {
		n.f0.accept(this);
		if(this.inEssay)
		{
			for(int i=0; i< this.currentNotes.size(); i++)
			{
				if(this.currentNotes.get(i).contains("!"))	this.consolePrint("°");
				if(this.currentNotes.get(i).contains("#"))	this.consolePrint("Í");
				int dur=3;
				this.consolePrint(this.notes.get(this.currentNotes.get(i).replace("!", "").replace("#", "")).charAt(3)+"");
				
			}
		}
	}

	/** Unflagged()	
	 *   
	 * f0-> < SEMIBREVE > | < MINIM > | < CROTCHET >
	 */	
	@Override
	public void visit(Unflagged n) {
		n.f0.accept(this);		
		if(this.inEssay)
		{
			for(int i=0; i< this.currentNotes.size(); i++)
			{
				if(this.currentNotes.get(i).contains("!"))	this.consolePrint(">");
				if(this.currentNotes.get(i).contains("#"))	this.consolePrint("Á");
				int dur=0;
				if(((NodeToken)n.f0.choice).tokenImage.equals("(2)")) dur=1;
				if(((NodeToken)n.f0.choice).tokenImage.equals("(4)")) dur=2;
				this.consolePrint(this.notes.get(this.currentNotes.get(i).replace("!", "").replace("#", "")).charAt(dur)+"");
			}
		}
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
		else console.setText(console.getText() + msg);
	}
	
	/**
	 * Utility method to print staff aligned.
	 */
	private void alignStaf()
	{
		this.beats=0;
		String text=console.getText();
		String result="";
		StringTokenizer tk= new StringTokenizer(text);
		int max=0;
		while(tk.hasMoreTokens())
		{
			String temp=tk.nextToken("-");
			if(temp.length()>max) max=temp.length();
		}
		tk= new StringTokenizer(text);
		while(tk.hasMoreTokens())
		{
			String temp=tk.nextToken("-");		
			while(temp.length()<max)
			{
				if((max-temp.length())%2==1) temp="5"+temp;
				else  temp+="5";
			}
			this.beats++;
			if(!temp.contains("=")){ temp+="-"; }
			if(this.beats%4==0) temp+="\n`";
			result+=temp;
		}
		
		tk= new StringTokenizer(result);
		result=tk.nextToken("=");
		for(int i=result.length()-1;i>=0;i--)
		{
			if(result.charAt(i)=='5' || result.charAt(i)=='`' || result.charAt(i)=='\n' ) result=result.substring(0,i);
			else i=-1;
		}
		result+="=";
		result="`"+result;
		console.setText(result);
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
	

}