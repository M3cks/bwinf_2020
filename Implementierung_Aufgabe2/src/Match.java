package aufgabe2;

import java.util.ArrayList;

/**
 * Klasse fuer die Zuweisungen von Obstsorten und Schuesseln
 * 
 * @author cfmec
 *
 */
public class Match {
	// die betreffende Sorte
	private String sort;
	
	// die Schuesselnummern, welche dieser Sorte zugeordnet wurden
	private ArrayList<Integer> bowlNums;
	
	/**
	 * Konstruktor einer Zuweisung
	 * 
	 * @param sort  Die Sorte, bei der eine Zuweisung stattfinden soll
	 */
	public Match(String sort) {
		this.sort = sort;
		this.bowlNums = new ArrayList<>();
	}
	
	/**
	 * Fuegt der Schuesselnummerliste eine Schuesselnummer hinzu.
	 * 
	 * @param bowlNum  Die hinzuzufuegende Schuesselnummer
	 */
	public void addBowlNum(int bowlNum) {
		this.bowlNums.add(bowlNum);
	}
	
	//Getter
	public String getSort() {
		return this.sort;
	}
	
	public ArrayList<Integer> getBowlNums() {
		return this.bowlNums;
	}
}
