package gorI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFasta {

    public static ArrayList<Sequence> read(String path) {

        String fileName = path;

        File file = new File(fileName);

        ArrayList<Sequence> sequences = new ArrayList<>();

        if (!file.canRead() || !file.isFile())
            System.exit(0);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fileName));
            String zeile = null;

            String titel = "";
            String seq = "";
            String secStructure = "";

            while ((zeile = in.readLine()) != null) {
                //System.out.println(zeile);

                if (zeile.startsWith(">")) {

                    if (!titel.equals("")) {
                        sequences.add(new Sequence(seq, secStructure, titel));
                        seq = "";
                    }

                    titel = zeile.replace(">", "").strip();

                } else {
                    seq += zeile.strip();
                }
            }

            sequences.add(new Sequence(seq, secStructure, titel));
            /*
            for (Sequence s : sequences) {
                System.out.println(s.titel);
                System.out.println(s.seq);
                System.out.println(s.secStructure);
            }
             */

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }

        for (Sequence seq : sequences) {
            //System.out.println(seq.titel);
            //System.out.println(seq.seq);
        }

        return sequences;
    }

    /*
    public static void main(String[] args) {
        read("C:/Users/hanna/Desktop/CB513.fasta");
    }
     */

}
