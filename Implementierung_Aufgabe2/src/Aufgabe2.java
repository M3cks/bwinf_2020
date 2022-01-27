package aufgabe2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Klasse zur Loesung des Problems
 * 
 * @author Carl Friedrich Mecking
 */

public class Aufgabe2 {
	
	/**
	 * Generierung der Adjazenzmatrix
	 * 
	 * @param data Datenobjekt der gewaehlten Testdatei
	 * @return Adjazenzmatrix als Darstellung des Graphen
	 */
	private static int[][] genMatrix(Data data) {

		// Adjazenzmatrix zur Darstellung des Graphen
		int[][] matrix = new int[data.getMaxSorts()+1][data.getMaxSorts()+1];
		
		// Einfuegen erster Zeile und Spalte zur Identifizierung von Sorten und Schuesseln
		for (int i = 0; i < matrix.length; i++) {
			matrix[i][0] = i;
			matrix[0][i] = i;
		}
		
		// Fuellen der Adjazenzmatrix nach Beobachtungen
		for (int i = 0; i < data.getSeenBowls().size(); i++) {
			String[] seenBowlsArr = data.getSeenBowls().get(i).split(" ");
			String[] seenSortsArr = data.getSeenSorts().get(i).split(" ");
			int[] seenBowlsNums = new int[seenBowlsArr.length];
			for (int j = 0; j < seenBowlsArr.length; j++) {
				seenBowlsNums[j] = Integer.parseInt(seenBowlsArr[j]);
			}
			
			// Erhoeht das Gewicht jeder Kante zwischen den beobachteten Sorten und den dazugehoerigen beobachteten Schuesseln.
			for (int j = 0; j < seenSortsArr.length; j++) {
				int sortIndex = data.getAllSorts().indexOf(seenSortsArr[j]);
				for (int k = 0; k < seenBowlsNums.length; k++) {
					matrix[sortIndex+1][seenBowlsNums[k]] = matrix[sortIndex+1][seenBowlsNums[k]] + 1;
				}
			}
		}
		
		return matrix;
	}
		
	/**
	 * Gibt eine Matrix auf die Konsole aus.
	 * 
	 * @param matrix die auszugebene Matrix
	 */
	private static void printMatrix(int[][] matrix, String msg) {
		System.out.print(msg);
		for (int i = 0; i < matrix.length; i++) {
			System.out.println();
			for (int j = 0; j < matrix[i].length; j++) {
				// Nur extra einruecken bei zweistelligen Zahlen.
				if (matrix[i][j] < 10) {
					System.out.print("  " + matrix[i][j]);
				} else {
					System.out.print(" " + matrix[i][j]);
				}
			}
		}
		System.out.println();
		System.out.println();
	}
	
	/**
	 * Kopiert eine Adjazenzmatrix ohne die angegebene Zeile und Spalte.
	 * 
	 * @param matrix die Adjazenzmatrix
	 * @param row die zu entfernende Zeile
	 * @param col die zu entfernende Spalte
	 * @return Adjazenzmatrix ohne die entsprechende Zeile und Spalte
	 */
	private static int[][] removeSingle(int[][] matrix, int row, int col) {
		// Anzahl der uebersprungenen Zeilen | hier entweder 0 oder 1
		int rowSkipped = 0;
		// die neue Adjazenzmatrix
		int[][] newMatrix = new int[matrix.length-1][matrix.length-1];
		
		// Kopiert alle Zeilen ohne die angegebene Reihe
		for (int i = 0; i < matrix.length; i++) {
			if (i != row) {
				// Anzahl der uebersprungenen Spalten | hier entweder 0 oder 1
				int colSkipped = 0;
				
				// Kopiert alle Spalten ohne die angegebene Reihe
				for (int j = 0; j < matrix[i].length; j++) {
					if (j != col) {
						newMatrix[i-rowSkipped][j-colSkipped] = matrix[i][j];
					} else {
						colSkipped++;
					}
				}
			} else {
				rowSkipped++;
			}
		}
		
		return newMatrix;
	}
	
	/**
	 * Kopiert eine Adjazenzmatrix ohne die angegebenen Zeilen und Spalten.
	 * 
	 * @param matrix die Adjazenzmatrix
	 * @param rows Liste der zu entfernenden Zeilen
	 * @param cols Liste der zu entfernenden Spalten
	 * @return Adjazenzmatrix ohne die entsprechenden Zeilen und Spalten
	 */
	private static int[][] removeMultiple(int[][] matrix, ArrayList<Integer> rows, ArrayList<Integer> cols) {
		int[][] newMatrix = new int[matrix.length-rows.size()][matrix.length-cols.size()];
		
		// Anzahl der uebersprungenen Zeilen
		int skippedRows = 0;
		
		// Kopiert die Zeilen, welche nicht entfernt werden sollen.
		for (int i = 0; i < matrix.length; i++) {
			
			// Testet, ob sich die momentane Zeile unter den zu entfernenden Zeilen befindet.
			boolean isNotRow = true;
			for (int k = 0; k < rows.size(); k++) {
				if (i == rows.get(k)) {
					isNotRow = false;
					break;
				}
			}
			
			// Kopiert entsprechende Kanten Zeile, wenn die Zeile nicht entfernt werden soll.
			if (isNotRow) {
				// Anzahl der uebersprungenen Spalten
				int skippedCols = 0;
				
				// Testet, ob sich die momentane Spalte unter den zu entfernenden Spalten befindet.
				for (int j = 0; j < matrix[i].length; j++) {
					boolean isNotCol = true;
					for (int k = 0; k < cols.size(); k++) {
						if (j == cols.get(k)) {
							isNotCol = false;
						}
					}
					
					// Kopiert die Kante, wenn die Spalte nicht entfernt werden soll.
					if (isNotCol) {
						newMatrix[i-skippedRows][j-skippedCols] = matrix[i][j];
					} else {
						// Ansonsten wird die Spalte uebersprungen.
						skippedCols++;
					}
				}
			} else {
				// Ansonsten wird die Zeile uebersprungen.
				skippedRows++;
			}
		}
				
		return newMatrix;
	}
	
	/**
	 * Weist Knoten mit ungewichteten Kanten zu und entfernt sie.
	 * 
	 * @param matrix die Adjazenzmatrix
	 * @param matches die Liste der gefundenen Zuweisungen
	 * @return vollstaendige Liste der Zuweisungen
	 */
	private static ArrayList<Match> assignUnweighted(int[][] matrix, ArrayList<Match> matches) {
		// Liste der zu entfernenden Spaltenindizes
		ArrayList<Integer> colIndices = new ArrayList<>();
		
		// Liste der zu entfernenden Zeilenindizes
		ArrayList<Integer> rowIndices = new ArrayList<>();
		
		//Durchlaeuft die Matrix und ermittelt Zeilen und Spalten, die nur mit null gewichtet sind.
		for (int i = 1; i < matrix.length; i++) {
			boolean isColEmpty = true;
			boolean isRowEmpty = true;
			for (int j = 1; j < matrix[i].length; j++) {
				if (matrix[i][j] != 0) {
					isRowEmpty = false;
				}
				
				if (matrix[j][i] != 0) {
					isColEmpty = false;
				}
			}
			
			if (isColEmpty) {
				colIndices.add(i);
			}
			
			if (isRowEmpty) {
				rowIndices.add(i);
			}
		}
		
		// Weisst die Schuesselnummerknoten den Sortenknoten zu, wenn es Knoten ohne Kanten gab.
		if (colIndices.size() > 0) {
			for (int i = 0; i < rowIndices.size(); i++) {
				for (int j = 0; j < colIndices.size(); j++) {
					matches.get(matrix[rowIndices.get(i)][0]-1).addBowlNum(matrix[0][colIndices.get(j)]);
				}
			}
			
			// Reduziert die Matrix um die zugewiesenen Knoten.
			matrix = removeMultiple(matrix, rowIndices, colIndices);
			printMatrix(matrix, "Anwendung Phase I:");
		}
		
		// Startet den ersten Rekursionsaufruf der Hauptloesungsmethode.
		return assignWeighted(matrix, matches);
	}
	 
	/**
	 * Rekursive Methode zur Zuweisung von Sorten und Schuesseln und Reduzierung der Adjazenzmatrix
	 * 
	 * @param matrix die momentane Adjazenzmatrix
	 * @param matches die Liste der gefundenen Zuweisungen
	 * @return die Liste aller Zuweisungen
	 */
	private static ArrayList<Match> assignWeighted (int[][] matrix, ArrayList<Match> matches) {
		// Summen der Spalten
		int[] colSums = new int[matrix.length-1];
		
		// Maxima der Spalten
		int[] colMaxs = new int[matrix.length-1];
		
		// hoechstes Kantengewicht der gesamten Adjazenzmatrix
		int globalMax = -1;
		
		// Durchlaeuft die Spalten und ermittelt Spaltensumme, -maxima und das hoechste Kantengewicht der gesamten Matrix.
		for (int i = 1; i < matrix.length; i++) {
			// Momentane(s) Spaltensumme / Spaltenmaximum
			int colMax = -1;
			int colSum = 0;
			
			// Durchlaeuft die Reihen und ermittelt momenatne(s) Spaltensumme /-maximum
			for (int j = 1; j < matrix[i].length; j++) {
				if (matrix[j][i] > globalMax) {
					globalMax = matrix[j][i];
				}
				if (matrix[j][i] > colMax) {
					colMax = matrix[j][i];
				}
				
				// Spaltensumme wird mit Zeilenindex multipliziert, um eindeutige Zuweisungen eher finden zu koennen.
				colSum += matrix[j][i] * j;
			}
			
			colMaxs[i-1] = colMax;
			colSums[i-1] = colSum;
		}
		
		// Liste von Spaltenindizes, bei denen in den Spalten ein globales Maximum existiert und die Kantenkonstellation nur
		// einmal vorkommt.
		ArrayList<Integer> singleIndexes = new ArrayList<>();
		HashMap<Integer, ArrayList<Integer>> multipleIndexes = new HashMap<>();
		
		// Durchlaeuft alle Spaltenmaxima.
		for (int i = 0; i < colMaxs.length; i++) {
			
			// Sucht nach Spalten, die ein globales Maximum besitzen und deren Kantenkosntellation nur einmal vorkommt.
			if (colMaxs[i] == globalMax) {
				boolean isSingle = true;
				for (int j = 0; j < colMaxs.length; j++) {
					if (i != j && colMaxs[j] == globalMax && colSums[i] == colSums[j]) {
						
						boolean isEqual = true;
						for (int k = 1; k < matrix.length; k++) {
							if (matrix[k][i+1] != matrix[k][j+1]) {
								isEqual = false;
								break;
							}
						}
						
						isSingle = !isEqual;
						
						if (isEqual) {
							if (multipleIndexes.containsKey(i+1)) {
								multipleIndexes.get(i+1).add(j+1);
							} else {
								multipleIndexes.put(i+1, new ArrayList<Integer>());
								multipleIndexes.get(i+1).add(j+1);
							}
						}
						
					} 
				}
				
				if (isSingle) {
					singleIndexes.add(i+1);
				}
			}
		}
		
		// Knoten mit einmaliger Kantenkonstellation und globalem Maximum existieren noch
		// => diese Knoten zuerst zuordnen
		if (!singleIndexes.isEmpty()) {
			
			// Durchlaeuft die Spalten mit einmaligen Kantenkonstellationen und globalem Maximum.
			for (int i = 0; i < singleIndexes.size(); i++) {
				
				// Anzahl der Maxima in der momentanen Spalte
				int maxCount = 0;
				
				// Index der Zeile in der ein Maximum vorkommt | findet nur Verwendung, wenn in der Spalte ausschliesslich ein 
				// Maximum existiert
				int maxRowIndex = 0;
				
				// Ermittelt die Anzahl der Maxima in den entsprechenden Spalten und den Zeilenindex, falls nur 
				// ein Maximum existiert.
				for (int j = 1; j < matrix.length; j++) {
					if (matrix[j][singleIndexes.get(i)] == globalMax) {
						maxCount++;
						maxRowIndex = j;
					}
				}
				
				// Nur ein Maximum existiert in der momentanen Spalte.
				if (maxCount == 1) {
					
					// Ermittelt, ob dieses Maximum auch in seiner Zeile nur einmal vorkommt.
					boolean isSingleInRow = true;
					for (int j = 1; j < matrix[i].length; j++) {
						if (singleIndexes.get(i) != j && matrix[maxRowIndex][j] == globalMax) {
							isSingleInRow = false;
							break;
						}
					}
					
					// Ermittelte Kante kommt sowohl in seiner Spalte als auch in seiner Zeile nur einmal vor
					// => Zuweisung der Schuesselnummer zu der entsprechenden Frucht
					// => Entfernen der beiden Knoten und deren Kanten
					// => N�chster Rekursionsaufruf
					if (isSingleInRow) {
						matches.get(matrix[maxRowIndex][0]-1).addBowlNum(matrix[0][singleIndexes.get(i)]);
						matrix = removeSingle(matrix, maxRowIndex, singleIndexes.get(i));
						printMatrix(matrix, "Anwendung Phase III:");
						return assignWeighted(matrix, matches);
					}
				}
			}
		
		// Es existiert kein Knoten mit globalem Maximum und einmaliger Kantenkosntellation mehr.
		} else {
			
			// Durchlaeuft die Spalten mit gleicher Kantenkonstellation.
			for (int key : multipleIndexes.keySet()) {
				
				// Anzahl der Maxima in der momentanen Spalte
				int maxCount = 0;
				
				// Indizes von zugehoerigen Zeilen gleicher Kantenkonstellation
				ArrayList<Integer> equalMaxRows = new ArrayList<>();
				
				// Durchlaeuft momentane Spalte mit gleicher Kantenkonstellation.
				for (int i = 1; i < matrix.length; i++) {
					
					// Erhoeht die Anzahl der Maxima von der momentanen Spalte und fuegt die entsprechenden Zeilen der 
					// Liste hinzu.
					if (matrix[i][key] == globalMax) {
						maxCount++;
						equalMaxRows.add(i);
					}
				}

				// Ist die Anzahl der Maxima von Knoten gleicher Konstellation so gross, wie die Anzahl von Knoten, welche
				// diese Konstellation von Kanten besitzen?
				// 	=> Zuweisung, Reduzierung und naechster Rekursionsaufruf
				if (maxCount == multipleIndexes.get(key).size()+1) {
					// Vervollstaendigt die Liste von Spaltenindizes der Spalten gleicher Kantenkonstellation
					multipleIndexes.get(key).add(key);
					
					// Durchlaeuft die Zeilen, also Sorten, welche zugewiesen werden muessen.
					for (int i = 0; i < equalMaxRows.size(); i++) {
						
						// Durchlaeuft die Anzahl der Spalten, also Schuesseln, welche zugewiesen werden muessen.
						for (int j = 0; j < multipleIndexes.get(key).size(); j++) {
							
							// Weist die momentane Schuessel der momentanen Sorte zu.
							matches.get(matrix[equalMaxRows.get(i)][0]-1).addBowlNum(matrix[0][multipleIndexes.get(key).get(j)]);
						}
					}
					
					// Reduzierung der Matrix um die zugewiesenen Knoten
					matrix = removeMultiple(matrix, equalMaxRows, multipleIndexes.get(key));
					
					// Ausgabe der neuen Matrix
					printMatrix(matrix, "Anwendung Phase IV:");
					
					// Naechster Rekursionsaufruf
					return assignWeighted(matrix, matches);
				}
			}
		}
		return matches;
	}
	
	/**
	 * Konsoleneingabe: Wahl der Testdatei.
	 * 
	 * @return absoluter Dateipfad zur gewaehlten Testdatei.
	 */
	private static String consoleInterface() {
		Scanner in = new Scanner(System.in);
        System.out.println("Bitte waehlen Sie eine der folgenden Dateien:");
        System.out.println("0 - spiesse0.txt (Beispieldatei aus Aufgabenstellung)");
        System.out.println("1 - spiesse1.txt");
        System.out.println("2 - spiesse2.txt");
        System.out.println("3 - spiesse3.txt");
        System.out.println("4 - spiesse4.txt");
        System.out.println("5 - spiesse5.txt");
        System.out.println("6 - spiesse6.txt");
        System.out.println("7 - spiesse7.txt");
        System.out.print("=> ");
        String fileName = "";
        try {
        	int fileNum = in.nextInt();
        	if (fileNum > 7 || fileNum < 0) {
        		System.out.println();
            	System.out.println("Bitte waehlen Sie eine gueltige Nummer!");
            	return consoleInterface();
        	}
        	String path = new File("").getAbsolutePath() + "\\res\\";
        	switch(fileNum) {
        	case 0:
        		fileName = path + "spiesse0.txt";
        		break;
        	case 1:
        		fileName = path + "spiesse1.txt";
        		break;
        	case 2:
        		fileName = path + "spiesse2.txt";
        		break;
        	case 3:
        		fileName = path + "spiesse3.txt";
        		break;
        	case 4:
        		fileName = path + "spiesse4.txt";
        		break;
        	case 5:
        		fileName = path + "spiesse5.txt";
        		break;
        	case 6:
        		fileName = path + "spiesse6.txt";
        		break;
        	case 7:
        		fileName = path + "spiesse7.txt";
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
	 * Gibt eine informative Aussage ueber die gefundenen Zuweisungen auf die Konsole aus.
	 * 
	 * @param matches die vollstaendige Liste der Zuweisungen
	 * @param data das zu der gewaehlte Datei erstellte Datenobjekt
	 */
	private static void output(ArrayList<Match> matches, Data data) {
		// Ausgabe der Zuweisungen je Sorte
		System.out.println();
		System.out.println("Die jeweiligen Obstsorten koennen in folgenden Schuesseln liegen:");
		for (int i = 0; i < matches.size(); i++) {
			System.out.print(matches.get(i).getSort() + ": ");
			for (int j = 0; j < matches.get(i).getBowlNums().size(); j++) {
				System.out.print(matches.get(i).getBowlNums().get(j) + " ");
			}
			System.out.println();
		}
		System.out.println();
		
		// Gibt die Wunschsorten aus
		System.out.print("Demnach koennen die Wunschsorten ");
		for (int i = 0; i < data.getTargetSorts().split(" ").length; i++) {
			if (i < data.getTargetSorts().split(" ").length-2) {
				System.out.print(data.getTargetSorts().split(" ")[i] + ", ");
			} else if (i == data.getTargetSorts().split(" ").length-2) {
				System.out.print(data.getTargetSorts().split(" ")[i]);
			} else {
				System.out.print(" und " + data.getTargetSorts().split(" ")[i]);
			}
		}
		
		// Sammelt die Anzahl der Schuesseln, welche den Wunschsorten zugeordnet wurden.
		ArrayList<Integer> bowls = new ArrayList<>();
		for (int i = 0; i < data.getTargetSorts().split(" ").length; i++) {
			for (int j = 0; j < matches.get(i).getBowlNums().size(); j++) {
				boolean isIn = false;
				if (bowls.size() == 0) {
					bowls.add(matches.get(i).getBowlNums().get(j));
				} else {
					for (int k = 0; k < bowls.size(); k++) {
						if (bowls.get(k) == matches.get(i).getBowlNums().get(j)) {
							isIn = true;
						}
					}
					if (!isIn) {
						bowls.add(matches.get(i).getBowlNums().get(j));
					}
				}
			}
		}
		
		// Ausgabe der Anzahl der Schuesseln, in denen die Wunschsorten zu finden sind.
		System.out.print(" in insgesamt " + bowls.size() + " Schuesseln zu finden sein.");
		System.out.println();
		
		// Ausgabe der Loesbarkeitsgarantie
		if (bowls.size() == data.getTargetSorts().split(" ").length) {
			System.out.println("Der Wunschspiess kann durch Abgehen der Schuesseln eindeutig zusammengestellt werden.");
		} else {
			String[] targetSorts = data.getTargetSorts().split(" ");
			
			// Liste der Sorten, welche noch dem Wunschspiess hinzugefuegt werden muessten, damit fuer dessen Zusammenstellung eine Garantie besteht.
			ArrayList<String> sortsToAdd = new ArrayList<String>();
			
			// Durchlaeuft die Wunschsorten.
			for (int i = 0; i < targetSorts.length; i++) {
				if (matches.get(i).getBowlNums().size() > 1) {
					// Durchlaeuft alle restlichen Sorten, wenn die momentane Wunschsorte eine Schuesselnummerliste, groesser eins, hat.
					for (int j = targetSorts.length; j < data.getAllSorts().size(); j++) {
						
						// Fuegt die momentane Sorte der Liste noch hinzuzufuegenden Sorten hinzu, wenn ihre Schuesselnummerliste gleich mit der
						// Schuesselnummerliste einer Wunschsorte ist.
						if (matches.get(i).getBowlNums().equals(matches.get(j).getBowlNums()) && sortsToAdd.indexOf(matches.get(j).getSort()) == -1) {
							sortsToAdd.add(matches.get(j).getSort());
						}
					}
				}
			}
		
			// Ausgabe der hinzuzufuegenden Sorten.
			if (sortsToAdd.size() > 1) {
				System.out.print("Durch Hinzufuegen der Sorten ");
			} else {
				System.out.print("Durch Hinzufuegen der Sorte ");
			}
			for (int i = 0; i < sortsToAdd.size(); i++) {
				if (i <= sortsToAdd.size()-3) {
					System.out.print(sortsToAdd.get(i) + ", ");
				} else if (i == sortsToAdd.size()-2){
					System.out.print(sortsToAdd.get(i) + " und ");
				} else {
					System.out.print(sortsToAdd.get(i));
				}
			}
			System.out.print(" zu dem Wunschspiess, besteht eine Garantie fuer dessen richtige Zusammenstellung.");
		}
	}

	/**
	 * Hauptptogramm:
	 * => Erstellt Datenobjekt
	 * => Erstellt Zuweisungsliste
	 * => Wendet den ALgorithmus auf die gewaehlte Datei an
	 * => Gibt zu den Zuweisungen eine informative Nachricht aus
	 * 
	 * @throws IOException Fehler bei der Konsoleneingabe
	 */
	public static void main(String[] args) throws IOException {
		
		// Erstellung des Datenobjektes
        Data data = new Data(consoleInterface());
        
        // Generierung der Matrix
		int[][] matrix = genMatrix(data);
		
		printMatrix(matrix, "Ausgangsmatrix:");
		
		// Generierung der Zuweisungsliste
		ArrayList<Match> matches = new ArrayList<>();
		for (int i = 0; i < data.getAllSorts().size(); i++) {
			matches.add(new Match(data.getAllSorts().get(i)));
		}
				
		// Anwendendung des Algorithmus
		matches = assignUnweighted(matrix, matches);
		
		output(matches, data);
	}
}
  