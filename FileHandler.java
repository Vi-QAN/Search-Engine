import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class FileHandler {
    
    public FileHandler(){
        System.out.println("Welcome to here");

    }

    public static List<String> readFile(String filePath){
        List<String> lines = new ArrayList<String>();
        try {
            Scanner scan = new Scanner(new FileReader(filePath));
            while (scan.hasNextLine()){
                lines.add(scan.nextLine());
            }
            scan.close();
        } catch (IOException e){
            System.err.println("File opening error " + e.getMessage());
        }
        return lines;
    }

    public void writeFile(String filePath,String content){
        try {
            FileWriter writer = new FileWriter(filePath,true);
            writer.write(content);
            writer.close();
        } catch (IOException e){
            System.err.println("File opening error " + e.getMessage());
        }
    }

}
