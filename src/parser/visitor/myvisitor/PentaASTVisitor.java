package parser.visitor.myvisitor;

import java.awt.Dimension;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

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
 * Visitor for AST Tree creation.
  * It gives no semantic check.
  * Callable only after semantic check by PentaParseVisitor if it gives positive result.
  * While parsing, it explore strophe nodes only on declaration phase.
  * @author Matteo Poggi
 */
public class PentaASTVisitor implements IVoidVisitor{

	private DefaultMutableTreeNode tree;
	private HashMap<String,String> durations;
	private int currentBeat=0;
	private String currentNote;
	private List<String> currentDurations;
	

	/**
	 * Visitor's constructor.
	 */
	public PentaASTVisitor(){
		this.durations= new HashMap<String,String>();
		this.durations.put("(1)", "SEMIBREVE");
		this.durations.put("(2)", "MINIM");
		this.durations.put("(4)", "CROTCHET");
		this.durations.put("(8)", "QUAVER");
		this.durations.put("(16)", "SEMIQUAVER");
		this.durations.put("(32)", "DEMISEMIQUAVER");
		this.durations.put("(64)", "HEMIDEMISEMIQUAVER");
	}
	@Override
	public void visit(NodeChoice n) {
		// TODO Auto-generated method stub
		
	}

	public void visit(final NodeList n) {
		for (final Iterator<INode> e = n.elements(); e.hasNext();) {
			e.next().accept(this);
		}
		return;
	}

	public void visit(final NodeListOptional n) {
		if (n.present()) {
			for (final Iterator<INode> e = n.elements(); e.hasNext();) {
				e.next().accept(this);
			}
			return;
		} else
			return;
	}

	public void visit(final NodeOptional n) {
		if (n.present()) {
			n.node.accept(this);
			return;
		} else
			return;
	}

	@Override
	public void visit(NodeSequence n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NodeTCF n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NodeToken n) {
		// TODO Auto-generated method stub
		
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
		DefaultMutableTreeNode app = null;
		n.f0.accept(this);
		n.f1.accept(this);
		app= new DefaultMutableTreeNode("SHEET "+n.f1.tokenImage);
		n.f2.accept(this);
		n.f3.accept(this);
		app.add(tree);
		n.f4.accept(this);
		app.add(tree);
		n.f5.accept(this);
		tree = app;
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
		DefaultMutableTreeNode app = new DefaultMutableTreeNode("DECLARATIONS");
		n.f0.accept(this);
		if(n.f0.present()) app.add(tree);
		n.f1.accept(this);
		app.add(tree);
		n.f2.accept(this);
		app.add(tree);
		n.f3.accept(this);
		app.add(tree);
		n.f4.accept(this);
		tree=app;
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
		tree = new DefaultMutableTreeNode("BEAT");
		tree.add(new DefaultMutableTreeNode(n.f2.tokenImage+"/"+((NodeToken)n.f4.choice).tokenImage.replace("(","").replace(")","")));
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
		tree = new DefaultMutableTreeNode("VELOCITY");
		tree.add(new DefaultMutableTreeNode(n.f2.tokenImage));
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
		tree = new DefaultMutableTreeNode("SPEED");
		tree.add(new DefaultMutableTreeNode(n.f2.tokenImage));
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
		tree = new DefaultMutableTreeNode("INSTRUMENT");
		tree.add(new DefaultMutableTreeNode(n.f2.tokenImage));
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
		DefaultMutableTreeNode app=(DefaultMutableTreeNode) tree.getParent();
		DefaultMutableTreeNode temp=tree;
		DefaultMutableTreeNode strophe;
		n.f0.accept(this);
		n.f1.accept(this);
		tree = new DefaultMutableTreeNode("STROPHE "+n.f1.tokenImage);
		app.add(tree);
		n.f2.accept(this);
		n.f3.accept(this);
		n.f4.accept(this);
		
		tree=temp;
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
		DefaultMutableTreeNode app=(DefaultMutableTreeNode) tree.getParent();		
		n.f0.accept(this);
		n.f1.accept(this);
		tree = new DefaultMutableTreeNode("ESSAY "+n.f1.tokenImage);
		app.add(tree);
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
		DefaultMutableTreeNode app= (DefaultMutableTreeNode) tree.getParent();
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
			tree.add(new DefaultMutableTreeNode(((NodeToken)n.f0.choice).toString()));
		}
	}
	
	/** Beat()
	 *   
	 * f0-> Sign()
	 * f1-> "|"
	 */
	@Override
	public void visit(Beat n) {
		this.currentBeat++;

		n.f0.accept(this);
		n.f1.accept(this);
}

	/** Sign()
	 *   
	 * f0-> Group() | Single() 
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
		this.currentDurations=new ArrayList<String>();
		this.currentNote="";
		DefaultMutableTreeNode app=(DefaultMutableTreeNode) tree.getParent();
		DefaultMutableTreeNode temp=tree;
		DefaultMutableTreeNode sign;
		this.currentNote="[";
		
		n.f0.accept(this);
		n.f1.accept(this);
		
		
		n.f2.accept(this);
		n.f3.accept(this);
		this.currentNote+="]";
		sign = new DefaultMutableTreeNode(this.currentNote);
		temp.add(sign);
		app.add(tree);
		n.f4.accept(this);

		String dur="";
		String last="";
		for(String s : this.currentDurations)
			{
				if(s.equals("."))
				{
					s=last.replace("(", "").replace(")","");
					s="("+(Integer.parseInt(s)*2)+")";
				}
				dur+=this.durations.get(s)+"+";
				last=s;
			}
		dur=dur.substring(0,dur.length()-1);
		tree=new DefaultMutableTreeNode(dur);
		sign.add(tree);
		temp.add(sign);
		tree=temp;
		
	}
	
	/** Others()	
	 *   
	 * f0-> ","
	 * f1-> Note()
	 */
	@Override
	public void visit(Others n) {
		n.f0.accept(this);
		this.currentNote+=",";
		n.f1.accept(this);
	}

	/** Single()	
	 *   
	 * f0-> Note() | <  SILENCE >
	 * f1-> Duration()
	 */	
	@Override
	public void visit(Single n) {
		this.currentNote="";
		DefaultMutableTreeNode app=(DefaultMutableTreeNode) tree.getParent();
		DefaultMutableTreeNode temp=tree;
		DefaultMutableTreeNode sign;
		n.f0.accept(this);
		if(n.f0.choice instanceof NodeToken) sign = new DefaultMutableTreeNode("SILENCE");
		else sign = new DefaultMutableTreeNode(this.currentNote);
		n.f1.accept(this);
		
		String dur="";
		String last="";
		for(String s : this.currentDurations)
			{
				if(s.equals("."))
				{
					s=last.replace("(", "").replace(")","");
					s="("+(Integer.parseInt(s)*2)+")";
				}
				dur+=this.durations.get(s)+"+";
				last=s;
			}
		dur=dur.substring(0,dur.length()-1);
		tree=new DefaultMutableTreeNode(dur);
		sign.add(tree);
		temp.add(sign);
		tree=temp;
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
		this.currentNote+=semi+n.f1.tokenImage;
	}

	/** Duration()	
	 *   
	 * f0-> (Flagged() | Unflagged())  
	 * f1-> < DOT >
	 * f2-> Extend()
	 */	
	@Override
	public void visit(Duration n) {
		this.currentDurations=new ArrayList<String>();
		n.f0.accept(this);
		n.f1.accept(this);
		if(n.f1.present()) this.currentDurations.add(".");
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
		if(n.f2.present()) this.currentDurations.add(".");
	}

	/** Flagged()	
	 *   
	 * f0-> < QUAVER > | < SEMIQUAVER >  | < DEMISEMIQUAVER >  | < HEMIDEMISEMIQUAVER >
	 */	
	@Override
	public void visit(Flagged n) {
		n.f0.accept(this);
		this.currentDurations.add(((NodeToken)n.f0.choice).tokenImage);
		 
	}

	/** Unflagged()	
	 *   
	 * f0-> < SEMIBREVE > | < MINIM > | < CROTCHET >
	 */	
	@Override
	public void visit(Unflagged n) {
		n.f0.accept(this);	
		this.currentDurations.add(((NodeToken)n.f0.choice).tokenImage);
		tree=new DefaultMutableTreeNode(this.durations.get(((NodeToken)n.f0.choice).tokenImage)); 
	}

	
	
	/**
	 * Getter for DefaultMutableTreeNode tree, where the AST is being built.
	 * 
	 * @return
	 * 		DefaultMutableTreeNode object.
	 */
	public DefaultMutableTreeNode getTree() {
		return tree;
	}

	/**
	 * Setter for DefaultMutableTreeNode.
	 * 
	 * @param tree
	 * 		DefaultMutableTreeNode object.
	 */
	public void setTree(DefaultMutableTreeNode tree) {
		this.tree = tree;
	}
	
	
	
}


