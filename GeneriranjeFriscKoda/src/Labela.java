
public class Labela {

	private boolean isJeFunkcija;

	private boolean isJePrazno;

	private Cvor cvor;

	private String labela;

	public Labela(Cvor cvor, String labela) {
		this.cvor = cvor;
		this.labela = labela;
	}

	public Cvor getCvor() {
		return cvor;
	}

	public void setCvor(Cvor cvor) {
		this.cvor = cvor;
	}

	public String getLabela() {
		return labela;
	}

	public void setLabela(String labela) {
		this.labela = labela;
	}

	public boolean isJeFunkcija() {
		return isJeFunkcija;
	}

	public void setJeFunkcija(boolean isJeFunkcija) {
		this.isJeFunkcija = isJeFunkcija;
	}

	public boolean isJePrazno() {
		return isJePrazno;
	}

	public void setJePrazno(boolean isJePrazno) {
		this.isJePrazno = isJePrazno;
	}

	public String izgradi() {

		String sol = " `DW ";
		int num;

		if (!this.cvor.getIme().startsWith("'")) {

			if (cvor.getIme().equals("main")) {

				return "";

			}

			num = Integer.parseInt(this.cvor.getIme());

		} else {

			num = (int) this.cvor.getIme().charAt(1);

		}

		int numMaska = (1 << 8);
		numMaska--;

		for (int i = 0; i < 4; i++) {

			sol += "%D " + (num & numMaska) + ", ";
			num >>= 8;

		}

		StringBuilder sb = new StringBuilder();

		sb.append(sol.substring(0, sol.length() - 2));

		sb.append("\n");

		return sb.toString();
	}

}
