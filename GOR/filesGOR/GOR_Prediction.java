package gorI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GOR_Prediction {

    //berechnet auf Basis der Count-Matrix die Prediction-Werte
    public HashMap<String, HashMap<String, double[]>> computePredMatrix(HashMap<String, HashMap<String, double[]>> hm) {

        //Sollten am besten Integer-Werte sein!!
        HashMap<String, Double> secStructureFreq = new HashMap<>();
        secStructureFreq.put("H", 0.0);
        secStructureFreq.put("E", 0.0);
        secStructureFreq.put("C", 0.0);

        //!!!! Morgen ueberpruefen
        //fuer die einzelnen Matrizen (E, C, H) wird auf Basis der jeweiligen mittleren Matrixpalte die Summe der Sekundaerstrukturen berechnet

        for (String secStr: hm.keySet()) {
            for (String aa: hm.get(secStr).keySet()) {
                secStructureFreq.put(secStr, secStructureFreq.get(secStr) + hm.get(secStr).get(aa)[8]);
                //System.out.println(secStructureFreq.get(secStr) + model.get(secStr).get(aa)[8]);
            }

        }

        //System.out.println("H: " + secStructureFreq.get("H"));
        //System.out.println("C: " + secStructureFreq.get("C"));
        //System.out.println("E: " + secStructureFreq.get("E"));

        count2final(secStructureFreq, hm);

        return hm;
    }


    public HashMap<String, HashMap<String, double[]>> count2final(HashMap<String, Double> secStructureFreq, HashMap<String, HashMap<String, double[]>> model) {

        HashMap<String, double[]> E;
        HashMap<String, double[]> C;
        HashMap<String, double[]> H;

        HashMap<String, HashMap<String, double[]>> model_computed = new HashMap<>();

        // leer 20 x 17 Matrizen die die umgerechneten Werte des Models enthalten sollen
        E = createEmptyMatrix();
        C = createEmptyMatrix();
        H = createEmptyMatrix();

        // Matrizen befuellen:

        // E
        for (String str : model.get("E").keySet()) {
            //System.out.println(str);

            for (int i = 0; i < 17; i++) {
                double a = model.get("E").get(str)[i];
                double b = model.get("C").get(str)[i] + model.get("H").get(str)[i];

                //System.out.println("a: " + ((double) a) + "  b: " + b);

                //System.out.println(this.secStructureHM);
                double ergebnis = Math.log(((double) a) / ((double) b)) + (Math.log(((double) secStructureFreq.get("H") + ((double) secStructureFreq.get("C"))) / ((double) secStructureFreq.get("E"))));

                //System.out.println(ergebnis);

                E.get(str)[i] = ergebnis;
            }
        }
        model_computed.put("E", E);

        // HELIX
        for (String str : model.get("H").keySet()) {
            for (int i = 0; i < 17; i++) {
                double a = model.get("H").get(str)[i];
                double b = model.get("E").get(str)[i] + model.get("C").get(str)[i];

                //System.out.println("a: " + ((double) a) + "  b: " + b);

                //System.out.println(this.secStructureHM);
                double ergebnis = Math.log(((double) a) / ((double) b)) + Math.log(((double) secStructureFreq.get("E") + (double) secStructureFreq.get("C")) / ((double) secStructureFreq.get("H")));

                //System.out.println(ergebnis);

                H.get(str)[i] = ergebnis;

            }
        }

        model_computed.put("H", H);

        // C
        for (String str : model.get("C").keySet()) {
            for (int i = 0; i < 17; i++) {
                double a = model.get("C").get(str)[i];
                double b = model.get("H").get(str)[i] + model.get("E").get(str)[i];

                //System.out.println("a: " + ((double) a) + "  b: " + b);

                double ergebnis = Math.log(((double) a) / ((double) b)) + Math.log(((double) secStructureFreq.get("H") + (double) secStructureFreq.get("E")) / ((double) secStructureFreq.get("C")));

                //System.out.println(ergebnis);

                C.get(str)[i] = ergebnis;
            }
        }
        model_computed.put("C", C);

        return model_computed;
    }


    //ok
    double normalizeValues(double value, double min, double max) {
        return ((value - min) / (max - min)) * 9;
    }

    public HashMap<String, double[]> createEmptyMatrix() {

        String[] aminoAcidArray = {"A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V"};

        HashMap<String, double[]> matrixASvSSe = new HashMap<>();

        for (String aAA : aminoAcidArray) {

            double[] rows = new double[17];

            for (int i = 0; i < 17; i++) {
                rows[i] = 0;
            }

            matrixASvSSe.put(aAA, rows);

        }

        return matrixASvSSe;
    }

    // gibt eine HashMap auf der Konsole aus
    public void printMatrix(HashMap<String, double[]> hm, String titel) {


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

            double[] value = hm.get(s);

            for (double a : value) {

                line += String.valueOf(a);
                line += "\t";
            }
            System.out.println(line);

        }
        System.out.println("");

    }

    public void writeToTxt(String path, boolean probabilities, ArrayList<Sequence> sequences) throws IOException {

        //FileWriter fw = new FileWriter(path); //dynamisches Festlegen des Speicherverzeichnisses prinzipiell moeglich

        for (Sequence s : sequences) {

            System.out.println("> " + s.titel);
            System.out.println("AS " + s.seq);
            System.out.print("PS " + s.predictedSecStructure + "\n");
            if (!(s.secStructure == null || s.secStructure == "")) {
                System.out.print("RS " + s.secStructure + "\n");
            }
            if (probabilities) {
                System.out.print("PH " + s.pH + "\n");
                System.out.print("PE " + s.pE + "\n");
                System.out.print("PC " + s.pC + "\n");
            }


        }
        //fw.close();
    }

}
