import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("Please give us the path to file as an argument");
            System.exit(-1);
        }

        Decryptor decryptor = new Decryptor();
        String encryptedText = null;
        try {
            encryptedText = new String(Files.readAllBytes(Paths.get(args[0])));
        } catch (IOException e) {
            System.err.println(e.toString());
            System.exit(-1);
        }
        System.out.println(encryptedText);
        System.out.println(decryptor.decrypt(encryptedText));
    }
}
