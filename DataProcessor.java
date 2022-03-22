/* 
    DataProcessor class used to store content of documents in Trie data structure (DS). 
    Trie DS will contain methods to convert list of strings to 3-way trie
*/

import java.util.List;
import java.util.ArrayList;
public class DataProcessor {

    // method return the node of given string take 3 arguments root node, key string 
    // and current index of being compared char in key string
    public Node get(Node root, String key, int index){
        if (root == null){
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
    public Node put(Node root,String key,int value, int index){
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

    public static void main(String[] args){
        DataProcessor dp = new DataProcessor();
        Node root = null;
        List<String> list = new ArrayList<String>();
        list.add("hello");
        list.add("welcome");
        list.add("morning");
        list.add("hell");
        for (int i = 0; i < list.size();i++){
            root = dp.put(root,list.get(i),i,0);
        }
        if (root != null){
            Node temp = dp.get(root, "hell", 0);
            System.out.println(temp.val.toString());
        }
        else {
            System.out.println("no root");
        }
        
        
    }

}
