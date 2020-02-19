import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class EpsilonNKA {

	private List<String> nezavrsniZnakoviGramatike;
	private Map<String, List<String>> produkcijeGramatike;
	private List<String> zavrsniZnakoviGramatike;
	private ZapocinjeTablica zapocinje;
	private int brojacID;
	private String pocetno;
	private Stanje pocetnoStanje;
	private List<Stanje> pravaStanja = new ArrayList<>();
	private List<Prijelazi> prijelazi;

	public List<Prijelazi> getPrijelazi() {
		return prijelazi;
	}

	public void setPrijelazi(List<Prijelazi> prijelazi) {
		this.prijelazi = prijelazi;
	}

	public EpsilonNKA(List<String> nezavrsniZnakoviGramatike, List<String> zavrsniZnakoviGramatike,
			Map<String, List<String>> produkcijeGramatike, ZapocinjeTablica zapocinje) {
		
		this.nezavrsniZnakoviGramatike = nezavrsniZnakoviGramatike;
		this.produkcijeGramatike = produkcijeGramatike;
		this.zavrsniZnakoviGramatike = zavrsniZnakoviGramatike;
		this.zapocinje = zapocinje;
		this.pocetno = nezavrsniZnakoviGramatike.get(0);
		this.prijelazi = new ArrayList<>();

		inicijalizirajAutomat();
	}

	public List<String> getNezavrsniZnakoviGramatike() {
		return nezavrsniZnakoviGramatike;
	}

	public void setNezavrsniZnakoviGramatike(List<String> nezavrsniZnakoviGramatike) {
		this.nezavrsniZnakoviGramatike = nezavrsniZnakoviGramatike;
	}

	private void inicijalizirajAutomat() {
		for (Entry<String, List<String>> entry : produkcijeGramatike.entrySet()) {
			if (entry.getKey().equals(pocetno)) {
				TreeSet<String> drvo = new TreeSet<String>();
				drvo.add("$");
				Stanje stanje = new Stanje(brojacID++, entry.getKey(), entry.getValue(), 0, drvo);
				this.pocetnoStanje = stanje;
				pravaStanja.add(stanje);
			}
		}
		stvoriStanjaIPrijelaze();
	}

	private void stvoriStanjaIPrijelaze() {

		for (int i = 0; i < pravaStanja.size(); i++) {

			Stanje stanje = pravaStanja.get(i);

			if (stanje.isJeLiValjanaStavka()) {
				continue;
			}

			Stanje novoStanje = new Stanje(brojacID++, stanje.getLijevaStrana(), stanje.getDesnaStrana(),
					stanje.getRedniBrojTockice() + 1, new TreeSet<String>(stanje.getSkupZapocinje()));

			if (!pravaStanja.contains(novoStanje)) {
				pravaStanja.add(novoStanje);
			}

			Prijelazi prijelaz = new Prijelazi(stanje, novoStanje,
					stanje.getDesnaStrana().get(stanje.getRedniBrojTockice()));
			prijelazi.add(prijelaz);
			Set<String> zapocinjeNovo = new TreeSet<String>();

			if (zavrsniZnakoviGramatike.contains(stanje.getDesnaStrana().get(stanje.getRedniBrojTockice()))) {
				continue;
			}

			for (int j = stanje.getRedniBrojTockice() + 1; j < stanje.getDesnaStrana().size(); j++) {

				String str = stanje.getDesnaStrana().get(j);
				for (String strTmp : zapocinje.getZapocinjeZavrsni().get(str)) {

					if (zavrsniZnakoviGramatike.contains(strTmp)) {
						zapocinjeNovo.add(strTmp);
					}
				}
				if (!zapocinje.getPrazniNezavrsni().contains(str)) {
					break;
				}
			}

			boolean zastavica = true;

			for (int j = stanje.getRedniBrojTockice() + 1; j < stanje.getDesnaStrana().size(); j++) {
				String str = stanje.getDesnaStrana().get(j);
				if (!zapocinje.getPrazniNezavrsni().contains(str)) {
					zastavica = false;
				}
			}

			if (zastavica == true) {
				zapocinjeNovo.addAll(stanje.getSkupZapocinje());
			}

			String poslijeTockice = stanje.getDesnaStrana().get(stanje.getRedniBrojTockice());

			if (produkcijeGramatike.get(poslijeTockice) == null) {
				continue;
			}

			for (String desnaStrana : produkcijeGramatike.get(poslijeTockice)) {

				String[] tmp = desnaStrana.trim().split(" ");
				List<String> polje = new ArrayList<String>();

				for (String str : tmp) {
					polje.add(str);
				}

				Stanje novo = null;

				if (polje.get(0).equals("$")) {
					novo = new Stanje(brojacID++, poslijeTockice, polje, 1, zapocinjeNovo);
				} else {
					novo = new Stanje(brojacID++, poslijeTockice, polje, 0, zapocinjeNovo);
				}

				prijelazi.add(new Prijelazi(stanje, novo, "$"));

				if (!pravaStanja.contains(novo)) {
					pravaStanja.add(novo);
				}
			}
		}
	}

	public Stanje getPocetnoStanje() {
		return pocetnoStanje;
	}

	public void setPocetnoStanje(Stanje pocetnoStanje) {
		this.pocetnoStanje = pocetnoStanje;
	}

}