package gorI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class GOR_Training {

    // erstellt eine leere Matrix von Position -8 bis +8 fuer alle Aminosaeuren
    public HashMap<String, int[]> createEmptyMatrix() {

        String[] aminoAcidArray = {"A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V"};

        HashMap<String, int[]> matrixASvSSe = new HashMap<>();

        for (String aAA : aminoAcidArray) {

            int[] rows = new int[17];

            for (int i = 0; i < 17; i++) {
                rows[i] = 0;
            }

            matrixASvSSe.put(aAA, rows);

        }

        return matrixASvSSe;
    }


    // gibt eine HashMap auf der Konsole aus
    public void printMatrix(HashMap<String, int[]> hm, String titel) {


        String headRow = titel;
        headRow += " |\t\t";
        for (int i = -8; i <= 8; i++) {
            headRow += String.valueOf(i);
            headRow += "\t\t";
        }
        System.out.println(headRow);
        System.out.println("--+-------------------------------------------------------------------------------------------------------------------------------------------");

        for (String s : hm.keySet()) {

            String line = s;
            line += " |";
            line += "\t\t";

            int[] value = hm.get(s);

            for (int a : value) {

                line += String.valueOf(a);
                line += "\t";
            }
            System.out.println(line);

        }
        System.out.println("");

    }


    /*
     * wandelt eine Matrix in einen Tab seperierten String um, der in eine tsv Tabelle geschrieben werden kann
     * */
    public String matricesToString(HashMap<String, int[]> matrix, String titelMatrix) {

        StringBuilder str = new StringBuilder("=");
        str.append(titelMatrix);
        str.append("=");
        str.append("\n\n");

        for (String s : matrix.keySet()) {

            str.append(s);

            for (int i : matrix.get(s)) {

                str.append("\t");
                str.append(i);

            }
            str.append("\n");

        }
        str.append("\n\n");

        return str.toString();

    }

    //Schreibt Count-Matrizen in TSV-Datei fuer nachfolgenden Predict-Schritt
    public void writeToTSV(String path, HashMap<String, HashMap<String, int[]>> hm, String version) throws IOException {

        FileWriter fw=new FileWriter(path); //dynamisches Festlegen des Speicherverzeichnisses prinzipiell moeglich

        String head = "";
        switch (version) {
            case "gor3" -> head = "Matrix4D";
            case "gor4" -> head = "Matrix6D";
        }

        fw.write("// " + head +"\n\n");

        SortedSet<String> keys = new TreeSet<>(hm.keySet());
        for (String key : keys) {
            fw.write(matricesToString(hm.get(key), key));
        }

        /*
        for (String key : hm.keySet()) {
            fw.write(matricesToString(hm.get(key), key));
        }
        fw.write("\n\n");

         */

        fw.close();

    }


}
