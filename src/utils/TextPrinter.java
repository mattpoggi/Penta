package utils;

import java.awt.*;
import java.awt.event.*;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
 
/**
* Utility Class used to print text.
* 
* @author Matteo Poggi
* 
*/
public class TextPrinter implements Printable {
	private String name;
	private String text;
	private Font font;
 
	/**
	* Constructor. 
	* 
	*  * @param text
	 * 		text to be printed.
	 * @param font
	 * 		Font used for the text to be printed.
	* 
	* @author Matteo Poggi
	* 
	*/
	public TextPrinter(String name, String text, Font font)
	{
		this.name=name;
		this.text=text;
		this.font=font;
	}
 
	/**
	* Printing function. Split the text in many lines and print it (Assumption: printing only one page).
	* @throws PrinterException 
	* @param g
	* 		Graphic object for the rendering
	* @param pf
	* 		Font used for the text to be printed.
	* @param page
	* 		Number of page
	* 
	* @author Matteo Poggi
	* 
	*/
    public int print(Graphics g, PageFormat pf, int page) throws
                                                        PrinterException, ArrayIndexOutOfBoundsException {
 
       
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        StringTokenizer tk= new StringTokenizer(this.text);
        ArrayList<String> lines= new ArrayList<String>();
        while(tk.hasMoreTokens())
        {
        	lines.add(tk.nextToken("\n"));
        }
        int max=0;
        for(String s : lines)
        {
        	if(s.length()>max) max=s.length();
        }
        
        g.drawString(this.name, 0, 25);
 
        float size=64f;
        if(max>40) size= 41*size/max;
        g.setFont(this.font.deriveFont(size));
        FontMetrics metrics = g.getFontMetrics(this.font);
        int lineHeight = metrics.getHeight();
        double pageHeight = pf.getImageableHeight();
        
        int linesPerPage = ((int)pageHeight)/lineHeight;
        int numBreaks = (lines.size())/linesPerPage;
        int[] pageBreaks = new int[numBreaks];
        for (int b=0; b < numBreaks; b++) {
            pageBreaks[b] = (b+1)*linesPerPage; 
        }
        int y = 0; 
        int start = (page == 0) ? 0 : pageBreaks[page-1];
        int end   = (page == pageBreaks.length)
                         ? lines.size() : pageBreaks[page];
        for (int line=start; line<end; line++) {
            y += lineHeight;
            g.drawString(lines.get(line), 0, y);
        }
        
        
		//for(int i=0; i<lines.size();i++) g.drawString(lines.get(i), 0, 50*(i+1));
        
 
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
   
}