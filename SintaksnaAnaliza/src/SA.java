import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Stack;

public class SA {

	private static Stack<Object> stog = new Stack<>();
	private static Stack<Cvor> cvoroviStog = new Stack<>();
	private static Map<StanjeDka, Map<String, Akcija>> akcije;
	private static Map<StanjeDka, Map<String, Akcija>> novoStanje;
	private static StanjeDka pocetnoStanjeDka;
	private static GenerirajLRTablicu tablica;
	private static Cvor korijen;
	private static List<String> sinkronizacijskiZnakoviGramatike;

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		ucitajTablicu();
		akcije = tablica.getAkcije();
		novoStanje = tablica.getNovoStanje();

		Scanner scanner = new Scanner(System.in);

		pocetnoStanjeDka = tablica.getPocetnoStanjeDka();
		stog.add("$");
		stog.add(pocetnoStanjeDka);

		boolean reduciraj = false;
		String zadnjaProcitana = null;
		List<String> redovi = new ArrayList<>();

		while (scanner.hasNext()) {
			String line = scanner.nextLine();

			redovi.add(line);
		}

		int brojac = 0;
		cvoroviStog.push(new Cvor("$", new ArrayList<Cvor>()));

		while (true) {

			String uniformniZnak = null;
			LeksickaJedinka leksickaJedinka = null;

			if (brojac == redovi.size()) {
				uniformniZnak = "$";
			} else {
				String line = redovi.get(brojac);
				leksickaJedinka = new LeksickaJedinka(redovi.get(brojac));
				uniformniZnak = leksickaJedinka.getUniformniZnak();
			}

			StanjeDka trenutnoStanje = (StanjeDka) stog.peek();
			Map<String, Akcija> mapaAkcije = akcije.get(trenutnoStanje);
			Akcija akcija = mapaAkcije.get(uniformniZnak);

			if (akcija == null) {

				System.err.println("Redak: " + leksickaJedinka.getRedak());
				System.err.print("Ocekivani uniformni znakovi: ");
				for (Entry<String, Akcija> entry : mapaAkcije.entrySet()) {
					System.err.print(entry.getKey() + " ");
				}

				System.err.println();
				System.err.println("Leksicka jedinka: " + leksickaJedinka.toString());
				LeksickaJedinka pronadjenaLeksickaJedinka = null;

				while (brojac < redovi.size()) {

					LeksickaJedinka tmp = new LeksickaJedinka(redovi.get(brojac));
					if (sinkronizacijskiZnakoviGramatike.contains(tmp.getUniformniZnak())) {
						pronadjenaLeksickaJedinka = tmp;
						break;
					}
					brojac++;
				}

				if (pronadjenaLeksickaJedinka == null) {
					System.err.println("Neoporavljiva pogreska");
					System.exit(1);
				}

				while (stog.size() > 1) {

					trenutnoStanje = (StanjeDka) stog.peek();
					if (akcije.get(trenutnoStanje).get(pronadjenaLeksickaJedinka.getUniformniZnak()) != null) {
						akcija = akcije.get(trenutnoStanje).get(pronadjenaLeksickaJedinka.getUniformniZnak());
						leksickaJedinka = pronadjenaLeksickaJedinka;
						uniformniZnak = leksickaJedinka.getUniformniZnak();
						korijen = cvoroviStog.peek();
						break;
					}
					stog.pop();
					stog.pop();
					if (stog.size() == 0) {
						System.err.println("Neoporavljiva pogreska");
						System.exit(1);
					}
					cvoroviStog.pop();
				}
			}

			if (akcija.getVrstaAkcije().equals("Pomakni")) {
				brojac++;
				stog.push(uniformniZnak);
				stog.push(akcija.getStanjeDka());
				Cvor noviCvor = new Cvor(leksickaJedinka.toString(), new ArrayList<>());
				cvoroviStog.push(noviCvor);
			}

			if (akcija.getVrstaAkcije().equals("Reduciraj")) {

				reduciraj = true;
				String lijevo = akcija.getLijevaStrana();
				List<String> desno = akcija.getDesnaStrana();
				if (!akcija.getDesnaStrana().get(0).equals("$")) {
					for (int i = 0; i < desno.size() * 2; ++i) {
						stog.pop();
					}
				}

				List<Cvor> djeca = new ArrayList<>();
				Cvor noviCvor = new Cvor(lijevo, djeca);
				if (akcija.getDesnaStrana().get(0).equals("$")) {
					noviCvor.dodajMalog(new Cvor("$", new ArrayList<Cvor>()));
				} else {
					for (int i = 0; i < desno.size(); ++i) {
						noviCvor.dodajMalog(cvoroviStog.peek());
						cvoroviStog.pop();
					}
				}

				korijen = noviCvor;
				cvoroviStog.push(noviCvor);
				StanjeDka prijeLijeveStraneRedukcije = (StanjeDka) stog.peek();
				stog.push(lijevo);

				Map<String, Akcija> mapaNovoStanje = novoStanje.get(prijeLijeveStraneRedukcije);
				StanjeDka stanje = mapaNovoStanje.get(lijevo).getStanjeDka();
				stog.push(stanje);
			}
			if (akcija.getVrstaAkcije().equals("Prihvati")) {
				break;
			}
		}
		Cvor.ispisi(korijen, 0);
		scanner.close();
	}

	@SuppressWarnings("unchecked")
	private static void ucitajTablicu() throws ClassNotFoundException, IOException {
		FileInputStream fis = new FileInputStream("LRTablica.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		tablica = (GenerirajLRTablicu) ois.readObject();
		fis = new FileInputStream("analizator/sinkronizacijski.tmp");
		ois = new ObjectInputStream(fis);
		sinkronizacijskiZnakoviGramatike = (List<String>) ois.readObject();
		ois.close();
	}
}
