package gorI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PredictionGor1 extends GOR_Prediction {

    // enthaelt die vorherzusagenden Sequenzen
    ArrayList<Sequence> sequences;

    // enthaelt die Matrizen
    HashMap<String, HashMap<String, double[]>> model; //enthaelt eingelesene Count-Matrix
    HashMap<String, HashMap<String, double[]>> model_computed = new HashMap<>(); //enthaelt Prediction-Matrix
    HashMap<String, Double> secStructureFreq = new HashMap<>();

    public PredictionGor1(String pathFasta, String pathModel) {
        this.sequences = ReadFasta.read(pathFasta); // .fasta einlesen
        this.model = ReadModel.read(pathModel);     // Module aus Training einlesen
    }

    //berechnet auf Basis der Count-Matrix die Prediction-Werte
    public void computePredMatrix() {

        //Sollten am besten Integer-Werte sein!!

        secStructureFreq.put("H", 0.0);
        secStructureFreq.put("E", 0.0);
        secStructureFreq.put("C", 0.0);
        secStructureFreq.put("total", 0.0);

        //!!!! Morgen ueberpruefen
        //fuer die einzelnen Matrizen (E, C, H) wird auf Basis der jeweiligen mittleren Matrixpalte die Summe der Sekundaerstrukturen berechnet

        for (String secStr: model.keySet()) {
            for (String aa: model.get(secStr).keySet()) {
                secStructureFreq.put(secStr, secStructureFreq.get(secStr) + model.get(secStr).get(aa)[8]);
                secStructureFreq.put("total", secStructureFreq.get("total") + model.get(secStr).get(aa)[8]);
                //System.out.println(secStructureFreq.get(secStr) + model.get(secStr).get(aa)[8]);
            }

        }

        //System.out.println("H: " + secStructureFreq.get("H"));
        //System.out.println("C: " + secStructureFreq.get("C"));
        //System.out.println("E: " + secStructureFreq.get("E"));

        count2final(secStructureFreq);

    }

    public Sequence predict(Sequence sequence) {

        // Die HashMap speichert fuer die drei Sekundaerstrukturen die Wahrscheinlichkeit fuer jede Position der Sequenz
        HashMap<String, double[]> output = new HashMap<>();
        output.put("E", new double[sequence.seq.length()]);
        output.put("H", new double[sequence.seq.length()]);
        output.put("C", new double[sequence.seq.length()]);

        // Durchlaufen aller Fenster der Sequenz
        for (int i = 0; i < sequence.seq.length() - 16; i++) {

            // Durchlaufen aller Sekundaerstrukturen
            for (String secStru : model_computed.keySet()) {

                // Durchlauf durch das Fenster der entsprechenden Sekundaerstruktur
                for (int j = 0; j <= 16; j++) {

                    if (model_computed.get(secStru).keySet().contains(String.valueOf(sequence.seq.charAt(j + i))))
                        output.get(secStru)[i + 8] += model_computed.get(secStru).get(String.valueOf(sequence.seq.charAt(j + i)))[j];

                }

            }

        }


        for (String ss : output.keySet()) {

            for (int i = 0; i < output.get(ss).length; i++) {

                double d = output.get(ss)[i];

                output.get(ss)[i] = (Math.exp(d) * ( secStructureFreq.get(ss) / (secStructureFreq.get("total") - secStructureFreq.get(ss)))) / (1 + Math.exp(d) * ( secStructureFreq.get(ss) / (secStructureFreq.get("total") - secStructureFreq.get(ss))));

            }

        }




        String finalSecStructure = "--------";

        for (int k = 8; k < sequence.seq.length() - 8; k++) {

            double max_column = Integer.MIN_VALUE;
            String max_structure = "?";

            for (String secStru : output.keySet()) {

                if (output.get(secStru)[k] > max_column) {
                    max_column = output.get(secStru)[k];
                    max_structure = secStru;

                }

            }

            finalSecStructure += max_structure;

        }
        finalSecStructure += "--------";
        //System.out.println(sequence.titel);
        //System.out.println(sequence.seq);
        //System.out.println(finalSecStructure);
        //System.out.println(sequence.seq.length() + " " + finalSecStructure.length());

        sequence.predictedSecStructure = finalSecStructure;

        // max und min Wert in Output
        double max = Integer.MIN_VALUE;
        double min = Integer.MAX_VALUE;
        for (String o : output.keySet()) {

            for (double d : output.get(o)) {

                if (d > max) {
                    max = d;
                } else if (d < min) {
                    min = d;
                }

            }

        }
        //System.out.println("Maximum: " + max + "   Minimum: " + min);

        //Normalisierung

        for (String o : output.keySet()) {

            for (int i = 0; i < output.get(o).length; i++) {

                output.get(o)[i] = normalizeValues(output.get(o)[i], min, max);

            }

        }




        for (String secStru : output.keySet()) {
            //System.out.println(secStru);
            //System.out.println(Arrays.toString(output.get(secStru)));
            String probability = "--------";
            for (int i = 8; i < output.get(secStru).length - 8; i++) {
                probability += Math.round(output.get(secStru)[i]);
            }
            probability += "--------";

            switch (secStru) {
                case "H" -> sequence.pH = probability;
                case "E" -> sequence.pE = probability;
                case "C" -> sequence.pC = probability;
            }
            //System.out.println(sequence.pH);
            //System.out.println(sequence.pE);
            //System.out.println(sequence.pC);
        }

        return sequence;

    }

    public void count2final(HashMap<String, Double> secStructureFreq) {

        HashMap<String, double[]> E;
        HashMap<String, double[]> C;
        HashMap<String, double[]> H;

        // leer 20 x 17 Matrizen die die umgerechneten Werte des Models enthalten sollen
        E = createEmptyMatrix();
        C = createEmptyMatrix();
        H = createEmptyMatrix();

        // Matrizen befuellen:

        // E
        for (String str : model.get("E").keySet()) {
            //System.out.println(str);

            for (int i = 0; i < 17; i++) {
                double a = this.model.get("E").get(str)[i];
                double b = this.model.get("C").get(str)[i] + this.model.get("H").get(str)[i];

                //System.out.println("a: " + ((double) a) + "  b: " + b);

                //System.out.println(this.secStructureHM);
                double ergebnis = Math.log(((double) a) / ((double) b)) + (Math.log(((double) secStructureFreq.get("H") + ((double) secStructureFreq.get("C"))) / ((double) secStructureFreq.get("E"))));

                //ergebnis = (Math.exp(ergebnis) / (Math.exp(ergebnis) + 1));

                //System.out.println(ergebnis);

                E.get(str)[i] = ergebnis;
            }
        }
        model_computed.put("E", E);

        // HELIX
        for (String str : model.get("H").keySet()) {
            for (int i = 0; i < 17; i++) {
                double a = this.model.get("H").get(str)[i];
                double b = this.model.get("E").get(str)[i] + this.model.get("C").get(str)[i];

                //System.out.println("a: " + ((double) a) + "  b: " + b);

                //System.out.println(this.secStructureHM);
                double ergebnis = Math.log(((double) a) / ((double) b)) + Math.log(((double) secStructureFreq.get("E") + (double) secStructureFreq.get("C")) / ((double) secStructureFreq.get("H")));

                //ergebnis = (Math.exp(ergebnis) / (Math.exp(ergebnis) + 1));

                //System.out.println(ergebnis);

                H.get(str)[i] = ergebnis;

            }
        }

        model_computed.put("H", H);

        // C
        for (String str : model.get("C").keySet()) {
            for (int i = 0; i < 17; i++) {
                double a = this.model.get("C").get(str)[i];
                double b = this.model.get("H").get(str)[i] + this.model.get("E").get(str)[i];

                //System.out.println("a: " + ((double) a) + "  b: " + b);

                double ergebnis = Math.log(((double) a) / ((double) b)) + Math.log(((double) secStructureFreq.get("H") + (double) secStructureFreq.get("E")) / ((double) secStructureFreq.get("C")));

                //ergebnis = (Math.exp(ergebnis) / (Math.exp(ergebnis) + 1));

                //System.out.println(ergebnis);

                C.get(str)[i] = ergebnis;
            }
        }
        model_computed.put("C", C);
    }

    //ok
    double normalizeValues(double value, double min, double max) {
        return ((value - min) / (max - min)) * 9;
    }

    //ok
    public void writeToTxt(String path, boolean probabilities) throws IOException {

        //FileWriter fw = new FileWriter(path); //dynamisches Festlegen des Speicherverzeichnisses prinzipiell moeglich

        for (Sequence s : sequences) {

            System.out.print("> " + s.titel + "\n");
            System.out.print("AS " + s.seq + "\n");
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


}

