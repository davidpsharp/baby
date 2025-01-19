package com.ccs.baby.debug;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;



public class BabyAsmSyntaxHighlighter {
    
    static Color color_VSCodeClassNameGreen = new Color(75,190,167);
    static Color color_VSCodeVariableNameBlue = new Color(135,191,221);
    static Color color_VSCodeCommentGreen = new Color(97,137,79);
    static Color color_VSCodeMethodNameYellow = new Color(197,197,153);
    static Color color_VSCodeNumberGreen = new Color(182,206,169);

    static Color color_background_off_black = new Color(31,31,31);
    static Color color_foreground_yellow = color_VSCodeMethodNameYellow;

    Style mnemonicStyle;
    Style commentStyle;
    Style numberStyle;
    Style labelStyle;

    Pattern compiledPattern;

    public BabyAsmSyntaxHighlighter(JTextPane textPane)
    {
        String mnemonics = "\\b(cmp|stp|num|sub|ldn|sto|adr|jmp|jrp|CMP|STP|NUM|SUB|LDN|STO|ADR|JMP|JRP)\\b";
        String comments = "\\;.*"; // if i add \\b at start of this then no more mismatched magenta comments
        String numbers = "(-)*(\\d)+"; // TODO: asterisk is wrong - should be 0 or 1 minus signs
        String labels = "\\b[A-Za-z\\_]+\\:"; // likely wrong

        // TODO: mnemonics showing up in comments need coloring ascomments 
        // TODO: remove decimals - this is an integer number system
        // TODO: add hex and binary number support
        // TODO: get parent style first to pass in when adding new style?
        // TODO: consider different highlight numbers for NUM different to line nmbers for LDN, STO, SUB ?
        // TODO: highlight colour when selected isn't good - bright blue.
        // TODO: build list of defined labels to flag up undefined labels in a different colour
        // TODO: highlight unused labels that have been defined unnecessarily


        // Set font style attributes for each type
        mnemonicStyle = textPane.addStyle("MnemonicStyle", null);
        StyleConstants.setForeground(mnemonicStyle, BabyAsmSyntaxHighlighter.color_VSCodeVariableNameBlue);

        commentStyle = textPane.addStyle("CommentStyle", null);
        StyleConstants.setForeground(commentStyle, BabyAsmSyntaxHighlighter.color_VSCodeCommentGreen);

         numberStyle = textPane.addStyle("NumberStyle", null);
        StyleConstants.setForeground(numberStyle, BabyAsmSyntaxHighlighter.color_VSCodeNumberGreen);

        labelStyle = textPane.addStyle("LabelStyle", null);
        StyleConstants.setForeground(labelStyle, BabyAsmSyntaxHighlighter.color_VSCodeClassNameGreen);

        // build single pattern
        String pattern = String.format("(%s)|(%s)|(%s)|(%s)", mnemonics, comments, numbers, labels);

        compiledPattern = Pattern.compile(pattern);
    }

    // TODO: issues seen w interactive editing re-highlighting, likely inter-related.
    // 1) in general, nothing is re-applying the default style to the doc when the reg-exes don't apply - do we need to reset the whole line on edit?
    // 2) should only match first mnemonic on the line, a second is incorrect
    // 3) when inserting a new line it inherits the style of the previous line e.g. append after a comment will still be styled green
    // 4) removing ; from start of comment doesn't uncomment it

    public void highlight(StyledDocument doc) {
     
        try
        {
            String text = doc.getText(0, doc.getLength());
            Matcher matcher = compiledPattern.matcher(text);

            while (matcher.find())
            {
                // The group number logic is tricky depending on your regexp!! https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#groupname
                // Capturing groups are numbered by counting their opening parentheses from left to right. In the expression ((A)(B(C))), for example, there are four such groups:
                //  1    	((A)(B(C)))
                //  2    	(A)
                //  3    	(B(C))
                //  4    	(C)
                // Group zero always stands for the entire expression.

                if (matcher.group(1) != null) {
                    //debugMatcher(1,matcher,doc);
                    doc.setCharacterAttributes(matcher.start(1), matcher.end(1) - matcher.start(1), mnemonicStyle, false);
                } else if (matcher.group(3) != null) {
                    //debugMatcher(3,matcher,doc);
                    doc.setCharacterAttributes(matcher.start(3), matcher.end(3) - matcher.start(3), commentStyle, false);
                } else if (matcher.group(4) != null) {
                    //debugMatcher(4,matcher,doc);
                    doc.setCharacterAttributes(matcher.start(4), matcher.end(4) - matcher.start(4), numberStyle, false);
                } else if (matcher.group(7) != null) {
                    //debugMatcher(7,matcher,doc);
                    doc.setCharacterAttributes(matcher.start(7), matcher.end(7) - matcher.start(7), labelStyle, false);
                }
            }
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    private static void debugMatcher(int group, Matcher matcher, StyledDocument doc) throws BadLocationException
    {
        System.out.println(group + " " + matcher.group(group) + ": start:" + matcher.start(0) + " end:" + matcher.end(0) + " substr:" + doc.getText(matcher.start(0), matcher.end(0) - matcher.start(0)));
    }
}
