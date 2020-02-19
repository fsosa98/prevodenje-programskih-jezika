import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class GSA {

	private static List<String> nezavrsniZnakoviGramatike = new ArrayList<>();
	private static List<String> zavrsniZnakoviGramatike = new ArrayList<>();
	private static List<String> sinkronizacijskiZnakoviGramatike = new ArrayList<>();
	private static Map<String, List<String>> produkcijeGramatike = new LinkedHashMap<String, List<String>>();

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		String line = scanner.nextLine();
		String[] parts = line.split("\\s+");
		for (int i = 1; i < parts.length; ++i) {
			nezavrsniZnakoviGramatike.add(parts[i]);
		}

		line = scanner.nextLine();
		parts = line.split("\\s+");
		for (int i = 1; i < parts.length; ++i) {
			zavrsniZnakoviGramatike.add(parts[i]);
		}

		line = scanner.nextLine();
		parts = line.split("\\s+");
		for (int i = 1; i < parts.length; ++i) {
			sinkronizacijskiZnakoviGramatike.add(parts[i]);
		}

		String zadnji = null;
		while (scanner.hasNext()) {
			line = scanner.nextLine();

			if (line.startsWith(" ")) {
				List<String> nekaLista = produkcijeGramatike.get(zadnji);
				if (nekaLista == null) {
					nekaLista = new ArrayList<>();
				}
				nekaLista.add(line.trim());
				produkcijeGramatike.put(zadnji, nekaLista);
			} else {
				zadnji = line;
			}
		}

		scanner.close();

		String novoPocetnoStanje = nezavrsniZnakoviGramatike.get(0) + "'";

		nezavrsniZnakoviGramatike.add(0, novoPocetnoStanje);

		List<String> sviZnakovi = new ArrayList<>(nezavrsniZnakoviGramatike);
		sviZnakovi.addAll(zavrsniZnakoviGramatike);

		List<String> lista = new ArrayList<String>();
		lista.add(nezavrsniZnakoviGramatike.get(1));
		produkcijeGramatike.put(novoPocetnoStanje, lista);

		ZapocinjeTablica zapocinjeTablica = new ZapocinjeTablica(nezavrsniZnakoviGramatike, zavrsniZnakoviGramatike,
				produkcijeGramatike);

		EpsilonNKA nka = new EpsilonNKA(nezavrsniZnakoviGramatike, zavrsniZnakoviGramatike, produkcijeGramatike,
				zapocinjeTablica);

		DKA dka = new DKA(nka.getPrijelazi(), nka.getPocetnoStanje(), sviZnakovi);

		GenerirajLRTablicu tablica = new GenerirajLRTablicu(nezavrsniZnakoviGramatike, zavrsniZnakoviGramatike, dka,
				produkcijeGramatike);

		Set<StanjeDka> stanjaDka = new HashSet<>();
		for (PrijelazDKA prijelaz : dka.getDkaPrijelazi()) {
			stanjaDka.add(prijelaz.lijevoDio);
			stanjaDka.add(prijelaz.desniDio);
		}

		FileOutputStream fos;
		try {
			fos = new FileOutputStream("analizator/" + "LRTablica" + ".tmp");
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(tablica);

			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Nesto nije u redu");
		}

		try {
			fos = new FileOutputStream("analizator/" + "sinkronizacijski" + ".tmp");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(sinkronizacijskiZnakoviGramatike);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Nesto nije u redu");
		}
	}

}
