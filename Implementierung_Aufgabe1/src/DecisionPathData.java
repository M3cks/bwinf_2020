package aufgabe1;

/**
 * Klasse fuer die Bereitstellung der Daten eines Entscheidungsweges.
 * 
 * @author cfmec
 *
 */
public class DecisionPathData {
	private int[] tP;
	boolean[] selectedPRs;
	private int costs;
	
	/**
	 *Konstruktor
	 *
	 * @param tP			Zeitplan
	 * @param selectedPRs	selektierte Voranmeldungen	 
	 * @param costs			Ertrag
	 */
	public DecisionPathData(int[] tP, boolean[] selectedPRs, int costs) {
		this.tP = tP;
		this.selectedPRs = selectedPRs;
		this.costs = costs;
	}
	
	// Leerer Konstruktor, falls beim Brute-Force Verfahren die Voranmeldung nicht miteinbezogen werden kann.
	public DecisionPathData() {}
	
	
	// Getter
	public int[] getTP() {
		return this.tP;
	}
	
	public boolean[] getSelectedPRs() {
		return this.selectedPRs;
	}
	
	public int getCosts() {
		return this.costs;
	}
}
