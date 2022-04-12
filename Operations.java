/* 
    
*/
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
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
        DataProcessor.collect(lastChar.mid, prefix, keys);
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
    public static List<Integer> search(Node root, String[] words){
        List<Integer> result = new ArrayList<Integer>();
        
        for (int i = 0; i < words.length; i++){
            Node lastChar = DataProcessor.get(root,words[i],0);
            if (lastChar == null){
                result.add(-2); // any word not found will be assign -2 value
            }
            else {
                result.addAll(lastChar.val);
            }
        }
        return result;
    }
    
    // method to check if a word is in the document return true false
    public static boolean search(Node root, String word){
        return DataProcessor.contains(root, word, 0);
    }
    
}
