/*
321/2011117 Πάναλης Κυριάκος
 */
package diplomatiki;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.util.SupplierUtil;

/*Στην παρούσα κλάση περιλαμβάνονται οι κυριότερες μέθοδοι που σχετίζονται με
  την δημιουργία του γράφου*/
public class Graph {

    private Vertex vObject;
    private BufferedReader in = null;//χρήση για ανάγνωση από αρχείο
    private BufferedReader inLog = null;//χρήση για ανάγνωση από αρχείο
    private String line;//Μεταβλητη που χρησιμοποιείτε για ανάγνωση από αρχείο
    private final static double AVERAGE_RADIUS_OF_EARTH_M = 6371000;//μέση απόσταση από το κέντρο της γης σε μέτρα
    private ArrayList<Vertex> vertexList = new ArrayList<>();//λίστα στην οποία αποθηκεύονται όλες οι κορυφές
    private ArrayList<Vertex> tempList;//λίστα που χρησιμοποιείται για προσωρινή αποθήκευση κορυφών
    private int edgeId = 0;
    private org.jgrapht.Graph<Vertex, Edge> dGraph;
    /*private double lowX=999; 
    private double lowY;    //μεταβλητές που χρησιμοποιούνται για την εύρεση των max και min συντεταγμένων
    private double highX;
    private double highY=-999;
     */
    private int noMatchCnt;
    private int maxIterations = 100;
    private double dampingFactor = 0.85;
    private String r;
    private String data = "files/restaurants.txt";

    Graph() {
        this.dGraph = new DefaultDirectedWeightedGraph<>(Edge.class);

    }
//Μέθοδος που διαβάζει το log file και καλεί την μέθοδο που πραγματοποιεί την αναζήτηση στην βάση με τα εστιατόρια

    public int randomInt(int min, int max) {

        int a = ThreadLocalRandom.current().nextInt(min, max + 1);

        return a;
    }

    public void takeLog(String fileName) {
        String logLine;
        int count = 1;
        try {
            inLog = new BufferedReader(new FileReader(fileName));
            while ((logLine = inLog.readLine()) != null) {
                System.out.printf("\n\n");
                System.out.println("----------New Log File------- id: " + count);
                System.out.printf("\n");
                String[] arrOfStr = logLine.split("\\|");
                int id = Integer.parseInt(arrOfStr[0]);
                double logX = Double.parseDouble(arrOfStr[1]);
                double logY = Double.parseDouble(arrOfStr[2]);
                int rad = Integer.parseInt(arrOfStr[3]);
                /* k */ int k = 15;//Integer.parseInt(arrOfStr[4]);
                String words = arrOfStr[5].replace("[", "").replace("]", "");
                String[] logKwords = words.split(",");
                searchInDB(logX, logY, rad, logKwords, k);
                count++;
            }//while

            /*   
            System.out.println("lowest X point: "+lowX);
            System.out.println("lowest Y point: "+lowY);
            System.out.println("Heighest X point: "+highX);
            System.out.println("Heighest Y point: " +highY);
             */
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
//Μέθοδος που παίρνει ως όρισμα μία log εγγραφή και πραγματοποιεί αναζήτηση στο αρχείο με τα εστιατόρια

    public void searchInDB(double logX, double logY, int radius, String[] logKwords, int k) {
        try {
            in = new BufferedReader(new FileReader(data));
            tempList = new ArrayList<>();//δημιουργία λίστας για κάθε εγγραφή log
            while ((line = in.readLine()) != null) {

                String[] arrOfStr = line.split("\\|");
                String restId = arrOfStr[0];
                String name = arrOfStr[1];
                float rank = Float.parseFloat(arrOfStr[2]);
                double restX = Double.parseDouble(arrOfStr[3]);
                double restY = Double.parseDouble(arrOfStr[4]);

                String words = arrOfStr[5];
                String[] DBkWords = words.split(",");
                //υπολογισμος απόστασης απο log σε resturant
                int d = calculateDistanceInKilometer(logX, logY, restX, restY);
                // findLowestX(restX);
                // findLowestY(restY);
                // findHighestX(restX);
                // findHighestY(restY);
                if (radius >= d) {// εαν το εστιατόριο βρίσκεται στην επιθυμητή ακτίνα

                    boolean match = false;
                    int matchCount = 0;
                    //έλεγχος για λέξεις κλειδιά 
                    int w = logKwords.length;
                    for (int i = 0; i < w; i++) { //προσπέλαση του πίνακα με τις λέξεις κλειδιά της log εγγραφής
                        for (String DBkWord : DBkWords) {
                            //προσπέλαση του πίνακα με τις λέξεις κλειδιά του εστιατορίου
                            if (logKwords[i].replaceAll("\\s+", "").equalsIgnoreCase(DBkWord.replaceAll("\\s+", ""))) {
                                //ελεγχος εαν ταιριάζουν οι λέξεις κλειδια
                                matchCount++;
                            }
                        }
                    }
                    if (matchCount >= w) {
                        int index = checkforVertex(restId);//έλεγχος για το αν υπάρχει ήδη κορυφή
                        if (index != -1) {//αν υπάρχει ήδη κορυφή
                            tempList.add(vertexList.get(index));//Λίστα που κρατάει προσωρινά ολες τις εγγραφες που ταιριάζουν στο ερώτημα
                            vertexList.get(index).setDistanceFromLog(d);//ενημέρωση της απόστασης από νέο log

                        } else {//αν δεν υπάρχει κορυφή
                            Vertex newVertex = new Vertex(restId, name, rank, restX, restY, DBkWords);//δημιουργία νέας
                            newVertex.setDistanceFromLog(d);
                            tempList.add(newVertex);
                        }
                    }
                }
            }//while
            if (!tempList.isEmpty()) {
                createGraph(tempList, k, logKwords);
            } else {
                noMatchCnt++;
                System.out.println("no match");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//Μέθοδος που είναι υπεύθυνη για την δημιουργία του γράφου
    public void createGraph(ArrayList<Vertex> tempList, int k, String[] logKeywords) {

        Collections.sort(tempList); //ταξινόμηση των κορυφών με βάση την απόσταση
        if (k >= tempList.size()) { //εαν υπάρχουν λιγότερα από τα επιθυμητα εστιατόρια 
            k = tempList.size();
        } else {
            tempList = trimSortedList(tempList, k);//διαγραφή των επιπλέον κορυφών από την λίστα
        }
        if (k > 1) {//εαν η ερώτηση δεν είναι τοπ-1
            for (k = k - 1; k >= 0; k--) {//ξεκινάμε από τον κόμβο με την μεγαλύτερη απόσταση από την τοποθεσία του χρήστη

                if (k >= 1) {
                    if (tempList.get(k).getExistInGraph()) {//υπάρχει ο κόμβος κ

                        if (tempList.get(k - 1).getExistInGraph()) {//υπάρχουν και οι δύο κόμβοι
                            if (!dGraph.containsEdge(tempList.get(k), tempList.get(k - 1))) {//δεν συνδέονται μεταξύ τους
                                edgeId++;
                                dGraph.addEdge(tempList.get(k), tempList.get(k - 1), new Edge(logKeywords));

                            } else {//συνδέονται μεταξύ τους
                                dGraph.getEdge(tempList.get(k), tempList.get(k - 1)).convertTags(logKeywords);
                            }
                        } else {//υπάρχει ο κ αλλά όχι ο κ-1
                            tempList.get(k - 1).setExistInGraph(true);
                            vertexList.add(tempList.get(k - 1));
                            dGraph.addVertex(tempList.get(k - 1));
                            edgeId++;
                            dGraph.addEdge(tempList.get(k), tempList.get(k - 1), new Edge(logKeywords));

                        }
                    } else {//δεν υπάρχει ο κόμβος κ
                        tempList.get(k).setExistInGraph(true);
                        vertexList.add(tempList.get(k));
                        dGraph.addVertex(tempList.get(k));
                        if (tempList.get(k - 1).getExistInGraph()) {//υπάρχει ο k-1
                            edgeId++;
                            dGraph.addEdge(tempList.get(k), tempList.get(k - 1), new Edge(logKeywords));

                        } else {//δεν υπάρχει ούτε ο κ-1
                            tempList.get(k - 1).setExistInGraph(true);
                            vertexList.add(tempList.get(k - 1));
                            dGraph.addVertex(tempList.get(k - 1));
                            edgeId++;

                            dGraph.addEdge(tempList.get(k), tempList.get(k - 1), new Edge(logKeywords));

                        }
                    }
                }//for
                int o = k + 1;
                System.out.println("Top-" + o + " Restaurant name: " + tempList.get(k).getName() + " id: " + tempList.get(k).getId() + " Distance from user in meters :" + tempList.get(k).getDistance());
            }

        } else {
            //εαν το ερώτημα είναι τοπ-1 τότε απλά δημιουργείται ο κόμβος χωρίς κάποια ακμη
            if (!tempList.get(0).getExistInGraph()) {
                tempList.get(0).setExistInGraph(true);
                vertexList.add(tempList.get(0));//ανανέωση της global λίστας με τους κόμβους
                dGraph.addVertex(tempList.get(0));//προσθήκη του κόμβου στον γράφο
            }
            System.out.println("Top-1 : " + tempList.get(0).getName() + " rest id: " + tempList.get(0).getId() + " Distance in meters :" + tempList.get(0).getDistance());

        }
    }

    public void runWeightedPR(String word) {
        Set<Edge> eSet = dGraph.edgeSet();
        boolean match = false;

        for (Edge e : eSet) {

            double weight = e.getQueryCount() * 0.5;
            List<String> tags = e.getTagList();
            for (int i = 0; i < tags.size(); i++) {
                if (tags.get(i).contains(word)) {
                    // System.out.println(tags.get(i));
                    match = true;
                }
            }
            if (match) {
                weight = weight * 10;
            }

            dGraph.setEdgeWeight(e, weight);
            //System.out.println(dGraph.getEdgeWeight(e));
            match = false;
        }

        runPageRank();
    }

    public void viewStats() {
        System.out.println();
        System.out.println("....................Total vertexes: " + vertexList.size() + " ....................");
        System.out.println("....................Total edges: " + edgeId + " ....................");
        System.out.println("....................No matching logs: " + noMatchCnt + " ....................");
        System.out.println();
    }

//Μέθοδος που ελέγχει εαν υπάρχει ήδη κορυφή για κάποιο συγκεκριμένο εστιατόριο
    public int checkforVertex(String restId) {
        int found = -1;
        for (int i = 0; i < vertexList.size(); i++) {
            if (vertexList.get(i).getId().equalsIgnoreCase(restId)) {
                found = i;
                break;
            }
        }
        return found;
    }

    public int calculateDistanceInKilometer(double userLat, double userLng,
            double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_M * c));
    }

//διαγραφή επιπλέον κόμβων από μία λίστα κορυφών
    public ArrayList<Vertex> trimSortedList(ArrayList<Vertex> l, int k) {
        int i;

        for (i = l.size() - 1; i >= k; i--) {
            l.remove(i);
        }

        return l;
    }
//μέθοδος που χρησιμοποιείτε για την οπτικοποίηση του γράφου

    public void drawGraph() throws IOException {
        JGraphXAdapter<Vertex, Edge> graphAdapter = new JGraphXAdapter<Vertex, Edge>(dGraph);

        // mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);

        Hashtable<String, Object> style = new Hashtable<String, Object>();
        //μορφοποίηση των κόμβων
        style.put(mxConstants.STYLE_FILLCOLOR, "#B8FFF5");
        style.put(mxConstants.STYLE_STROKEWIDTH, 0.8);
        style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        style.put(mxConstants.STYLE_FONTSIZE, 10);

        mxStylesheet stylesheet = graphAdapter.getStylesheet();
        //μορφοποίηση των ακμών
        Map<String, Object> edgeStyle = stylesheet.getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.ARROW_SIZE = 10);
        edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        edgeStyle.put(mxConstants.STYLE_FONTSIZE, 7);
        edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);

        stylesheet.setDefaultVertexStyle(style);
        stylesheet.setDefaultEdgeStyle(edgeStyle);

        layout.execute(graphAdapter.getDefaultParent());

        graphAdapter.getModel().beginUpdate();

        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 3, Color.WHITE, true, null);
        File imgFile = new File("files/graphImage.png");
        ImageIO.write(image, "PNG", imgFile);

        System.out.println();
        System.out.println("== == == == == == == == == == == == == == ==");
        System.out.println("image created at files folder(graphImage.PNG)");
        System.out.println("== == == == == == == == == == == == == == ==");
    }

    public void runPageRank() {
        VertexScoringAlgorithm<Vertex, Double> pr = new PageRank<>(dGraph, dampingFactor, maxIterations);
        Map<Vertex, Double> scores = pr.getScores();
        scores.forEach((k, v) -> k.setPRscore(v));
        displayPRvalues();

    }

    public void displayPRvalues() {
        vertexList.sort(Comparator.comparing(Vertex::getPRscore).reversed());
        System.out.println("== == == == == == == == == == == == == == == ");
        System.out.println("Top 10 Restaurants : ");
        int cnt = vertexList.size();

        for (int i = 0; i < 10 && i < cnt; i++) {
            System.out.println("* * * ");
            System.out.println("Id: " + vertexList.get(i).getId() + " Score: " + vertexList.get(i).getPRscore() + " Restaurant name : " + vertexList.get(i).getName());
        }
        System.out.println("== == == == == == == == == == == == == == == ");

    }

    public void exportGraph() {
        DOTExporter<Vertex, Edge> exporter = new DOTExporter<>(v -> v.getId());
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("id", DefaultAttribute.createAttribute(v.getId()));
            map.put("name", DefaultAttribute.createAttribute(v.getName()));
            map.put("score", DefaultAttribute.createAttribute(v.getPRscore()));
            map.put("x", DefaultAttribute.createAttribute(v.getX()));
            map.put("y", DefaultAttribute.createAttribute(v.getY()));
            map.put("keywords", DefaultAttribute.createAttribute(v.getKeywords()));
            return map;
        });

        exporter.setEdgeAttributeProvider((e) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(e.toString()));
            map.put("noOfQueries", DefaultAttribute.createAttribute(e.getQueryCount() - 1));

            return map;
        });
        try {

            FileWriter fw = new FileWriter("files/graph.dot");
            BufferedWriter bw = new BufferedWriter(fw);

            exporter.exportGraph(dGraph, bw);
            System.out.println("---------------------------------------------- ");
            System.out.println("Graph exported succesfuly at files/graph.DOT ");
            System.out.println("----------------------------------------------");

        } catch (IOException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void importGraph() {

        DOTImporter<Vertex, Edge> importer = new DOTImporter<>();
        org.jgrapht.Graph<Vertex, Edge> graph = new DefaultDirectedWeightedGraph<Vertex, Edge>(SupplierUtil.createSupplier(Vertex.class), SupplierUtil.createSupplier(Edge.class));

        Map<Vertex, Map<Vertex, Attribute>> attrs = new HashMap<>();

        importer.addVertexAttributeConsumer((p, a) -> {
            Vertex v = p.getFirst();
            String attrName = p.getSecond();
            switch (attrName) {
                case "id":
                    v.setId(a.getValue());
                    break;
                case "name":
                    v.setName(a.getValue());
                    break;
                case "score":
                    double score = Double.parseDouble(a.getValue());
                    v.setPRscore(score);
                    break;
                case "x":
                    double x = Double.parseDouble(a.getValue());
                    v.setLx(x);
                    break;
                case "y":
                    double y = Double.parseDouble(a.getValue());
                    v.setLy(y);
                    break;
                case "keywords":

                    v.setKeywords(a.getValue());
                    break;
            }

        });
        importer.addEdgeAttributeConsumer((p, a) -> {
            Edge e = p.getFirst();

            String attrLabel = p.getSecond();
            switch (attrLabel) {
                case "label":
                    e.setTags(a.getValue());
                    break;
                case "noOfQueries":
                    int d = Integer.parseInt(a.getValue());
                    e.setQueryCount(d);
                    break;
            }

        });

        try {
            importer.importGraph(graph, new FileReader("files/graph.dot"));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }

        org.jgrapht.Graph<Vertex, Edge> labeledGraph = new DefaultDirectedGraph<>(SupplierUtil.createSupplier(Vertex.class), SupplierUtil.createSupplier(Edge.class), true);
        for (Vertex v : graph.vertexSet()) {

            labeledGraph.addVertex(v);
            vertexList.add(v);
        }

        for (Edge e : graph.edgeSet()) {

            Vertex source = graph.getEdgeSource(e);
            Vertex target = graph.getEdgeTarget(e);
            edgeId++;
            labeledGraph.addEdge(source, target);
            System.out.println();
            System.out.print(source.getId() + "-->" + target.getId());
            System.out.println();

        }

        dGraph = graph;

    }
    /* μέθοδοι που χρησιμοποιήθηκαν κατα την ευρεση της έκτασης που
       βρίσκονται τα εστιατόρια
   
    public void findLowestX(double o) {

        if(lowX>o ) {
            lowX = o;
            r = restId;
        }
    }

    public void findLowestY(double y) {

        if(lowY > y) {
            lowY = y;
        }
    }

    public void findHighestY(double q) {

        if(highY < q) {
            highY = q;
         //   System.out.println("  " + highY);
        }
    }

    public void findHighestX(double x) {

        if(highX < x) {
            highX = x;
        }

    } */

}
