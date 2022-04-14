/*
    Document class is information class that contains getter and setter methods and attributes that 
    a document has

*/

import java.io.File;

public class Document {
    // attributes
    private Node head; // pointer to the first node of trie
    private int size = 0; // size of the document
    private String filePath; // document absolute path
    private float score = 0; // score will be updated later
    private String fileName; // file name extracted from file path

    public Document(String filePath){
        this.filePath = filePath;
        setFileName(this.filePath);
    }    

    public void setFileName(String filePath){
        File file = new File(filePath);
        this.fileName = file.getName();
    }

    public String getFileName() {
        return this.fileName;
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
