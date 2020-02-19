import java.io.Serializable;

public class Prijelazi implements Serializable {

	private static final long serialVersionUID = -4284486520999100008L;

	private Stanje lijevoStanje;
	private Stanje desnoStanje;
	private String str;

	public Prijelazi(Stanje lijevoStanje, Stanje desnoStanje, String str) {
		super();
		this.lijevoStanje = lijevoStanje;
		this.desnoStanje = desnoStanje;
		this.str = str;
	}

	public Stanje getLijevoStanje() {
		return lijevoStanje;
	}

	public void setLijevoStanje(Stanje lijevoStanje) {
		this.lijevoStanje = lijevoStanje;
	}

	public Stanje getDesnoStanje() {
		return desnoStanje;
	}

	public void setDesnoStanje(Stanje desnoStanje) {
		this.desnoStanje = desnoStanje;
	}

	public String getStr() {
		return this.str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desnoStanje == null) ? 0 : desnoStanje.hashCode());
		result = prime * result + ((lijevoStanje == null) ? 0 : lijevoStanje.hashCode());
		result = prime * result + ((str == null) ? 0 : str.hashCode());
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
		Prijelazi other = (Prijelazi) obj;
		if (desnoStanje == null) {
			if (other.desnoStanje != null)
				return false;
		} else if (!desnoStanje.equals(other.desnoStanje))
			return false;
		if (lijevoStanje == null) {
			if (other.lijevoStanje != null)
				return false;
		} else if (!lijevoStanje.equals(other.lijevoStanje))
			return false;
		if (str == null) {
			if (other.str != null)
				return false;
		} else if (!str.equals(other.str))
			return false;
		return true;
	}
}
