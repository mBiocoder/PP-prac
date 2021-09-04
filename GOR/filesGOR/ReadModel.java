package gorI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ReadModel {

    public static HashMap<String, HashMap<String, double[]>> read(String path) {

        String fileName = path;

        File file = new File(fileName);

        HashMap<String, HashMap<String, double[]>> model = new HashMap<>();

        if (!file.canRead() || !file.isFile())
            System.exit(0);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fileName));
            String zeile = null;

            String structure = "";

            HashMap<String, double[]> rows = new HashMap<>();

            while ((zeile = in.readLine()) != null) {
                //System.out.println(zeile);

                if (zeile.startsWith("//") || zeile.isEmpty() || zeile.equals(" ") || zeile.equals("\t") || zeile.equals("\n")) {
                    continue;
                }

                if (zeile.startsWith("=")) {

                    if (!structure.equals("")) {
                        // zu Hashmap hinzufuegen
                        model.put(structure, rows);

                        rows = new HashMap<>();
                    }


                    structure = String.valueOf(zeile.replace("=", ""));

                } else {
                    String[] arrLine = zeile.split("\t");
                    String aa = arrLine[0];
                    double[] values = new double[17];
                    for (int i = 1; i <= values.length; i++) {
                        values[i-1] = Double.parseDouble(arrLine[i]);
                    }

                    rows.put(aa, values);

                }

            }
            model.put(structure, rows);

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

        for (String secStru : model.keySet()) {

            //System.out.println(secStru);

            for (String aa : model.get(secStru).keySet()) {
                //System.out.println(aa);
                //System.out.println();
                //System.out.println(Arrays.toString(model.get(secStru).get(aa))); //--> double[]
            }
        }

        return model;
    }


    public static int gorVersion(String path) {

        int gor = 0;

        String fileName = path;

        File file = new File(fileName);

        if (!file.canRead() || !file.isFile())
            System.exit(0);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fileName));
            String zeile = null;

            while ((zeile = in.readLine()) != null) {

                if (zeile.startsWith("// ")) {
                    String inhalt = zeile.replace("// ", "").strip();

                    switch (inhalt) {
                        case "Matrix3D" -> gor = 1;
                        case "Matrix4D" -> gor = 3;
                        case "Matrix6D" -> gor = 4;
                        case "gor1" -> gor = 1;
                        case "gor3" -> gor = 3;
                        case "gor4" -> gor = 4;
                    }

                } else continue;

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
            }
        }

        return gor;
    }

    /*
    public static void main(String[] args) {
        read("C:\\Users\\leona\\OneDrive\\Desktop\\Blatt3\\GOR\\Model_Output.tsv");
    }
     */

}
