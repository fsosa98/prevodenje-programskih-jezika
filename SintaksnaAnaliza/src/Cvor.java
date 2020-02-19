import java.util.List;

public class Cvor {

	private String uniformniZnak;
	private List<Cvor> djecica;
	public static StringBuilder sb = new StringBuilder();

	public Cvor(String uniformniZnak, List<Cvor> djecica) {
		this.uniformniZnak = uniformniZnak;
		this.djecica = djecica;
	}

	public String getUniformniZnak() {
		return uniformniZnak;
	}

	public void setUniformniZnak(String uniformniZnak) {
		this.uniformniZnak = uniformniZnak;
	}

	public List<Cvor> getDjecica() {
		return djecica;
	}

	public void setDjecica(List<Cvor> djecica) {
		this.djecica = djecica;
	}

	public void dodajMalog(Cvor decec) {
		djecica.add(decec);
	}

	public static void ispisi(Cvor korijen, int br) {
		String razmak = "";
		for (int i = 0; i < br; i++)
			razmak += " ";

		System.out.println(razmak + korijen);
		List<Cvor> djecica = korijen.getDjecica();
		for (int i = djecica.size() - 1; i >= 0; i--) {
			ispisi(djecica.get(i), br + 1);
		}
	}

	public static void ispisiUBuffer(Cvor korijen, int br) {
		String razmak = "";
		for (int i = 0; i < br; i++)
			razmak += " ";
		sb.append(razmak + korijen);
		sb.append("\n");
		List<Cvor> djecica = korijen.getDjecica();
		for (Cvor dijete : djecica) {
			ispisi(dijete, br + 1);
		}
	}

	@Override
	public String toString() {
		return this.getUniformniZnak();
	}
}
