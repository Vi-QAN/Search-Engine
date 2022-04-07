// search term will be splited into different words passed to search 
// note: create number of threads using AutoLoad according to size of file map read each file asyncronously, add root node to docs queue
// create number of threads using AutoSearch accroding to size of file map search each Trie asyncronously,
// return List of result including file name, index of splited words in the document array
// search for exact word
// compute compatibility level by dividing the total frequency of search term over document word count 


import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Engine {
    // fileMap stores fileName, and filePath in {key,value} pair 
    // to perform duplicate checker in File Choosing located in GUI class
    // and used as resourse to read multiple files in FileHandler class
    public final static HashMap<String,Document> fileMap = new HashMap<String,Document>();

    // pointer to dictionary
    private static MetaData dictionary;
    private final String dictFile = "Dictionary.txt";

    // class to get the result including size and head back from reading threads
    // used in readDict(), readDocs()
    private class MetaData {
        public int size;
        public Node head;

        public MetaData(int size, Node head){
            this.size = size;
            this.head = head;
        }
    }

    // choices for sort
    static enum SortOption {
        ALPHABET,
        ACCURACY
    }

    // constructor to initialize fundamentals of engine such as Dictionary
    public Engine(){
        readDict();
    }

    ////////////////////////////////
    // SEARCHING FUNCTION FOR ENGINE
    ////////////////////////////////

    // API function used for search used in GUI
    public List<Document> search(String term){
        if (term.isEmpty()){
            return null;
        }

        // read in files and load to the program
        readDocs();

        // split the search term into words
        String[] words = term.split("\\,|\\.| ");
        
        // wildcard mask check
        words = addWildcard(words);
        
        // search for the term in loaded files
        // calculate score and update document info in fileMap
        searchDocs(words);

        fileMap.forEach((fileName,doc) -> {
            System.out.println("File Name: " + fileName + " Score: " + doc.getScore());
        });

        // sort result and return to display
        return sortResult(SortOption.ACCURACY.toString());
    }
    
    // API method for lookUp used in AutoComplete
    public static List<String> lookUp(String word){
        Queue<String> result = Operations.keysThatMatch(Engine.dictionary.head,word);
        if (result == null){
            return null;
        }
    
        return new ArrayList<String>(result);
    }

    // API method for searching for the occurence of a word in dictionary
    public static boolean findOccurrence(String word){
        return Operations.search(Engine.dictionary.head, word);
    }

    // API method for searching for the occurence of a word in a particular file
    public static boolean findOccurrence(String word, Node root){
        return Operations.search(root, word);
    }

    // API method for sorting result 
    public List<Document> sortResult(String option){
        List<Document> sortedFiles = new ArrayList<>();
        if (option.equals(SortOption.ACCURACY.toString())){
            sortedFiles = (List<Document>) fileMap.values().stream()
                            .filter(file -> file.getScore() > 0)
                            .sorted(Comparator.comparingDouble(Document::getScore).reversed())
                            .collect(Collectors.toList());
        }
        else if (option.equals(SortOption.ALPHABET.toString())){
            sortedFiles = (List<Document>) fileMap.values().stream()
                            .filter(file -> file.getScore() > 0)
                            .sorted(Comparator.comparing(Document::getFilePath))
                            .collect(Collectors.toList());
        }
        else {
            System.out.println("Out of option range");
        }
        return sortedFiles;
    }

    //////////////////////////
    //DICTIONARY HANDLING PART
    //////////////////////////

    // method load dictionary to perform word suggestion and spell checker
    private void readDict(){
        AutoLoad load = new AutoLoad(dictFile);
        load.start();
        try {
            load.join();
            Engine.dictionary = load.getResult();
        } catch (Exception e){
            System.out.println("Collect result in readDict() is interrupted");
        }
    }
    
    /////////////////////////////////////////
    //Document handling part for Engine class
    ///////////////////////////////////////// 

    // read all the documents at once using multi-threading
    private void readDocs() {
        AutoLoad[] readers = new AutoLoad[fileMap.size()];
        int i = 0;
        for (Document doc : fileMap.values()) {
            readers[i] = new AutoLoad(doc.getFilePath());
            readers[i].start();
            i++;
        };
        try {
            i = 0;
            for (Document doc : fileMap.values()){
                readers[i].join();
                MetaData result = readers[i].getResult();
                doc.setHead(result.head);
                doc.setSize(result.size);
                i++;
            }
            fileMap.forEach((docName,docData) -> {
                System.out.printf("File Name: %s File Data: %s\n",docName,docData.toString());
            });
        } catch (InterruptedException ex){
            System.out.println("Collect result in readDocs() is interrupted");
        }   
    }
    
    // search across multiple documents, calculate and assign score to 
    // fileMap using multi threading 
    private void searchDocs(String[] words){
        int i = 0;
        // thread storage
        AutoSearch[] searchers = new AutoSearch[fileMap.size()];

        // create threads to read file asychronously
        for (Document doc : fileMap.values()){
            searchers[i] = new AutoSearch(doc.getHead(),words);
            searchers[i].start();
            i++;
        }

        // get result for the threads
        i = 0;
        try {
            for (Document doc : fileMap.values()){
                //results[i] = searchers[i].getResult();
                searchers[i].join();
                doc.setScore(ScoreCalculator.calculate(ScoreCalculator.getFrequency(searchers[i].getResult()), doc.getSize()));
                i++;
            }
        } catch (InterruptedException e){
            System.out.println("Collect result in searchDocs() is interrupted");
        }
    }

    // calculate score of each document according to search term
    private class ScoreCalculator {
        public static int getFrequency(int[] data){
            int sum = 0;
            for (int i = 0; i < data.length;i++){
                sum += data[i];
            }
            return sum;
        }
    
        // function calculate the matching score of each document
        private static float calculate(int frequency, int total){
            return (float)frequency / total;
        }
    }

    // expand the search words if it contains the wildcard mask
    private String[] addWildcard(String[] words){
        List<String> newWords = Arrays.asList(words);
        
        for (int i = 0; i < words.length;i++){
            if (words[i].contains("*")){
                int wildcardInd = words[i].indexOf("*");
                String temp = words[i].substring(0, wildcardInd).toLowerCase();
                List<String> additional = lookUp(temp);
                
                if (additional != null){
                    newWords = Stream.concat(newWords.stream(), additional.stream()).collect(Collectors.toList());
                    
                }

                newWords.remove(i);
                newWords.add(temp);
                
            }
        }
        // String[] complete = new String[newWords.size()];
        // for (int i = 0; i < newWords.size();i++){
        //     complete[i] = newWords.get(i);
        // }
        return newWords.stream().toArray(String[] :: new);
    }
    

    /////////////////////////
    //MULTI-THREADING CLASSES
    /////////////////////////

    // AutoSearch used in searching for a terms for 
    private class AutoSearch extends Thread {
        private Node start;
        private String[] term;
        private int[] result;
        
        public AutoSearch(Node start,String[] term){
            this.start = start;
            this.term = term;
        }
        public void run(){
            this.result = Operations.search(this.start,this.term);
        }
        public int[] getResult(){
            return this.result;
        }
    }

    // AutoLoad class used in reading files and building tries for each of them 
    private class AutoLoad extends Thread {
        private String filePath;
        private MetaData result;

        public AutoLoad(String filePath) {
            this.filePath = filePath;
        }
        public void run(){
            List<String> content = FileHandler.readFile(filePath);
            this.result = new MetaData(content.size(),Operations.build(content));
        }
        public MetaData getResult(){
            return this.result; 
        }
    }

    public static void main(String args[]){
        // Engine engine = new Engine();
        // Document hello = new Document("index");
        // Document hello1 = new Document("hello");
        // Document hello2 = new Document("awelcome");

        // Engine.fileMap.put("index.txt", hello);
        // Engine.fileMap.put("hello.txt", hello1);
        // Engine.fileMap.put("awelcome.txt",hello2);
        // List<Document> sorted = engine.sortResult(Engine.SortOption.ALPHABET.toString());
        // for (Document file : sorted){
        //     System.out.println(file.getFilePath());
        // }
        List<String> content = FileHandler.readFile("index.txt");
        Node node = Operations.build(content);
        System.out.println(Engine.findOccurrence("hello",node));
    }
    
    

    
}
