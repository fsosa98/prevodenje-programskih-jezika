import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class GLA {

	private static HashMap<String, String> regularneDefinicije = new HashMap<String, String>();
	private static List<String> stanjaLeksickogAnalizatora = new ArrayList<>();
	private static List<String> imenaLeksickihJedinki = new ArrayList<>();
	private static List<PraviloLeksickogAnalizatora> pravilaLeksickogAnalizatora = new ArrayList<PraviloLeksickogAnalizatora>();

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		String line = null;
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			if (line.startsWith("%X")) {
				break;
			}
			String[] parts = line.split(" ");
			String imeRegularneDefinicije = parts[0].substring(1, parts[0].length() - 1);
			char[] regularniIzraz = parts[1].toCharArray();
			StringBuilder sb = new StringBuilder();
			String nesto = "";
			boolean zagrada = false;
			for (int i = 0; i < regularniIzraz.length; i++) {
				if (regularniIzraz[i] == '{') {
					zagrada = true;
					continue;
				}
				if (regularniIzraz[i] == '}') {
					if (!regularneDefinicije.containsKey(sb.toString())) {
						nesto += "{" + sb.toString() + "}";
					} else {
						nesto += "(" + regularneDefinicije.get(sb.toString()) + ")";
					}
					sb.setLength(0);
					zagrada = false;
					continue;
				}
				if (zagrada) {
					sb.append(regularniIzraz[i]);
				}
				if (!zagrada) {
					nesto += regularniIzraz[i];
				}
			}
			regularneDefinicije.put(imeRegularneDefinicije, nesto);
		}
		String[] parts = line.split(" ");
		for (String part : parts) {
			if (part.equals("%X"))
				continue;
			stanjaLeksickogAnalizatora.add(part);
		}

		line = scanner.nextLine();
		parts = line.split(" ");
		for (String part : parts) {
			if (part.equals("%L"))
				continue;
			imenaLeksickihJedinki.add(part);
		}

		List<String> stringoviJednogStanja = new ArrayList<String>();

		String endLine = "}";
		while (scanner.hasNext()) {
			line = scanner.nextLine();

			stringoviJednogStanja.add(line.trim());

			if (line.equals(endLine)) {
				obradiStanje(stringoviJednogStanja);
				stringoviJednogStanja.clear();
			}

		}

		scanner.close();

		zapisiRjesenje("regularneDefinicije", regularneDefinicije);
		zapisiRjesenje("stanjaLeksickogAnalizatora", stanjaLeksickogAnalizatora);
		zapisiRjesenje("imenaLeksickihJedinki", imenaLeksickihJedinki);
		zapisiRjesenje("pravilaLeksickogAnalizatora", pravilaLeksickogAnalizatora);

	}

	private static void obradiStanje(List<String> stringoviJednogStanja) {
		String stanje = null;
		String regularniIzraz = "";
		List<String> argumentiAkcije = new ArrayList<String>();

		boolean pravilaAnalizatoraObrada = false;
		for (int i = 0; i < stringoviJednogStanja.size(); i++) {
			String line = stringoviJednogStanja.get(i);
			if (line.equals("{")) {
				pravilaAnalizatoraObrada = true;
				continue;
			}

			if (line.equals("}")) {
				pravilaAnalizatoraObrada = false;
				continue;
			}

			if (pravilaAnalizatoraObrada) {
				argumentiAkcije.add(line);
			}

			if (line.startsWith("<")) {
				stanje = line.substring(1, line.indexOf('>'));
				String nekaj = line.substring(line.indexOf('>') + 1, line.length());
				regularniIzraz = zamjeniSmece(nekaj);
			}

		}
		PraviloLeksickogAnalizatora praviloLeksickogAnalizatora = new PraviloLeksickogAnalizatora(stanje,
				regularniIzraz, argumentiAkcije);
		pravilaLeksickogAnalizatora.add(praviloLeksickogAnalizatora);

	}

	private static String zamjeniSmece(String string) {
		if (string.equals("\\}") || string.equals("\\{")) {
			return string;
		}
		char[] regularniIzraz = string.toCharArray();
		StringBuilder sb = new StringBuilder();
		String nesto = "";
		boolean zagrada = false;
		for (int i = 0; i < regularniIzraz.length; i++) {
			if (regularniIzraz[i] == '\\' && (i + 1) < regularniIzraz.length) {

				if ((i == 0) || (i > 0 && regularniIzraz[i - 1] != '\\')) {

					if (regularniIzraz[i + 1] == '{') {
						i++;
						nesto += regularniIzraz[i];
						continue;
					} else if (regularniIzraz[i + 1] == '}') {
						i++;
						nesto += regularniIzraz[i];
						continue;
					}

				}

			}
			if (regularniIzraz[i] == '{') {
				zagrada = true;
				continue;
			}

			if (regularniIzraz[i] == '}') {
				nesto += "(" + regularneDefinicije.get(sb.toString()) + ")";
				sb.setLength(0);
				zagrada = false;
				continue;
			}
			if (zagrada) {
				sb.append(regularniIzraz[i]);
			}
			if (!zagrada) {
				nesto += regularniIzraz[i];
			}
		}
		return nesto;
	}

	private static void zapisiRjesenje(String name, Object obj) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("analizator/" + name + ".tmp");
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(obj);

			oos.close();
		} catch (Exception e) {
			System.err.println("Nesto nije u redu");
		}

	}
}
