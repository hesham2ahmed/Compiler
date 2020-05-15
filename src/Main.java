import Scanner.*;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        String path_to_file = "src/test1.txt";
        // Read code from the file
        String code = readFile(path_to_file);

        // scan this code to tokens
        Scanner scanner = new Scanner(code);
        while(scanner.hasNestToken()){
            Token token = scanner.nextToken();
            // these tokens and their types
            System.out.print(token.getToken());
            System.out.println(" "+token.getType());
        }
    }

    private static String readFile(String path){
        String code = "";
        try {
            File file = new File(path);
            java.util.Scanner scanner = new java.util.Scanner(file);
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                code += line;
            }
            scanner.close();
        }catch (FileNotFoundException e){
            System.out.println(e);
        }
        return code;
    }
}
