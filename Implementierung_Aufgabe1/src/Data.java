package aufgabe1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Klasse zum Einlesen der Dateien
 * 
 * @author cfmec
 */
public class Data {
	// Liste der Voranmeldungsobjekte
	private ArrayList<PreRegistration> pRs = new ArrayList<>();
	
	/**
	 * Liesst die Datei ein und erstellt Voranmeldungsobjekte.
	 * 
	 * @param fileName
	 */
	public Data (String fileName) {
		try {
			// Erstellung der Datei.
            File file = new File(fileName);
            
            // Scanner zum Lesen der Datei.
            Scanner scanner = new Scanner(file);
            
            // Ueberspringen der Anzahl der Voranmeldungen.
            scanner.nextLine();
            int i = 0;
            
            // Durchlaeuft die Voranmeldungen und erstellt anhand der Daten Voranmeldungsobjekte.
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                String[] splitNL = nextLine.split(" ");
                PreRegistration pR = new PreRegistration(
                    Integer.parseInt(splitNL[0]),
                    Integer.parseInt(splitNL[1]),
                    Integer.parseInt(splitNL[2]),
                    i
                );
                pRs.add(pR);
                i++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error when trying to read file!");
            e.printStackTrace();
        }
	}
	
	// Getter
	public ArrayList<PreRegistration> getPRs() {
		return this.pRs;
	}
}
