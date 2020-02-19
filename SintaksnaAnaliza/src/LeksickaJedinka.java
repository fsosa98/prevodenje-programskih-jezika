public class LeksickaJedinka {

	private String uniformniZnak;
	private int redak;
	private String grupiraniZnakovi;

	public LeksickaJedinka(String line) {
		String[] parts = line.split(" ");
		int prviRazmak = line.indexOf(" ");
		int drugiRazmak = line.indexOf(" ", prviRazmak + 1);

		this.uniformniZnak = parts[0];
		this.redak = Integer.parseInt(parts[1]);
		this.grupiraniZnakovi = line.substring(drugiRazmak + 1);
	}

	public LeksickaJedinka(String uniformniZnak, int redak, String grupiraniZnakovi) {
		this.uniformniZnak = uniformniZnak;
		this.redak = redak;
		this.grupiraniZnakovi = grupiraniZnakovi;
	}

	public String getUniformniZnak() {
		return uniformniZnak;
	}

	public void setUniformniZnak(String uniformniZnak) {
		this.uniformniZnak = uniformniZnak;
	}

	public int getRedak() {
		return redak;
	}

	public void setRedak(int redak) {
		this.redak = redak;
	}

	public String getGrupiraniZnakovi() {
		return grupiraniZnakovi;
	}

	public void setGrupiraniZnakovi(String grupiraniZnakovi) {
		this.grupiraniZnakovi = grupiraniZnakovi;
	}

	@Override
	public String toString() {
		return new String(uniformniZnak + " " + redak + " " + grupiraniZnakovi);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grupiraniZnakovi == null) ? 0 : grupiraniZnakovi.hashCode());
		result = prime * result + redak;
		result = prime * result + ((uniformniZnak == null) ? 0 : uniformniZnak.hashCode());
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
		LeksickaJedinka other = (LeksickaJedinka) obj;
		if (grupiraniZnakovi == null) {
			if (other.grupiraniZnakovi != null)
				return false;
		} else if (!grupiraniZnakovi.equals(other.grupiraniZnakovi))
			return false;
		if (redak != other.redak)
			return false;
		if (uniformniZnak == null) {
			if (other.uniformniZnak != null)
				return false;
		} else if (!uniformniZnak.equals(other.uniformniZnak))
			return false;
		return true;
	}
}