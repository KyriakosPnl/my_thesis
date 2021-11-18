package diplomatiki;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*Class that represents an edge of the graph*/
public class Edge implements Serializable {

    private int edgeId = 0;
    private List<String> tags;
    private int count = 0;
    private List<String> tList = new ArrayList<>();

    Edge() {
    }

    Edge(String[] tags) {
        this.edgeId = edgeId++;

        this.tags = convertTags(tags);

    }

    public void setTags(String s) {
        String[] strArr = s.split("\\,");
        tags = convertTags(strArr);
    }

    public void setQueryCount(int s) {
        count = s;
    }

    public List<String> convertTags(String[] t) {
        count++;
        for (int i = 0; i < t.length; i++) {
            if (!tList.contains(t[i])) {

                tList.add(t[i]);
            }
        }
        return tList;
    }

    public int getQueryCount() {
        return count;
    }

    public List<String> getTagList() {
        return tags;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : tags) {
            sb.append(s);
            sb.append(" ,");
        }

        return sb.toString();
    }

}