package gorI;

import java.util.HashMap;

public class TrainingGor3 extends GOR_Training {

    // key: Aminosaeure, Struktur value: HashMap mit key AS & value: count Werte
    HashMap<String, HashMap<String, int[]>> model;

    public TrainingGor3() {
        this.model = new HashMap<>();

        String[] possibleAS = {"A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V"};
        String[] possibleSS = {"E", "C", "H"};

        for (String as : possibleAS) {

            for (String ss : possibleSS) {

                String key = (as + "," + ss);

                model.put(key, createEmptyMatrix());
            }
        }
    }


    public void countToHM(Sequence seq) {

        //int indexStart = indexCenter-8;
        //int indexEnd = indexCenter + 8;

        for (int startIndex = 0; startIndex <= seq.seq.length()-17; startIndex++) {

            String asCenter = String.valueOf(seq.seq.charAt(startIndex + 8));
            String ssCenter = String.valueOf(seq.secStructure.charAt(startIndex + 8));


            String key = asCenter + "," + ssCenter;

            if (!model.keySet().contains(key))
                continue;

            for (int i = startIndex; i < 17+startIndex; i++) {

                String currentAS = String.valueOf(seq.seq.charAt(i));
                if (model.get(key).keySet().contains(currentAS))
                    model.get(key).get(currentAS)[i-startIndex] += 1;
                else continue;
            }
        }
    }







    public static void main(String[] args) {

        TrainingGor3 t3 = new TrainingGor3();
        int count = 0;

        /*
        for (String s : t3.model.keySet()) {

            t3.printMatrix(t3.model.get(s), s);
            count++;
        }

         */

        System.out.println("Matrizen: " + count);

        t3.countToHM(new Sequence("AAABBBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE","bla"));

        for (String s : t3.model.keySet()) {

            t3.printMatrix(t3.model.get(s), s);
            count++;
        }

    }


}
