/*
    Node class associated with DataProcessor class to store document in character nodes
    last node will be the last character in a string contain the list of indices of that string 
    in the document. If it's not last character in the string the list will be null
*/

import java.util.List;
import java.util.ArrayList;

public class Node {
    // character value
    public char c;

    // children nodes
    public Node left,mid,right;

    // index of string in the string list
    // nomarlly will be null unless it's the last char of the string 
    public List<Integer> val = new ArrayList<Integer>();

    // constructor to create a node
    public Node(char c){
        this.c = c;
    }
}
