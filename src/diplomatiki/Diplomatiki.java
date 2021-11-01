/*
321/2011117 Πάναλης Κυριάκος
 */
package diplomatiki;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*Η κλάση αυτή περιλαμβάνει την main μέθοδο, και σκοπός της είναι η κλήση 
  των απαραίτητων συναρτήσεων για την λειτουργία του προγράμματος. Επίσης, 
  εδώ δημιουργείτε και το μενού που προσφέρει κάποιες βασικές λειτουργίες
  στον χρήστη*/

public class Diplomatiki {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //  LogClass k=new LogClass("files/log.txt");
            // k.createRandLog();

            menu();
        } catch (IOException ex) {
            Logger.getLogger(Diplomatiki.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void menu() throws IOException {
        String bigger_log = "files/largeLog.txt";
        String small_log = "files/smallLog.txt";
        String c;
        // LogClass j=new LogClass("files/log3.txt");
        //j.createRandLog();
        do {
            System.out.println("-- -- -- Type 1 to add query and view results :");
            System.out.println("-- -- -- Type 2 to create graph for random large log file (largeLog) :");
            System.out.println("-- -- -- Type 3 to create graph for small file (smallLog) :");
            System.out.println("-- -- -- Type 4 to import existing graph from file graph.DOT :");
            System.out.println("-- -- -- Type 0 to Exit Program");

            Graph g = new Graph();
            Scanner answer = new Scanner(System.in);
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    LogClass l = new LogClass(small_log);/* Ο constructor της κλάσης LogClass παιρνει ως παράμετρο 
                                                             το όνομα του log αρχείου που θα χρησιμοποιηθεί*/
                    addQuery(l, g);
                    System.out.println("Query added to log file!");
                    break;
                case 2: {
                    g.takeLog(bigger_log);
                    g.viewStats();
                    try {
                        g.drawGraph();
                    } catch (IOException ex) {
                        Logger.getLogger(Diplomatiki.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("*********************************");
                    System.out.println("Do you want to export graph? (Y/N) ");
                    System.out.println("*********************************");
                    c = answer.nextLine();

                    if (c.equalsIgnoreCase("Y")) {
                        g.exportGraph();
                    }

                    break;
                }
                case 3: {
                    g.takeLog(small_log);
                    g.viewStats();
                    try {
                        g.drawGraph();
                    } catch (IOException ex) {
                        Logger.getLogger(Diplomatiki.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("*********************************");
                    System.out.println("Do you want to export graph? (Y/N) ");
                    System.out.println("*********************************");
                    c = answer.nextLine();

                    if (c.equalsIgnoreCase("Y")) {
                        g.exportGraph();
                    }

                    break;
                }
                case 4: {
                    g.importGraph();

                    try {
                        g.drawGraph();
                    } catch (IOException ex) {
                        Logger.getLogger(Diplomatiki.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    g.viewStats();

                    break;
                }
                default:
                    System.exit(0);
            }
            System.out.println("************************************************");
            System.out.println("Do you want to run the PageRank algorithm? (Y/N)");
            System.out.println("************************************************");
            c = answer.nextLine();
            if (c.equalsIgnoreCase("Y")) {
                g.runPageRank();
            }
            System.out.println();
            System.out.println("************************************************");
            System.out.println("Do you want to run the Weighted-PageRank algorithm? (Y/N)");
            System.out.println("************************************************");
            c = answer.nextLine();
            if (c.equalsIgnoreCase("Y")) {
                System.out.println();
                System.out.println(".....Please Enter keyword : ");
                Scanner scan = new Scanner(System.in);
                String keyword = scan.nextLine();
                g.runWeightedPR(keyword);
            }
        } while (true);

    }
// Μέθοδος που δημιουργεί μια νέα καταχώρηση στο αρχείο log σύμφωνα με τις παραμέτρους που θέτει ο χρήστης

    public static void addQuery(LogClass log, Graph g) {
        String[] array = new String[5];
        Scanner sc = new Scanner(System.in);
        System.out.println("Type the value of latitude in your position(X): ");
        String x = sc.nextLine();
        System.out.println("Type the value of longitude in your position(Y): ");
        String y = sc.nextLine();
        System.out.println("Type the radius in meters : ");
        String rad = sc.nextLine();
        System.out.println("Type the amount of restaurants you want to find (k): ");
        String k = sc.nextLine();

        System.out.println("Type the amount of keywords you want to use :");
        int length = sc.nextInt();
        sc.nextLine();
        String[] kwords = new String[length];

        for (int i = 0; i < length; i++) {
            int j = i + 1;

            System.out.println("Enter the " + j + " word : ");
            kwords[i] = sc.nextLine();
        }

        log.writeToLog(x, y, rad, k, kwords);
        double latX = Double.parseDouble(x);
        double lonY = Double.parseDouble(y);
        int radius = Integer.parseInt(rad);
        int top_k = Integer.parseInt(k);
        g.searchInDB(latX, lonY, radius, kwords, top_k);

    }
}
