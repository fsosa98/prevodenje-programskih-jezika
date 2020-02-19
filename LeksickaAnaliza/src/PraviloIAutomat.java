
public class PraviloIAutomat {

	private Automat automat;
	private PraviloLeksickogAnalizatora pravilo;

	public PraviloIAutomat(Automat automat, PraviloLeksickogAnalizatora pravilo) {
		this.automat = automat;
		this.pravilo = pravilo;
	}

	public Automat getAutomat() {
		return automat;
	}

	public void setAutomat(Automat automat) {
		this.automat = automat;
	}

	public PraviloLeksickogAnalizatora getPravilo() {
		return pravilo;
	}

	public void setPravilo(PraviloLeksickogAnalizatora pravilo) {
		this.pravilo = pravilo;
	}

}
