import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class LA {

	private static ArrayList<String> stanja = new ArrayList<String>();
	private static ArrayList<String> leksickeJedinke = new ArrayList<String>();
	private static ArrayList<PraviloLeksickogAnalizatora> pravila = new ArrayList<PraviloLeksickogAnalizatora>();
	private static ArrayList<Automat> automati = new ArrayList<Automat>();
	private static ArrayList<Automat> aktivniAutomati = new ArrayList<Automat>();
	private static ArrayList<PraviloIAutomat> pravilaIAutomati = new ArrayList<PraviloIAutomat>();
	private static String ulaznaDatoteka;
	private static String pocetnoStanje;
	private static int pos, redak;

	public static void main(String[] args) {

		try {

			ucitajStanja();

			ucitajLeksickeJedinke();

			ucitajPravila();

		} catch (Exception ex) {

			ex.printStackTrace();

		}

		pocetnoStanje = stanja.get(0);

		inicijalizirajAutomate();

		ucitajDatoteku();

		for (PraviloIAutomat pravilo : pravilaIAutomati) {

			if (pravilo.getPravilo().getStanje().equals(pocetnoStanje)) {

				aktivniAutomati.add(pravilo.getAutomat());

			}

		}

		while (pos < ulaznaDatoteka.length()) {

			int maxPrihvatio = -1;

			Automat maxPrihvatioAutomat = null;

			for (Automat atmt : aktivniAutomati) {
				atmt.reinitialize();
			}

			int dobrih = aktivniAutomati.size();

			for (int i = pos; i < ulaznaDatoteka.length(); i++) {

				if (dobrih == 0) {
					break;
				}

				int brojDobrih = 0;
				boolean prolazi = false;

				for (int j = aktivniAutomati.size() - 1; j >= 0; j--) {

					Automat tmpAutomat = aktivniAutomati.get(j);
					if (ulaznaDatoteka.charAt(i) == '|') {
						tmpAutomat.obaviPrijelaze(ulaznaDatoteka.charAt(i));
					} else {
						tmpAutomat.obaviPrijelaze(ulaznaDatoteka.charAt(i));
					}

					if (tmpAutomat.prihvacaSe()) {
						prolazi = true;
						maxPrihvatioAutomat = tmpAutomat;
					}

					if (!tmpAutomat.getTrenutnaStanja().isEmpty()) {
						brojDobrih++;
					}

				}

				dobrih = brojDobrih;

				if (prolazi == true) {
					maxPrihvatio = i;
				}

			}

			if (maxPrihvatio == -1) {
				pos++;
				continue;

			}

			String maxMatch = ulaznaDatoteka.substring(pos, maxPrihvatio + 1);

			PraviloLeksickogAnalizatora rule = null;

			for (PraviloIAutomat pravilo : pravilaIAutomati) {

				if (pravilo.getAutomat().equals(maxPrihvatioAutomat)) {

					rule = pravilo.getPravilo();

				}

			}

			String imeLeksickeJedinke = "";
			boolean ispisuj = true;

			for (String action : rule.getArgumentiAkcije()) {

				if (leksickeJedinke.contains(action)) {

					imeLeksickeJedinke = leksickeJedinke.get(leksickeJedinke.indexOf(action));

				}

				if (action.split(" ")[0].equals("VRATI_SE")) {

					int naZnak = Integer.parseInt(action.split(" ")[1]);

					maxMatch = ulaznaDatoteka.substring(pos, pos + naZnak);

					maxPrihvatio = pos + naZnak - 1;

				}

				if (action.equals("NOVI_REDAK")) {

					redak++;

				}

				if (action.split(" ")[0].equals("UDJI_U_STANJE")) {

					String imeStanja = action.split(" ")[1];

					aktivniAutomati.clear();

					for (PraviloIAutomat pravilo : pravilaIAutomati) {

						if (pravilo.getPravilo().getStanje().equals(imeStanja)) {

							Automat automat2 = pravilo.getAutomat();
							aktivniAutomati.add(automat2);

						}

					}

				}

				if (action.split(" ")[0].equals("-")) {
					ispisuj = false;
				}
			}

			if (ispisuj == true) {

				System.out.println(imeLeksickeJedinke + " " + (redak + 1) + " " + maxMatch);

			}

			pos = maxPrihvatio + 1;

		}

	}

	private static void inicijalizirajAutomate() {

		for (PraviloLeksickogAnalizatora pravilo : pravila) {

			Automat automat = new Automat(pravilo.getRegularniIzraz());
			PraviloIAutomat tmp = new PraviloIAutomat(automat, pravilo);
			automati.add(automat);
			pravilaIAutomati.add(tmp);

		}

	}

	@SuppressWarnings("unchecked")
	private static void ucitajPravila() throws IOException, ClassNotFoundException {

		FileInputStream fis = new FileInputStream("analizator/pravilaLeksickogAnalizatora.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		pravila = (ArrayList<PraviloLeksickogAnalizatora>) ois.readObject();

		ois.close();

	}

	@SuppressWarnings("unchecked")
	private static void ucitajLeksickeJedinke() throws ClassNotFoundException, IOException {

		FileInputStream fis = new FileInputStream("analizator/imenaLeksickihJedinki.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		leksickeJedinke = (ArrayList<String>) ois.readObject();

		ois.close();

	}

	@SuppressWarnings("unchecked")
	private static void ucitajStanja() throws ClassNotFoundException, IOException {

		FileInputStream fis = new FileInputStream("analizator/stanjaLeksickogAnalizatora.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		stanja = (ArrayList<String>) ois.readObject();
		ois.close();

	}

	private static void ucitajDatoteku() {

		Scanner sc = new Scanner(System.in);

		StringBuilder sb = new StringBuilder();

		while (sc.hasNextLine()) {

			sb.append(sc.nextLine()).append("\n");

		}

		ulaznaDatoteka = sb.toString();

		sc.close();

	}

}
