import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

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

    // class to store file information in term of list instead of hash map
    // used in sorting result
    private class FileInfo {
        private String fileName;
        private float score;

        public FileInfo(String fileName, Document detail){
            this.fileName = fileName;
            this.score = detail.getScore();
        }

        public String getName(){
            return this.fileName;
        }

        public float getScore(){
            return this.score;
        }
        
    }

    // constructor to initialize fundamentals of engine such as Dictionary
    public Engine(){
        readDict();
    }

    ////////////////////////////////
    // SEARCHING FUNCTION FOR ENGINE
    ////////////////////////////////

    // API function used for search used in GUI
    public List<String> search(String term){
        if (term.isEmpty()){
            return null;
        }

        // read in files and load to the program
        readDocs();

        // split the search term into words
        String[] words = term.split(" ");

        // search for the term in loaded files
        // calculate score and update document info in fileMap
        searchDocs(words);

        fileMap.forEach((fileName,doc) -> {
            System.out.println("File Name: " + fileName + " Score: " + doc.getScore());
        });

        // sort result and return to display
        return sortResult();
    }
    
    // API method for lookUp used in AutoComplete
    public String lookUp(String word){
        Queue<String> result = Operations.keysThatMatch(Engine.dictionary.head,word);
        if (result == null){
            return null;
        }
        return result.remove();
    }

    // API method for sorting result 
    public List<String> sortResult(){
        List<FileInfo> files = new ArrayList<>();
        fileMap.forEach((fileName,detail) -> {
            files.add(new FileInfo(fileName, detail));
        });

        List<FileInfo> sortedFiles = (List<FileInfo>) files.stream()
                            .sorted(Comparator.comparingDouble(FileInfo::getScore).reversed())
                            .collect(Collectors.toList());
        List<String> result = new ArrayList<>();
        sortedFiles.forEach((f) -> {
            result.add(f.getName());
        });
        return result;
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

    // search term will be splited into different words passed to search 
    // note: create number of threads using AutoLoad according to size of file map read each file asyncronously, add root node to docs queue
    // create number of threads using AutoSearch accroding to size of file map search each Trie asyncronously,
    // return List of result including file name, index of splited words in the document array
    // search for exact word
    // compute compatibility level by ??? 

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
    

    /////////////////////////
    //MULTI-THREADING CLASSES
    /////////////////////////

    // AutoSearch used in searching for a terms for 
    private class AutoSearch extends Thread {
        Node start;
        String[] term;
        int[] result;
        
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
        Engine engine = new Engine();
        Document hello = new Document("hello");
        hello.setScore((float)4.4);
        Document hello1 = new Document("hello1");
        hello1.setScore((float)2.1);
        Document hello2 = new Document("hello2");
        hello2.setScore((float)2.4);
        Engine.fileMap.put("Hello.txt", hello);
        Engine.fileMap.put("Hello1.txt", hello1);
        Engine.fileMap.put("Hello2.txt",hello2);
        List<String> sorted = engine.sortResult();
        for (String file : sorted){
            System.out.println(file);
        }
    }
    
    

    
}
