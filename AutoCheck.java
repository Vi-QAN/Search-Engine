/*
    Class to perform spell checking on the entered words
*/


import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.Color;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
public class AutoCheck implements DocumentListener {
    private JTextPane searchTerm;

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
        
        // check if document is empty
        if (pos < 1){
            return;
        }

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

        String word = content.substring(start + 1,pos).toLowerCase();
        
        // find occurence in the dictionary
        boolean result = Engine.findOccurrence(word);

        if (result){
            return;
        }
        
        // find the matching strings with given word
        List<String> matches = similarityCheck(word,Engine.lookUp(word));

        // checker handling either highlight incorrect spelling word or make the change itself
        try {
            SwingUtilities.invokeLater(new Highlight(this.searchTerm,start + 1,matches,word));
        } catch (Exception e) {
            System.out.println("Error");
        }
        
        // try {
            
        //     this.searchTerm.getDocument().render(new Highlight(this.searchTerm));
        // } catch (Exception e){
        //     System.out.println("Error");
        // }
    }

    // filter out the strings that does not have the same length and 
    // cannot be similar after one swap
    private List<String> similarityCheck(String word, List<String> keys){
        if (keys == null){
            return null;
        }
        List<String> result = new ArrayList<String>(); 
        List<String> words = keys.stream()
                        .filter(key -> key.length() != word.length())
                        .collect(Collectors.toList());
        if (words.size() == 0){
            return null;
        }
        else {
            for (int i = 0; i < words.size();i++){
                String compareStr = result.get(i);
                String backUp = compareStr;
                int countDiff = 0;
                int[] diffs = new int[2];
                for (int j = 0;j < word.length(); j++){
                    if (word.charAt(j) != compareStr.charAt(j)){
                        diffs[countDiff] = j;
                        countDiff++;
                    }
                    if (countDiff > 2){
                        break;
                    }
                }
                if (countDiff <= 2){
                    char[] arr = compareStr.toCharArray();
                    char temp = arr[diffs[0]];
                    arr[diffs[0]] = arr[diffs[1]];
                    arr[diffs[1]] = temp;
                    compareStr = String.valueOf(arr);
                }
                if (compareStr.equals(word)){
                    result.add(backUp);
                }
            }
        }
        if (result.size() == 0){
            return null;
        }
        return result;
    }

    
    private class Highlight implements Runnable {
        private JTextPane searchTerm;
        private List<String> matches;
        private String word;
        private int position;

        // hightlight the word setting
        private static StyleContext context = new StyleContext();
        private static final Style wrongSpelling = context.addStyle("RED", null);
        
        public Highlight(JTextPane searchTerm,int position, List<String> matches, String word){
            this.searchTerm = searchTerm;
            this.matches = matches;
            this.word = word;
            this.position = position;
            wrongSpelling.addAttribute(StyleConstants.Foreground, Color.RED);
        }
        @Override
        public void run(){
            
            try {
                StringBuffer sb = new StringBuffer(this.searchTerm.getText());
                if (this.matches != null){
                    sb.replace(position, position + this.matches.get(0).length(), this.matches.get(0));
                    this.searchTerm.setText(sb.toString());
                }
                else {
                    String lhs = sb.substring(0, position);
                    String rhs = sb.substring(position + word.length());
                    this.searchTerm.setText("");
                    this.searchTerm.getDocument().insertString(0, lhs, null);
                    this.searchTerm.getDocument().insertString(position, word,wrongSpelling);
                    this.searchTerm.getDocument().insertString(position + word.length(), rhs,null);
                }
                
                
                
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        
    }
}
