/* 
    Operations abstract class includes 3 abstract methods (startsWith,build,read)
    which will be forced to implemented in Document class and Dictionary class
*/
import java.util.List;
public abstract class Operations {
    protected abstract String startsWith(Node root,String prefix);
    protected abstract void build(List<String> content);
    protected abstract void read();
    
}
