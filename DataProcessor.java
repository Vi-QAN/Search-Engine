/* 
    DataProcessor class is a utility class used to store content of documents in Trie data structure (DS). 
    Trie DS will contain methods to convert list of strings to 3-way trie
    Trie DS provide quicker search for string which is O(log3(n)) instead of O(log2(n)) like binary search

    Methods 
        - public static Node get(Node root, String key, int index) - get index of a given string
        - public static Node put(Node root,String key,int value, int index) - add a string to Trie
        - public static void collect(Node root,String prefix, Queue<String> keys) - collect all string in Trie that have given prefix
        - public static boolean contains(Node root, String key, int index) - check if Trie contain a given key

*/
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
public class DataProcessor {

    // method return the node of given string take 3 arguments root node, key string 
    // and current index of being compared char in key string
    public static Node get(Node root, String key, int index){
        if (root == null){
            return null;
        }
        if (index == key.length()){
            return root;
        }

        char c = key.charAt(index);

        if (c < root.c){
            return get(root.left,key,index);
        }
        else if (c > root.c){
            return get(root.right,key,index);
        }
        else if (index < key.length() - 1){
            return get(root.mid,key,index + 1);
        }
        else { return root;}
        
    }

    // method to add character by character to the trie
    public static Node put(Node root,String key,int value, int index){
        if (index >= key.length()){
            return root;
        }
        
        char c = key.charAt(index);
        if (root == null){ 
            root = new Node(c); 
        }
        if (c < root.c) {
            root.left = put(root.left,key,value,index);
        }
        else if (c > root.c) {
            root.right = put(root.right,key,value,index);
        }
        else if (index < key.length() - 1){
            root.mid = put(root.mid,key,value,index + 1);
        }
        else {
            root.val.add(value);
        }
        return root;
    }

    // collect a string with given prefix using queue as storage
    public static void collect(Node root,String prefix, Queue<String> keys){
        if (root == null){
            return;
        }
    
        collect(root.left,prefix ,keys);
        collect(root.right,prefix, keys);
        collect(root.mid, prefix + root.c, keys);
        
        if (root.mid == null){
            keys.add(prefix + root.c);
        }
        
        
    }

    //method return a boolean value used to check if a word is contained in the document
    public static boolean contains(Node root, String key, int index){
        if (key.length() == index){
            return true;
        }

        if (root == null){
            return false;
        }
        
        char c = key.charAt(index);
        if (c < root.c){
            return contains(root.left,key,index);
        }
        else if ( c > root.c){
            return contains(root.right,key,index);
        }
        else {
            return contains(root.mid,key,index + 1);
        }
    }

    public static void main(String[] args){
        Node root = null;
        List<String> list = new ArrayList<String>();
        list.add("hello");
        list.add("welcome");
        list.add("morning");
        list.add("hell");
        for (int i = 0; i < list.size();i++){
            root = DataProcessor.put(root,list.get(i),i,0);
        }
        if (root != null){
            Node temp = DataProcessor.get(root, "hell", 0);
            System.out.println(temp.val.toString());
        }
        else {
            System.out.println("no root");
        }
        
        
    }

    
}
