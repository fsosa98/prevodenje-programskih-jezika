import java.io.Serializable;
import java.util.List;

public class Akcija implements Serializable {

	private static final long serialVersionUID = -6634210238918041579L;
	private String vrstaAkcije;
	private StanjeDka idDKAStanja;
	private String lijevaStrana;
	private List<String> desnaStrana;

	public Akcija(String vrstaAkcije, StanjeDka idDKAStanja) {

		this.vrstaAkcije = vrstaAkcije;
		this.idDKAStanja = idDKAStanja;
	}

	public Akcija(String vrstaAkcije) {
		this.vrstaAkcije = vrstaAkcije;
	}

	public Akcija(String vrstaAkcije, String lijevaStrana, List<String> desnaStrana) {
		this.vrstaAkcije = vrstaAkcije;
		this.lijevaStrana = lijevaStrana;
		this.desnaStrana = desnaStrana;

		this.vrstaAkcije = vrstaAkcije;
	}

	public String getVrstaAkcije() {
		return vrstaAkcije;
	}

	public void setVrstaAkcije(String vrstaAkcije) {
		this.vrstaAkcije = vrstaAkcije;
	}

	public StanjeDka getStanjeDka() {
		return idDKAStanja;
	}

	public void setStanjeDka(StanjeDka stanjeDka) {
		this.idDKAStanja = stanjeDka;
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

}
