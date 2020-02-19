import java.io.Serializable;

public class PrijelazDKA implements Serializable {

	private static final long serialVersionUID = -6197325130937521811L;
	public StanjeDka lijevoDio;
	public String prelazi;
	public StanjeDka desniDio;

	public PrijelazDKA(StanjeDka pocetnoStanje, String prelazi, StanjeDka noviPrijelaz) {
		super();
		this.lijevoDio = pocetnoStanje;
		this.prelazi = prelazi;
		this.desniDio = noviPrijelaz;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desniDio == null) ? 0 : desniDio.hashCode());
		result = prime * result + ((lijevoDio == null) ? 0 : lijevoDio.hashCode());
		result = prime * result + ((prelazi == null) ? 0 : prelazi.hashCode());
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
		PrijelazDKA other = (PrijelazDKA) obj;
		if (desniDio == null) {
			if (other.desniDio != null)
				return false;
		} else if (!desniDio.equals(other.desniDio))
			return false;
		if (lijevoDio == null) {
			if (other.lijevoDio != null)
				return false;
		} else if (!lijevoDio.equals(other.lijevoDio))
			return false;
		if (prelazi == null) {
			if (other.prelazi != null)
				return false;
		} else if (!prelazi.equals(other.prelazi))
			return false;
		return true;
	}

}
