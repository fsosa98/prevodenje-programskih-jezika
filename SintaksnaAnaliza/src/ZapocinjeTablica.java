import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ZapocinjeTablica {

	private List<String> nezavrsniZnakoviGramatike;
	private Map<String, List<String>> produkcijeGramatike;
	private List<String> zavrsniZnakoviGramatike;
	private HashMap<String, List<String>> zapocinjeZavrsni = new HashMap<String, List<String>>();
	private Set<String> prazniNezavrsni = new TreeSet<>();
	private List<String> redakTablice = new ArrayList<String>();
	private int[][] zapocinje;

	public ZapocinjeTablica(List<String> nezavrsniZnakoviGramatike, List<String> zavrsniZnakoviGramatike,
			Map<String, List<String>> produkcijeGramatike) {

		this.nezavrsniZnakoviGramatike = nezavrsniZnakoviGramatike;
		this.produkcijeGramatike = produkcijeGramatike;
		this.zavrsniZnakoviGramatike = zavrsniZnakoviGramatike;

		redakTablice.addAll(nezavrsniZnakoviGramatike);
		redakTablice.addAll(zavrsniZnakoviGramatike);

		int zapocinjeSize = nezavrsniZnakoviGramatike.size() + zavrsniZnakoviGramatike.size();
		zapocinje = new int[zapocinjeSize][zapocinjeSize];

		for (String nezavrsni : nezavrsniZnakoviGramatike) {

			if (nezavrsni.equals(nezavrsniZnakoviGramatike.get(0))) {
				continue;
			}
			for (String produkcija : produkcijeGramatike.get(nezavrsni)) {

				if (produkcija.equals("$")) {
					prazniNezavrsni.add(nezavrsni);
				}
			}
		}

		boolean biloPromjene = true;

		while (biloPromjene) {

			biloPromjene = false;
			for (String nezavrsni : nezavrsniZnakoviGramatike) {
				if (nezavrsni.equals(nezavrsniZnakoviGramatike.get(0))) {
					continue;
				}
				if (!prazniNezavrsni.contains(nezavrsni)) {
					for (String produkcija : produkcijeGramatike.get(nezavrsni)) {
						String produkcijeZnakovi[] = produkcija.split(" ");
						boolean sadrziSve = true;
						for (String z : produkcijeZnakovi) {
							if (!prazniNezavrsni.contains(z)) {
								sadrziSve = false;
							}
						}
						if (sadrziSve) {
							prazniNezavrsni.add(nezavrsni);
							biloPromjene = true;
						}
					}
				}
			}
		}
		generirajZapocinjeIzravnoZnakom();
		genererirajZapocinjeZnakom();

		generirajZapocinjeNezavrsni();
	}

	private void generirajZapocinjeIzravnoZnakom() {

		for (String nezavrsni : nezavrsniZnakoviGramatike) {

			List<String> nezavrsniLista = produkcijeGramatike.get(nezavrsni);
			for (String produkcija : nezavrsniLista) {

				produkcija = produkcija.trim();
				String polje[] = produkcija.split(" ");
				for (int k = 0; k < polje.length; k++) {
					String znak = produkcija.split(" ")[k];
					if (znak.equals("$"))
						continue;
					zapocinje[nezavrsniZnakoviGramatike.indexOf(nezavrsni)][redakTablice.indexOf(znak)] = 1;
					if (!prazniNezavrsni.contains(znak)) {
						break;
					}
				}

			}
		}
	}

	private void genererirajZapocinjeZnakom() {
		int n = nezavrsniZnakoviGramatike.size() + zavrsniZnakoviGramatike.size();
		for (int i = 0; i < n; i++) {
			zapocinje[i][i] = 1;
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					for (int l = 0; l < n; l++) {
						if (zapocinje[j][k] == 1 && zapocinje[k][l] == 1) {
							zapocinje[j][l] = 1;
						}
					}
				}
			}
		}
	}

	private void generirajZapocinjeNezavrsni() {
		int n = nezavrsniZnakoviGramatike.size() + zavrsniZnakoviGramatike.size();
		for (String nezavrsni : nezavrsniZnakoviGramatike) {
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < n; i++) {
				if (zapocinje[nezavrsniZnakoviGramatike.indexOf(nezavrsni)][i] == 1) {
					list.add(redakTablice.get(i));
				}
			}
			zapocinjeZavrsni.put(nezavrsni, list);
		}
		for (String zavrsni : zavrsniZnakoviGramatike) {
			List<String> list = new ArrayList<String>();
			list.add(zavrsni);
			zapocinjeZavrsni.put(zavrsni, list);
		}
	}

	public Set<String> getPrazniNezavrsni() {
		return prazniNezavrsni;
	}

	public void setPrazniNezavrsni(Set<String> prazniNezavrsni) {
		this.prazniNezavrsni = prazniNezavrsni;
	}

	public HashMap<String, List<String>> getZapocinjeZavrsni() {
		return zapocinjeZavrsni;
	}

	public void setZapocinjeZavrsni(HashMap<String, List<String>> zapocinjeZavrsni) {
		this.zapocinjeZavrsni = zapocinjeZavrsni;
	}
}