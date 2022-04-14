/*
    Engine class is a control class that handles input collected in GUI class and perform task 
    such as search single word, search multiple words, and search exactly the phrase with ""

    Multi-threading is used to speed up document reading, loading and building Ternary Trie 
    for each document. It's also used for searching asynchronously for given words across loaded files
    
    search term will be splited into different words passed to search 
    create number of threads using AutoLoad according to size of file map read each file asyncronously, add root node to docs queue
    create number of threads using AutoSearch accroding to size of file map search each Trie asyncronously,
    return List of result including file name, index of splited words in the document array
    search for exact word
    compute compatibility level by dividing the total frequency of search term over length of entered search term length

*/





import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    // dictionary information used in auto check(spell check) and auto complete(word suggestion) classes
    private static MetaData dictionary;
    private final String dictFile = "Dictionary.txt"; // dictionary file

    // search result documents that contains the given search term
    private List<Document> searchResult = new ArrayList<Document>();

    // choices for sort results
    static enum SortOption {
        ALPHABET,
        ACCURACY
    }

    // searching modes distinguish between search with "" and normal search
    enum SearchingMode {
        APROXIMATE,
        EXACT
    }

    // set default searching mode
    private SearchingMode defaultMode = SearchingMode.APROXIMATE;
    private SearchingMode mode = defaultMode;

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

    // constructor to initialize fundamentals of engine such as Dictionary
    public Engine(){
        readDict(); // read dictionary file
    }

    // get search result documents
    public List<Document> getSearchResult(){
        return this.searchResult;
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

        // check if search term contain "" otherwise it will use default mode
        int start = term.indexOf('\"');
        if (start != -1){
            int end = term.lastIndexOf('\"');
            mode = SearchingMode.EXACT;
            term = term.substring(start + 1,end);
        }
        else {
            mode = defaultMode;
        }

        // split the search term into words
        String[] words = term.split("\\,|\\.| ");
        
        // wildcard mask check then add possible words to array
        words = addWildcard(words); 
        
        // search for the term in loaded files stored as key value pair indices of each searched word
        // map to document
        HashMap<Document,List<Integer>> frequencyList = searchDocs(words);
        frequencyList.forEach((doc,list) -> {
            System.out.println(doc.getFileName());
            System.out.println(list.toString());
        });
        // calculate score and update document info in fileMap
        calculateScore(frequencyList, words);

        // find the exact match when "" is detected 
        if (!mode.equals(defaultMode)){
            searchResult = findExact(frequencyList, words.length);
        }
        else {
            // remove documents that have 0% match as in default mode
            searchResult = fileMap.values().stream().filter(file -> file.getScore() > 0).collect(Collectors.toList());
        }
        searchResult.forEach((doc) -> {
            System.out.println(doc.toString());
        });

        fileMap.forEach((fileName,doc) -> {
            System.out.println("File Name: " + fileName + " Score: " + doc.getScore());
        });

        // sort result and return to display default as accuracy
        return sortResult(SortOption.ACCURACY.toString(),searchResult);
    }
    
    // API method for lookUp used in AutoComplete
    // pass in word need to search 
    public static List<String> lookUp(String word){
        // find all words that have given word pattern
        Queue<String> result = Operations.keysThatMatch(Engine.dictionary.head,word);
        if (result == null){
            return null;
        }
        // return result as a list
        return new ArrayList<String>(result);
    }

    // API method for searching for the occurence of a word in dictionary
    public static boolean findOccurrence(String word){
        return Operations.search(Engine.dictionary.head, word);
    }

    // API method for searching for the occurence of a word in a particular file 
    // take the word and the pointer to the first node of the document
    public static boolean findOccurrence(String word, Node root){
        return Operations.search(root, word);
    }

    // API method for find exact phrase search with the aid of ""
    public List<Document> findExact(HashMap<Document,List<Integer>> frequencyList,int termLength){
        // attribute
        List<Document> result = new ArrayList<>();
        
        // if term length is 1 return all documents that contains the term
        if (termLength == 1){
            result.addAll(frequencyList.keySet());
            return result;
        }

        // iterate through documents in result list
        frequencyList.forEach((doc,freq) -> {
            // check if there are consecutive indices in index list
            if (checkExact(freq, termLength)){
                result.add(doc);
            }
        });

        return result;
    }

    // method to check if frequency contains an consecutive group of integers
    public boolean checkExact(List<Integer> frequency,int termLength){
        // length of consecutive indices
        int consecutiveSequence = 0;

        // longest consecutive length
        int longestSequence = Integer.MIN_VALUE;

        // sort the frequency list
        Collections.sort(frequency);

        for (int i = 1;i < frequency.size(); i++){
            // sequence length will be increased per pair of consecutive indices
            // there for it will be 1 less then term length
            if (consecutiveSequence == (termLength - 1)){
                break;
            }

            // check if next index is one greater than current index 
            if ((frequency.get(i) - frequency.get(i - 1)) == 1){
                consecutiveSequence++;
                if (consecutiveSequence > longestSequence){
                    longestSequence = consecutiveSequence;
                }
            }
            else {
                consecutiveSequence = 0;
            }
        }
        return longestSequence >= 1 ;
    }
    
    // API method for sorting result 
    public List<Document> sortResult(String option,List<Document> searchResult){
        List<Document> sortedFiles = new ArrayList<>();

        // sort according to accuracy
        if (option.equals(SortOption.ACCURACY.toString())){
            sortedFiles = (List<Document>) searchResult.stream()
                            .filter(file -> file.getScore() > 0)
                            .sorted(Comparator.comparingDouble(Document::getScore).reversed())
                            .collect(Collectors.toList());
        } // sort according to alphabet
        else if (option.equals(SortOption.ALPHABET.toString())){
            sortedFiles = (List<Document>) searchResult.stream()
                            .filter(file -> file.getScore() > 0)
                            .sorted(Comparator.comparing(Document::getFileName))
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
        // create a thread to read dictionary
        AutoLoad load = new AutoLoad(dictFile);
        load.start();
        try {
            load.join();
            Engine.dictionary = load.getResult(); // get the pointer to the head of dictionary from the thread
        } catch (Exception e){
            System.out.println("Collect result in readDict() is interrupted");
        }
    }
    
    /////////////////////////////////////////
    //Document handling part for Engine class
    ///////////////////////////////////////// 

    // read all the documents at once using multi-threading
    private void readDocs() {
        // create threads to read documents asynchronously
        // stored in array to ensure the order of documents
        // thread result will be pointer to that document
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
    private HashMap<Document,List<Integer>> searchDocs(String[] words){
        // Frequency List of each word
        HashMap<Document,List<Integer>> frequencyList = new HashMap<>();
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
        try {
            i = 0;
            for (Document doc : fileMap.values()){
                searchers[i].join();
                frequencyList.put(doc,searchers[i].getResult());
                i++;
            }
        } catch (InterruptedException e){
            System.out.println("Collect result in searchDocs() is interrupted");
        }

        return frequencyList;
    }

    // method to calculate score of each document and save it to document metadata
    private void calculateScore(HashMap<Document,List<Integer>> frequencyList, String[] terms){
        frequencyList.forEach((doc,freList) -> {
            doc.setScore(ScoreCalculator.calculate(ScoreCalculator.getFrequency(freList, terms), terms.length));
        });
    }


    // calculate score of each document according to search term
    private class ScoreCalculator {
        public static int getFrequency(List<Integer> data,String[] terms){
            int similarity = terms.length;
            for (int i = 0; i < data.size();i++){
                if (data.get(i) == -2){ // because if the word not found -2 value is inserted instead
                    similarity--;
                }
            }
            return similarity;
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
        return newWords.stream().toArray(String[] :: new);
    }
    

    /////////////////////////
    //MULTI-THREADING CLASSES
    /////////////////////////

    // AutoSearch used in searching for a terms for 
    private class AutoSearch extends Thread {
        private Node start;
        private String[] term;
        private List<Integer> result;
        
        public AutoSearch(Node start,String[] term){
            this.start = start;
            this.term = term;
        }
        public void run(){
            this.result = Operations.search(this.start,this.term);
        }
        public List<Integer> getResult(){
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
