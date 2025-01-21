package com.ccs.baby.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

public class Debugger extends JFrame implements ActionListener
{
    
    JTextPane textPane;
    JScrollPane scrollPane;

    StyledDocument doc;
    BabyAsmSyntaxHighlighter highlighter;

    Highlighter hilite;
    DefaultHighlightPainter currentLinePainter;

    Color currentDebugLineColor = new Color(76,76,25);
    Style currentLineStyle;

    private final boolean SHOW_IN_PROGRESS_DEBUGGER = false;

    public Debugger()
    {
        // if we don't want to share this feature yet then do nothing
        if(!SHOW_IN_PROGRESS_DEBUGGER)
            return;
            
        setTitle("Baby Debugger");
        setSize(1000, 800);

        // set up key UI components
        textPane = new JTextPane();
        doc = textPane.getStyledDocument();

        // wrap in a panel to prevent line-wrapping in the JTextPane
        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(textPane); 
        // TODO: mousewheel scroll speed seems slow - is this since I introduced noWrapPanel?

        scrollPane = new JScrollPane(noWrapPanel);

        this.add(scrollPane);

        highlighter = new BabyAsmSyntaxHighlighter(textPane);

        // default style
        textPane.setFont( new Font("Monospaced",Font.BOLD,13) );
        
        textPane.setForeground(BabyAsmSyntaxHighlighter.color_foreground_yellow);
        textPane.setBackground(BabyAsmSyntaxHighlighter.color_background_off_black);

        hilite = new MyHighlighter();
        textPane.setHighlighter(hilite);
        currentLinePainter = new DefaultHighlighter.DefaultHighlightPainter(currentDebugLineColor);

        //DebuggerDocumentListener docListener = new DebuggerDocumentListener(); // doc, highlighter);
        
        // set up style to be assigned to line with caret on
        //currentLineStyle = textPane.addStyle("currentLineStyle", null);
        //StyleConstants.setBackground(currentLineStyle, currentDebugLineColor);
        
        Caret caret = textPane.getCaret();
        caret.setBlinkRate(700);
        textPane.setCaret(caret);
        textPane.setCaretColor(Color.LIGHT_GRAY);
        
        // TODO: light OR dark style
        // TODO: handle tabs as spaces consistently
        // TODO: add menu bar?
        // TODO: slightly modify the grey background for the line with the caret on so easier to find
        // TODO: wide window then selecting a block (wider than longest row with characters on) and then deselecting still leaves selected colour on right side

        // quick test code to get something on screen
        try
        {
            testLoadFileIntoPane();

            highlighter.highlight(doc);

            setCaretPositionOnLine(6);

            int caretIndex = textPane.getCaretPosition();

            System.out.println("current line:" + getCurrentLine(textPane));

            // highlight line with caret currently on it (using style or highlighter TBC ???)
            //setStyleForCurrentLine(textPane);

            //System.out.println("line of caret:"+caretIndex + " start:"+lineStart + " end:" + lineEnd);
        }
        catch(Exception ex)
        {
            // and this is why java is out of fashion....
            System.out.println(ex.toString());
        }

        // set up updates to syntax highlighting
        doc.addDocumentListener(new DebuggerDocumentListener());

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae)
    {
        
    }

    // TODO: first line of doc is line 2 or maths wrong somewhere ?!
    private void setCaretPositionOnLine(int line)
    {
        // per example here: https://github.com/tips4java/tips4java/blob/main/source/RXTextUtilities.java
        Element root = textPane.getDocument().getDefaultRootElement();
        line = Math.max(line, 1);
        line = Math.min(line, root.getElementCount());
        int startOfLineOffset = root.getElement( line - 1 ).getStartOffset();
        textPane.setCaretPosition( startOfLineOffset );
    }

    // based on grabbed from https://forums.oracle.com/ords/apexds/post/getting-current-line-from-jtextpane-or-am-i-stupid-2320
    // TODO: needs rewrite - very inefficient but seems to work and shows how to access some of the element APIs, 
    public void setStyleForCurrentLine(JTextPane textTx) throws BadLocationException
	{

        // TODO: should I be using this DefaultHighlighter approach instead?
        // https://stackoverflow.com/questions/4670734/how-to-set-a-custom-background-color-on-a-line-in-a-jtextpane

        // Get section element
		Element section = textTx.getDocument().getDefaultRootElement();

		// Get number of paragraphs.
		// In a text pane, a span of characters terminated by single
		// newline is typically called a paragraph.
		int paraCount = section.getElementCount();

		int position = textTx.getCaret().getDot();
		
		// Get index ranges for each paragraph
		for (int i = 0; i < paraCount; i++)
		{
			Element e1 = section.getElement(i);
			
			int rangeStart = e1.getStartOffset();
			int rangeEnd = e1.getEndOffset();
			
            if (position >= rangeStart && position <= rangeEnd)
            {
                // replace set to false as want to merge with syntax highlighting attributes
                //doc.setCharacterAttributes(rangeStart, rangeEnd - rangeStart, currentLineStyle, false);
                // TODO: this only sets background to the last character on the line, if line empty will not show, should go whole way across the JTextPane.
                
                // try highlight approach, might do full line - not working
                hilite.addHighlight(rangeStart, rangeEnd - rangeStart, currentLinePainter);

                return;
            }
		}
    }

    // grabbed from https://forums.oracle.com/ords/apexds/post/getting-current-line-from-jtextpane-or-am-i-stupid-2320
    // TODO: needs rewrite - very inefficient but seems to work and shows how to access some of the element APIs, 
    public String getCurrentLine(JTextPane textTx)
	{
		// Get section element
		Element section = textTx.getDocument().getDefaultRootElement();

		// Get number of paragraphs.
		// In a text pane, a span of characters terminated by single
		// newline is typically called a paragraph.
		int paraCount = section.getElementCount();

		int position = textTx.getCaret().getDot();
		
		// Get index ranges for each paragraph
		for (int i = 0; i < paraCount; i++)
		{
			Element e1 = section.getElement(i);
			
			int rangeStart = e1.getStartOffset();
			int rangeEnd = e1.getEndOffset();
			
			try 
			{
				String para = textTx.getText(rangeStart, rangeEnd-rangeStart);
				
				if (position >= rangeStart && position <= rangeEnd)
					return para;
			}
			catch (BadLocationException ex) 
			{
				System.err.println("Get current line from editor error: " + ex.getMessage());
			}
		}
		return null;
	}
    

    // experimental logic to get demo up and running
    private void testLoadFileIntoPane() throws FileNotFoundException, IOException, BadLocationException
    {

        java.net.URL url = getClass().getClassLoader().getResource("demos/diffeqt.s");
        if(url == null)
            throw new IOException("File not found: " + "demos/diffeqt.s");
        java.io.InputStream assemblyReader = url.openStream();

		// create buffered reader from input stream
		BufferedReader reader = new BufferedReader(new InputStreamReader(assemblyReader));
        
        String line;
        while ((line = reader.readLine()) != null)
        {
            doc.insertString(doc.getLength(), line + "\n", null);
        }
        reader.close();
    }

   
    class DebuggerDocumentListener implements DocumentListener {
        //StyledDocument _doc;
        //BabyAsmSyntaxHighlighter _highlighter;

        //public DebuggerDocumentListener(StyledDocument doc, Highlighter highlighter)
        /*
        public DebuggerDocumentListener()
        {
            _doc = doc;
            _highlighter = highlighter;

        }
        */

        // TODO: would looping over element changes be more efficient? https://stackoverflow.com/questions/28095131/get-changed-content-in-documentevent 

        int updateCount = 0;

        @Override
        public void changedUpdate(DocumentEvent e) {
            // Gives notification that an attribute or set of attributes changed.
            // TODO: does this require syntax highlighting update, possibly not???
            // this fires 81 times for every keypress, presumably due to all doc style changes from the insert/update?
            //updateCount++;
            //System.out.println("changedUpdate:" + updateCount + " time:" + System.currentTimeMillis() );
            //updateHighlighting();
            //throw new UnsupportedOperationException("Unimplemented method 'changedUpdate'");
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            System.out.println("insertUpdate");
            // TODO: make much more efficient, surely only needs to update current line and possibly the previous OR next if enter is pressed
            // does that assumption still hold if a big block is pasted in or deleted?
            // would reduce changedUpdate firing
            //System.out.println("offset:" + e.getOffset() + " length:" + e.getLength() );
            
            updateHighlighting();
            //throw new UnsupportedOperationException("Unimplemented method 'insertUpdate'");
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            System.out.println("removeUpdate");
            updateHighlighting();
            //throw new UnsupportedOperationException("Unimplemented method 'removeUpdate'");
        }

        private void updateHighlighting()
        { 

            // need to postpone execution until Document lock is released otherwise get java.lang.IllegalStateException: Attempt to mutate in notification
            Runnable doHighlight = new Runnable() {
                @Override
                public void run() {
                    highlighter.highlight(doc);
                }
            };       
            SwingUtilities.invokeLater(doHighlight);

        }

    }


}

// experiment with example
// from https://stackoverflow.com/questions/4670734/how-to-set-a-custom-background-color-on-a-line-in-a-jtextpane
class MyHighlighter extends DefaultHighlighter
{

    private JTextComponent component;

    /**
     * @see javax.swing.text.DefaultHighlighter#install(javax.swing.text.JTextComponent)
     */
    @Override
    public final void install(final JTextComponent c)
    {
        super.install(c);
        this.component = c;
    }

    /**
     * @see javax.swing.text.DefaultHighlighter#deinstall(javax.swing.text.JTextComponent)
     */
    @Override
    public final void deinstall(final JTextComponent c)
    {
        super.deinstall(c);
        this.component = null;
    }

    /**
     * Same algo, except width is not modified with the insets.
     * 
     * @see javax.swing.text.DefaultHighlighter#paint(java.awt.Graphics)
     */
    @Override
    public final void paint(final Graphics g)
    {
        final Highlighter.Highlight[] highlights = getHighlights();
        final int len = highlights.length;
        for (int i = 0; i < len; i++)
        {
            Highlighter.Highlight info = highlights[i];
            if (info.getClass().getName().indexOf("LayeredHighlightInfo") > -1)
            {
                // Avoid allocing unless we need it.
                final Rectangle a = this.component.getBounds();
                final Insets insets = this.component.getInsets();
                a.x = insets.left;
                a.y = insets.top;
                // a.width -= insets.left + insets.right + 100;
                a.height -= insets.top + insets.bottom;
                for (; i < len; i++)
                {
                    info = highlights[i];
                    if (info.getClass().getName().indexOf(
                            "LayeredHighlightInfo") > -1)
                    {
                        final Highlighter.HighlightPainter p = info
                                .getPainter();
                        p.paint(g, info.getStartOffset(), info
                                .getEndOffset(), a, this.component);
                    }
                }
            }
        }
    }
}
