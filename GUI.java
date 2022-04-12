/*
    GUI class contains methods to handle display onto the screen
*/


import javax.swing.*;
import javax.swing.border.TitledBorder;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultStyledDocument;

import java.awt.event.*;
import java.awt.*;

import java.io.File;
import java.util.List;

public class GUI {
    // create main frame for GUI
    private JFrame frame;

    // create panel
    private JPanel mainPanel, displayField, searchField, resultField, resultPanel;

    // create JTextField for searchTerm
    private JTextField chosenFiles;
    private JTextPane searchTerm;
    
    // create button
    private JButton searchBtn;

    
    private final Engine engine = new Engine();
    
    final DefaultStyledDocument doc = new DefaultStyledDocument();

    public GUI() {
        // initialize properties for frame
        frame = new JFrame("My Google");
        frame.setSize(800,500);
        frame.setLocation(10, 20);
        frame.setVisible(true);
        frame.setLayout(null); // set frame layout to null as default value is BorderLayout
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

        // initialize properties for main panel
        mainPanel = new JPanel(); 
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0,0)); // remove the gap between flow layouts
        mainPanel.setBackground(Color.GRAY);
        mainPanel.setSize(new Dimension(frame.getWidth(),frame.getHeight()));

        // display field contains resultField and sortBy dropdown button
        // initialize properties for displayField
        displayField = new JPanel();
        displayField.setLayout(new FlowLayout(FlowLayout.LEFT,0,0)); 
        displayField.setBackground(Color.WHITE);
        displayField.setPreferredSize(new Dimension(mainPanel.getWidth(),mainPanel.getHeight() - 200));
        displayField.setLocation(0, 0);
        mainPanel.add(displayField);
        
        // add display result panel  to display field 
        addDisplay();

        // add sorting options dropdown button
        addFilter();

        // searchField contain searchTerm 
        // initialize properties for searchField
        searchField = new JPanel();
        searchField.setBorder(null);
        searchField.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchField.setBackground(Color.WHITE);
        searchField.setPreferredSize(new Dimension(mainPanel.getWidth(),mainPanel.getHeight() - displayField.getHeight()));
        mainPanel.add(searchField);

        // add search term text field to searchField panel and perform spell check on entered term 
        addSearch();

        // add choose file(s) including file choose button 
        // and text field to display chosen files 
        // that user want to search to searchField panel
        addChoosing();

        // add search button to searchField
        addSearchBtn();

        // add main panel to frame
        frame.add(mainPanel);
       
    }
    ///////////////////////////////////////////
    // METHODS FOR COMPONENTS IN DISPLAY FIELDS 
    // addDisplay, addFilter
    ///////////////////////////////////////////

    // method used for displaying returned esult according the chosen filter
    private void addDisplay() {
        // initialize properties for resultField
        resultField = new JPanel();
        resultField.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),"Result",TitledBorder.LEFT,TitledBorder.TOP,null,Color.BLUE
        ));
        Dimension resultFieldDimension = new Dimension(mainPanel.getWidth() - 150,mainPanel.getHeight() - 210);
        resultField.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        resultField.setPreferredSize(resultFieldDimension);
        resultField.setSize(resultFieldDimension);
        
        // Label dimension
        Dimension labelDimension = new Dimension(resultField.getWidth() / 2 - 10,20);
        JLabel fileName = new JLabel("File Name");
        fileName.setPreferredSize(labelDimension);
        JLabel accuracy = new JLabel("Accuracy(%)");
        accuracy.setPreferredSize(labelDimension);
        resultField.add(fileName);
        resultField.add(accuracy);

        Dimension resultPanelDimension = new Dimension(resultField.getWidth() - 7,resultField.getHeight() - 47);
        resultPanel = new JPanel();
        resultPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        resultPanel.setPreferredSize(resultPanelDimension);
        resultPanel.setSize(resultPanelDimension);
        resultField.add(new JScrollPane(resultPanel));

        displayField.add(resultField);
    }

    // method used for add filter button to displayField
    private void addFilter(){
        // filterPanel dimension
        Dimension filterDimension = new Dimension(120,mainPanel.getHeight() - 210);
        // create panel to obtain filter
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        filterPanel.setPreferredSize(filterDimension);
        
        filterPanel.setBackground(Color.WHITE);
        displayField.add(filterPanel);

        // available options
        String[] options = {Engine.SortOption.ACCURACY.toString(), Engine.SortOption.ALPHABET.toString()};
        
        // initialize properties for drop down
        JComboBox<String> dropDown = new JComboBox<>(options);
        dropDown.setPreferredSize(new Dimension((int)filterDimension.getWidth(),50));
        dropDown.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),"Filter By \u2B83",TitledBorder.LEFT,TitledBorder.TOP,null,Color.BLUE
        ));
        
        // add to display field
        filterPanel.add(dropDown);

        // option handler
        dropDown.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String selected = (String) dropDown.getSelectedItem();
                List<Document> result = engine.sortResult(selected,engine.getSearchResult());
                displayResult(result);
            }
        }); 
        
        
    }

    ////////////////////////////////////////
    //METHODS FOR COMPONENTS IN SEARCH FIELD
    //addSearch addChoosing addSearchBtn
    ////////////////////////////////////////

    // method used to create and add searchTerm which is used in collecting user search,
    // perform spell check and suggestions for entered word
    private void addSearch(){
        // initialize properties for searchTerm
        searchTerm = new JTextPane(doc);
        searchTerm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),"Search Term",TitledBorder.LEFT,TitledBorder.TOP
        ));
        searchTerm.setPreferredSize(new Dimension(mainPanel.getWidth() - 200,50));
        searchTerm.setText("Enter term");

        // insert searchTerm into searchField panel
        searchField.add(searchTerm);

        // create auto complete
        AutoComplete autoComplete = new AutoComplete(searchTerm);
        
        InputMap tabInputMap = searchTerm.getInputMap();
        ActionMap tabActionMap = searchTerm.getActionMap();
        tabInputMap.put(KeyStroke.getKeyStroke("TAB"), AutoComplete.COMMIT_ACTION);
        am.put(AutoComplete.COMMIT_ACTION, autoComplete.new CommitAction(searchTerm));
        // property change listener using documentListener to collect changes in text fields
        searchTerm.getDocument().addDocumentListener(autoComplete);
        //searchTerm.getDocument().addDocumentListener(new AutoCheck(searchTerm));

        searchTerm.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent fe){
                if (searchTerm.getText().equals("Enter term")){
                    searchTerm.setText("");
                }
                
            }

            @Override
            public void focusLost(FocusEvent fe){
                
            }
        });
    }

    

    // method used for adding chosenFiles text field and choose file button to searchField panel
    private void addChoosing(){
        // create a smaller panel to obtain text field and a button
        JPanel chooseFile = new JPanel();
        chooseFile.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        chooseFile.setPreferredSize(new Dimension(mainPanel.getWidth(),50));
        chooseFile.setBackground(Color.WHITE);
        searchField.add(chooseFile);

        // initialize properties for chosenFiles
        chosenFiles = new JTextField();
        chosenFiles.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),"Files",TitledBorder.LEFT,TitledBorder.TOP
        ));
        chosenFiles.setPreferredSize(new Dimension(mainPanel.getWidth() - 200,50));
        chosenFiles.setText("Enter file name");
        chooseFile.add(chosenFiles);

        // initialize properties for chooseFile button
        JButton chooseFileBtn = new JButton("Add a file");
        chooseFileBtn.setPreferredSize(new Dimension(100,30));
        chooseFile.add(chooseFileBtn);

        // set icon for add file button
        JLabel addFile = new JLabel(new ImageIcon("images/add-icon.png"));
        addFile.setPreferredSize(new Dimension(30,30));
        chooseFile.add(addFile);

        // action listener added to collect files
        chooseFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                // reset the text field
                chosenFiles.setText("");

                // JFileChooser
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Text Files", "txt");
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setFileFilter(filter);
                chooser.setMultiSelectionEnabled(true);
                int returnVal = chooser.showOpenDialog(frame);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        

                        // get selected files
                        File[] files = chooser.getSelectedFiles();
                        
                        for (int i = 0; i < files.length;i++){
                            // display selected files to the screen
                            // in chosen files text field
                            if ((i + 1) == files.length){
                                chosenFiles.setText(chosenFiles.getText() + files[i].getName());
                            }
                            else {
                                chosenFiles.setText(chosenFiles.getText() + files[i].getName() + ", " );
                            }
                            // save file name and file path which will be retrieved by search function in Engine class
                            Engine.fileMap.put(files[i].getName(),new Document(files[i].getAbsolutePath()));
                        }
                        
                    } catch (Exception fe){
                        System.err.println("Chosen File not found " + fe.getMessage());
                    }                    
        
                }
            }
        });
    }

    private void addSearchBtn(){
        // create panel to obtain search button
        JPanel searchBtnPanel = new JPanel();
        searchBtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchBtnPanel.setBackground(null);
        searchBtnPanel.setPreferredSize(new Dimension(mainPanel.getWidth(),50));
        searchField.add(searchBtnPanel);

        // initialize properties for search button
        searchBtn = new JButton("Search");
        searchBtn.setPreferredSize(new Dimension(100,30));

        // set icon for search button
        JLabel searchIcon = new JLabel(new ImageIcon("images/search-icon.png"));
        searchIcon.setPreferredSize(new Dimension(30,30));

        // add to panel
        searchBtnPanel.add(searchBtn);
        searchBtnPanel.add(searchIcon);

        // add action listener to button
        searchBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                List<Document> result = engine.search(searchTerm.getText().trim());
                displayResult(result);
                

            }
        });
    }
    
    // add results into resultField panel 
    private void displayResult(List<Document> result){
        if (resultPanel.getComponents().length != 0){
            resultPanel.removeAll();
        }

        File filePath;
        Float score;
        if (result != null){
            for (Document file : result){
                filePath = new File(file.getFilePath());
                if (file.getScore() >= 0){
                    score = file.getScore() * 100;
                    JLabel name = new JLabel(filePath.getName());
                    name.addMouseListener(new MouseClick());
                    name.setName(filePath.toString());
                    JLabel accuracy = new JLabel(score.toString());
                    name.setPreferredSize(new Dimension(resultPanel.getWidth() / 2 - 10,20));
                    accuracy.setPreferredSize(new Dimension(resultPanel.getWidth() / 2 - 10,20));
                    resultPanel.add(name);
                    resultPanel.add(accuracy);
                }
                
            }
        }
        else {
            JLabel noResult = new JLabel("No result found");
            resultField.add(noResult);
        }
    }
    
    // a mouse click listener class
    private class MouseClick implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent me){
            try {
                Desktop.getDesktop().open(new File(me.getComponent().getName()));
            } catch (Exception e){
                System.err.println("Error opening file");
            }
        
        }

        @Override
        public void mousePressed(MouseEvent e){
            
        }

        @Override
        public void mouseReleased(MouseEvent e){

        }

        @Override 
        public void mouseExited(MouseEvent e){

        }

        @Override 
        public void mouseEntered(MouseEvent e){
            e.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

    }
    
}
