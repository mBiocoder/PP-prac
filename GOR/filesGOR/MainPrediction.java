package gorI;

//--probabilities true --format "txt" --seq "C:/Users/hanna/Desktop/CB513.fasta" --model "C:/Users/hanna/Desktop/model_output.tsv"

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MainPrediction {
    /*
     * Geparste Parameter werden in einer HashMap als (key, value) Paare gespeichert (z.B. key = "-int", value = "10"
     * Set = ungeordnete Menge von Elementen
     * HashSets sind viel effizienter als Listen oder ArrayListen (z.B. bei der .contains() Operation)
     */
    private HashMap<String, String> parameterMap;
    private HashSet<String> allowedOptions;
    private HashSet<String> obligatoryOptions;

    /*
     * Instanzvariablen fuer die Erweiterung
     */
    private HashSet<String> intOptions;
    private HashSet<String> doubleOptions;
    private HashSet<String> fileOptions;

    /*
     * Parser-Konstruktor
     * Wird der Typangabe in der Methodendeklaration ein "..." nachgestellt,
     * so kann man der Methode ene variable Anzahl an Parameter übergeben.
     * Diese Parameter werden in einem Array gespeichert.
     * @param allowedOptions ist vom Typ String[]
     * Beispielaufruf: new CmdParser("-a", "-c", "hallo", "42");
     *                      Der Wert vom allowedOptions[0] ist "-a".
     */
    public MainPrediction(String...allowedOptions) {
        this.allowedOptions = new HashSet<>();
        this.allowedOptions.addAll(Arrays.asList(allowedOptions));
        this.obligatoryOptions = new HashSet<>();
        this.parameterMap = new HashMap<>();
        this.intOptions = new HashSet<>();
        this.doubleOptions = new HashSet<>();
        this.fileOptions = new HashSet<>();
    }
    /*
     * @param options (ist ein String[]) wird in der obligatoryOptionsMenge gespeichert
     */
    public void setObligatoryOptions(String...options) {
        this.obligatoryOptions.addAll(Arrays.asList(options));
    }

    /*
     * Diese Methode parst die Kommandozeilenparameter aus dem String Array
     * @param args und speichert sie in der HashMap parameterMap.
     */
    public void parseArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (!allowedOptions.contains(args[i])) {
                    throw new RuntimeException("Parameter not allowed: " + args[i]);
                } else if (i + 1 == args.length) {
                    throw new RuntimeException("Last parameter has no value: " + args[i]);
                } else if (args[i + 1].startsWith("--")) {
                    throw new RuntimeException("Parameter without value: " + args[i]);
                } else if (parameterMap.containsKey(args[i])) {
                    throw new RuntimeException("Parameter already in map: " + args[i]);
                } else {
                    /*
                     * in der Map werden (key, value Paare gespeichert
                     * (z.B. "-double 4.5 -mandatory test" als ("-double", "4.5"), ("-mandatory", "test"))
                     * key = Argument, was mit "-" anfaengt ("-double")
                     * value = das nächste Wort ("4.5"), durch ++i wird gleichzeitig auch der Index i erhoeht
                     */
                    parameterMap.put(args[i], args[++i]);
                }
            } else {
                throw new RuntimeException("Arguments have to start wit '--'");
            }
        }
        /*
         * Testen ob alle obligatorischen Options enthalten sind
         */
        for (String option : obligatoryOptions) {
            if (!parameterMap.containsKey(option)) {
                throw new RuntimeException("Obligatory option missing: " + option);
            }
        }
    }

    /**
     * Festlegen, welche Option vom Typ int (bzw. double oder File) sein dürfen
     */
    public void setInt(String...intOptions) {
        this.intOptions.addAll(Arrays.asList(intOptions));
    }

    public void setDouble(String...doubleOptions) {
        this.doubleOptions.addAll(Arrays.asList(doubleOptions));
    }

    public void setFile(String...fileOptions) {
        this.fileOptions.addAll(Arrays.asList(fileOptions));
    }

    /**
     * @parem option: key in der HashMap (z.B. "-double")
     * @return String value aus der HashMap (z.B. "4.5")
     */
    public String getValue(String option) {
        if (!parameterMap.containsKey(option)) {
            return null;
        } else {
            return parameterMap.get(option);
        }
    }

    /**
     * Gibt den Wert einer intOption aus der HashMap zurück.
     * Falls inOption nicht als int angegeben wurd (durch Methode setInt(...)) wird eine Exception geworfen
     * @return Wert ist kein String, sondern wird zum Integer geparst
     */
    public Integer getInt(String option) {
        if (!parameterMap.containsKey(option)) {
            return null;
        } else if (intOptions.contains(option)) {
            return Integer.parseInt(getValue(option));
        } else {
            throw new RuntimeException("Option value is not an Integer: " + option);
        }
    }

    public Double getDouble(String option) {
        if (!parameterMap.containsKey(option)) {
            return null;
        } else if (doubleOptions.contains(option)) {
            return Double.parseDouble(getValue(option));
        } else {
            throw new RuntimeException("Option value is not a Double "+ option);
        }
    }

    public File getFile(String option) {
        if (!parameterMap.containsKey(option)) {
            return null;
        } else if (fileOptions.contains(option)) {
            return new File(getValue(option));
        } else {
            throw new RuntimeException("option value is not a File: " + option);
        }
    }


    public static void main(String[] args) throws IOException {

        MainTraining parser = new MainTraining("--format", "--seq", "--model", "--maf", "--probabilities");
        parser.setObligatoryOptions("--format");
        parser.setObligatoryOptions("--model");

        parser.parseArguments(args);

        String format = parser.getValue("--format");
        String seq = parser.getValue("--seq");
        String model = parser.getValue("--model");
        String maf = parser.getValue("--maf");
        String probabilties = parser.getValue("--probabilities");

        //System.out.println("Format: " + format);
        //System.out.println("Seq: " + seq);
        //System.out.println("Model: " + model);
        //System.out.println("Maf: " + maf);
        //System.out.println("Probabilities: " + probabilties);

        int gor_version = ReadModel.gorVersion(model);
        //System.out.println(ReadModel.gorVersion(model));

        if (gor_version == 1) {
            if (seq != null) {

                PredictionGor1 p = new PredictionGor1(seq, model);

                p.computePredMatrix();

                for (Sequence s : p.sequences) {
                    if (s.seq.length() >= 17) {
                        s = p.predict(s);
                    } else {
                        System.out.println("> " + s.titel);
                        System.out.println("AS " + s.seq);
                        System.out.print("PS ");
                        for (int i = 0; i < s.seq.length(); i++) {
                            System.out.print("-");
                        }
                        System.out.println("");
                        System.exit(0);
                    }
                }


                //System.out.println(seq);
                //System.out.println(Arrays.toString(seq.split(".fasta")));
                String path = seq.split(".fasta")[0];
                path += "_prediction";
                if (format.equals("txt"))
                    path += ".prd";
                else
                    path += ".html";
                p.writeToTxt(path, false);

                //p.printMatrix(p.model_computed.get("E"), "E");

            }
        } else if (gor_version == 3) {

            if (seq != null) {

                PredictionGor3 p = new PredictionGor3(seq, model);

                p.computePredMatrix();

                for (Sequence s : p.sequences) {
                    if (s.seq.length() >= 17) {
                        s = p.predict(s);
                    } else {
                        System.out.println("> " + s.titel);
                        System.out.println("AS " + s.seq);
                        System.out.print("PS ");
                        for (int i = 0; i < s.seq.length(); i++) {
                            System.out.print("-");
                        }
                        System.out.println("");
                        System.exit(0);
                    }
                }


                String path = seq.split(".fasta")[0];
                path += "_prediction";
                if (format.equals("txt"))
                    path += ".prd";
                else
                    path += ".html";
                p.writeToTxt(path, false, p.sequences);

            }

                //p.printMatrix(p.model_computed.get("E"), "E");

        } else if (gor_version == 4) {

            if (seq != null) {

                PredictionGor4 p = new PredictionGor4(seq, model);

                p.computePredMatrix();

                for (Sequence s : p.sequences) {
                    if (s.seq.length() >= 17) {
                        s = p.predict(s);
                    } else {
                        System.out.println("> " + s.titel);
                        System.out.println("AS " + s.seq);
                        System.out.print("PS ");
                        for (int i = 0; i < s.seq.length(); i++) {
                            System.out.print("-");
                        }
                        System.out.println("");
                        System.exit(0);
                    }
                }


                String path = seq.split(".fasta")[0];
                path += "_prediction";
                if (format.equals("txt"))
                    path += ".prd";
                else
                    path += ".html";
                p.writeToTxt(path, false, p.sequences);
            }


        }

    }
}
