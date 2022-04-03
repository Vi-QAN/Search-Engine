/*
    FileHandler class handles operations related to file such as readFile, check if file exists,
    collect chosen files which will be used in Document class
*/

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class FileHandler {

    // read single file
    public static List<String> readFile(String filePath){
        List<String> lines = new ArrayList<String>();
        try {
            if (isFileExists(filePath)){
                Scanner scan = new Scanner(new File(filePath)).useDelimiter(" |\n");
                while (scan.hasNext()){
                    lines.add(scan.next());
                }
                scan.close();
            }
            else {
                System.out.println("File Not Exists");
            }
        } catch (IOException e){
            System.err.println("File opening error " + e.getMessage());
        }
        return lines;
    }

    // check if a file exists using FileReader
    public static boolean isFileExists(String filePath){
        try {
            File file = new File(filePath);
        } catch (Exception e){
            return false;
        }
        return true;

    }

    /////////////////////////////////
    //METHODS TO COLLECT CHOSEN FILES
    /////////////////////////////////

    // compare recorded chosen files if it's edited in text field
    // return a set of final file names
    // public static Set<String> finalizeFiles(String chosenFiles){
    //     Set<String> chosen = splitFiles(chosenFiles);
    //     Set<String> recorded = Engine.fileMap.keySet();
    //     chosen.retainAll(recorded);

    //     // chosen.forEach((item) -> {
    //     //     System.out.println(item);
    //     // });
    //     return chosen;
    // }
    
    // // get the name of chosen files displayed in text field
    // private static Set<String> splitFiles(String chosenFiles){
    //     HashSet<String> files = new HashSet<String>();
    //     Scanner scan = new Scanner(chosenFiles).useDelimiter(Pattern.compile(", "));
    
    //     while (scan.hasNext()){
    //         System.out.println(scan.next().toString());
    //     } 
    //     return files;
    // }

    public static void main(String[] args){
        // FileHandler.readFile("Dictionary.txt").forEach((word) -> {
        //     System.out.println(word);
        // });
        FileHandler.readFile("Dictionary.txt");
    }

}
