import java.util.ArrayList;

public class Tablica {

	private Tablica otac;

	private ArrayList<Cvor> deklarirano;

	private boolean unutarPetlje;

	public Tablica(Tablica otac, ArrayList<Cvor> deklarirano) {

		this.otac = otac;
		this.deklarirano = deklarirano;

	}

	public Tablica getOtac() {
		return otac;
	}

	public void setOtac(Tablica otac) {
		this.otac = otac;
	}

	public ArrayList<Cvor> getDeklarirano() {
		return deklarirano;
	}

	public void setDeklarirano(ArrayList<Cvor> deklarirano) {
		this.deklarirano = deklarirano;
	}

	public void dodajDeklarirano(Cvor cvor) {

		deklarirano.add(cvor);

	}

	public boolean isUnutarPetlje() {

		return unutarPetlje;

	}

	public void setUnutarPetlje(boolean unutarPetlje) {

		this.unutarPetlje = unutarPetlje;

	}

	public Tablica copy() {
		ArrayList<Cvor> dj = new ArrayList<>();
		this.deklarirano.forEach(dj::add);
		Tablica tabela = new Tablica(this.otac, dj);
		tabela.setUnutarPetlje(this.unutarPetlje);

		return tabela;
	}

}
