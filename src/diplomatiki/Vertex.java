package diplomatiki;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

/*This class represents a vertex of the graph*/
public class Vertex implements Comparable, Serializable {

    private String id;
    private String name;
    private double rank;
    private double lx;
    private double ly;
    private String[] keywords;
    private ArrayList<Edge> allEdges = new ArrayList<>();
    private int distance = -1;
    private boolean exists = false;
    private double pRScore = 0;

    Vertex() {
    }

    Vertex(String id, String name, double rank, double lx, double ly, String[] keywords) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.lx = lx;
        this.ly = ly;
        this.keywords = keywords;

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPRscore(double score) {
        pRScore = score;
    }

    public void setLx(double lx) {
        this.lx = lx;
    }

    public void setLy(double ly) {
        this.ly = ly;
    }

    public void setKeywords(String k) {
        keywords = k.split("\\,");

    }

    public void setExistInGraph(boolean exists) {
        this.exists = exists;
    }

    public void setDistanceFromLog(int d) {
        distance = d;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return lx;
    }

    public double getY() {
        return ly;
    }

    public String getKeywords() {
        StringBuilder sb = new StringBuilder();
        for (String s : keywords) {
            sb.append(s);
            sb.append(" ,");
        }

        return sb.toString();

    }

    public ArrayList<Edge> getEdges() {
        return allEdges;
    }

    public int getDistance() {
        return distance;
    }

    public boolean getExistInGraph() {
        return exists;
    }

    public double getPRscore() {
        return pRScore;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("####0.000");
        // String v= String.format("%.2d",pRScore ); //id + "PRAnk: "+pRScore;
        return "id: " + id;//+" pr="+ df.format(pRScore);

    }

    /**
     *
     * @param compareVertex
     * @return
     */
    @Override
    public int compareTo(Object compareVertex) {
        int compare = ((Vertex) compareVertex).getDistance();
        /* For Ascending order*/
        return this.distance - compare;

    }
}