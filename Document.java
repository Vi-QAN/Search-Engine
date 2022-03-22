import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Document extends Operations{
    private List<Node> docs = new ArrayList<Node>();

    // fileMap stores fileName, and filePath in {key,value} pair 
    // to perform duplicate checker in File Choosing located in GUI class
    // and used as resourse to read multiple files in FileHandler class
    public static HashMap<String,String> fileMap = new HashMap<String,String>();


    @Override
    protected void read() {
        
        fileMap.forEach((fileName, filePath) -> {
            List<String> content = FileHandler.readFile(filePath);
            build(content);
        });
    }    

    @Override
    protected void build(List<String> content){
        Node root = null;
        DataProcessor dp = new DataProcessor();
        for (int i = 0; i < content.size();i++){
            root = dp.put(root, content.get(i), i, 0);
        }
    }

    @Override
    protected String startsWith(Node root, String word){
        String result = "";
        return result;
    }


}
