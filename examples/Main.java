import edu.csupomona.cs.cs411.project1.lexer.ToyLexer;
import edu.csupomona.cs.cs411.project1.lexer.Token;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		ToyLexer l;
		for (String arg : args) {
			Path p = Paths.get(arg);
			if (!Files.isReadable(p)) {
				System.out.format("Cannot read from file: \"%s\"%n", p);
				continue;
			}

			try (		InputStream in = Files.newInputStream(p);
					BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
				l = new ToyLexer(br);
				for (Token t : l) {
					// Token stream
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
