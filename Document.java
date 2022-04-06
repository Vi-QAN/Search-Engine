
public class Document {
    private Node head;
    private int size = 0;
    private String filePath;
    private float score = 0;

    public Document(String filePath){
        this.filePath = filePath;
    }    

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public String getFilePath(){
        return this.filePath;
    }
    
    public void setHead(Node head){
        if (head == null){
            return;
        }
        this.head = head;
    }

    public Node getHead(){
        return this.head;
    }

    public void setSize(int size){
        this.size = size;
    }

    public int getSize(){
        return this.size;
    }

    public void setScore(float score){
        this.score = score;
    }

    public float getScore(){
        return this.score;    
    }

    public String toString(){
        String info = "Path: " + this.filePath + " " + " Size: " + this.size + " First Character: " + this.head.c;
        return info;    
    }

}
