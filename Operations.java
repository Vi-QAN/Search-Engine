/* 
    
*/
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
public class Operations {
    //////////////////////////////////////////////////////////////////
    //GENERAL METHODS TO PERFORM SEARCHING WITH KEY, SAVING TEXT FILES 
    //CONTENT WITH DataProcessor class
    //USED IN DOCUMENT SET UP AND DICTIONARY SET UP
    //////////////////////////////////////////////////////////////////
    
    // method to get the string with a prefix stored in a queue
    public static Queue<String> keysThatMatch(Node root, String prefix){
        Queue<String> keys = new ArrayDeque<String>();

        // check if prefix exists
        Node lastChar = DataProcessor.get(root,prefix,0);
        if (lastChar == null){
            return null;
        }
        DataProcessor.collect(lastChar, prefix, keys);
        return keys;
    }

   // abstract method from Operations class
    public static Node build(List<String> content){
        Node root = null;
        for (int i = 0; i < content.size();i++){
            root = DataProcessor.put(root, content.get(i), i, 0);
        }
        return root;
    }

    // search for multiple words in a document and return frequency of each word 
    // in form of array of integer
    public static int[] search(Node root, String[] words){
        int[] results = new int[words.length];
        
        for (int i = 0; i < words.length; i++){
            Node lastChar = DataProcessor.get(root,words[i],0);
            if (lastChar == null){
                results[i] = -1; // any word not found will be assign frequency of -1
            }
            else {
                results[i] = lastChar.val.size();
            }
        }
        return results;
    }
    
}
