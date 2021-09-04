package gorI;

import java.util.HashMap;

public class TrainingGor4 extends GOR_Training {

    HashMap<String, HashMap<String, int[]>> model = new HashMap<>();

    String[] possibleAS = {"A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V"};
    String[] possibleSS = {"E", "C", "H"};
    String[] possiblePos = {"-8", "-7","-6","-5","-4","-3","-2","-1","0","1","2","3","4","5","6","7","8"};


    public TrainingGor4() {
        this.model = new HashMap<>();

        for (String ss : possibleSS) {

            for (String as1 : possibleAS) {

                for (String as2 : possibleAS) {

                    for (String pos : possiblePos) {

                        String key = (ss + "," + as1 + "," + as2 + "," + pos);

                        model.put(key, createEmptyMatrix());

                    }
                }
            }
        }
    }


    public void countToHM(Sequence seq) {

        //int indexStart = indexCenter-8;
        //int indexEnd = indexCenter + 8;

        for (int startIndex = 0; startIndex <= seq.seq.length()-17; startIndex++) {

            String asCenter = String.valueOf(seq.seq.charAt(startIndex + 8));
            String ssCenter = String.valueOf(seq.secStructure.charAt(startIndex + 8));


            for (int i = startIndex; i < 17+startIndex; i++) {

                String as_i = String.valueOf(seq.seq.charAt(i));

                String key = ssCenter + "," + asCenter + "," + as_i + "," + String.valueOf(i-8-startIndex);

                if (!model.keySet().contains(key))
                    continue;

                for (int j = i+1; j < 17+startIndex; j++) {

                    String currentAS = String.valueOf(seq.seq.charAt(j));
                    if (model.get(key).keySet().contains(currentAS))
                        model.get(key).get(currentAS)[j-startIndex] += 1;
                    else continue;
                }
            }
        }
    }





    public static void main(String[] args) {
        TrainingGor4 g4 = new TrainingGor4();


        g4.countToHM(new Sequence("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV", "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE", "bla"));

        /*
        for (String key : g4.model.keySet()) {
            g4.printMatrix(g4.model.get(key), key);
        }
         */

        for (String s : g4.possiblePos) {
            g4.printMatrix(g4.model.get("E,V,V," + s), "E,V,V,"+s);
        }
        System.out.println(g4.model.size());
    }


}
