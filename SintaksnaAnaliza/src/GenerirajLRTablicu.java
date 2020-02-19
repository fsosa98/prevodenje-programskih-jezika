import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenerirajLRTablicu implements Serializable {

	private static final long serialVersionUID = -5712845820753646661L;
	private List<String> nezavrsniZnakoviGramatike;
	private List<String> zavrsniZnakoviGramatike;
	private Set<PrijelazDKA> dkaPrijelazi = new HashSet<>();
	private Set<StanjeDka> stanjaDKA = new HashSet<StanjeDka>();
	private StanjeDka pocetnoStanjeDka;
	private Map<String, List<String>> produkcijeGramatike = new LinkedHashMap<String, List<String>>();

	private DKA dka;

	private Map<StanjeDka, Map<String, Akcija>> akcije;
	private Map<StanjeDka, Map<String, Akcija>> novoStanje;

	public Map<StanjeDka, Map<String, Akcija>> getAkcije() {
		return akcije;
	}

	public void setAkcije(Map<StanjeDka, Map<String, Akcija>> akcije) {
		this.akcije = akcije;
	}

	public Map<StanjeDka, Map<String, Akcija>> getNovoStanje() {
		return novoStanje;
	}

	public void setNovoStanje(Map<StanjeDka, Map<String, Akcija>> novoStanje) {
		this.novoStanje = novoStanje;
	}

	public StanjeDka getPocetnoStanjeDka() {
		return pocetnoStanjeDka;
	}

	public void setPocetnoStanjeDka(StanjeDka pocetnoStanjeDka) {
		this.pocetnoStanjeDka = pocetnoStanjeDka;
	}

	public GenerirajLRTablicu(List<String> nezavrsniZnakoviGramatike, List<String> zavrsniZnakoviGramatike, DKA dka,
			Map<String, List<String>> produkcijeGramatike) {

		this.produkcijeGramatike = produkcijeGramatike;
		this.nezavrsniZnakoviGramatike = nezavrsniZnakoviGramatike;
		this.zavrsniZnakoviGramatike = zavrsniZnakoviGramatike;
		this.dka = dka;
		this.pocetnoStanjeDka = dka.getPocetno();
		this.dkaPrijelazi = dka.getDkaPrijelazi();

		for (PrijelazDKA prijelaz : dkaPrijelazi) {
			stanjaDKA.add(prijelaz.lijevoDio);
			stanjaDKA.add(prijelaz.desniDio);
		}

		this.akcije = new HashMap<StanjeDka, Map<String, Akcija>>();
		this.novoStanje = new HashMap<StanjeDka, Map<String, Akcija>>();

		for (StanjeDka dkaStanje : stanjaDKA) {

			HashMap<String, Akcija> akcijeMap = new HashMap<String, Akcija>();

			akcije.put(dkaStanje, akcijeMap);

			HashMap<String, Akcija> novoStanjeMap = new HashMap<String, Akcija>();

			novoStanje.put(dkaStanje, novoStanjeMap);
		}

		generirajTablicuIspravno();

	}

	private void generirajTablicuIspravno() {

		for (StanjeDka dkaStanje : stanjaDKA) {
			for (Stanje stanje : dkaStanje.getStavke()) {
				if (stanje.isJeLiValjanaStavka()) {
					Set<String> skupZapocinje = stanje.getSkupZapocinje();
					Map<String, Akcija> red = akcije.get(dkaStanje);
					for (String jedanIzSkupaZapocinje : skupZapocinje) {
						Akcija pravaAkcija = null;
						if (red.containsKey(jedanIzSkupaZapocinje)) {

							Akcija akcijaUTablici = red.get(jedanIzSkupaZapocinje);
							Akcija akcijaKojaZeliBitUTablici = new Akcija("Reduciraj", stanje.getLijevaStrana(),
									stanje.getDesnaStrana());

							if (akcijaUTablici == null) {
								pravaAkcija = akcijaKojaZeliBitUTablici;
								red.put(jedanIzSkupaZapocinje, pravaAkcija);
								continue;
							}

							Set<String> kljuceviProdukcija = produkcijeGramatike.keySet();
							boolean nasli = false;
							for (String kljuc : kljuceviProdukcija) {
								if (kljuc.equals(akcijaUTablici.getLijevaStrana())
										|| kljuc.equals(akcijaKojaZeliBitUTablici.getLijevaStrana())) {
									List<String> produkcije = produkcijeGramatike.get(kljuc);
									for (String produkcija : produkcije) {
										if (produkcija.equals(akcijaUTablici.getLijevaStrana())) {
											pravaAkcija = akcijaUTablici;
											nasli = true;
											break;
										}

										if (produkcija.equals(akcijaKojaZeliBitUTablici.getLijevaStrana())) {
											pravaAkcija = akcijaKojaZeliBitUTablici;
											nasli = true;
											break;
										}
									}
								}
								if (nasli) {
									break;
								}

							}

							System.err.println("Reduciraj/Reduciraj");
						}
						if (pravaAkcija == null) {
							red.put(jedanIzSkupaZapocinje,
									new Akcija("Reduciraj", stanje.getLijevaStrana(), stanje.getDesnaStrana()));

						} else {
							red.put(jedanIzSkupaZapocinje, pravaAkcija);
						}

					}
				}
			}
		}

		for (PrijelazDKA prijelazDKA : dkaPrijelazi) {

			String stupac = prijelazDKA.prelazi;
			if (zavrsniZnakoviGramatike.contains(stupac)) {
				Map<String, Akcija> red = akcije.get(prijelazDKA.lijevoDio);
				red.put(stupac, new Akcija("Pomakni", prijelazDKA.desniDio));
			}

			if (nezavrsniZnakoviGramatike.contains(stupac)) {
				Map<String, Akcija> red = novoStanje.get(prijelazDKA.lijevoDio);
				red.put(stupac, new Akcija("Stavi", prijelazDKA.desniDio));
			}
		}

		for (StanjeDka dkaStanje : stanjaDKA) {
			for (Stanje stavka : dkaStanje.getStavke()) {
				if (stavka.getLijevaStrana().equals(pocetnoStanjeDka.getGlavnoStanje().getLijevaStrana())
						&& stavka.getDesnaStrana().equals(pocetnoStanjeDka.getGlavnoStanje().getDesnaStrana())
						&& stavka.isJeLiValjanaStavka()) {

					Set<String> skupZapocinje = stavka.getSkupZapocinje();
					Map<String, Akcija> red = akcije.get(dkaStanje);

					for (String jedanIzSkupaZapocinje : skupZapocinje) {
						red.put(jedanIzSkupaZapocinje, new Akcija("Prihvati"));
					}

					break;
				}
			}

		}
	}

}
