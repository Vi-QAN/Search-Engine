/*
    Code from this class is from online source

*/
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;

public class AutoComplete implements DocumentListener{
    ///////////////////////
    //AUTO COMPLETE METHODS
    ///////////////////////

    // create a thread for auto complete
    // variables for auto complete function
    public static final String COMMIT_ACTION = "commit";
    public static enum Mode { INSERT, COMPLETION };
    private Mode mode = Mode.INSERT;
    
    private JTextPane searchField;
    public AutoComplete(JTextPane searchField) {
        this.searchField = searchField;
    }

    @Override
    public void insertUpdate(DocumentEvent de) {
        if (de.getLength() != 1) {
            return;
        }
        
        int pos = de.getOffset();
        String content = null;
        try {
            content = searchField.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Find where the word starts
        int w;
        for (w = pos; w >= 0; w--) {
            if (! Character.isLetter(content.charAt(w))) {
                break;
            }
        }
        if (pos - w < 2) {
            // Too few chars
            return;
        }
        String prefix = content.substring(w + 1).toLowerCase();
        List<String> matches = Engine.lookUp(prefix);
        if (matches != null){
            String suggestion = matches.get(0).substring(pos - w);
            SwingUtilities.invokeLater(new CompleteTask(searchField,pos + 1,suggestion));
        } else {
            mode = Mode.INSERT;
        }

    }
    @Override
    public void removeUpdate(DocumentEvent e){
    }
    @Override
    public void changedUpdate(DocumentEvent de){

    }
    
    private class CompleteTask implements Runnable {
        private String completion;
        private int position;
        private JTextPane searchField;

        public CompleteTask(JTextPane searchField, int position, String completion){
            this.searchField = searchField;
            this.position = position;
            this.completion = completion;
        }
        public void run() {
            StringBuffer sb = new StringBuffer(this.searchField.getText());
            sb.insert(position, completion);
            this.searchField.setText(sb.toString());
            this.searchField.setCaretPosition(position + completion.length());
            this.searchField.moveCaretPosition(position);
            mode = Mode.COMPLETION;
        }
    }

    // dectect action for accepting suggested word
    public class CommitAction extends AbstractAction {
        private JTextPane searchField;
        public CommitAction (JTextPane searchField){
            this.searchField = searchField;
        }
        public void actionPerformed(ActionEvent ev) {
            if (mode == Mode.COMPLETION) {
                int pos = this.searchField.getSelectionEnd();
                StringBuffer sb = new StringBuffer(this.searchField.getText());
                sb.insert(pos," ");
                this.searchField.setText(sb.toString());
                this.searchField.setCaretPosition(pos);
                this.searchField.moveCaretPosition(pos + 1);
                mode = Mode.INSERT;
            } else {
                this.searchField.replaceSelection(" ");
            }
        }
    }
}
