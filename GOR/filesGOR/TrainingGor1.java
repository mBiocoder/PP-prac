package gorI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TrainingGor1 extends GOR_Training {

    HashMap<String, int[]> E_temp;
    HashMap<String, int[]> C_temp;
    HashMap<String, int[]> H_temp;

    ArrayList<Sequence> trainingSet;

    public TrainingGor1() {

        this.E_temp = createEmptyMatrix();
        this.C_temp = createEmptyMatrix();
        this.H_temp = createEmptyMatrix();

        this.trainingSet = new ArrayList<>();

    }


    //Methode, mit deren Hilfe sequentiell die Count-Matrizen erstellt wird --> evtl. ueberarbeiten
    public void addSequenceToTrainingSet(Sequence seq) {

        trainingSet.add(seq);
        countToHM(seq);

    }

    // gibt eine HashMap auf der Konsole aus
    public void printMatrix(String ehc) {

        HashMap<String, int[]> hm;
        switch (ehc){
            case "C_temp" -> hm = this.C_temp;
            case "E_temp" -> hm = this.E_temp;
            case "H_temp" -> hm = this.H_temp;
            default -> hm = this.H_temp;
        }

        String headRow = ehc;
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

    /* Gegeben:
     *   - eine Sequence mit AS-Sequenz und sek. Struktur
     *   - eine Position, der als zentral betrachteten AS
     *   --> Methode, um die Count-basierten Matrizen anzufertigen
     */
    public void countToHM(Sequence seq) {

        //int indexStart = indexCenter-8;
        //int indexEnd = indexCenter + 8;

        for (int startIndex = 0; startIndex <= seq.seq.length()-17; startIndex++) {
            String ssCenter = String.valueOf(seq.secStructure.charAt(startIndex + 8));
            String asCenter = String.valueOf(seq.seq.charAt(startIndex + 8));

            //if (!E_temp.keySet().contains(asCenter))
                //continue;

            for (int i = startIndex; i < 17+startIndex; i++) {

                String currentAS = String.valueOf(seq.seq.charAt(i));

                if (!E_temp.keySet().contains(currentAS))
                    continue;

                switch (ssCenter) {
                    case "C" -> this.C_temp.get(currentAS)[i-startIndex] += 1;
                    case "E" -> this.E_temp.get(currentAS)[i-startIndex] += 1;
                    case "H" -> this.H_temp.get(currentAS)[i-startIndex] += 1;
                }
            }
        }
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

        return str.toString();

    }

    //Schreibt Count-Matrizen in TSV-Datei fuer nachfolgenden Predict-Schritt
    public void writeToTSV(String path) throws IOException{

        FileWriter fw=new FileWriter(path); //dynamisches Festlegen des Speicherverzeichnisses prinzipiell moeglich

        fw.write("// Matrix3D\n\n");
        fw.write(matricesToString(this.C_temp,"C"));
        fw.write("\n\n");
        fw.write(matricesToString(this.E_temp,"E"));
        fw.write("\n\n");
        fw.write(matricesToString(this.H_temp,"H"));

        fw.close();

    }

}

