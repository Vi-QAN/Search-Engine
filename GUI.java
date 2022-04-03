/*
    GUI class contains methods to handle display onto the screen
*/


import javax.swing.*;
import javax.swing.border.TitledBorder;

import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.*;
import java.awt.*;

import java.io.File;

import java.util.List;

public class GUI {
    // create main frame for GUI
    private JFrame frame;

    // create panel
    private JPanel mainPanel, displayField, searchField, resultField;

    // create JTextField for searchTerm
    private JTextField searchTerm,chosenFiles;
    
    // create button
    private JButton searchBtn;

    
    private final Engine engine = new Engine();
    

    public GUI() {
        // initialize properties for frame
        frame = new JFrame("My Google");
        frame.setSize(700,500);
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
            BorderFactory.createEtchedBorder(),"Result",TitledBorder.LEFT,TitledBorder.TOP
        ));
        resultField.setPreferredSize(new Dimension(mainPanel.getWidth() - 110,mainPanel.getHeight() - 210));
        displayField.add(resultField);
        
    }

    // method used for add filter button to displayField
    private void addFilter(){
        // create panel to obtain filter
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        filterPanel.setPreferredSize(new Dimension(100,mainPanel.getHeight() - 210));
        filterPanel.setBackground(Color.WHITE);
        displayField.add(filterPanel);

        // available options
        String[] options = {"Alphabet","Accuracy"};
        
        // initialize properties for drop down
        JComboBox<String> dropDown = new JComboBox<>(options);
        dropDown.setPreferredSize(new Dimension(100,50));
        dropDown.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),"Filter By",TitledBorder.LEFT,TitledBorder.TOP
        ));
        // add to display field
        filterPanel.add(dropDown);

        // option handler
        dropDown.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String selected = (String)dropDown.getSelectedItem();
                System.out.println(selected);
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
        searchTerm = new JTextField();
        searchTerm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),"Search Term",TitledBorder.LEFT,TitledBorder.TOP
        ));
        searchTerm.setPreferredSize(new Dimension(mainPanel.getWidth() - 200,50));
        searchTerm.setText("Enter term");

        // insert searchTerm into searchField panel
        searchField.add(searchTerm);

        // create auto complete
        AutoComplete autoComplete = new AutoComplete(searchTerm);
        
        InputMap im = searchTerm.getInputMap();
        ActionMap am = searchTerm.getActionMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), AutoComplete.COMMIT_ACTION);
        am.put(AutoComplete.COMMIT_ACTION, autoComplete.new CommitAction(searchTerm));
        // property change listener using documentListener to collect changes in text fields
        searchTerm.getDocument().addDocumentListener(autoComplete);
     
         // collect final entered search
         // action listener added to collect user search term 
         searchTerm.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e){
                 String term = searchTerm.getText();
                 System.out.println(term);
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

        // action listener added to collect files
        chooseFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
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
                            if (Engine.fileMap.isEmpty()){
                                chosenFiles.setText(files[i].getName());
                            }
                            else {
                                if (!Engine.fileMap.containsKey(files[i].getName())){
                                    chosenFiles.setText(chosenFiles.getText() + ", " + files[i].getName());
                                }
                            }
                            // save file name and file path which will be retrieved by search function in Engine class
                            Engine.fileMap.put(files[i].getName(),new Document(files[i].getAbsolutePath()));
                        }
                        
                    } catch (Exception fe){
                        System.err.println("Chosen File not found " + fe.getMessage());
                    }
                        // Document.fileMap.forEach((key,value) -> {
                        //     System.out.printf("Key: %s Value: %s\n",key,value);
                        // });
                    
        
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
        searchBtnPanel.add(searchBtn);

        // add action listener to button
        searchBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                // String files = chosenFiles.getText();
                // FileHandler.finalizeFiles(files);
                // String term = searchTerm.getText();
                if (resultField.getComponents().length != 0){
                    resultField.removeAll();
                }
                List<String> result = engine.search(searchTerm.getText().trim());
                if (result != null){
                    for (String file : result){
                        JLabel name = new JLabel("\t" + file);
                        name.setPreferredSize(new Dimension(resultField.getWidth(),20));
                        resultField.add(name);
                    }
                }
                

            }
        });
    }

    
    
}
