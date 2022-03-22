/*
    Dictionary class handles word look up used for auto complete when typing search term
    Extended from Operations class

*/

import java.util.Queue;
import java.util.ArrayDeque;
import java.util.List;
import java.lang.Thread;


public class Dictionary extends Operations{
    private Node dict;
    private final String filePath = "Dict.txt";

    public Dictionary(){
        Thread autoLoad = new Thread(new AutoLoad());
        autoLoad.start();
    }

    // use file handler to read dictionary text file
    @Override
    protected void read(){
        List<String> content = FileHandler.readFile(this.filePath);
        build(content);
    }

    // form trie for dictionary
    @Override
    protected void build(List<String> content){
        DataProcessor dp = new DataProcessor();
        for (int i = 0; i < content.size();i++){
            this.dict = dp.put(this.dict,content.get(i),i,0);
        }
        
        
    }

    // method to get the string with a prefix stored in a queue
    @Override
    public String startsWith(Node root, String prefix){
        DataProcessor dp = new DataProcessor();
        Queue<String> keys = new ArrayDeque<String>();

        // check if prefix exists
        Node lastChar = dp.get(root,prefix,0);
        if (lastChar == null){
            return null;
        }
        collect(lastChar, prefix, keys);
        return keys.remove();
    }

    // collect a string with given prefix using queue as storage
    private void collect(Node root,String prefix, Queue<String> keys){
        if (root.mid == null || root.val.size() != 0){
            keys.add(prefix);
            return;
        }
        try {
            collect(root.mid, prefix + root.mid.c, keys);
        } catch (Exception e){
            System.out.println("Out of bound");
        }
        
    }

    public Node getDict(){
        return this.dict;
    }

    private class AutoLoad implements Runnable {
        public void run() {
            read();
        }
    }

}
