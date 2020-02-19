import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SemantickiAnalizator {
	
	Cvor korijen;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		List<String> lines = new ArrayList<String>();
		
		while(sc.hasNextLine()) {
			String line = sc.nextLine();
			lines.add(line);
		}
		
		UlazniParser.izgradiStablo(lines);
		
		new Analizator(UlazniParser.korijen);
		sc.close();
		
	}
	
	
}