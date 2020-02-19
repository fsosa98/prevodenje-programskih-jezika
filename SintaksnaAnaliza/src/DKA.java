import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DKA implements Serializable {

	private static final long serialVersionUID = 5976199593328282714L;

	public StanjeDka getPocetno() {
		return pocetno;
	}

	public void setPocetno(StanjeDka pocetno) {
		this.pocetno = pocetno;
	}

	private List<Prijelazi> epsilonNkaPrijelazi = new ArrayList<Prijelazi>();
	private Set<PrijelazDKA> dkaPrijelazi = new HashSet<>();
	private StanjeDka pocetno;
	private static Set<Stanje> svaStanja = new HashSet<Stanje>();
	private static Set<StanjeDka> svaDkaStanja = new HashSet<StanjeDka>();
	private List<String> sviZnakovi;

	public DKA(List<Prijelazi> epsilonNkaPrijelazi, Stanje pocetnoObicnoStanje, List<String> sviZnakovi) {

		this.sviZnakovi = sviZnakovi;

		this.epsilonNkaPrijelazi = epsilonNkaPrijelazi;

		Set<Stanje> epsilonOkruzenjeTrenutnogStanja = new HashSet<Stanje>();
		pocetnoObicnoStanje.epsilonOkruzenje(epsilonOkruzenjeTrenutnogStanja, epsilonNkaPrijelazi);
		this.pocetno = new StanjeDka(pocetnoObicnoStanje, epsilonOkruzenjeTrenutnogStanja);

		List<StanjeDka> dkaStanjaZaObradit = new ArrayList<>();
		dkaStanjaZaObradit.add(pocetno);

		while (dkaStanjaZaObradit.size() != 0) {
			StanjeDka trenutnoStanje = dkaStanjaZaObradit.get(0);
			dkaStanjaZaObradit.remove(0);

			for (String znak : sviZnakovi) {

				Set<Stanje> stanjaEps = new HashSet<>();
				for (Prijelazi prijelaz : epsilonNkaPrijelazi) {
					if (prijelaz.getStr().equals(znak)
							&& trenutnoStanje.getStavke().contains(prijelaz.getLijevoStanje())) {
						stanjaEps.add(prijelaz.getDesnoStanje());
					}
				}
				Set<Stanje> epsilonOkruzenjeDesnoNovoStanje = new HashSet<Stanje>();
				for (Stanje stanjeZaEps : stanjaEps) {
					stanjeZaEps.epsilonOkruzenje(epsilonOkruzenjeDesnoNovoStanje, epsilonNkaPrijelazi);
				}
				if (epsilonOkruzenjeDesnoNovoStanje.isEmpty()) {
					continue;
				}
				StanjeDka novoStanje = new StanjeDka(epsilonOkruzenjeDesnoNovoStanje.iterator().next(),
						epsilonOkruzenjeDesnoNovoStanje);

				dkaPrijelazi.add(new PrijelazDKA(trenutnoStanje, znak, novoStanje));

				if (!svaDkaStanja.contains(novoStanje)) {
					dkaStanjaZaObradit.add(novoStanje);

				}
				svaDkaStanja.add(novoStanje);

			}

		}

	}

	public Set<PrijelazDKA> getDkaPrijelazi() {
		return dkaPrijelazi;
	}

}
