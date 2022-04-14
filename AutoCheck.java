/*
    Class to perform spell checking on the entered words
    
    every time a character is entered it will extract the word and search for that word in dictionary

    if word not found, it will be highlighted red 
*/

import java.awt.Color;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;


public class AutoCheck implements DocumentListener {
    private JTextPane searchTerm;


    // last correct character
    int lastCorrect = 0;

    public AutoCheck(JTextPane searchTerm){
        this.searchTerm = searchTerm;
    }

    @Override
    public void insertUpdate(DocumentEvent de){
        changedUpdate(de);
    }

    @Override
    public void removeUpdate(DocumentEvent de){
    }

    @Override
    public void changedUpdate(DocumentEvent de){
        if (de.getLength() != 1){
            return;
        }
        int pos = de.getOffset();
    

        // get the current content of text field
        String content = null;
        try {
            content = this.searchTerm.getText();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        // get the word start point
        int start;
        for (start = pos; start >= 0;start--){
            if (!Character.isLetter(content.charAt(start))){
                break;
            }
        }

        String word = content.substring(start + 1).toLowerCase();
        
        // find occurence in the dictionary
        boolean exist = Engine.findOccurrence(word);

        if (exist){
            // reset the color if it's correct word
            MutableAttributeSet mas = this.searchTerm.getInputAttributes();
            mas.removeAttribute(mas);
            return;
        }    

        // checker handling either highlight incorrect spelling word 
        try {
            SwingUtilities.invokeLater(new Highlight(this.searchTerm,start + 1,word));
            
        } catch (Exception e) {
            System.out.println("Error");
        }
    
    }

    // highlight class is a thread so that the wrong spelling word can be highlighted red
    // spontaneously while user is typing
    private class Highlight implements Runnable {
        private JTextPane searchTerm;
        private String word;
        private int position;

        // hightlight the word setting
        private static StyleContext context = new StyleContext();
        private static final Style wrongSpelling = context.addStyle("RED", null);
        
        public Highlight(JTextPane searchTerm,int position, String word){
            this.searchTerm = searchTerm;
            this.word = word;
            this.position = position;

            // set color style for wrong spelling word
            wrongSpelling.addAttribute(StyleConstants.Foreground, Color.RED);
        }
        @Override
        public void run(){
            try {
                // get substring of string before cursor index
                StringBuffer sb = new StringBuffer(this.searchTerm.getText());
                String lhs = sb.substring(0, position);

                // empty the text pane
                this.searchTerm.setText("");

                // insert string with no color
                this.searchTerm.getDocument().insertString(0, lhs, null);

                // insert word with red
                this.searchTerm.getDocument().insertString(position, word,wrongSpelling);
                
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }    
    }
    
  
}
