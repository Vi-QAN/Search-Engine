/* 
    DataProcessor class used to store content of documents in Trie data structure (DS). 
    Trie DS will contain methods to convert list of strings to 3-way trie

*/
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
public class DataProcessor {

    // method return the node of given string take 3 arguments root node, key string 
    // and current index of being compared char in key string
    public static Node get(Node root, String key, int index){
        if (root == null || key.isEmpty()){
            return null;
        }

        // get current comparing char
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
        if (key.isEmpty()){
            return null;
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
        if (root.mid == null){
            keys.add(prefix);
            return;
        }
        if (root.mid.left != null){
            collect(root.mid.left,prefix + root.mid.left.c ,keys);
        }
        if (root.mid.right != null){
            collect(root.mid.right,prefix + root.mid.right.c, keys);
        }
        
        try {
            collect(root.mid, prefix + root.mid.c, keys);
        } catch (Exception e){
            System.out.println("Out of bound");
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
