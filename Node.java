/*
    Node class associated with DataProcessor class to store character
*/

import java.util.List;
import java.util.ArrayList;

public class Node {
    // character value
    char c;

    // children nodes
    Node left,mid,right;

    // index of string in the string list
    // nomarlly will be null unless it's the last char of the string 
    List<Integer> val = new ArrayList<Integer>();

    // constructor to create a node
    public Node(char c){
        this.c = c;
    }
}
