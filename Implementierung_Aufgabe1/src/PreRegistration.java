package aufgabe1;

import java.util.Comparator;

/**
 * Klasse fuer die Datenspeicherung einer Voranmeldung.
 * 
 * @author cfmec
 *
 */
class PreRegistration {
	// Startzeit
    private int start;
    
    // Endezeit
    private int end;
    
    // Anzahl der Stunden
    private int time;
    
    // Laenge des Flohmarktstandes
    private int length;
    
    // Ertrag
    private int costs;
    
    // ID für das spaetere Rückgaengigmachen der Sortierung.
    private int id;

    // Konstruktor
    public PreRegistration(int start, int end, int length, int id) {
        this.start = start;
        this.end = end;
        this.time = end - start;
        this.length = length;
        this.costs = length * time;
        this.id = id;
    }

    // Getter
    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getTime() {
        return time;
    }
    
    public int getLength() {
    	return length;
    }
    
    public int getCosts() {
    	return costs;
    }
    
    public int getId() {
    	return id;
    }
}

/**
 * Comparator fuer das Sortieren der Voranmeldungen.
 * 
 * @author cfmec
 *
 */
class PRComparator implements Comparator<PreRegistration> {
	@Override
	// Sortiert die Voranmeldungen in folgender Reihenfolge:
	//	Startzeit (klein) -> Endezeit (groß) -> Ertrag (groß)
	public int compare(PreRegistration a, PreRegistration b) {
		if (a.getStart() < b.getStart()) {
			return -1;
		} else if (a.getStart() > b.getStart()) {
			return 1;
		} else {
			if (a.getEnd() > b.getEnd()) {
				return -1;
			} else if (a.getEnd() < b.getEnd()) {
				return 1;
			} else {
				if (a.getCosts() > b.getCosts()) {
					return -1;
				} else if (a.getCosts() < b.getCosts()) {
					return 1;
				} else {
					return 0;
				}
			}
		} 
	}
}
