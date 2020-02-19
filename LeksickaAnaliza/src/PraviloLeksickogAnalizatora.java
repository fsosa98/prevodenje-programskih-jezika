import java.io.Serializable;
import java.util.List;

public class PraviloLeksickogAnalizatora implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stanje;
	private String regularniIzraz;
	private List<String> argumentiAkcije;
	
	
	public PraviloLeksickogAnalizatora(String stanje, String regularniIzraz, List<String> argumentiAkcije) {
		super();
		this.stanje = stanje;
		this.regularniIzraz = regularniIzraz;
		this.argumentiAkcije = argumentiAkcije;
	}
	public String getStanje() {
		return stanje;
	}
	public void setStanje(String stanje) {
		this.stanje = stanje;
	}
	public String getRegularniIzraz() {
		return regularniIzraz;
	}
	public void setRegularniIzraz(String regularniIzraz) {
		this.regularniIzraz = regularniIzraz;
	}
	public List<String> getArgumentiAkcije() {
		return argumentiAkcije;
	}
	public void setArgumentiAkcije(List<String> argumentiAkcije) {
		this.argumentiAkcije = argumentiAkcije;
	}

	
}
