package aufgabe2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Klasse zur Datenbereitstellung.
 * 
 * @author Carl Friedrich Mecking
 */

public class Data {
	// Obergrenze fuer die Anzahl der Sorten
	private int maxSorts;
	// Wunschsorten
	private String targetSorts;
	// Liste der Schuesseln je nach Beobachtung
	private ArrayList<String> seenBowls = new ArrayList<>();
	// Liste der Sorten je nach Beobachtung
	private ArrayList<String> seenSorts = new ArrayList<>();
	// Liste aller beobachteten Sorten
	private ArrayList<String> allSorts = new ArrayList<>();
	
	/**
	 * 
	 * @param filename Relativer Pfad zur Datei
	 */
	public Data(String filename) {
		try {
			File file = new File(filename);
			Scanner scanner = new Scanner(file);
			maxSorts = Integer.parseInt(scanner.nextLine());
			targetSorts = scanner.nextLine();
			scanner.nextLine();
			while (scanner.hasNextLine()) {
				seenBowls.add(scanner.nextLine());
				seenSorts.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			e.printStackTrace();
		}
		
		// Fuegt der Liste aller Sorten zuerst die Wunschsorten hinzu.
		String[] targetSortsArr = targetSorts.split(" ");
		for (int i = 0; i < targetSortsArr.length; i++) {
			allSorts.add(targetSortsArr[i]);
		}
		
		// Fuegt der Liste aller Sorten stueckweise alle beobachteten Sorten hinzu, wenn sie nicht bereits in der Liste vorhanden sind.
		for (int i = 0; i < seenSorts.size(); i++) {
			String[] sortsArr = seenSorts.get(i).split(" ");
			for (int j = 0; j < sortsArr.length; j++) {
				if (allSorts.indexOf(sortsArr[j]) == -1) {
					allSorts.add(sortsArr[j]);
				}
			}
		}
		
		// Ist die Anzahl der gegebenen Sorten nicht so gross, wie die Obergrenze, wird eine unbekannte Sorte eingefuegt.
		if (allSorts.size() != maxSorts) {
			for (int i = 0; i < maxSorts-allSorts.size(); i++) {
				allSorts.add("unknown-" + i);
			}
		}
	}
	
	// Getter
	public int getMaxSorts() {
		return maxSorts;
	}

	public String getTargetSorts() {
		return targetSorts;
	}

	public ArrayList<String> getSeenBowls() {
		return seenBowls;
	}

	public ArrayList<String> getSeenSorts() {
		return seenSorts;
	}
	
	public ArrayList<String> getAllSorts() {
		return allSorts;
	}
}
