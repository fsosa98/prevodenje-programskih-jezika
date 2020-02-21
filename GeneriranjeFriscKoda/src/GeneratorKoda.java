
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GeneratorKoda {

	public static Cvor korijen;

	public static FileOutputStream fos;

	public static void main(String[] args) throws FileNotFoundException {
		Scanner scanner = new Scanner(System.in);

		String line = scanner.nextLine();
		int dubinaPrethodnog = line.indexOf(line.trim());

		Cvor prethodniCvor = new Cvor(line.trim(), dubinaPrethodnog, null, new ArrayList<Cvor>());

		korijen = prethodniCvor.kopijaCvora();
		Cvor trenutniCvor;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (line.equals("kraj")) {
				break;
			}

			int dubina = line.indexOf(line.trim());

			if (dubina > dubinaPrethodnog) {
				trenutniCvor = new Cvor(line.trim(), dubina, prethodniCvor, new ArrayList<Cvor>());

				prethodniCvor.dodajMalog(trenutniCvor);

				dubinaPrethodnog = dubina;
				prethodniCvor = trenutniCvor.kopijaCvora();
			}

			else if (dubina == dubinaPrethodnog) {
				trenutniCvor = new Cvor(line.trim(), dubina, prethodniCvor.getOtac(), new ArrayList<Cvor>());

				prethodniCvor.getOtac().dodajMalog(trenutniCvor);
				prethodniCvor = trenutniCvor.kopijaCvora();

			} else {

				for (int i = 0; i < dubinaPrethodnog - dubina + 1; i++) {
					prethodniCvor = prethodniCvor.getOtac();
				}
				trenutniCvor = new Cvor(line.trim(), dubina, prethodniCvor, new ArrayList<Cvor>());
				prethodniCvor.dodajMalog(trenutniCvor);
				dubinaPrethodnog = dubina;
				prethodniCvor = trenutniCvor.kopijaCvora();

			}

		}

		Analizator bob = new Analizator(korijen);

		fos = new FileOutputStream("a.frisc");

		korijen.ispis();

		for (Labela labela : bob.labele) {

			StringBuilder sb = new StringBuilder();

			if (!labela.isJeFunkcija()) {

				sb.append(labela.getLabela());

				if (labela.isJePrazno()) {
					try {
						sb.append(" DW %D 0\n");
						GeneratorKoda.fos.write(sb.toString().getBytes());
						GeneratorKoda.fos.write("\n".getBytes());
						continue;
					} catch (Exception e) {
					}

				}

				try {
					String str = labela.izgradi();
					sb.append(str);
					GeneratorKoda.fos.write(sb.toString().getBytes());
					GeneratorKoda.fos.write("\n".getBytes());
				} catch (Exception ex) {
				}

			}

		}

		scanner.close();
	}

	public static void izgradiStablo(List<String> lines) {
		String lajn = lines.get(0);
		int dubinaPrethodnog = lajn.indexOf(lajn.trim());

		Cvor prethodniCvor = new Cvor(lajn.trim(), dubinaPrethodnog, null, new ArrayList<Cvor>());

		korijen = prethodniCvor.kopijaCvora();
		Cvor trenutniCvor;
		for (int j = 1; j < lines.size(); j++) {
			String line = lines.get(j);
			if (line.equals("kraj")) {
				break;
			}

			int dubina = line.indexOf(line.trim());

			if (dubina > dubinaPrethodnog) {
				trenutniCvor = new Cvor(line.trim(), dubina, prethodniCvor, new ArrayList<Cvor>());

				prethodniCvor.dodajMalog(trenutniCvor);

				dubinaPrethodnog = dubina;
				prethodniCvor = trenutniCvor.kopijaCvora();
			}

			else if (dubina == dubinaPrethodnog) {
				trenutniCvor = new Cvor(line.trim(), dubina, prethodniCvor.getOtac(), new ArrayList<Cvor>());

				prethodniCvor.getOtac().dodajMalog(trenutniCvor);
				prethodniCvor = trenutniCvor.kopijaCvora();

			} else {

				for (int i = 0; i < dubinaPrethodnog - dubina + 1; i++) {
					prethodniCvor = prethodniCvor.getOtac();
				}
				trenutniCvor = new Cvor(line.trim(), dubina, prethodniCvor, new ArrayList<Cvor>());
				prethodniCvor.dodajMalog(trenutniCvor);
				dubinaPrethodnog = dubina;
				prethodniCvor = trenutniCvor.kopijaCvora();

			}
		}

	}

}
