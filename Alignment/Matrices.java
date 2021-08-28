import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;

public class Matrices {
    //scoringmatrix, tracebackmatrix

    public static HashMap<Pair, Float> getscoreMatrices( String matrices){

        String[] asNames = {"A","R","N","D","C","Q","E","G","H","I","L","K","M","F","P","S","T","W","Y","V"};

        BufferedReader reader;
        HashMap<Pair, Float> saver = new HashMap<>();

        try {
            reader = new BufferedReader(new FileReader(matrices));
            // reader = new BufferedReader(new FileReader(substituitionmatrix));
            String line;

            int count = 0;

            while(( line = reader.readLine())!=null){

                line = line.replaceAll("      ","\t"); //6 whitespaces
                line = line.replaceAll("     ","\t"); //5 whitespaces, trim (strip)

                if (line.startsWith("MATRIX")){
                    String[] values = line.replace("MATRIX", "").split("\t");

                    List<String> lValues = new ArrayList<String>(Arrays.asList(values));
                    lValues.removeAll(Collections.singleton(""));

                    Float[] score = new Float[lValues.size()];

                    //values = Arrays.copyOfRange(values, 0, values.length);

                    for (int row = 0; row < lValues.size(); row++) {
                        score[row] = Float.parseFloat(lValues.get(row));
                    }

                    for (int row = 0; row < score.length; row++) {
                        Pair asPairs = new Pair(asNames[row], asNames[count]);
                        Pair asPairsRev = new Pair(asNames[count], asNames[row]);
                        saver.put(asPairs, score[row]);
                        saver.put(asPairsRev, score[row]);
                    }

                    count++;

                }
            }
            reader.close();
        }

        catch (IOException e){
            e.printStackTrace();
        }

        return saver;
    }


    /*public static void main(String[] args) {
        Matrices matricesObject = new Matrices();
        HashMap<Pair, Float> saver = matricesObject.getscoreMatrices();
        //System.out.println(Arrays.toString(saver.keySet()));
        saver.entrySet().stream().forEach(entry -> System.out.println(entry.getKey() + " " + entry.getValue()));
    }*/

}
