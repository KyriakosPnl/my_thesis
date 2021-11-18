package diplomatiki;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
//This class creates a rendom file that represents the users queries 

public class LogClass {

    private int idCount;
    private File logFile;

    private int lastLine = 0;
    private int lX = 24;//  min x (24.xxxx)
    private int lΥ = -124;//min y (-124.xxxxx)
    private int hX = 48;//highest x (48.xxxxx)
    private int hY = -69;//highest y (-69.xxxxx)
    private int I = 2000;//how many "queries" will be generated
    private final String[] keywords = {"Burgers", "American", "Fish And Chips", "International", "Mediterranean", "Sandwiches", "Mongolian", "Fusion", "European", "Tapas",
        "Mexican", "Southwestern", "Tex Mex", "Cajun", "Creole", "Bistro", "Grill", "Seafood", "Steak", "Pizza", "Subs", "Persian", "American", "Middle Eastern",
        "Indian", "Asian", "Deli", "Juices", "Pakistani", "Seafood", "Smoothies", "German", "Austrian", "Chinese", "American", "Californian", "Deli", "Diner", "Greek", "Italian",
        "European", "French", "American", "Contemporary", "Continental", "International", "Healthy", "Mediterranean", "Pasta", "Salad", "American", "Burgers", "Fast Food",
        "Cafe", "Californian", "Coffee", "Healthy", "Ice Cream", "Pub Food", "Vegan", "Vegetarian", "Traditional", "French", "Buffet", "Pizza", "Healthy", "Salad", "Pasta",
        "Bakery", "Vietnamese", "Burgers", "Chicken", "Wings", "Japanese", "Soup", "Korean", "Indian", "Pacific Rim", "Vegetarian", "Thai", "Dim Sum", "Southern"};

    LogClass() {
    }

    LogClass(String fileName) {
        logFile = new File(fileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(LogClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        idCount = getLastLine();

    }

    public void createRandLog() {
        String x, y, r, k, o, ka;

        for (int i = 1; i <= I; i++) {
            x = randomDoubleGen(lX, hX, 100000, 999999);
            y = randomDoubleGen(lΥ, hY, 100000, 999999);
            r = randomInt(10000, 100000);
            k = randomInt(1, 5);
            o = randomInt(1, 2);
            int j = Integer.parseInt(o);
            String[] keys = new String[j];
            for (int w = 0; w < j; w++) {
                ka = randomInt(0, 56);
                int u = Integer.parseInt(ka);

                keys[w] = keywords[u];

            }
            writeToLog(x, y, r, k, keys);

        }

    }

    public String randomInt(int min, int max) {

        int a = ThreadLocalRandom.current().nextInt(min, max + 1);
        String s = String.valueOf(a);
        return s;
    }

    public String randomDoubleGen(int min1, int max1, int min2, int max2) {

        int randomNum = ThreadLocalRandom.current().nextInt(min1, max1 + 1);

        int randomNum2 = ThreadLocalRandom.current().nextInt(min2, max2 + 1);
        String s1 = String.valueOf(randomNum);
        String s2 = String.valueOf(randomNum2);
        String fnl = s1 + "." + s2;

        return fnl;

    }

    public int getLastLine() {

        String last, line;
        int cnt = 1;

        try {
            BufferedReader input = new BufferedReader(new FileReader(logFile));
            while ((line = input.readLine()) != null) {
                cnt++;

            }
        } catch (IOException ex) {
            Logger.getLogger(LogClass.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cnt;
    }

    public void writeToLog(String x, String y, String radius, String k, String[] keywords) {
        BufferedWriter bw = null;
        String id = String.valueOf(idCount);
        String newLine = id + "|" + x + "|" + y + "|" + radius + "|" + k + "|" + Arrays.toString(keywords) + "\n";
        try {

            FileWriter fw = new FileWriter(logFile, true);
            bw = new BufferedWriter(fw);
            idCount++;
            bw.write(newLine);
            System.out.println("File added to logfile");

        } catch (IOException ioe) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                System.out.println("Error in closing the BufferedWriter" + ex);
            }
        }

    }

}
