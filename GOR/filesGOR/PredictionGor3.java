package gorI;

import java.util.ArrayList;
import java.util.HashMap;

public class PredictionGor3 extends GOR_Prediction {

    // enthaelt die vorherzusagenden Sequenzen
    ArrayList<Sequence> sequences;

    // enthaelt die Matrizen
    HashMap<String, HashMap<String, double[]>> model; //enthaelt eingelesene Count-Matrix
    HashMap<String, HashMap<String, double[]>> model_computed = new HashMap<>(); //enthaelt Prediction-Matrix


    HashMap<String, Double> secStructureFreq = new HashMap<>();


    public PredictionGor3(String pathFasta, String pathModel) {
        this.sequences = ReadFasta.read(pathFasta); // .fasta einlesen
        this.model = ReadModel.read(pathModel);     // Module aus Training einlesen

        // model_computed mit leeren Werten befuellen
        String[] possibleAS = {"A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V"};
        String[] possibleSS = {"E", "C", "H"};
        for (String as : possibleAS) {
            for (String ss : possibleSS) {
                String key = as + "," + ss;
                this.model_computed.put(key, createEmptyMatrix());
            }
        }
        // jede Zelle aller Matrizen von model += 1

        for (String key : this.model.keySet()) {
            for (String row : this.model.get(key).keySet()) {
                for (int i = 0; i < this.model.get(key).get(row).length; i++) {
                    this.model.get(key).get(row)[i] += 0.01;
                }
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //berechnet auf Basis der Count-Matrix die Prediction-Werte
    public void computePredMatrix() {

        String[] possibleAS = {"A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V"};
        String[] possibleSS = {"E", "C", "H"};

        for (String as : possibleAS) {
            for (String ss : possibleSS) {
                String key = as + "," + ss;
                secStructureFreq.put(key, 0.0);
            }
        }
        secStructureFreq.put("total", 0.0);

        //!!!! Morgen ueberpruefen
        //fuer die einzelnen Matrizen (E, C, H) wird auf Basis der jeweiligen mittleren Matrixpalte die Summe der Sekundaerstrukturen berechnet

        // fuer jede Matrix aus dem Model
        for (String id: model.keySet()) {
            //System.out.println(secStr);

            // extrahieren der sekundaer Struktur
            //String sec = id.split(",")[1];

            // durchgehen durch alle Zeilen (aller Matrizen)
            for (String aa: model.get(id).keySet()) {
                //System.out.println(aa);

                // Die Anzahl der verschiedenen sek. strukturen werden gezaehlt / die Summe aller sek. Strukturen
                secStructureFreq.put(id, secStructureFreq.get(id) + model.get(id).get(aa)[8]);
                secStructureFreq.put("total", secStructureFreq.get("total") + model.get(id).get(aa)[8]);


                //System.out.println(secStructureFreq.get(secStr) + model.get(secStr).get(aa)[8]);
            }

        }

        //System.out.println("H: " + secStructureFreq.get("H"));
        //System.out.println("C: " + secStructureFreq.get("C"));
        //System.out.println("E: " + secStructureFreq.get("E"));

        count2final(secStructureFreq);

    }

    // berechnen der finalen Matrix auf basis der count Matrix
    public void count2final(HashMap<String, Double> secStructureFreq) {
        // Matrizen befuellen:
        String[] possibleAS = {"A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V"};

            // alle Matrizen, die zur AS gehoeren --> log odd
            for (String id : this.model.keySet()) {



                String as = id.split(",")[0];

                String secStructure = id.split(",")[1];

                for (String aa_zeile : possibleAS) {

                    for (int i = 0; i < 17; i++) {
                        double a = this.model.get(id).get(aa_zeile)[i];
                        //System.out.println("id: "+ id);
                        String b1 = "";
                        String b2 = "";
                        if (secStructure.equals("H")) {
                            b1 = "C";
                            b2 = "E";
                        } else if (secStructure.equals("C")) {
                            b1 = "H";
                            b2 = "E";
                        } else if (secStructure.equals("E")) {
                            b1 = "H";
                            b2 = "C";
                        }
                        double b = this.model.get(as + "," + b1).get(aa_zeile)[i] + this.model.get(as + "," + b2).get(aa_zeile)[i];

                        //System.out.println("a: " + a + "   b: " +b);
                        //System.out.println("secStructure: " + secStructure + "   b1: " + b1 + "   b2: " + b2);

                        double ergebnis = Math.log((a) / (b)) + (Math.log(((double) secStructureFreq.get(as + "," + b1) + ((double) secStructureFreq.get(as + "," + b2))) / ((double) secStructureFreq.get(id))));

                        //System.out.println("ergebnis: " + ergebnis);
                        //System.out.println("hm.get(as): " + hm.get(as)[i]);

                        //hm.get(as)[i] = ergebnis;
                        this.model_computed.get(id).get(aa_zeile)[i] = ergebnis;

                    }
                }
        }
    }



    public Sequence predict(Sequence sequence) {

        // Die HashMap speichert fuer die drei Sekundaerstrukturen die Wahrscheinlichkeit fuer jede Position der Sequenz
        HashMap<String, double[]> output = new HashMap<>();
        output.put("E", new double[sequence.seq.length()]);
        output.put("H", new double[sequence.seq.length()]);
        output.put("C", new double[sequence.seq.length()]);

        //printMatrix(output, "output");

        // Durchlaufen aller Sekundaerstrukturen
        String[] possible_sec_Structures = {"E", "C", "H"};
        for (String secStru : possible_sec_Structures/*model_computed.keySet()*/) {

            // Durchlaufen aller Fenster der Sequenz
            for (int i = 0; i < sequence.seq.length() - 16; i++) {

                //printMatrix(model_computed.get(secStru), secStru);
                //System.out.println(sequence.seq);

                String aa = String.valueOf(sequence.seq.charAt(i+8));

                // Durchlauf durch das Fenster der entsprechenden Sekundaerstruktur
                for (int j = 0; j <= 16; j++) {

                    String observed_AA = String.valueOf(sequence.seq.charAt(j + i));

                    String key = "";
                    if (model_computed.get("C,C").keySet().contains(aa)) {
                        key = aa + "," + secStru;

                        //if (! Double.isNaN(model_computed.get(key).get(observed_AA)[j]))
                        if (model_computed.get(key).keySet().contains(observed_AA))
                            output.get(secStru)[i + 8] += model_computed.get(key).get(observed_AA)[j];
                        //System.out.println(aa+ " "+observed_AA+" "+secStru+" "+(i+8) +": "+output.get(secStru)[i + 8]);
                        //printMatrix(model_computed.get(key), key);
                    }
                    //output.get(secStru)[i + 8] += 10;

                }

            }

        }


        //printMatrix(output, "output");

        /////////////////////////////////////////////////////////////////////////////////

        for (String ss : output.keySet()) {

            for (int i = 0; i < output.get(ss).length; i++) {

                double d = output.get(ss)[i];

                String ss2 = "";
                String ss3 = "";

                if (ss.equals("H")) {
                    ss2 = "E";
                    ss3 = "C";
                } else if (ss.equals("E")) {
                    ss2 = "H";
                    ss3 = "C";
                } else if (ss.equals("C")) {
                    ss2 = "H";
                    ss3 = "E";
                }
                String as = String.valueOf(sequence.seq.charAt(i));

                //System.out.println("d:" + d + "   SS: " + ss);



                if (model_computed.get("A,C").keySet().contains(as))
                    output.get(ss)[i] = ((Math.exp(d)) * (( secStructureFreq.get(as+","+ss) / (secStructureFreq.get(as+","+ss2) + secStructureFreq.get(as+","+ss3))))) / (1 + ((Math.exp(d)) * ( secStructureFreq.get(as+","+ss) / (secStructureFreq.get(as+","+ss2) + secStructureFreq.get(as+","+ss3)))));

                //System.out.println(output.get(ss)[i]);
            }
            //printMatrix(output, "output");
        }


        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        //printMatrix(output, "output");


        // Fall beruecksichtigen, dass AS Sequenz kuerzer als 17 ist


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

        //printMatrix(output, "output");

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



}
