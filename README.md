# Search-Engine
Search Engine completely built by Java programming language 
Data structure to store documents' content is built from scratch using ternary trie
    DataProcessor class 
        Usability
            - Ternary Trie method implementations

        Methods
            - public static Node get(Node root, String key, int index) - get index of a given string
            - public static Node put(Node root,String key,int value, int index) - add a string to Trie
            - public static void collect(Node root,String prefix, Queue<String> keys) - collect all string in Trie that have given prefix
            - public static boolean contains(Node root, String key, int index) - check if Trie contain a given key


    Operations class
        Usability
            - API class to use Ternary Trie
        Methods
            - public static Queue<String> keysThatMatch(Node root, String prefix) - find all strings that contain given pattern
            - public static Node build(List<String> content) - build the trie
            - public static List<Integer> search(Node root, String[] words) - search list of words in trie return list of indices of each word
            - public static boolean search(Node root, String word) - search for a word in document return boolean

    Node class
        Usability
            - store characters in nodes
        Methods
            - public Node(char c) - constructor to create a node
    
    GUI class
        Usability
            - interface for user
        Methods
            - public GUI() - construct GUI
            - private void addDisplay() 
            - private void addFilter()
            - private void addSearch()
            - private void addChoosing()
            - private void addSearchBtn()
            - private void displayResult(List<Document> result)
            - private class MouseTraverse extends MouseAdapter
            - private class KeyTraverse extends AbstractAction

    Document class
        Usability
            - store document information
        Methods
            - getters and setters
    
    FileHandler class
        Usability
            - open read file, trimming words
        Methods
            - public static List<String> readFile(String filePath)
            - public static boolean isFileExists(String filePath)
            - public static String wordProcessing(String word) - method is built from scratch instead of using trim()

    AutoCheck class
        Usability
            - spell check and wrong spelling word highlight
        Methods
            - public AutoCheck(JTextPane searchTerm)
            - public void insertUpdate(DocumentEvent de)
            - public void removeUpdate(DocumentEvent de)
            - public void changedUpdate(DocumentEvent de)
            - private class Highlight implements Runnable 

    Engine class
        Usability
            - control class
        Methods
            - public Engine()
            - public List<Document> getSearchResult()
            - public List<Document> search(String term)
            - public static List<String> lookUp(String word)
            - public static boolean findOccurrence(String word)
            - public List<Document> findExact(HashMap<Document,List<Integer>> frequencyList,int termLength)
            - public boolean checkExact(List<Integer> frequency,int termLength)
            - public List<Document> sortResult(String option,List<Document> searchResult)
            - private void readDict()
            - private void readDocs()
            - private HashMap<Document,List<Integer>> searchDocs(String[] words)
            - private void calculateScore(HashMap<Document,List<Integer>> frequencyList, String[] terms)
            - private class ScoreCalculator
            - private String[] addWildcard(String[] words)
            - private class AutoSearch extends Thread
            - private class AutoLoad extends Thread