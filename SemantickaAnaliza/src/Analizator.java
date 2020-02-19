import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Analizator {

	private Cvor korijen;

	private Tablica djelokrug;

	private Tablica globalniDjelokrug;

	private Tablica lokalniDjelokrug;

	private Set<String> definiraneFunkcije = new HashSet<>();
	
	private Set<String> deklariraneFunkcije = new HashSet<>();

	private boolean imamoMajn = false;

	private BiFunction<String, String, Boolean> kastabilno = (t, u) -> t.equals(u)
			|| (u.equals("int") && t.equals("char"));

	private BiFunction<String, Tablica, Cvor> dohvatiLokalnoDeklariranoSDjlokrugom = new BiFunction<String, Tablica, Cvor>() {

		@Override
		public Cvor apply(String t, Tablica u) {

			if (djelokrug.getOtac() == null) {
				for (Cvor c : djelokrug.getDeklarirano()) {

					if (c.getIme().equals(t)) {
						return c;
					}
				}
			} else {
				do {
					for (Cvor c : djelokrug.getDeklarirano()) {

						if (c.getIme().equals(t)) {
							return c;
						}
					}

					djelokrug = djelokrug.getOtac();

				} while (djelokrug.getOtac() != null);
			}

			return null;
		};

	};

	private Function<String, Cvor> dohvatiLokalnoDeklarirano = new Function<String, Cvor>() {

		@Override
		public Cvor apply(String t) {

			for (Cvor c : lokalniDjelokrug.getDeklarirano()) {

				if (c.getIme().equals(t)) {

					return c;

				}
			}
			return null;
		}
	};


	private BiFunction<String, Tablica, List<Cvor>> dohvatiListuDeklariranihFunkcija = new BiFunction<String, Tablica, List<Cvor>>() {

		@Override
		public List<Cvor> apply(String t, Tablica djelokrug) {
			List<Cvor> listaDeklariranih = new ArrayList<Cvor>();
			if (djelokrug == null) {
				return null;
			}

			if (djelokrug.getOtac() == null) {
				for (Cvor c : djelokrug.getDeklarirano()) {

					if (c.isJeFunkcija() && c.getIme().equals(t)) {
						listaDeklariranih.add(c);

					}
				}
			} else {
				do {
					for (Cvor c : djelokrug.getDeklarirano()) {

						if (c.isJeFunkcija() && c.getIme().equals(t)) {
							listaDeklariranih.add(c);

						}
					}

					djelokrug = djelokrug.getOtac();

				} while (djelokrug.getOtac() != null);
			}

			return listaDeklariranih;

		}
	};

	private Function<Cvor, Boolean> primjenjivoDoNizaZnakova = new Function<Cvor, Boolean>() {

		@Override
		public Boolean apply(Cvor t) {
			boolean zastavica = true;
			while (!t.getDjecica().isEmpty()) {
				if (t.getDjecica().size() != 1) {

					zastavica = false;
				}

				t = t.dajMalogNaIndeksu(0);

			}
			if (zastavica == false)
				return zastavica;
			return t.getSadrzaj().contains("NIZ_ZNAKOVA");
		}
	};

	private Function<Cvor, Integer> duljinaZnakova = new Function<Cvor, Integer>() {

		@Override
		public Integer apply(Cvor t) {
			while (!t.getDjecica().isEmpty())
				t = t.dajMalogNaIndeksu(0);
			return t.getSadrzaj().split(" ")[2].length() - 2;
		}
	};

	public Analizator(Cvor korijen) {
		this.korijen = korijen;

		djelokrug = new Tablica(null, new ArrayList<Cvor>());
		lokalniDjelokrug = new Tablica(null, new ArrayList<Cvor>());

		globalniDjelokrug = djelokrug;

		try {

			analiziraj();

		} catch (Iznimka e) {
			System.out.println(e.getMessage());

		}
	}

	private void analiziraj() throws Iznimka {
		prijevodnaJedinica(korijen);

		if (!imamoMajn) {

			throw new Iznimka("main");

		}

		for (String deklarirano : deklariraneFunkcije) {

			if (!definiraneFunkcije.contains(deklarirano)) {

				throw new Iznimka("funkcija");

			}

		}
	}

	private void prijevodnaJedinica(Cvor cvor) throws Iznimka {

		if (cvor.getDjecica().size() == 1) {

			vanjskaDeklaracija(cvor.dajMalogNaIndeksu(0));

		} else {

			prijevodnaJedinica(cvor.dajMalogNaIndeksu(0));
			vanjskaDeklaracija(cvor.dajMalogNaIndeksu(1));
		}

	}

	private void vanjskaDeklaracija(Cvor cvor) throws Iznimka {

		Cvor mali = cvor.dajMalogNaIndeksu(0);


		if (mali.getSadrzaj().equals("<definicija_funkcije>")) {

			definicijaFunkcije(mali);

		} else if (mali.getSadrzaj().equals("<deklaracija>")) {

			deklaracija(mali);
		}
	}


	private void definicijaFunkcije(Cvor cvor) throws Iznimka {

		imeTipa(cvor.dajMalogNaIndeksu(0));

		if (cvor.dajMalogNaIndeksu(0).isJeKonstanta()) {
			
			throw new Iznimka(cvor.toString());
		}
		List<Cvor> deklariraneFunckije = dohvatiListuDeklariranihFunkcija.apply(cvor.dajMalogNaIndeksu(1).getIme(),
				djelokrug);
		if (deklariraneFunckije != null) {
			for (Cvor c : deklariraneFunckije) {
				if (c.isJeDefiniran()) {
					throw new Iznimka(cvor.toString());
				}
			}
		}



		String idn = cvor.dajMalogNaIndeksu(3).getSadrzaj();

		if (idn.startsWith("KR_VOID")) {
			while (djelokrug.getOtac() != null) {
				djelokrug = djelokrug.getOtac();
			}

			List<Cvor> deklariraneGlobalneFunckije = dohvatiListuDeklariranihFunkcija
					.apply(cvor.dajMalogNaIndeksu(0).getIme(), djelokrug);

			for (Cvor funkcija : deklariraneGlobalneFunckije) {
				if (!funkcija.getTip(djelokrug).equals(cvor.dajMalogNaIndeksu(0).getTip(djelokrug))) {
					throw new Iznimka(cvor.toString());
				}
			}



			cvor.setJeDefiniran(true);
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.dodajTip("void");


			cvor.setIme(cvor.dajMalogNaIndeksu(1).getIme());
			if (cvor.getIme().equals("main") && cvor.getTip(djelokrug).equals("int")) {
				imamoMajn = true;
			}

			djelokrug.dodajDeklarirano(cvor);
			lokalniDjelokrug.dodajDeklarirano(cvor);
			definiraneFunkcije.add(cvor.getIme());
			slozenaNaredba(cvor.dajMalogNaIndeksu(5));

		} else {
			listaParametara(cvor.dajMalogNaIndeksu(3));
			while (djelokrug.getOtac() != null) {
				djelokrug = djelokrug.getOtac();
			}
			List<Cvor> deklariraneGlobalneFunckije = dohvatiListuDeklariranihFunkcija
					.apply(cvor.dajMalogNaIndeksu(1).getIme(), djelokrug);
			for (Cvor c : deklariraneGlobalneFunckije) {
				if (!c.getTip(djelokrug).equals(cvor.dajMalogNaIndeksu(0).getTip(djelokrug))) {
					throw new Iznimka(cvor.toString());
				}

			}

			definiraneFunkcije.add(cvor.getIme());
			djelokrug.dodajDeklarirano(cvor);
			lokalniDjelokrug.dodajDeklarirano(cvor);

			cvor.setJeDefiniran(true);
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(1).getIme());

			cvor.dajMalogNaIndeksu(5).setTipovi(cvor.dajMalogNaIndeksu(3).getTipovi(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(3).getTipovi(djelokrug));
			cvor.dajMalogNaIndeksu(5).setImena(cvor.dajMalogNaIndeksu(3).getImena());
			cvor.setImena(cvor.dajMalogNaIndeksu(3).getImena());

			slozenaNaredba(cvor.dajMalogNaIndeksu(5));

		}

	}

	private void listaParametara(Cvor cvor) {
		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().equals("<deklaracija_parametra>")) {

			deklaracijaParametra(cvor.dajMalogNaIndeksu(0));

			cvor.dodajTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.getImena().add(cvor.dajMalogNaIndeksu(0).getIme());
		} else {

			listaParametara(cvor.dajMalogNaIndeksu(0));
			deklaracijaParametra(cvor.dajMalogNaIndeksu(2));
			if (cvor.dajMalogNaIndeksu(0).getImena().contains(cvor.dajMalogNaIndeksu(2).getIme())) {
				throw new Iznimka(cvor.toString());
			}

			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.dodajTip(cvor.dajMalogNaIndeksu(2).getTip(djelokrug));

			cvor.setImena(cvor.dajMalogNaIndeksu(0).getImena());
			cvor.getImena().add(cvor.dajMalogNaIndeksu(2).getIme());
		}
	}

	private void deklaracijaParametra(Cvor cvor) {

		imeTipa(cvor.dajMalogNaIndeksu(0));

		if (cvor.dajMalogNaIndeksu(0).getTip(djelokrug).equals("void")) {
			throw new Iznimka(cvor.toString());
		}

		if (cvor.getDjecica().size() == 2) {

			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));

		} else {
			cvor.setTip("niz" + cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
		}

		cvor.setIme(cvor.dajMalogNaIndeksu(1).getIme());

	}

	private void listaDeklaracija(Cvor cvor) {
		if (cvor.isUnutarPetlje()) {
			for (Cvor c : cvor.getDjecica()) {
				c.setUnutarPetlje(true);
			}
		}

		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().equals("<deklaracija>")) {
			deklaracija(cvor.dajMalogNaIndeksu(0));
		} else {
			listaDeklaracija(cvor.dajMalogNaIndeksu(0));
			deklaracija(cvor.dajMalogNaIndeksu(1));
		}
	}

	private void imeTipa(Cvor cvor) {

		if (cvor.getDjecica().size() == 1) {

			specifikatorTipa(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));

		} else if (cvor.getDjecica().size() == 2) {

			specifikatorTipa(cvor.dajMalogNaIndeksu(1));

			if (cvor.dajMalogNaIndeksu(1).getTip(djelokrug).equals("void")) {

				throw new Iznimka(cvor.toString());

			}

			cvor.setTip(cvor.dajMalogNaIndeksu(1).getTip(djelokrug));

			cvor.setJeKonstanta(true);

		}

	}

	private void specifikatorTipa(Cvor cvor) {

		String tipSpecifikatoraTipa = cvor.dajMalogNaIndeksu(0).getSadrzaj().trim().split(" ")[0];

		if (tipSpecifikatoraTipa.equals("KR_VOID")) {

			cvor.setTip("void");

		} else if (tipSpecifikatoraTipa.equals("KR_INT")) {

			cvor.setTip("int");

		} else if (tipSpecifikatoraTipa.equals("KR_CHAR")) {

			cvor.setTip("char");

		}

	}

	private void deklaracija(Cvor cvor) {

		imeTipa(cvor.dajMalogNaIndeksu(0));

		
		cvor.dajMalogNaIndeksu(1).setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));

		if (cvor.isUnutarPetlje()) {

			cvor.dajMalogNaIndeksu(1).setUnutarPetlje(true);

		}

		if (cvor.dajMalogNaIndeksu(0).isJeKonstanta()) {

			cvor.dajMalogNaIndeksu(1).setJeKonstanta(true);

		}

		listaInitDeklaratora(cvor.dajMalogNaIndeksu(1));

	}

	private void listaInitDeklaratora(Cvor cvor) {

		if (cvor.getDjecica().size() == 1) {

			cvor.dajMalogNaIndeksu(0).setTip(cvor.getTip(djelokrug));

			if (cvor.isUnutarPetlje()) {

				cvor.dajMalogNaIndeksu(0).setUnutarPetlje(true);

			}

			if (cvor.isJeKonstanta()) {

				cvor.dajMalogNaIndeksu(0).setJeKonstanta(true);

			}

			initDeklarator(cvor.dajMalogNaIndeksu(0));

		} else {

			cvor.dajMalogNaIndeksu(0).setTip(cvor.getTip(djelokrug));

			if (cvor.isUnutarPetlje()) {

				cvor.dajMalogNaIndeksu(0).setUnutarPetlje(true);

			}

			if (cvor.isJeKonstanta()) {

				cvor.dajMalogNaIndeksu(0).setJeKonstanta(true);

			}

			listaInitDeklaratora(cvor.dajMalogNaIndeksu(0));

			cvor.dajMalogNaIndeksu(2).setTip(cvor.getTip(djelokrug));

			if (cvor.isUnutarPetlje()) {

				cvor.dajMalogNaIndeksu(2).setUnutarPetlje(true);

			}

			if (cvor.isJeKonstanta()) {

				cvor.dajMalogNaIndeksu(2).setJeKonstanta(true);

			}

			initDeklarator(cvor.dajMalogNaIndeksu(2));

		}

	}

	private void initDeklarator(Cvor cvor) {

		if (cvor.getDjecica().size() == 1) {

			cvor.dajMalogNaIndeksu(0).setTip(cvor.getTip(djelokrug));

			if (cvor.isUnutarPetlje()) {

				cvor.dajMalogNaIndeksu(0).setUnutarPetlje(true);

			}

			if (cvor.isJeKonstanta()) {

				cvor.dajMalogNaIndeksu(0).setJeKonstanta(true);

			}

			izravniDeklarator(cvor.dajMalogNaIndeksu(0));

			if (cvor.dajMalogNaIndeksu(0).isJeKonstanta()) {

				throw new Iznimka(cvor.toString());

			}


		} else {
			cvor.dajMalogNaIndeksu(0).setTip(cvor.getTip(djelokrug));

			if (cvor.isUnutarPetlje()) {

				cvor.dajMalogNaIndeksu(0).setUnutarPetlje(true);

			}

			if (cvor.isJeKonstanta()) {

				cvor.dajMalogNaIndeksu(0).setJeKonstanta(true);

			}

			izravniDeklarator(cvor.dajMalogNaIndeksu(0));

			inicijalizator(cvor.dajMalogNaIndeksu(2));

			String tip1 = cvor.dajMalogNaIndeksu(0).getTip(djelokrug);
			String tip2 = cvor.dajMalogNaIndeksu(2).getTip(djelokrug);

			if (tip1.startsWith("niz")) {

				if (!(cvor.dajMalogNaIndeksu(0).getDjecica().size() > cvor.dajMalogNaIndeksu(2).getDjecica().size())) {
					throw new Iznimka(cvor.toString());
				}

				for (String tip : cvor.dajMalogNaIndeksu(2).getTipovi(djelokrug)) {

					if (!kastabilno.apply(tip, tip1.substring(3))) {

						throw new Iznimka(cvor.toString());

					}

				}

			} else {

				if (!kastabilno.apply(tip2, tip1) || cvor.dajMalogNaIndeksu(2).isJeFunkcija()) {

					throw new Iznimka(cvor.toString());

				}

			}

		}

	}

	private void izravniDeklarator(Cvor cvor) {

		if (cvor.getDjecica().size() == 1) {

			if (cvor.getTip(djelokrug).equals("void")) {
				throw new Iznimka(cvor.toString());

			}

			ArrayList<Cvor> lokalnoDeklarirano = djelokrug.getDeklarirano();

			if (lokalnoDeklarirano != null) {
				for (Cvor cv : lokalnoDeklarirano) {

					if (cv.getIme().equals(cvor.dajMalogNaIndeksu(0).getIme())) {
						throw new Iznimka(cvor.toString());

					}

				}
			}


			djelokrug.dodajDeklarirano(cvor);
			lokalniDjelokrug.dodajDeklarirano(cvor);

			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setJeDefiniran(false);


		} else if (cvor.dajMalogNaIndeksu(2).getSadrzaj().contains("BROJ")) {

			if (cvor.getTip(djelokrug).equals("void")) {

				throw new Iznimka(cvor.toString());

			}
			ArrayList<Cvor> lokalnoDeklarirano = djelokrug.getDeklarirano();
			if (lokalnoDeklarirano != null) {
				for (Cvor cv : lokalnoDeklarirano) {

					if (cv.getIme().equals(cvor.getIme())) {

						throw new Iznimka(cvor.toString());

					}

				}
			}

			int broj = Integer.parseInt(cvor.dajMalogNaIndeksu(2).getSadrzaj().trim().split(" ")[2]);
			if (broj > 1024 || broj <= 0) {

				throw new Iznimka(cvor.toString());

			}
			djelokrug.dodajDeklarirano(cvor);
			lokalniDjelokrug.dodajDeklarirano(cvor);
			cvor.setTip("niz" + cvor.getTip(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());

			cvor.setJeDefiniran(false);
			cvor.setDuljinaNiza(broj);

		} else if (cvor.dajMalogNaIndeksu(2).getSadrzaj().contains("KR_VOID")) {
			String ime = cvor.dajMalogNaIndeksu(0).getIme();

			if (dohvatiLokalnoDeklarirano.apply(ime) != null) {

				
				if (!(dohvatiLokalnoDeklarirano.apply(ime).getTipovi(djelokrug).size() == 1
						&& dohvatiLokalnoDeklarirano.apply(ime).getTipovi(djelokrug).get(0).equals("void"))) {
					throw new Iznimka(cvor.toString());
				}

				cvor.dodajTip("void");
				cvor.setIme(ime);

			} else {

				cvor.setIme(ime);
				cvor.dodajTip("void");
				deklariraneFunkcije.add(cvor.getIme());
				djelokrug.dodajDeklarirano(cvor);
				lokalniDjelokrug.dodajDeklarirano(cvor);

			}

		} else {


			listaParametara(cvor.dajMalogNaIndeksu(2));

			String ime = cvor.dajMalogNaIndeksu(0).getIme();

			cvor.setTipovi(cvor.dajMalogNaIndeksu(2).getTipovi(djelokrug));
			cvor.setIme(ime);

			if (dohvatiLokalnoDeklarirano.apply(ime) == null) {

				

				deklariraneFunkcije.add(cvor.getIme());
				djelokrug.dodajDeklarirano(cvor);
				lokalniDjelokrug.dodajDeklarirano(cvor);

			} else {

				if (cvor.dajMalogNaIndeksu(2).getTipovi(djelokrug)
						.equals(dohvatiLokalnoDeklarirano.apply(ime).getTipovi(djelokrug))) {

					throw new Iznimka(cvor.toString());

				}

			}

		}
	}

	private void inicijalizator(Cvor cvor) {

		if (cvor.getDjecica().size() != 1) {

			listaIzrazaPridruzivanja(cvor.dajMalogNaIndeksu(1));
			cvor.setDuljinaNiza(cvor.dajMalogNaIndeksu(1).getDuljinaNiza());
			cvor.setTipovi(cvor.dajMalogNaIndeksu(1).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(1).getIme());

		} else {

			izrazPridruzivanja(cvor.dajMalogNaIndeksu(0));
			if (primjenjivoDoNizaZnakova.apply(cvor.dajMalogNaIndeksu(0))) {

				cvor.setDuljinaNiza(duljinaZnakova.apply(cvor));
				for (int i = 0; i < cvor.getDuljinaNiza(); i++) {

					cvor.dodajTip("char");

				}

			} else {

				cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
				cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));

			}

			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());

		}

	}

	private void listaIzrazaPridruzivanja(Cvor cvor) {

		if (cvor.getDjecica().size() != 1) {

			listaIzrazaPridruzivanja(cvor.dajMalogNaIndeksu(0));
			izrazPridruzivanja(cvor.dajMalogNaIndeksu(2));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.dodajTip(cvor.dajMalogNaIndeksu(2).getTip(djelokrug));
			cvor.setDuljinaNiza(cvor.dajMalogNaIndeksu(0).getDuljinaNiza() + 1);

		} else {

			izrazPridruzivanja(cvor.dajMalogNaIndeksu(0));
			cvor.dodajTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setDuljinaNiza(1);

		}

	}

	private void izrazPridruzivanja(Cvor cvor) {
		

		if (cvor.getDjecica().size() == 1) {

			logIliIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));

		} else {

			postfiksIzraz(cvor.dajMalogNaIndeksu(0));

			if (!cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug)) {

				throw new Iznimka(cvor.toString());

			}

			izrazPridruzivanja(cvor.dajMalogNaIndeksu(2));

			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug),
					cvor.dajMalogNaIndeksu(0).getTip(djelokrug))) {

				throw new Iznimka(cvor.toString());

			}

			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setLijeviIzraz(false);

		}

	}

	private void logIliIzraz(Cvor cvor) {

		if (cvor.getDjecica().size() == 1) {
			logIIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());

		} else {

			logIliIzraz(cvor.dajMalogNaIndeksu(0));

			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(0).getTip(djelokrug), "int")) {

				throw new Iznimka(cvor.toString());

			}

			logIIzraz(cvor.dajMalogNaIndeksu(2));

			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")) {

				throw new Iznimka(cvor.toString());

			}

			cvor.setTip("int");
			cvor.setLijeviIzraz(false);

		}

	}

	private void primarniIzraz(Cvor cvor) {

		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().startsWith("IDN")) {
			Cvor nasao = dohvatiLokalnoDeklariranoSDjlokrugom.apply(cvor.dajMalogNaIndeksu(0).getIme(),
					djelokrug.copy());

			if (nasao == null) {
				throw new Iznimka(cvor.toString());
			}

			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));


		}

		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().startsWith("BROJ")) {
			try {
				Integer.parseInt(cvor.dajMalogNaIndeksu(0).getIme());
				cvor.setTip("int");
				cvor.setLijeviIzraz(false);

			} catch (NumberFormatException e) {
				throw new Iznimka(cvor.toString());
			}
		}

		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().startsWith("ZNAK")) {

			String s = cvor.dajMalogNaIndeksu(0).getSadrzaj().split(" ")[2];
			s = s.substring(1, s.length() - 1);
			if (s.length() == 2) {
				if (!(s.charAt(0) == '\\')) {
					throw new Iznimka(cvor.toString());
				}
				char c = s.charAt(1);
				if (!(c == 't' || c == '0' || c == 'n' || c == '"' || c == '\'' || c == '\\')) {
					throw new Iznimka(cvor.toString());
				}
			} else if (s.length() == 1) {
			} else {
				throw new Iznimka(cvor.toString());
			}

			cvor.setTip("char");
			cvor.setLijeviIzraz(false);

		}

		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().startsWith("NIZ_ZNAKOVA")) {
			char[] data = cvor.dajMalogNaIndeksu(0).getSadrzaj().split(" ")[2].toCharArray();
			for (int i = 0; i < data.length; i++) {
				if (data[i] == '\\' && (i + 1) < data.length && !(data[i + 1] == '\\' || data[i + 1] == '\''
						|| data[i + 1] == 't' || data[i + 1] == '0' || data[i + 1] == 'n' || data[i + 1] == '"')) {
					throw new Iznimka(cvor.toString());
				}
			}
			cvor.setTip("nizchar");
			cvor.setJeKonstanta(true);
			cvor.setLijeviIzraz(false);
		}

		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().startsWith("L_ZAGRADA")) {
			izraz(cvor.dajMalogNaIndeksu(1));
			cvor.setTip(cvor.dajMalogNaIndeksu(1).getTip(djelokrug));
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(1).isLijeviIzraz(djelokrug));

		}

	}

	private void postfiksIzraz(Cvor cvor) {

		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().equals("<primarni_izraz>")) {

			primarniIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));

		} else if (cvor.dajMalogNaIndeksu(1).getSadrzaj().startsWith("L_UGL_ZAGRADA")) {

			postfiksIzraz(cvor.dajMalogNaIndeksu(0));

			if (!cvor.dajMalogNaIndeksu(0).getTip(djelokrug).substring(0, 3).equals("niz")) {

				throw new Iznimka(cvor.toString());

			}

			String X = cvor.dajMalogNaIndeksu(0).getTip(djelokrug).substring(3);
			izraz(cvor.dajMalogNaIndeksu(2));

			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")) {

				throw new Iznimka(cvor.toString());

			}

			cvor.setTip(X);

			cvor.setLijeviIzraz(!cvor.dajMalogNaIndeksu(0).isJeKonstanta());

		} else if (cvor.dajMalogNaIndeksu(1).getSadrzaj().startsWith("L_ZAGRADA")) {

			postfiksIzraz(cvor.dajMalogNaIndeksu(0));

			if (cvor.dajMalogNaIndeksu(2).getSadrzaj().equals("<lista_argumenata>")) {

				listaArgumenata(cvor.dajMalogNaIndeksu(2));


				if (cvor.dajMalogNaIndeksu(0).isJeFunkcija()) {


					if (cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug).size() == cvor.dajMalogNaIndeksu(2)
							.getTipovi(djelokrug).size()) {

						for (int i = 0; i < cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug).size(); i++) {

							if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTipovi(djelokrug).get(i),
									cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug).get(i))) {
								throw new Iznimka(cvor.toString());

							}

						}

					} else {

						throw new Iznimka(cvor.toString());
					

					}

				} else {

					throw new Iznimka(cvor.toString());

				}

			} else {

				if (cvor.dajMalogNaIndeksu(0).isJeFunkcija()) {

					if ("void".equals(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug).get(0))) {
						cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
					} else {
						throw new Iznimka(cvor.toString());
					}

				} else {
					throw new Iznimka(cvor.toString());
				}
			}
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setLijeviIzraz(false);

		} else {

			postfiksIzraz(cvor.dajMalogNaIndeksu(0));

			if (cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug)
					&& kastabilno.apply(cvor.dajMalogNaIndeksu(0).getTip(djelokrug), "int")) {

				cvor.setTip("int");

				cvor.setLijeviIzraz(false);

			} else {

				throw new Iznimka(cvor.toString());

			}
		}
	}

	private void listaArgumenata(Cvor cvor) {
		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().equals("<izraz_pridruzivanja>")) {
			izrazPridruzivanja(cvor.dajMalogNaIndeksu(0));
			cvor.dodajTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));

		}
		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().equals("<lista_argumenata>")) {
			listaArgumenata(cvor.dajMalogNaIndeksu(0));
			izrazPridruzivanja(cvor.dajMalogNaIndeksu(2));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.dodajTip(cvor.dajMalogNaIndeksu(2).getTip(djelokrug));
		}

	}

	private void unarniIzraz(Cvor cvor) {

		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().equals("<postfiks_izraz>")) {
			postfiksIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
		} else if (cvor.dajMalogNaIndeksu(0).getSadrzaj().equals("<unarni_operator>")) {
			castIzraz(cvor.dajMalogNaIndeksu(1));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(1).getTip(djelokrug), "int")) {
				throw new Iznimka(cvor.toString());
			}
			cvor.setTip("int");
			cvor.setLijeviIzraz(false);

		} else {

			unarniIzraz(cvor.dajMalogNaIndeksu(1));
			if (!cvor.dajMalogNaIndeksu(1).isLijeviIzraz(djelokrug)
					|| !kastabilno.apply(cvor.dajMalogNaIndeksu(1).getTip(djelokrug), "int")) {
				throw new Iznimka(cvor.toString());
			}
			cvor.setTip("int");
			cvor.setLijeviIzraz(false);

		}

	}

	private void castIzraz(Cvor cvor) {
		if (cvor.getDjecica().size() == 1) {
			unarniIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());

		} else {
			imeTipa(cvor.dajMalogNaIndeksu(1));
			castIzraz(cvor.dajMalogNaIndeksu(3));

			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(3).getTip(djelokrug),
					cvor.dajMalogNaIndeksu(1).getTip(djelokrug))
					&& !(cvor.dajMalogNaIndeksu(3).getTip(djelokrug).equals("int"))
					&& cvor.dajMalogNaIndeksu(1).getTip(djelokrug).equals("char")
					|| cvor.dajMalogNaIndeksu(3).isJeFunkcija() || cvor.dajMalogNaIndeksu(1).isJeFunkcija()) {

				throw new Iznimka(cvor.toString());

			}

			cvor.setTip(cvor.dajMalogNaIndeksu(1).getTip(djelokrug));
			cvor.setLijeviIzraz(false);

		}

	}

	private void multiplikativniIzraz(Cvor cvor) {

		if (cvor.dajMalogNaIndeksu(0).getSadrzaj().equals("<cast_izraz>")) {
			castIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));
		} else {
			multiplikativniIzraz(cvor.dajMalogNaIndeksu(0));
			if (kastabilno.apply(cvor.dajMalogNaIndeksu(0).getTip(djelokrug), "int")) {
				castIzraz(cvor.dajMalogNaIndeksu(2));
			} else {
				throw new Iznimka(cvor.getSadrzaj());
			}
			if (kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")) {
			} else {
				throw new Iznimka(cvor.getSadrzaj());
			}
			cvor.setTip("int");
			cvor.setLijeviIzraz(false);

		}

	}

	private void izraz(Cvor cvor) {

		if (cvor.getDjecica().size() == 1) {

			izrazPridruzivanja(cvor.dajMalogNaIndeksu(0));

			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));

		} else {

			izraz(cvor.dajMalogNaIndeksu(0));
			izrazPridruzivanja(cvor.dajMalogNaIndeksu(2));
			cvor.setTip(cvor.dajMalogNaIndeksu(2).getTip(djelokrug));
			cvor.setLijeviIzraz(false);

		}

	}

	private void odnosniIzraz(Cvor cvor) {

		if (cvor.getDjecica().size() == 1) {

			aditivniIzraz(cvor.dajMalogNaIndeksu(0));

			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));

		} else {

			odnosniIzraz(cvor.dajMalogNaIndeksu(0));

			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(0).getTip(djelokrug), "int")) {

				throw new Iznimka(cvor.toString());

			}

			aditivniIzraz(cvor.dajMalogNaIndeksu(2));

			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")) {

				throw new Iznimka(cvor.toString());

			}

			cvor.setTip("int");
			cvor.setLijeviIzraz(false);

		}
	}

	private void logIIzraz(Cvor cvor) {
		if (cvor.getDjecica().size() == 1) {

			binIliIzraz(cvor.dajMalogNaIndeksu(0));

			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));

		} else {


			logIIzraz(cvor.dajMalogNaIndeksu(0));

			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(0).getTip(djelokrug), "int")) {

				throw new Iznimka(cvor.toString());

			}

			binIliIzraz(cvor.dajMalogNaIndeksu(2));

			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")) {

				throw new Iznimka(cvor.toString());

			}

			cvor.setTip("int");
			cvor.setLijeviIzraz(false);
		}

	}

	private void aditivniIzraz(Cvor cvor) {
		if (cvor.getDjecica().size() != 1) {
			aditivniIzraz(cvor.dajMalogNaIndeksu(0));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(0).getTip(djelokrug), "int")) {
				throw new Iznimka(cvor.toString());
			}
			multiplikativniIzraz(cvor.dajMalogNaIndeksu(2));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")) {
				throw new Iznimka(cvor.toString());
			}
			cvor.setTip("int");
			cvor.setLijeviIzraz(false);
		} else {
			multiplikativniIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));
		}
	}

	private void jednakosniIzraz(Cvor cvor) {
		if (cvor.getDjecica().size() != 1) {
			jednakosniIzraz(cvor.dajMalogNaIndeksu(0));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(0).getTip(djelokrug), "int")
					|| cvor.dajMalogNaIndeksu(0).isJeFunkcija()) {
				throw new Iznimka(cvor.toString());
			}
			odnosniIzraz(cvor.dajMalogNaIndeksu(2));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")
					|| cvor.dajMalogNaIndeksu(2).isJeFunkcija()) {
				throw new Iznimka(cvor.toString());
			}
			cvor.setTip("int");
			cvor.setLijeviIzraz(false);
		} else {
			odnosniIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));
		}
	}

	private void binIIzraz(Cvor cvor) {
		if (cvor.getDjecica().size() != 1) {
			binIIzraz(cvor.dajMalogNaIndeksu(0));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(0).getTip(djelokrug), "int")) {
				throw new Iznimka(cvor.toString());
			}
			jednakosniIzraz(cvor.dajMalogNaIndeksu(2));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")) {
				throw new Iznimka(cvor.toString());
			}
			cvor.setTip("int");
			cvor.setLijeviIzraz(false);
		} else {
			jednakosniIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));
		}
	}

	private void binXiliIzraz(Cvor cvor) {
		if (cvor.getDjecica().size() != 1) {
			binXiliIzraz(cvor.dajMalogNaIndeksu(0));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(0).getTip(djelokrug), "int")) {
				throw new Iznimka(cvor.toString());
			}
			binIIzraz(cvor.dajMalogNaIndeksu(2));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")) {
				throw new Iznimka(cvor.toString());
			}
			cvor.setTip("int");
			cvor.setLijeviIzraz(false);
		} else {
			binIIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));
		}
	}

	private void binIliIzraz(Cvor cvor) {

		if (cvor.getDjecica().size() != 1) {
			binIliIzraz(cvor.dajMalogNaIndeksu(0));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(0).getTip(djelokrug), "int")) {
				throw new Iznimka(cvor.toString());
			}
			binXiliIzraz(cvor.dajMalogNaIndeksu(2));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")) {
				throw new Iznimka(cvor.toString());
			}
			cvor.setTip("int");
			cvor.setLijeviIzraz(false);
		} else {
			binXiliIzraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
			cvor.setLijeviIzraz(cvor.dajMalogNaIndeksu(0).isLijeviIzraz(djelokrug));
		}
	}

	private void naredba(Cvor cvor) {

		Cvor desno = cvor.dajMalogNaIndeksu(0);
		if (cvor.isUnutarPetlje()) {
			desno.setUnutarPetlje(true);
		}
		if (desno.getSadrzaj().equals("<naredba_skoka>")) {
			naredbaSkoka(desno);
		} else if (desno.getSadrzaj().equals("<naredba_grananja>")) {
			naredbaGrananja(desno);
		} else if (desno.getSadrzaj().equals("<naredba_petlje>")) {
			naredbaPetlje(desno);
		} else if (desno.getSadrzaj().equals("<slozena_naredba>")) {
			slozenaNaredba(desno);
		} else if (desno.getSadrzaj().equals("<izraz_naredba>")) {
			izrazNaredba(desno);
		}
	}

	private void naredbaPetlje(Cvor cvor) {
		if (cvor.getDjecica().size() == 5) {
			izraz(cvor.dajMalogNaIndeksu(2));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")
					|| cvor.dajMalogNaIndeksu(2).isJeFunkcija()) {
				throw new Iznimka(cvor.toString());
			}
			cvor.dajMalogNaIndeksu(4).setUnutarPetlje(true);
			naredba(cvor.dajMalogNaIndeksu(4));
		} else {
			izrazNaredba(cvor.dajMalogNaIndeksu(2));
			izrazNaredba(cvor.dajMalogNaIndeksu(3));
			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(3).getTip(djelokrug), "int")
					|| cvor.dajMalogNaIndeksu(3).isJeFunkcija()) {
				throw new Iznimka(cvor.toString());
			}
			if (cvor.getDjecica().size() == 6) {
				cvor.dajMalogNaIndeksu(5).setUnutarPetlje(true);
				naredba(cvor.dajMalogNaIndeksu(5));
			} else {
				izraz(cvor.dajMalogNaIndeksu(4));
				cvor.dajMalogNaIndeksu(6).setUnutarPetlje(true);
				naredba(cvor.dajMalogNaIndeksu(6));
			}
		}
	}

	private void naredbaGrananja(Cvor cvor) {
		izraz(cvor.dajMalogNaIndeksu(2));
		if (!kastabilno.apply(cvor.dajMalogNaIndeksu(2).getTip(djelokrug), "int")
				|| cvor.dajMalogNaIndeksu(2).isJeFunkcija()) {
			throw new Iznimka(cvor.toString());
		}
		naredba(cvor.dajMalogNaIndeksu(4));
		if (cvor.getDjecica().size() > 5) {
			naredba(cvor.dajMalogNaIndeksu(6));
		}
	}

	private void naredbaSkoka(Cvor cvor) {

		String type = "";
		Tablica tmpTablica = djelokrug.copy();

		boolean nasao = false;
		while (tmpTablica != null) {
			for (int i = tmpTablica.getDeklarirano().size() - 1; i >= 0; i--) {
				Cvor deklariran = tmpTablica.getDeklarirano().get(i);
				if (deklariran.isJeFunkcija() && deklariran.isJeDefiniran()) {
					type = deklariran.getTip(tmpTablica);
					nasao = true;
					break;
				}
			}
			if (nasao) {
				break;
			}
			tmpTablica = tmpTablica.getOtac();
		}

		if (cvor.getDjecica().size() == 3) {

			izraz(cvor.dajMalogNaIndeksu(1));

			if (!kastabilno.apply(cvor.dajMalogNaIndeksu(1).getTip(djelokrug), type)
					|| cvor.dajMalogNaIndeksu(1).isJeFunkcija()) {

				throw new Iznimka(cvor.toString());

			}

		} else if (cvor.dajMalogNaIndeksu(0).getSadrzaj().startsWith("KR_RETURN")) {

			if (!type.equals("void")) {
				throw new Iznimka(cvor.toString());
			}
		} else {
			if (!djelokrug.isUnutarPetlje()) {
				throw new Iznimka(cvor.toString());
			}
		}
	}

	private void izrazNaredba(Cvor cvor) {
		if (cvor.getDjecica().size() != 1) {
			izraz(cvor.dajMalogNaIndeksu(0));
			cvor.setTip(cvor.dajMalogNaIndeksu(0).getTip(djelokrug));
			cvor.setTipovi(cvor.dajMalogNaIndeksu(0).getTipovi(djelokrug));
			cvor.setIme(cvor.dajMalogNaIndeksu(0).getIme());
		} else {
			cvor.setTip("int");
		}
	}

	private void slozenaNaredba(Cvor cvor) {

		Tablica copyOfScope = new Tablica(djelokrug.getOtac(), djelokrug.getDeklarirano());

		lokalniDjelokrug = new Tablica(djelokrug, new ArrayList<>());

		if (djelokrug.isUnutarPetlje()) {

			copyOfScope.setUnutarPetlje(true);

		}

		Tablica newScope = new Tablica(copyOfScope, copyOfScope.getDeklarirano());

		if (cvor.isUnutarPetlje() || djelokrug.isUnutarPetlje()) {

			newScope.setUnutarPetlje(true);

		}

		djelokrug = newScope;

		if (cvor.isUnutarPetlje()) {
			djelokrug.setUnutarPetlje(true);
		}
		for (int i = 0; i < cvor.getTipovi(djelokrug).size(); ++i) {
			Cvor newNode = new Cvor("<" + cvor.getImena().get(i), -1, new ArrayList<>());
			newNode.setTip(cvor.getTipovi(djelokrug).get(i));
			if (cvor.isUnutarPetlje())
				newNode.setUnutarPetlje(true);

			newNode.setIme(cvor.getImena().get(i));

			djelokrug.dodajDeklarirano(newNode);
			lokalniDjelokrug.dodajDeklarirano(cvor);
		}


		if (cvor.getDjecica().size() == 3) {
			listaNaredbi(cvor.dajMalogNaIndeksu(1));
		} else {
			if (cvor.isUnutarPetlje())
				cvor.dajMalogNaIndeksu(1).setUnutarPetlje(true);
			listaDeklaracija(cvor.dajMalogNaIndeksu(1));
			listaNaredbi(cvor.dajMalogNaIndeksu(2));
		}
		djelokrug = djelokrug.getOtac();
	}

	private void listaNaredbi(Cvor cvor) {
		if (cvor.getDjecica().size() != 1) {
			listaNaredbi(cvor.dajMalogNaIndeksu(0));
			naredba(cvor.dajMalogNaIndeksu(1));
		} else {
			naredba(cvor.dajMalogNaIndeksu(0));
		}
	}

}