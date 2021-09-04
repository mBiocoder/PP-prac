package gorI;

// --db "C:/Users/hanna/Desktop/CB513DSSP.db" --method "gor1" --model "C:/Users/hanna/Desktop/model_output.tsv"

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MainTraining {
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
    public MainTraining(String...allowedOptions) {
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
                throw new RuntimeException("Arguments have to start with '--'");
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
        MainTraining parser = new MainTraining("--db", "--method", "--model");
        parser.setObligatoryOptions("--db");
        parser.setObligatoryOptions("--method");
        parser.setObligatoryOptions("--model");

        parser.parseArguments(args);

        String db = parser.getValue("--db");
        String method = parser.getValue("--method");
        String model = parser.getValue("--model");

        //System.out.println("DB: " + db);
        //System.out.println("Method: " + method);
        //System.out.println("Model: " + model);

        if (method.equals("gor1")) {

            TrainingGor1 training = new TrainingGor1();
            OpenDbFile o = new OpenDbFile();
            ArrayList<Sequence> seqAL = o.read(db);

            for (Sequence s : seqAL) {
                training.addSequenceToTrainingSet(s);
            }

            //training.printMatrix("E");
            //training.printMatrix("H");
            //training.printMatrix("C");

            training.writeToTSV(model);

        } else if (method.equals("gor3")) {

            TrainingGor3 training = new TrainingGor3();
            OpenDbFile o = new OpenDbFile();
            ArrayList<Sequence> seqAL = o.read(db);
            for (Sequence s : seqAL) {
                training.countToHM(s);
            }
            /*
            for (String s : training.model.keySet()) {

                training.printMatrix(training.model.get(s), s);

            }

             */

            training.writeToTSV(model, training.model, method);

        } else if (method.equals("gor4")) {

            TrainingGor4 training = new TrainingGor4();
            OpenDbFile o = new OpenDbFile();
            ArrayList<Sequence> seqAL = o.read(db);
            for (Sequence s : seqAL) {
                training.countToHM(s);
            }

            /*
            for (String key : training.model.keySet()) {
                training.printMatrix(training.model.get(key), key);
            }
             */

            training.writeToTSV(model, training.model, method);


        }
    }
}
