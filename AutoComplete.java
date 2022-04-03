import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
public class AutoComplete implements DocumentListener{
    ///////////////////////
    //AUTO COMPLETE METHODS
    ///////////////////////

    // create a thread for auto complete
    // variables for auto complete function
    public static final String COMMIT_ACTION = "commit";
    public static enum Mode { INSERT, COMPLETION };
    private Mode mode = Mode.INSERT;
    private final Engine engine = new Engine();
    
    private JTextField searchField;
    public AutoComplete(JTextField searchField) {
        this.searchField = searchField;
    }

    @Override
    public void insertUpdate(DocumentEvent de) {
        // TODO Auto-generated method stub
        if (de.getLength() != 1) {
            return;
        }
        
        int pos = de.getOffset();
        String content = null;
        try {
            content = this.searchField.getText(0, pos + 1);
        } catch (BadLocationException e) {
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
        String match = engine.lookUp(prefix);
        if (match != null){
            String suggestion = match.substring(pos - w);
            SwingUtilities.invokeLater(new CompleteTask(searchField,pos + 1,suggestion));
        } else {
            mode = Mode.INSERT;
        }

        
    }
    @Override
    public void removeUpdate(DocumentEvent e){
        changedUpdate(e);
    }
    @Override
    public void changedUpdate(DocumentEvent e){
        
        
    }
    private class CompleteTask implements Runnable {
        private String completion;
        private int position;
        private JTextField searchField;

        public CompleteTask(JTextField searchField, int position, String completion){
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
        private JTextField searchField;
        public CommitAction (JTextField searchField){
            this.searchField = searchField;
        }
        public void actionPerformed(ActionEvent ev) {
            if (mode == Mode.COMPLETION) {
                int pos = this.searchField.getSelectionEnd();
                StringBuffer sb = new StringBuffer(this.searchField.getText());
                sb.insert(pos," ");
                this.searchField.setText(sb.toString());
                this.searchField.setCaretPosition(pos + 1);
                mode = Mode.INSERT;
            } else {
                this.searchField.replaceSelection("\n");
            }
        }
    }
}
