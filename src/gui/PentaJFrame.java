package gui;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URI;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.LayoutStyle;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;


import parser.ParseException;
import parser.PentaParser;
import parser.syntaxtree.Scope;
import parser.visitor.myvisitor.PentaASTVisitor;
import parser.visitor.myvisitor.PentaMidiCompilerVisitor;
import parser.visitor.myvisitor.PentaPlayVisitor;
import parser.visitor.myvisitor.PentaStaffCompilerVisitor;
import parser.visitor.myvisitor.PentaVisitor;
import utils.TextPrinter;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class PentaJFrame extends javax.swing.JFrame {

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	private boolean parserInit=false;
	private PentaParser parser;
	
	private JMenuBar jMenuBar1;
	private AbstractAction abstractActionPlay;
	private AbstractAction abstractActionCompile;
	private JDialog jDialogAboutPenta;
	private AbstractAction abstractActionAboutPenta;
	private JMenuItem jMenuItem12;
	private JLabel jLabelPhoto;
	private JPanel jPanelPhoto;
	private JLabel jLabelMail;
	private JLabel jLabelMatrix;
	private JLabel jLabelName;
	private JPanel jPanelDatas;
	private JDialog jDialogAbout;
	private AbstractAction abstractAction1;
	private JMenuItem jMenuItem11;
	private AbstractAction abstractActionPrint;
	private JMenuItem jMenuItem4;
	private AbstractAction abstractActionStaff;
	private JMenuItem jMenuItem10;
	private JScrollPane jScrollPane4;
	private JTextArea jTextAreaStaff;
	private JScrollPane jScrollPane3;
	private JTree jTreeAST;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane1;
	private JFileChooser jFileChooserOpen;
	private JLabel jLabelImage;
	private JLabel jLabelVersion;
	private JLabel jLabelDoc;
	private JLabel jLabelGrammar;
	private JTextArea jTextAreaConsole;
	private JTextArea jTextAreaCode;
	private JPanel jPanel1;
	private AbstractAction abstractActionAbout;
	private JMenuItem jMenuItem9;
	private AbstractAction abstractActionAST;
	private JMenuItem jMenuItem8;
	private JMenuItem jMenuItem7;
	private JMenuItem jMenuItem6;
	private AbstractAction abstractActionBuild;
	private AbstractAction abstractActionNew;
	private JMenuItem jMenuItem5;
	private AbstractAction abstractActionExit;
	private AbstractAction abstractActionSave;
	private JMenuItem jMenuItem3;
	private AbstractAction abstractActionOpen;
	private JMenuItem jMenuItem2;
	private JMenuItem jMenuItem1;
	private JMenu jMenuHelp;
	private JMenu jMenuProject;
	private JMenu jMenu1;
	
	private String fileName="";
	private Thread current=null;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PentaJFrame inst = new PentaJFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public PentaJFrame() {
		super();
		this.setTitle("PENTA 0.1");
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().add(getJPanel1(), BorderLayout.CENTER);
			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(getJMenuBar1());
				{
					jMenu1 = new JMenu();
					jMenuBar1.add(jMenu1);
					jMenu1.setText("File");
					{
						jMenuItem1 = new JMenuItem();
						jMenu1.add(jMenuItem1);
						jMenuItem1.setText("jMenuItem1");
						jMenuItem1.setAction(getAbstractActionNew());
					}
					{
						jMenuItem2 = new JMenuItem();
						jMenu1.add(jMenuItem2);
						jMenuItem2.setText("jMenuItem2");
						jMenuItem2.setAction(getAbstractActionOpen());
					}
					{
						jMenuItem3 = new JMenuItem();
						jMenu1.add(jMenuItem3);
						jMenu1.add(getJMenuItem4());
						jMenu1.add(getJMenuItem11());
						jMenuItem3.setText("jMenuItem3");
						jMenuItem3.setAction(getAbstractActionSave());
					}
				}
				{
					jMenuProject = new JMenu();
					jMenuBar1.add(getJMenuProject());
					jMenuProject.setText("Project");
					{
						jMenuItem5 = new JMenuItem();
						jMenuProject.add(jMenuItem5);
						jMenuItem5.setText("jMenuItem5");
						jMenuItem5.setAction(getAbstractActionBuild());
					}
					{
						jMenuItem6 = new JMenuItem();
						jMenuProject.add(jMenuItem6);
						jMenuItem6.setText("jMenuItem6");
						jMenuItem6.setAction(getAbstractActionPlay());
					}
					{
						jMenuItem7 = new JMenuItem();
						jMenuProject.add(jMenuItem7);
						jMenuItem7.setText("jMenuItem7");
						jMenuItem7.setAction(getAbstractActionCompile());
					}
					{
						jMenuItem8 = new JMenuItem();
						jMenuProject.add(jMenuItem8);
						jMenuProject.add(getJMenuItem10());
						jMenuItem8.setText("jMenuItem8");
						jMenuItem8.setAction(getAbstractActionAST());
					}
				}
				{
					jMenuHelp = new JMenu();
					jMenuBar1.add(getJMenuHelp());
					jMenuHelp.setText("Help");
					{
						jMenuItem9 = new JMenuItem();
						jMenuHelp.add(jMenuItem9);
						jMenuHelp.add(getJMenuItem12());
						jMenuItem9.setText("About Author");
						jMenuItem9.setAction(getAbstractActionAbout());
					}
				}
			}
			pack();
			repaintJTree(null);
			this.setSize(810, 652);
			this.setResizable(false);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	public JMenuBar getJMenuBar1() {
		return jMenuBar1;
	}
	
	public JMenu getJMenuProject() {
		return jMenuProject;
	}
	
	public JMenu getJMenuHelp() {
		return jMenuHelp;
	}
	
	public AbstractAction getAbstractActionNew() {
		if(abstractActionNew == null) {
			abstractActionNew = new AbstractAction("New", null) {
				public void actionPerformed(ActionEvent evt) {
					if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
					getJTextAreaCode().setText("");
					try {
						jTextAreaStaff.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("LASSUS.TTF")).deriveFont(128f));
						jTextAreaStaff.setText("`555555555555555555555555555555-\n`55555555555555555555555555555-=");
					} catch (FontFormatException | IOException e) {
						jTextAreaStaff.setText("LASSUS Font not found! Function disabled!");
					}
					
					repaintJTree(null);
					fileName="";
				}
			};
		}
		return abstractActionNew;
	}
	
	public AbstractAction getAbstractActionOpen() {
		if(abstractActionOpen == null) {
			abstractActionOpen = new AbstractAction("Open", null) {
				public void actionPerformed(ActionEvent evt) {
					
					if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
					int returnVal = getJFileChooserOpen().showOpenDialog(PentaJFrame.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = getJFileChooserOpen().getSelectedFile();
						//getJTextFieldFileToSend().setText(file.getName());
						//filePathToSend = file.getAbsolutePath(); 
						//System.out.println(file.getAbsolutePath());
						StringBuffer fileBuffer;
						String fileString=null;
						String line;
						try {
							FileReader in = new FileReader(file);
							BufferedReader dis = new BufferedReader(in);
							fileBuffer = new StringBuffer();
							while ((line = dis.readLine()) != null) {
								fileBuffer.append(line+"\n");
							}
							in.close();
							fileString = fileBuffer.toString();
							//Necessario causa character encoding
							//System.out.println(System.getProperty("os.name"));
							if(System.getProperty("os.name").contains("Windows")) {
								fileString=fileString.replace('Ò','“');
							} else fileString=fileString.replace('ì','“');
						}
						catch  (IOException e ) {
							getJTextAreaConsole().setText("File not found!");
						}
						getJTextAreaCode().setText(fileString);
						getJTextAreaConsole().setText("File "+file.getName()+" open!");
						repaintJTree(null);
						try {
							jTextAreaStaff.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("LASSUS.TTF")).deriveFont(128f));
							jTextAreaStaff.setText("`555555555555555555555555555555-\n`55555555555555555555555555555-=");
						} catch (FontFormatException | IOException e) {
							jTextAreaStaff.setText("LASSUS Font not found! Function disabled!");

						}
						
						fileName=file.getName();
					}
				
					
				}
			};
		}
		return abstractActionOpen;
	}
	
	public AbstractAction getAbstractActionSave() {
		if(abstractActionSave == null) {
			abstractActionSave = new AbstractAction("Save as", null) {
				public void actionPerformed(ActionEvent evt) {
					if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
					{
						int returnVal = getJFileChooserOpen().showSaveDialog(PentaJFrame.this);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							int response = JOptionPane.OK_OPTION;
							File file = getJFileChooserOpen().getSelectedFile();
							if (file.exists ()) {
								response = JOptionPane.showConfirmDialog (null,
										"Overwrite existing file?","Confirm Overwrite",
										JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE);
							}
							if (response != JOptionPane.CANCEL_OPTION){
								try {
									PrintWriter out =
										new PrintWriter (new BufferedWriter (new FileWriter (file)));
									out.print (getJTextAreaCode().getText());
									out.flush ();
									out.close ();
								}
								catch (IOException e) {
									e.printStackTrace();
								}
							}
							fileName=file.getName();
							getJTextAreaConsole().setText("File "+file.getName()+" saved!");

							//					//
							//				      JTextArea text = new JTextArea("This is\na test.");
							//				      
							//				        FileWriter out = new FileWriter("test.txt");
							//				        text.write(out);
							//				        out.close();
							//				        //
						}
					}	
					
					
					
				}
			};
		}
		return abstractActionSave;
	}
	
	public AbstractAction getAbstractActionExit() {
		if(abstractActionExit == null) {
			abstractActionExit = new AbstractAction("Exit", null) {
				public void actionPerformed(ActionEvent evt) {
					if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
					System.exit(0);
					
				}
			};
		}
		return abstractActionExit;
	}
	
	public AbstractAction getAbstractActionBuild() {
		if(abstractActionBuild == null) {
			abstractActionBuild = new AbstractAction("Build", null) {
				public void actionPerformed(ActionEvent evt) {
					if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
					if(!parserInit) parser= new PentaParser(new StringReader(getJTextAreaCode().getText()));
					else parser.ReInit(new StringReader(getJTextAreaCode().getText()));
					parserInit=true;
					PentaVisitor v=null;
					try {
						v = new PentaVisitor(getJTextAreaConsole());
						Scope s;
						s=PentaParser.Scope();
						s.accept(v);
					} catch (MidiUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						v.consolePrint(e.getMessage());
					}
					
					
				}
			};
		}
		return abstractActionBuild;
	}
	
	public AbstractAction getAbstractActionPlay() {
		if(abstractActionPlay == null) {
			abstractActionPlay = new AbstractAction("Play", null) {
				public void actionPerformed(ActionEvent evt) {
					
					if(!parserInit) parser= new PentaParser(new StringReader(getJTextAreaCode().getText()));
					else parser.ReInit(new StringReader(getJTextAreaCode().getText()));
					parserInit=true;
					PentaVisitor v=null;
					try {
						v = new PentaVisitor(getJTextAreaConsole());
						final Scope s;
						s=PentaParser.Scope();
						s.accept(v);
						if(v.isSuccess())
						{
							if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
							current=new Thread( new Runnable(){
								public void run()
								{
									PentaPlayVisitor p;
									try {
										p = new PentaPlayVisitor(getJTextAreaConsole());
										s.accept(p);
									} catch (MidiUnavailableException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}	
							});
							current.start();
						}
					} catch (MidiUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						v.consolePrint(e.getMessage());
					}
					
					
					
				}
			};
		}
		return abstractActionPlay;
	}
	
	public AbstractAction getAbstractActionCompile() {
		if(abstractActionCompile == null) {
			abstractActionCompile = new AbstractAction("Compile to MIDI file", null) {
				public void actionPerformed(ActionEvent evt) {
					if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
					if(!parserInit) parser= new PentaParser(new StringReader(getJTextAreaCode().getText()));
					else parser.ReInit(new StringReader(getJTextAreaCode().getText()));
					parserInit=true;
					PentaVisitor v=null;
					try {
						v = new PentaVisitor(getJTextAreaConsole());
						Scope s;
						s=PentaParser.Scope();
						s.accept(v);
						if(v.isSuccess())
						{
							PentaMidiCompilerVisitor p= new PentaMidiCompilerVisitor(getJTextAreaConsole());
							s.accept(p);
						}
					} catch (MidiUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						v.consolePrint(e.getMessage());
					}
					
				}
			};
		}
		return abstractActionCompile;
	}
	
	private AbstractAction getAbstractActionAST() {
		if(abstractActionAST == null) {
			abstractActionAST = new AbstractAction("Visualize AST", null) {
				public void actionPerformed(ActionEvent evt) {
					if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
					if(!parserInit) parser= new PentaParser(new StringReader(getJTextAreaCode().getText()));
					else parser.ReInit(new StringReader(getJTextAreaCode().getText()));
					parserInit=true;
					PentaVisitor v=null;
					try {
						v = new PentaVisitor(getJTextAreaConsole());
						Scope s;
						s=PentaParser.Scope();
						s.accept(v);
						if(v.isSuccess())
						{
							PentaASTVisitor p= new PentaASTVisitor();
							s.accept(p);
							repaintJTree(p.getTree());
							getJTextAreaConsole().setText("AST successfully generated!");
						}
					} catch (MidiUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						v.consolePrint(e.getMessage());
					}
				}
			};
		}
		return abstractActionAST;
	}
	
	public AbstractAction getAbstractActionAbout() {
		if(abstractActionAbout == null) {
			abstractActionAbout = new AbstractAction("About Author", null) {
				public void actionPerformed(ActionEvent evt) {
					getJDialogAbout();
					jDialogAbout.move(600,400);
					jDialogAbout.setResizable(false);
					jDialogAbout.show();
				}
			};
		}
		return abstractActionAbout;
	}
	
	public JPanel getJPanel1() {
		if(jPanel1 == null) {
			jPanel1 = new JPanel();
			GroupLayout jPanel1Layout = new GroupLayout((JComponent)jPanel1);
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.setPreferredSize(new java.awt.Dimension(794, 590));
			jPanel1Layout.setHorizontalGroup(jPanel1Layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(jPanel1Layout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
				        .addGroup(jPanel1Layout.createParallelGroup()
				            .addComponent(getJScrollPane1(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 585, GroupLayout.PREFERRED_SIZE)
				            .addComponent(getJScrollPane2(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 585, GroupLayout.PREFERRED_SIZE))
				        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				        .addComponent(getJScrollPane3(), GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE))
				    .addGroup(jPanel1Layout.createSequentialGroup()
				        .addComponent(getJScrollPane4(), GroupLayout.PREFERRED_SIZE, 774, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(10, Short.MAX_VALUE));
			jPanel1Layout.setVerticalGroup(jPanel1Layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(jPanel1Layout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
				        .addComponent(getJScrollPane1(), GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)
				        .addGap(20)
				        .addComponent(getJScrollPane2(), GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE))
				    .addComponent(getJScrollPane3(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 319, GroupLayout.PREFERRED_SIZE))
				.addGap(20)
				.addComponent(getJScrollPane4(), 0, 229, Short.MAX_VALUE)
				.addContainerGap());
		}
		return jPanel1;
	}
	
	public JTextArea getJTextAreaCode() {
		if(jTextAreaCode == null) {
			jTextAreaCode = new JTextArea();
		}
		return jTextAreaCode;
	}
	
	public JTextArea getJTextAreaConsole() {
		if(jTextAreaConsole == null) {
			jTextAreaConsole = new JTextArea();
			jTextAreaConsole.setText("PENTA 0.1 Console");
			jTextAreaConsole.setBackground(new java.awt.Color(0,0,0));
			jTextAreaConsole.setForeground(new java.awt.Color(255,255,255));
			jTextAreaConsole.setEditable(false);
		}
		return jTextAreaConsole;
	}
	
	public JFileChooser getJFileChooserOpen() {
		if(jFileChooserOpen == null) {
			jFileChooserOpen = new JFileChooser();
		}
		return jFileChooserOpen;
	}
	
	private JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJTextAreaCode());
		}
		return jScrollPane1;
	}
	
	private JScrollPane getJScrollPane2() {
		if(jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(getJTextAreaConsole());
		}
		return jScrollPane2;
	}
	
	public JTree getJTreeAST() {
		if(jTreeAST == null) {
			jTreeAST = new JTree();
		}
		return jTreeAST;
	}
	
	private JScrollPane getJScrollPane3() {
		if(jScrollPane3 == null) {
			jScrollPane3 = new JScrollPane();
			jScrollPane3.setViewportView(getJTreeAST());
		}
		return jScrollPane3;
	}
	
	private void repaintJTree(TreeNode tree)
	{
		jTreeAST.setModel(new DefaultTreeModel(tree));
	}
	
	public JTextArea getJTextAreaStaff() {
		if(jTextAreaStaff == null) {
			jTextAreaStaff = new JTextArea();
			jTextAreaStaff.setEditable(false);
			try {
				jTextAreaStaff.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("LASSUS.TTF")).deriveFont(128f));
				jTextAreaStaff.setText("`555555555555555555555555555555-\n`55555555555555555555555555555-=");
			} catch (FontFormatException | IOException e) {
				jTextAreaStaff.setText("LASSUS Font not found! Function disabled!");
			}
			
		}
		return jTextAreaStaff;
	}
	
	private JScrollPane getJScrollPane4() {
		if(jScrollPane4 == null) {
			jScrollPane4 = new JScrollPane();
			jScrollPane4.setViewportView(getJTextAreaStaff());
		}
		return jScrollPane4;
	}
	
	private JMenuItem getJMenuItem10() {
		if(jMenuItem10 == null) {
			jMenuItem10 = new JMenuItem();
			jMenuItem10.setText("jMenuItem10");
			jMenuItem10.setAction(getAbstractActionStaff());
		}
		return jMenuItem10;
	}
	
	public AbstractAction getAbstractActionStaff() {
		if(abstractActionStaff == null) {
			abstractActionStaff = new AbstractAction("Write Staff", null) {
				public void actionPerformed(ActionEvent evt) {
					if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
					if(!parserInit) parser= new PentaParser(new StringReader(getJTextAreaCode().getText()));
					else parser.ReInit(new StringReader(getJTextAreaCode().getText()));
					parserInit=true;
					PentaVisitor v=null;
					try {
						v = new PentaVisitor(getJTextAreaConsole());
						Scope s;
						s=PentaParser.Scope();
						s.accept(v);
						if(v.isSuccess())
						{
							PentaStaffCompilerVisitor p= new PentaStaffCompilerVisitor(getJTextAreaStaff());
							s.accept(p);
							getJTextAreaConsole().setText("Staff successfully written!");
						}
					} catch (MidiUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						v.consolePrint(e.getMessage());
					} catch (FontFormatException e) {
						jTextAreaStaff.setText("LASSUS Font not found! Function disabled!");
					} catch (IOException e) {
						jTextAreaStaff.setText("LASSUS Font not found! Function disabled!");
					}
					
				}
			};
		}
		return abstractActionStaff;
	}
	
	private JMenuItem getJMenuItem4() {
		if(jMenuItem4 == null) {
			jMenuItem4 = new JMenuItem();
			jMenuItem4.setText("jMenuItem4");
			jMenuItem4.setAction(getAbstractActionPrint());
		}
		return jMenuItem4;
	}
	
	public AbstractAction getAbstractActionPrint() {
		if(abstractActionPrint == null) {
			abstractActionPrint = new AbstractAction("Print Staff", null) {
				public void actionPerformed(ActionEvent evt) {
					if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
					TextPrinter printer;
					try {
						printer = new TextPrinter(fileName,jTextAreaStaff.getText(),Font.createFont(Font.TRUETYPE_FONT, new File("LASSUS.TTF")).deriveFont(64f));
						PrinterJob job = PrinterJob.getPrinterJob();
				         job.setPrintable(printer);
				         boolean ok = job.printDialog();
				         if (ok) {
				             try {
				                  job.print();
				             } catch (PrinterException ex) {
									ex.printStackTrace();
				             }
				         }
					} catch (FileNotFoundException e) {
						
					} catch (FontFormatException e) {
						jTextAreaStaff.setText("LASSUS Font not found! Function disabled!");						e.printStackTrace();
					} catch (IOException e) {
						jTextAreaStaff.setText("LASSUS Font not found! Function disabled!");
					}
					
				}
			};
		}
		return abstractActionPrint;
	}
	
	private JMenuItem getJMenuItem11() {
		if(jMenuItem11 == null) {
			jMenuItem11 = new JMenuItem();
			jMenuItem11.setText("jMenuItem11");
			jMenuItem11.setAction(getAbstractAction1());
		}
		return jMenuItem11;
	}
	
	public AbstractAction getAbstractAction1() {
		if(abstractAction1 == null) {
			abstractAction1 = new AbstractAction("Exit", null) {
				public void actionPerformed(ActionEvent evt) {
					if(current!=null){ current.stop(); jTextAreaConsole.setText(""); }
					System.exit(0);
				}
			};
		}
		return abstractAction1;
	}
	
	public JDialog getJDialogAbout() {
		if(jDialogAbout == null) {
			jDialogAbout = new JDialog(this);
			jDialogAbout.setPreferredSize(new java.awt.Dimension(514, 283));
			jDialogAbout.setTitle("About me...");
			jDialogAbout.setModal(true);
			jDialogAbout.getContentPane().add(getJPanelDatas(), BorderLayout.CENTER);
			jDialogAbout.setSize(514, 283);
			
		}
		return jDialogAbout;
	}

	public JPanel getJPanelDatas() {
		if(jPanelDatas == null) {
			jPanelDatas = new JPanel();
			GroupLayout jPanelDatasLayout = new GroupLayout((JComponent)jPanelDatas);
			jPanelDatas.setLayout(jPanelDatasLayout);
			jPanelDatas.setPreferredSize(new java.awt.Dimension(488, 242));
			jPanelDatasLayout.setHorizontalGroup(jPanelDatasLayout.createSequentialGroup()
				.addComponent(getJPanelPhoto(), GroupLayout.PREFERRED_SIZE, 249, GroupLayout.PREFERRED_SIZE)
				.addGap(29)
				.addGroup(jPanelDatasLayout.createParallelGroup()
				    .addGroup(jPanelDatasLayout.createSequentialGroup()
				        .addGap(0, 0, Short.MAX_VALUE)
				        .addComponent(getJLabelMail(), GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE))
				    .addGroup(GroupLayout.Alignment.LEADING, jPanelDatasLayout.createSequentialGroup()
				        .addComponent(getJLabelMatrix(), GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 97, Short.MAX_VALUE))
				    .addGroup(GroupLayout.Alignment.LEADING, jPanelDatasLayout.createSequentialGroup()
				        .addComponent(getJLabelName(), GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 28, Short.MAX_VALUE)))
				.addContainerGap(15, 15));
			jPanelDatasLayout.setVerticalGroup(jPanelDatasLayout.createParallelGroup()
				.addComponent(getJPanelPhoto(), GroupLayout.Alignment.LEADING, 0, 307, Short.MAX_VALUE)
				.addGroup(GroupLayout.Alignment.LEADING, jPanelDatasLayout.createSequentialGroup()
				    .addGap(42)
				    .addComponent(getJLabelName(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(getJLabelMatrix(), GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(getJLabelMail(), GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
				    .addContainerGap(183, Short.MAX_VALUE)));
		}
		return jPanelDatas;
	}
	
	public JLabel getJLabelName() {
		if(jLabelName == null) {
			jLabelName = new JLabel();
			jLabelName.setText("Matteo Poggi");
		}
		return jLabelName;
	}
	
	private JLabel getJLabelMatrix() {
		if(jLabelMatrix == null) {
			jLabelMatrix = new JLabel();
			jLabelMatrix.setText("0000674767");
		}
		return jLabelMatrix;
	}
	
	private JLabel getJLabelMail() {
		if(jLabelMail == null) {
			jLabelMail = new JLabel();
			jLabelMail= linkify("matteo.poggi6@studio.unibo.it","mailto:matteo.poggi6@studio.unibo.it?cc=matteo.poggi6@gmail.com","Contact me!");
		}
		return jLabelMail;
	}
	
	public JPanel getJPanelPhoto() {
		if(jPanelPhoto == null) {
			jPanelPhoto = new JPanel();
			jPanelPhoto.add(getJLabelPhoto());
		}
		return jPanelPhoto;
	}
	
	public JLabel getJLabelPhoto() {
		if(jLabelPhoto == null) {
			jLabelPhoto = new JLabel();
			jLabelPhoto.setPreferredSize(new java.awt.Dimension(218, 225));
			jLabelPhoto.setIcon(new ImageIcon(getClass().getClassLoader().getResource("io.jpg")));
		}
		return jLabelPhoto;
	}
	
	public static JLabel linkify(final String text, String URL, String toolTip)
	{
		URI temp = null;
		try
		{
			temp = new URI(URL);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		final URI uri = temp;
		final JLabel link = new JLabel();
		link.setText("<HTML><FONT color=\"#000099\">"+text+"</FONT></HTML>");
		if(!toolTip.equals(""))
			link.setToolTipText(toolTip);
		link.setCursor(new Cursor(Cursor.HAND_CURSOR));
		link.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent arg0)
			{
				link.setText("<HTML><FONT color=\"#000099\">"+text+"</FONT></HTML>");
			}

			public void mouseEntered(MouseEvent arg0)
			{
				link.setText("<HTML><FONT color=\"#000099\"><U>"+text+"</U></FONT></HTML>");
			}

			public void mouseClicked(MouseEvent arg0)
			{
				if (Desktop.isDesktopSupported())
				{
					try
					{
						Desktop.getDesktop().browse(uri);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					JOptionPane pane = new JOptionPane("Could not open link.");
					JDialog dialog = pane.createDialog(new JFrame(), "");
					dialog.setVisible(true);
				}
			}

			public void mousePressed(MouseEvent e)
			{
			}

			public void mouseReleased(MouseEvent e)
			{
			}
		});
		return link;
	}
	
	private JMenuItem getJMenuItem12() {
		if(jMenuItem12 == null) {
			jMenuItem12 = new JMenuItem();
			jMenuItem12.setText("jMenuItem12");
			jMenuItem12.setAction(getAbstractActionAboutPenta());
		}
		return jMenuItem12;
	}
	
	public AbstractAction getAbstractActionAboutPenta() {
		if(abstractActionAboutPenta == null) {
			abstractActionAboutPenta = new AbstractAction("About Penta", null) {
				public void actionPerformed(ActionEvent evt) {
					getJDialogAboutPenta().move(600,400);
					getJDialogAboutPenta().show();
				}
			};
		}
		return abstractActionAboutPenta;
	}
	
	public JDialog getJDialogAboutPenta() {
		if(jDialogAboutPenta == null) {
			jDialogAboutPenta = new JDialog(this);
			GroupLayout jDialogAboutPentaLayout = new GroupLayout((JComponent)jDialogAboutPenta.getContentPane());
			jDialogAboutPenta.getContentPane().setLayout(jDialogAboutPentaLayout);
			jDialogAboutPenta.setTitle("About Penta...");
			jDialogAboutPenta.setPreferredSize(new java.awt.Dimension(381, 224));
			jDialogAboutPenta.setModal(true);
			jDialogAboutPenta.setSize(381, 224);
			jDialogAboutPentaLayout.setHorizontalGroup(jDialogAboutPentaLayout.createSequentialGroup()
				.addContainerGap(24, 24)
				.addGroup(jDialogAboutPentaLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, jDialogAboutPentaLayout.createSequentialGroup()
				        .addComponent(getJLabelDoc(), GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
				        .addGap(36))
				    .addGroup(GroupLayout.Alignment.LEADING, jDialogAboutPentaLayout.createSequentialGroup()
				        .addComponent(getJLabelGrammar(), GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
				        .addGap(19))
				    .addComponent(getJLabelVersion(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
				.addGap(31)
				.addComponent(getJLabelImage(), 0, 163, Short.MAX_VALUE)
				.addContainerGap(17, 17));
			jDialogAboutPentaLayout.setVerticalGroup(jDialogAboutPentaLayout.createSequentialGroup()
				.addContainerGap(30, 30)
				.addGroup(jDialogAboutPentaLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, jDialogAboutPentaLayout.createSequentialGroup()
				        .addComponent(getJLabelVersion(), GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 41, Short.MAX_VALUE)
				        .addComponent(getJLabelGrammar(), GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
				        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, GroupLayout.PREFERRED_SIZE)
				        .addComponent(getJLabelDoc(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				        .addGap(14))
				    .addComponent(getJLabelImage(), GroupLayout.Alignment.LEADING, 0, 126, Short.MAX_VALUE))
				.addContainerGap(29, 29));
		}
		return jDialogAboutPenta;
	}
	
	private JLabel getJLabelGrammar() {
		if(jLabelGrammar == null) {
			jLabelGrammar = new JLabel();
			jLabelGrammar=linkify("BNF GRAMMAR","PentaGrammar.html","Open grammar file!");
		}
		return jLabelGrammar;
	}
	
	public JLabel getJLabelDoc() {
		if(jLabelDoc == null) {
			jLabelDoc = new JLabel();
			 try {
				String current = new java.io.File( "." ).getCanonicalPath().replace("\\", "/");
				jLabelDoc=linkify("Javadoc",current+"/doc/index.html","Open javadoc!");
			 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return jLabelDoc;
	}
	
	private JLabel getJLabelVersion() {
		if(jLabelVersion == null) {
			jLabelVersion = new JLabel();
			jLabelVersion.setText("Penta v0.1");
		}
		return jLabelVersion;
	}
	
	public JLabel getJLabelImage() {
		if(jLabelImage == null) {
			jLabelImage = new JLabel();
			jLabelImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Chiave di violino.jpg")));
		}
		return jLabelImage;
	}

}


