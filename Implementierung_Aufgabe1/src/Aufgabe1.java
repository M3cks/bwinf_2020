package aufgabe1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Hauptklasse zur Loesung des Problems
 * 
 * @author cfmec
 */
public class Aufgabe1 {
	
	/**
	 * Testet, ob eine Voranmeldung noch auf den uebergebenen Zeitplan passt.
	 * 
	 * @param pR zu testende Voranmeldung
	 * @param tP der entsprechende Zeitplan
	 * @param maxCap maximale Laengenkapazitaet
	 * @param startTime Startzeit des Flohmarktes
	 * @return true, wenn die Voranmeldung noch miteinbezogen werden kann
	 * 		   false, wenn die Voranmeldung nicht mehr miteinbezogen werden kann
	 */
	private static boolean isPRFitting (PreRegistration pR, int[] tP, int maxCap, int startTime) {
		// Durchlaeuft die Zeiten des Zeitplans, welche von der Voranmeldung beansprucht werden.
		for (int i = pR.getStart() - startTime; i < pR.getEnd() - startTime; i++) {
			// Gibt false zurueck, wenn die Voranmeldung die Maximalkapazitaet uebersteigt.
    		if (tP[i] + pR.getLength() > maxCap) {
    			return false;
    		} else {
    			// Zieht die Voranmeldung ansonsten mit ein.
    			tP[i] = tP[i] + pR.getLength();
    		}
    	}
		return true;
	}
	
	/**
	 * Wendet das Brute-Force-Verfahren an, um die optimale Wahl aus Voranmeldungen zu treffen.
	 * 
	 * @param pRs           Liste der Voranmeldungen
	 * @param tP            Zeitplan des momentanen Entscheidungsweges
	 * @param selectedPRs   ausgewaehlte Voranmeldungen des momentanen Entscheidungsweges
	 * @param costs         Ertrag des momentanen Entscheidungsweges
	 * @param i             Laufindex ueber die Voranmeldungen
	 * @param startTime     Startzeit des Flohmarktes
	 * @param maxCap        maximale Kapazitaet
	 * @return 				Daten der endgueltigen Entscheidungswege
	 */
    private static DecisionPathData bruteForce(ArrayList<PreRegistration> pRs, int[] tP, boolean[] selectedPRs, 
    		int costs, int i, int startTime, int maxCap) {
    	
    	// Rekursionsanker: Wenn jede Voranmeldung in Betracht gezogen wurde, wird der endgueltige Entscheidungsweg zurueckgegeben.
    	if (i == pRs.size()) {
    		return new DecisionPathData(tP, selectedPRs, costs);
    	}
    	
    	// "copy of time plan": Eine Kopie des Zeitplanes, die fuer diesen Entscheidungsweg ergaenzt wird
    	int[] cTP = new int[tP.length];
    	
    	// "current selected pre registrations": Eine zu ergaenzende Kopie der ausgewaehlten Voranmeldungen fuer diesen Entscheidungsweg.
    	boolean[] cSPRs = new boolean[pRs.size()];
    	
    	//Kopiert die Auswahlmakierungen der Voranmeldungen
    	for (int j = 0; j < selectedPRs.length; j++) {
    		if (selectedPRs[j]) {
    			cSPRs[j] = true;
    		}
    	}
    	
    	//Kopiert den uebergebenen Zeitplan
    	for (int j = 0; j < cTP.length; j++) {
    		cTP[j] = tP[j];
    	}
    	
    	// Entscheidungsweg mit der Voranmeldung am momentanen Index miteinbezogen
    	DecisionPathData included = new DecisionPathData();
    	
    	// Der Entscheidungsweg wird gegangen, wenn die momentane Voranmeldung noch passt.
    	if (isPRFitting(pRs.get(i), cTP, maxCap, startTime)) {
    		// Markiert die Voranmeldung als ausgewaehlt.
    		cSPRs[i] = true;
    		
    		// Rekursionsaufruf mit den veraenderten Daten des Entscheidungsweges
    		included = bruteForce(pRs, cTP, cSPRs, costs+pRs.get(i).getCosts(), i+1, startTime, maxCap);
    	}
    	
    	// Rekursionsaufruf fuer den Entscheidungsweg exklusive der Voranmeldung am momentanen Index
    	DecisionPathData excluded = bruteForce(pRs, tP, selectedPRs, costs, i+1, startTime, maxCap);
    	
    	// Gibt den Entscheidungsweg zurueck, welcher einen hoeheren Ertrag bringt.
    	if (included.getCosts() > excluded.getCosts()) {
    		return included;
    	} else {
    		return excluded;
    	}
    }
    
    /**
     * Testet, ob alle Voranmeldungen passen.
     * 
     * @param pRs			Liste der Voranmeldungen
     * @param maxCap		Kapazitaetsimit
     * @param startTime		Startuhrzeit des Flohmarktes
     * @return				Einen optimalen Entscheidungsweg, wenn alle Voranmeldungen passen
     * 						Einen Entscheidungsweg mit dem Ertrag -1, um zu symbolisieren, dass nicht alle Voranmeldungen passen
     */
    private static DecisionPathData allFitting(ArrayList<PreRegistration> pRs, int maxCap, int startTime) {
    	// Wenn alle Voranmeldungen passen:
    	// Der dabei entstehende Ertrag
    	int costs = 0;
    
    	// Die Belegung des Zeitplanes
    	int[] tP = new int[10];
    	
    	// Die ausgewaehlten Voranmeldungen (alle)
    	boolean[] selected = new boolean[pRs.size()];
    	
    	// Durchlaeuft die Voranmeldungen.
    	for (int i = 0; i < pRs.size(); i++) {
    		
    		// Erhoeht den Ertrag und selektiert die Voranmeldung, falls sie noch passt.
    		if (isPRFitting(pRs.get(i), tP, maxCap, startTime)) {
    			costs += pRs.get(i).getCosts();
    			selected[i] = true;
    		} else {
    			// Gibt einen Entscheidungsweg mit Ertrag -1 zurueck, wenn nicht alle Voranmeldungen passen.
    			return new DecisionPathData(tP, selected, -1);
    		}
    	}
    	return new DecisionPathData(tP, selected, costs);
    }
    
    /**
     * Wendet das Greedy-Verfahren auf die vorsortierte Liste von Voranmeldungen an.
     * 
     * @param pRs 		Vorsortierte Liste der Voranmeldungen
     * @param maxCap	maximale Kapazitaet
     * @param startTime	Startzeit des Flohmarktes
     * @return    		Entscheidungsweg durch Greedy-Verfahren
     */
    private static DecisionPathData greedy(ArrayList<PreRegistration> pRs, int maxCap, int startTime) {
    	// Zeitplan des Entscheidungsweges
    	int[] tP = new int[10];
    	
    	// Array fuer die Selektierung der Voranmeldungen.
    	boolean selected[] = new boolean[pRs.size()];
    	
    	// Ertrag des Entscheidungsweges
    	int costs = 0;
    	
    	// Durchlaeuft die Voranmeldungen
		for (int i = 0; i < pRs.size(); i++) {
			
			// Aktuelle Voranmeldung
			PreRegistration pR = pRs.get(i);	
			
			// true, wenn die momentane Voranmeldung nicht mehr passt
			boolean isFull = false;
			
			// Durchlaeuft die Zeiten, welche durch die momentane Voranmeldung beansprucht werden
			for (int j = pR.getStart() - startTime; j < pR.getEnd() - startTime; j++) {
				
				// Testet, ob das Kapazitaetsmaximum erreicht wird.
				if (tP[j] + pR.getLength() > 1000) {
					isFull = true;
					break;
				}
			}
			
			// Wenn die Voranmeldung noch passt:
			if(!isFull) {
				// Selektiert die Voranmeldung.
				selected[i] = true;
				
				// Erhoeht den Gesamtertrag um den Ertrag der momentanen Voranmeldung.
				costs += pR.getCosts();
				
				// Durchlaeuft die entsprechenden Zeiten und belegt den Zeitplan.
				for (int j = pR.getStart() - 8; j < pR.getEnd() - 8; j++) {
    				tP[j] = tP[j] + pR.getLength();
				}
			}
		}
		
		
		// Gibt den approximativen Entscheidungsweg zurueck. 
		return new DecisionPathData(tP, selected, costs);
    }
    
    /**
     * Gibt das Ergebnis des gewaehlten Entscheidungsweges auf die Konsole aus.
     * 
     * @param pRs Liste der Voranmeldungen
     * @param dPD Auszugebender Entscheidungsweg
     * @param msg Auszugebende Nachricht
     */
    private static void printRes(ArrayList<PreRegistration> pRs, DecisionPathData dPD, String method, int startTime, int maxCap) {
    	// Ausgabe des Ertrags.
    	System.out.println("Ertrag: " + dPD.getCosts());
		
		// Gibt die Voranmeldungen aus und ob sie jeweils selektiert wurden.
		System.out.println();
		System.out.println("Voranmeldungen:");
		boolean allIncluded = true;
    	for (int i = 0; i < pRs.size(); i++) {
    		if (dPD.getSelectedPRs()[i]) {
    			System.out.println("ID: " + pRs.get(i).getId() + ", Uhrzeit: " + pRs.get(i).getStart() + "-" + pRs.get(i).getEnd() + ", Laenge: " + pRs.get(i).getLength() + 
        				", Ertrag: " + pRs.get(i).getCosts() + " - Diese Voranmeldung wurde miteinbezogen!");
    		} else {
    			System.out.println("ID: " + pRs.get(i).getId() + ", Uhrzeit: " + pRs.get(i).getStart() + "-" + pRs.get(i).getEnd() + ", Laenge: " + pRs.get(i).getLength() + 
        				", Ertrag: " + pRs.get(i).getCosts());
    			allIncluded = false;
    		}
    	}
    	
    	// Ermittlung der Kapazitaetsauslastungen mit allen Voranmeldungen inklusive.
    	int[] tPAll = new int[10];
    	for (int i = 0; i < pRs.size(); i++) {
    		// Durchlaeuft die Zeiten, welche durch die momentane Voranmeldung beansprucht werden
			for (int j = pRs.get(i).getStart() - startTime; j < pRs.get(i).getEnd() - startTime; j++) {
				tPAll[j] = tPAll[j] + pRs.get(i).getLength();
			}
    	}
    	
    	// Gibt die Zeitplanbelegung aus.
    	System.out.println();
    	System.out.println("Die Stunden konnten somit folgendermassen belegt werden:");
    	for (int i = 0; i < dPD.getTP().length; i++) {
    		System.out.println((startTime+i) + "-" + (startTime+1+i) + " Uhr: " + dPD.getTP()[i] + "m");
    	}
    	
    	// Gibt die Zeitplanbelegung mit allen Voranmeldungen inklusive aus.
    	System.out.println();
    	System.out.println("Bei Einbeziehung aller Voranmeldungen waeren in den einzelnen Stunden folgende Kapazitaetsauslastungen vorzufinden:");
    	// Ermittelt, ob der Zeitplan optimal belegt wurde.
    	boolean isBest = true;
    	for (int i = 0; i < tPAll.length; i++) {
    		if (tPAll[i] >= maxCap && dPD.getTP()[i] < maxCap && dPD.getTP()[i] != tPAll[i]) {
    			isBest = false;
    		}
    		System.out.println((startTime+i) + "-" + (startTime+1+i) + " Uhr: " + tPAll[i] + "m");
    	}
    	
		// Gibt eine Aussage ueber die Optimalitaet der Loesung aus.
    	if (allIncluded) {
    		System.out.println("Alle Voranmeldungen konnten miteinbezogen werden.");
    	} else {
    		System.out.println("Nur ein Teil der Voranmeldungen konnte miteinbezogen werden.");
    	}
    	if (method == "brute force" || method == "all fitting") {
    		System.out.println("Das Beispiel konnte durch Anwendung des dreigleisigen Verfahrens exakt geloest werden.");
    	} else if (isBest) {
    		System.out.println("Das Beispiel konnte durch Anwendung des dreigleisigen Verfahrens exakt geloest werden.");
    	} else {
    		System.out.println("Das Beispiel konnte durch Anwendung des dreigleisigen Verfahrens approximativ geloest werden.");
    	}    	
    }
    
	/**
	 * Konsoleneingabe: Wahl der Testdatei.
	 * 
	 * @return absoluter Dateipfad zur gewaehlten Testdatei.
	 */
	private static String consoleInterface() {
		Scanner in = new Scanner(System.in);
        System.out.println("Bitte waehlen Sie eine der folgenden Dateien:");
        System.out.println("1 - flohmarkt1.txt");
        System.out.println("2 - flohmarkt2.txt");
        System.out.println("3 - flohmarkt3.txt");
        System.out.println("4 - flohmarkt4.txt");
        System.out.println("5 - flohmarkt5.txt");
        System.out.println("6 - flohmarkt6.txt");
        System.out.println("7 - flohmarkt7.txt");
        System.out.print("=> ");
        String fileName = "";
        try {
        	int fileNum = in.nextInt();
        	if (fileNum > 7 || fileNum < 1) {
        		System.out.println();
            	System.out.println("Bitte waehlen Sie eine gueltige Nummer!");
            	return consoleInterface();
        	}
        	String path = new File("").getAbsolutePath() + "\\res\\";
        	switch(fileNum) {
	        	case 1:
	        		fileName = path + "flohmarkt1.txt";
	        		break;
	        	case 2:
	        		fileName = path + "flohmarkt2.txt";
	        		break;
	        	case 3:
	        		fileName = path + "flohmarkt3.txt";
	        		break;
	        	case 4:
	        		fileName = path + "flohmarkt4.txt";
	        		break;
	        	case 5:
	        		fileName = path + "flohmarkt5.txt";
	        		break;
	        	case 6:
	        		fileName = path + "flohmarkt6.txt";
	        		break;
	        	case 7:
	        		fileName = path + "flohmarkt7.txt";
	        		break;
        	}
        	return fileName;
        } catch (InputMismatchException e) {
        	System.out.println();
        	System.out.println("Bitte waehlen Sie eine gueltige Nummer!");
        	return consoleInterface();
        }
	}
	
	/**
	 * Hauptmethode fuer die Loesung des Problems.
	 * 
	 */
    public static void main(String[] args) {
    	// Nutzereingabe => Bestimmung des Dateipfades.
    	String filePath = consoleInterface();
    	
    	// Generiert eine Liste von Voranmeldungen aus den Daten der Datei.
    	ArrayList<PreRegistration> pRs = new Data(filePath).getPRs();   
    	
    	// Entscheidnungsweg fuer das Miteinbeziehen aller Voranmeldungen.
    	DecisionPathData allFitting = allFitting(pRs, 1000, 8);
    	
    	if (allFitting.getCosts() > -1) {
    		// Gibt den Entscheidungsweg aus, wenn alle Voranmeldungen passen.
    		printRes(pRs, allFitting, "all fitting", 8, 1000);
    	} else if (pRs.size() <= 50) {
    		// Brute-Force-Verfahren, wenn die Anzahl der Voranmeldungen <= 50 ist.
    		DecisionPathData res = bruteForce(pRs, new int[10], new boolean[pRs.size()], 0, 0, 8, 1000);
    		
    		// Gibt das Ergebnis der Brute-Force-Methode aus.
    		printRes(pRs, res, "brute force", 8, 1000);
    	} else {
    		// Greedy-Verfahren bei Voranmeldugen 
    		
    		// Vorsortierung der Voranmeldungen.
    		pRs.sort(new PRComparator());
    		
    		// Erstellt den Entscheidungsweg nach dem Greedy-Verfahren.
    		DecisionPathData res = greedy(pRs, 1000, 8);
    		
    		// Gibt das Ergebnis des Greedy-Verfahrens aus.
    		printRes(pRs, res, "greedy", 8, 1000);

    		
    	}
        
    }   
}
