package gorI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class OpenDbFile {

    public ArrayList<Sequence> read(String path) {

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
                    }

                    titel = zeile.replace(">", "").strip();

                } else if (zeile.startsWith("AS ")) {
                    seq = zeile.replace("AS ", "").strip();

                } else if (zeile.startsWith("SS ")) {
                    secStructure = zeile.replace("SS ", "").strip();
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

        return sequences;
    }

    /*
    public static void main(String[] args) {
        OpenDbFile o = new OpenDbFile();
        //o.read();
    }
     */

}
