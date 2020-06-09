import Parser.Parser;
import Scanner.*;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws Exception {
        String path_to_file = "src/test case3.txt";
        // Read code from the file
        String code = readFile(path_to_file);

        // testing the scanner
        Scanner scanner = new Scanner(code);
        while(scanner.hasNestToken()){
            Token token = scanner.nextToken();
            // these tokens and their types
            System.out.print("Token: " + token.getToken());
            System.out.print(", Type: "+token.getType());
            System.out.println(", Line: "+token.getLine());
        }

        Parser parser = new Parser(new Scanner(code));
        parser.start();


    }

    private static String readFile(String path){
        String code = "";
        try {
            File file = new File(path);
            java.util.Scanner scanner = new java.util.Scanner(file);
            int i = 1;
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                code += line + "\n" ;
                ++i;
            }
            scanner.close();
        }catch (FileNotFoundException e){
            System.out.println(e);
        }
        return code;
    }
}
