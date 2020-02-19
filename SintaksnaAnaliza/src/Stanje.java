import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Stanje implements Serializable {

	private static final long serialVersionUID = -2387912108935515267L;

	private String id;

	private String lijevaStrana;
	private List<String> desnaStrana;

	private boolean jeLiValjanaStavka;
	private Set<String> skupZapocinje;
	private int redniBrojTockice;

	public Stanje(int id, String lijevaStrana, List<String> desnaStrana, int redniBrojTockice,
			Set<String> skupZapocinje) {
		super();
		this.id = String.valueOf(id);
		this.lijevaStrana = lijevaStrana;
		this.desnaStrana = desnaStrana;
		this.redniBrojTockice = redniBrojTockice;
		this.skupZapocinje = skupZapocinje;

		int duljinaDesneStrane = desnaStrana.size();

		jeLiValjanaStavka = redniBrojTockice == duljinaDesneStrane;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLijevaStrana() {
		return lijevaStrana;
	}

	public void setLijevaStrana(String lijevaStrana) {
		this.lijevaStrana = lijevaStrana;
	}

	public List<String> getDesnaStrana() {
		return desnaStrana;
	}

	public void setDesnaStrana(List<String> desnaStrana) {
		this.desnaStrana = desnaStrana;
	}

	public Set<String> getSkupZapocinje() {
		return skupZapocinje;
	}

	public void setSkupZapocinje(Set<String> skupZapocinje) {
		this.skupZapocinje = skupZapocinje;
	}

	public void dodajElementUSkupZapocinje(String elem) {
		this.skupZapocinje.add(elem);
	}

	public boolean isJeLiValjanaStavka() {
		return jeLiValjanaStavka;
	}

	public void setJeLiValjanaStavka(boolean jeLiValjanaStavka) {
		this.jeLiValjanaStavka = jeLiValjanaStavka;
	}

	public int getRedniBrojTockice() {
		return redniBrojTockice;
	}

	public void setRedniBrojTockice(int redniBrojTockice) {
		this.redniBrojTockice = redniBrojTockice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desnaStrana == null) ? 0 : desnaStrana.hashCode());
		result = prime * result + (jeLiValjanaStavka ? 1231 : 1237);
		result = prime * result + ((lijevaStrana == null) ? 0 : lijevaStrana.hashCode());
		result = prime * result + redniBrojTockice;
		result = prime * result + ((skupZapocinje == null) ? 0 : skupZapocinje.hashCode());
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
		Stanje other = (Stanje) obj;
		if (desnaStrana == null) {
			if (other.desnaStrana != null)
				return false;
		} else if (!desnaStrana.equals(other.desnaStrana))
			return false;
		if (jeLiValjanaStavka != other.jeLiValjanaStavka)
			return false;
		if (lijevaStrana == null) {
			if (other.lijevaStrana != null)
				return false;
		} else if (!lijevaStrana.equals(other.lijevaStrana))
			return false;
		if (redniBrojTockice != other.redniBrojTockice)
			return false;
		if (skupZapocinje == null) {
			if (other.skupZapocinje != null)
				return false;
		} else if (!skupZapocinje.equals(other.skupZapocinje))
			return false;
		return true;
	}

	public void epsilonOkruzenje(Set<Stanje> okruzenje, List<Prijelazi> sviPrijelazi) {

		if (okruzenje.contains(this))
			return;
		okruzenje.add(this);

		for (Prijelazi p : sviPrijelazi) {
			if (p.getLijevoStanje().equals(this) && p.getStr().equals("$")) {
				p.getDesnoStanje().epsilonOkruzenje(okruzenje, sviPrijelazi);
			}
		}

		while (true) {
			boolean nasaoBaremJednu = false;
			for (Prijelazi prijelaz : sviPrijelazi) {
				if (prijelaz.getStr().equals("$")) {
					if (okruzenje.contains(prijelaz.getLijevoStanje())) {
						if (!okruzenje.contains(prijelaz.getDesnoStanje())) {
							nasaoBaremJednu = true;
							okruzenje.add(prijelaz.getDesnoStanje());
						}
					}
				}
			}
			if (!nasaoBaremJednu) {
				return;
			}
		}

	}

	public Stanje nadiStanje(Set<Stanje> svaStanja) {
		Stanje novoStanje = new Stanje(0, this.lijevaStrana, this.desnaStrana, this.redniBrojTockice + 1,
				this.skupZapocinje);
		return novoStanje;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(lijevaStrana + "->" + desnaStrana + " {");
		for (String s : this.getSkupZapocinje()) {
			sb.append(s + ",");
		}
		sb.append("} ____pozicija tockice: " + redniBrojTockice + "\n");
		return sb.toString();
	}
}
