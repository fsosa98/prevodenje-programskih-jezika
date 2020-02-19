import java.util.ArrayList;

public class Automat {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + brStanja;
		result = prime * result + ((epsPrijelazi == null) ? 0 : epsPrijelazi.hashCode());
		result = prime * result + obavljenoPrijelaza;
		result = prime * result + pocetnoStanje;
		result = prime * result + ((prijelazi == null) ? 0 : prijelazi.hashCode());
		result = prime * result + ((trenutnaStanja == null) ? 0 : trenutnaStanja.hashCode());
		result = prime * result + zavrsnoStanje;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Automat other = (Automat) obj;
		if (brStanja != other.brStanja)
			return false;
		if (epsPrijelazi == null) {
			if (other.epsPrijelazi != null)
				return false;
		} else if (!epsPrijelazi.equals(other.epsPrijelazi))
			return false;
		if (obavljenoPrijelaza != other.obavljenoPrijelaza)
			return false;
		if (pocetnoStanje != other.pocetnoStanje)
			return false;
		if (prijelazi == null) {
			if (other.prijelazi != null)
				return false;
		} else if (!prijelazi.equals(other.prijelazi))
			return false;
		if (trenutnaStanja == null) {
			if (other.trenutnaStanja != null)
				return false;
		} else if (!trenutnaStanja.equals(other.trenutnaStanja))
			return false;
		if (zavrsnoStanje != other.zavrsnoStanje)
			return false;
		return true;
	}

	private int brStanja, pocetnoStanje, zavrsnoStanje;
	private ArrayList<ParStanja> epsPrijelazi;
	private ArrayList<Prijelaz> prijelazi;
	private ArrayList<Integer> trenutnaStanja;

	private int obavljenoPrijelaza;

	public int getBrStanja() {
		return brStanja;
	}

	public void setBrStanja(int brStanja) {
		this.brStanja = brStanja;
	}

	public int getPocetnoStanje() {
		return pocetnoStanje;
	}

	public void setPocetnoStanje(int pocetnoStanje) {
		this.pocetnoStanje = pocetnoStanje;
	}

	public int getZavrsnoStanje() {
		return zavrsnoStanje;
	}

	public void setZavrsnoStanje(int zavrsnoStanje) {
		this.zavrsnoStanje = zavrsnoStanje;
	}

	public ArrayList<ParStanja> getEpsPrijelazi() {
		return epsPrijelazi;
	}

	public void setEpsPrijelazi(ArrayList<ParStanja> epsPrijelazi) {
		this.epsPrijelazi = epsPrijelazi;
	}

	public ArrayList<Prijelaz> getPrijelazi() {
		return prijelazi;
	}

	public void setPrijelazi(ArrayList<Prijelaz> prijelazi) {
		this.prijelazi = prijelazi;
	}

	public ArrayList<Integer> getTrenutnaStanja() {
		return trenutnaStanja;
	}

	public void setTrenutnaStanja(ArrayList<Integer> trenutnaStanja) {
		this.trenutnaStanja = trenutnaStanja;
	}

	public int getobavljenoPrijelaza() {
		return obavljenoPrijelaza;
	}

	public void setobavljenoPrijelaza(int obavljenoPrijelaza) {
		this.obavljenoPrijelaza = obavljenoPrijelaza;
	}

	public Automat(String regularniIzraz) {

		this.prijelazi = new ArrayList<Automat.Prijelaz>();
		this.epsPrijelazi = new ArrayList<Automat.ParStanja>();

		ParStanja par = this.pretvori(regularniIzraz);
		this.pocetnoStanje = par.getLijevoStanje();
		this.zavrsnoStanje = par.getDesnoStanje();
		
		this.trenutnaStanja = new ArrayList<Integer>();
		trenutnaStanja.add(this.pocetnoStanje);

		obaviEpsilonPrijelaze();

	}

	public void reinitialize() {

		trenutnaStanja.clear();
		trenutnaStanja.add(this.pocetnoStanje);
		this.obavljenoPrijelaza = 0;

		obaviEpsilonPrijelaze();

	}

	public Automat() {

	}

	public int novoStanje() {

		return this.brStanja++;

	}

	public boolean jeOperator(String regularniIzraz, int indeks) {

		int br = 0;

		while (indeks - 1 >= 0 && regularniIzraz.charAt(indeks-1) == '\\') {
			br = br + 1;
			indeks = indeks - 1;
		}
		return br % 2 == 0;

	}

	public boolean prihvacaSe() {

		for (int i : trenutnaStanja) {

			if (i == this.zavrsnoStanje) {
				return true;
			}

		}

		return false;

	}

	public void obaviPrijelaze(char c) {

		boolean flag = false;
		
		ArrayList<Integer> novaStanja = new ArrayList<Integer>();
		for (Prijelaz p : prijelazi) {
			if (!trenutnaStanja.contains(p.trenutnoStanje) || c != p.c) {
				continue;
			}
			flag = true;
			novaStanja.add(p.sljedeceStanje);

		}

		if (flag == true) {
			this.obavljenoPrijelaza++;
		}

		this.trenutnaStanja = novaStanja;
		this.obaviEpsilonPrijelaze();

	}

	private void obaviEpsilonPrijelaze() {

		boolean dodano = false;

		do {

			dodano = false;

			for (ParStanja p : this.epsPrijelazi) {

				if (this.trenutnaStanja.contains(p.lijevoStanje) && !this.trenutnaStanja.contains(p.desnoStanje)) {

					dodano = true;
					this.trenutnaStanja.add(p.desnoStanje);

				}

			}

		} while (dodano);

	}

	public ParStanja pretvori(String regularniIzraz) {
		

		ArrayList<String> izbori = new ArrayList<String>();

		int brZagrada = 0;
		int trenutni = 0;

		for (int i = 0; i < regularniIzraz.length(); i++) {

			if (regularniIzraz.charAt(i) == '(' && jeOperator(regularniIzraz, i)) {

				brZagrada++;

			} else if (regularniIzraz.charAt(i) == ')' && jeOperator(regularniIzraz, i)) {

				brZagrada--;

			} else if (brZagrada == 0 && regularniIzraz.charAt(i) == '|' && jeOperator(regularniIzraz, i)) {

				
				String podString=regularniIzraz.substring(trenutni, i);

				izbori.add(podString);
				trenutni = i + 1;

			}

		}

		if (!izbori.isEmpty()) {

			izbori.add(regularniIzraz.substring(trenutni, regularniIzraz.length()));
			

		}

		int lijevoStanje = novoStanje();
		int desnoStanje = novoStanje();

		if (!izbori.isEmpty()) {

			for (int i = 0; i < izbori.size(); i++) {

				ParStanja privremeno = pretvori(izbori.get(i));
				dodajEpsilonPrijelaz(lijevoStanje, privremeno.getLijevoStanje());
				dodajEpsilonPrijelaz(privremeno.getDesnoStanje(), desnoStanje);

			}

		} else {

			boolean prefiksirano = false;

			int zadnjeStanje = lijevoStanje;
			for (int i = 0; i < regularniIzraz.length(); i++) {
				int a, b;

				if (prefiksirano == true) {

					prefiksirano = false;

					char prijelazniZnak = regularniIzraz.charAt(i);

					if (regularniIzraz.charAt(i) == 't') {
						prijelazniZnak = '\t';
					} else if (regularniIzraz.charAt(i) == 'n') {
						prijelazniZnak = '\n';
					} else if (regularniIzraz.charAt(i) == '_') {
						prijelazniZnak = ' ';
					} else {
						prijelazniZnak = regularniIzraz.charAt(i);
					}

					a = novoStanje();
					b = novoStanje();

					dodajPrijelaz(a, b, prijelazniZnak);

				} else {

					if (regularniIzraz.charAt(i) == '\\') {
						prefiksirano = true;
						continue;
					}
					if (regularniIzraz.charAt(i) != '(') {

						a = novoStanje();
						b = novoStanje();

						if (regularniIzraz.charAt(i) == '$') {

							dodajEpsilonPrijelaz(a, b);

						} else {
							dodajPrijelaz(a, b, regularniIzraz.charAt(i));
						}

					} else {

						int j = -1;

						int brojZagrada = 1;

						for (int k = i + 1; k < regularniIzraz.length(); k++) {

							if (regularniIzraz.charAt(k) == '(') {
								brojZagrada++;
							} else if (regularniIzraz.charAt(k) == ')') {
								brojZagrada--;

							}

							if (brojZagrada == 0) {
								j = k;
								break;
							}

						}

						ParStanja privremeno = pretvori(regularniIzraz.substring(i + 1, j));

						a = privremeno.getLijevoStanje();
						b = privremeno.getDesnoStanje();
						i = j;

					}

				}

				if (i + 1 < regularniIzraz.length() && regularniIzraz.charAt(i + 1) == '*') {

					int x = a;
					int y = b;

					a = novoStanje();
					b = novoStanje();

					dodajEpsilonPrijelaz(a, x);
					dodajEpsilonPrijelaz(y, b);
					dodajEpsilonPrijelaz(a, b);
					dodajEpsilonPrijelaz(y, x);

					i = i + 1;

				}

				dodajEpsilonPrijelaz(zadnjeStanje, a);
				zadnjeStanje = b;

			}

			dodajEpsilonPrijelaz(zadnjeStanje, desnoStanje);

		}

		return new ParStanja(lijevoStanje, desnoStanje);
	}

	private void dodajPrijelaz(int a, int b, char prijelazniZnak) {

		this.prijelazi.add(new Prijelaz(a, b, prijelazniZnak));

	}

	private void dodajEpsilonPrijelaz(int s1, int s2) {

		this.epsPrijelazi.add(new ParStanja(s1, s2));
	}

	private static class ParStanja {

		private int lijevoStanje;
		private int desnoStanje;

		public ParStanja(int lijevoStanje, int desnoStanje) {

			this.lijevoStanje = lijevoStanje;
			this.desnoStanje = desnoStanje;

		}

		public int getLijevoStanje() {
			return lijevoStanje;
		}

		public void setLijevoStanje(int lijevoStanje) {
			this.lijevoStanje = lijevoStanje;
		}

		public int getDesnoStanje() {
			return desnoStanje;
		}

		public void setDesnoStanje(int desnoStanje) {
			this.desnoStanje = desnoStanje;
		}

	}

	private static class Prijelaz {

		private int trenutnoStanje, sljedeceStanje;
		private char c;

		public Prijelaz(int trenutnoStanje, int sljedeceStanje, char c) {

			this.trenutnoStanje = trenutnoStanje;
			this.sljedeceStanje = sljedeceStanje;
			this.c = c;

		}

		public int getTrenutnoStanje() {
			return trenutnoStanje;
		}

		public void setTrenutnoStanje(int trenutnoStanje) {
			this.trenutnoStanje = trenutnoStanje;
		}

		public int getSljedeceStanje() {
			return sljedeceStanje;
		}

		public void setSljedeceStanje(int sljedeceStanje) {
			this.sljedeceStanje = sljedeceStanje;
		}

		public char getC() {
			return c;
		}

		public void setC(char c) {
			this.c = c;
		}

	}

}
