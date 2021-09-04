package gorI;

import java.util.HashMap;

public class WriteToTSV {

    /*
     * wandelt eine Matrix in einen Tab seperierten String um, der in eine tsv Tabelle geschrieben werden kann
     * */

    public static String matricesToString(HashMap<String, double[]> matrix, String titelMatrix) {

        StringBuilder str = new StringBuilder(">");
        str.append(titelMatrix);
        str.append("\n");

        for (String s : matrix.keySet()) {

            str.append(s);

            for (double i : matrix.get(s)) {


                str.append("\t");
                str.append(i);

            }
            str.append("\n");

        }

        return str.toString();

    }

    /*
    public void writeToTSV(String path) throws IOException {

        FileWriter fw = new FileWriter(new File( "C:\\Users\\leona\\OneDrive\\Desktop\\Blatt3\\GOR\\Model_Output.tsv")); //dynamisches Festlegen des Speicherverzeichnisses prinzipiell moeglich

        fw.write(matricesToString(this.E, "E"));
        fw.write(matricesToString(this.C, "C"));
        fw.write(matricesToString(this.H, "H"));

        fw.close();

    }*/

}
