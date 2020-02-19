import java.io.Serializable;
import java.util.Set;

public class StanjeDka implements Serializable {

	private static final long serialVersionUID = -5422417093578434331L;
	private Stanje glavnoStanje;
	private Set<Stanje> stavke;

	public StanjeDka(Stanje glavnoStanje, Set<Stanje> stavke) {
		super();
		this.glavnoStanje = glavnoStanje;
		this.stavke = stavke;
	}

	public Stanje getGlavnoStanje() {
		return glavnoStanje;
	}

	public void setGlavnoStanje(Stanje glavnoStanje) {
		this.glavnoStanje = glavnoStanje;
	}

	public Set<Stanje> getStavke() {
		return stavke;
	}

	public void setStavke(Set<Stanje> stavke) {
		this.stavke = stavke;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((glavnoStanje == null) ? 0 : glavnoStanje.hashCode());
		result = prime * result + ((stavke == null) ? 0 : stavke.hashCode());
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
		StanjeDka other = (StanjeDka) obj;
		if (glavnoStanje == null) {
			if (other.glavnoStanje != null)
				return false;
		} else if (!glavnoStanje.equals(other.glavnoStanje))
			return false;
		if (stavke == null) {
			if (other.stavke != null)
				return false;
		} else if (!stavke.equals(other.stavke))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Ovo je glavno stanje " + glavnoStanje + " ");
		sb.append("Sad ide epsilon okruzenje stanje ");
		for (Stanje s : stavke) {
			if (s.equals(glavnoStanje)) {

				continue;

			}
			sb.append(s.toString());
		}
		return sb.toString();
	}

}
