import org.apache.commons.cli.*;

public class Runner {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder()
                .longOpt("go")
                .hasArg(true)
                .desc("gap open penalty (default -12)")
                .required(false)
                .build());
        options.addOption(Option.builder()
                .longOpt("ge")
                .hasArg(true)
                .desc("gap extend penalty (default -1)")
                .required(false)
                .build());
        options.addOption(Option.builder()
                .longOpt("dpmatrices")
                .hasArg(true)
                .desc("output dynamic programming matrices")
                //.numberOfArgs(Option.UNLIMITED_VALUES)
                .required(false)
                .build());
        options.addOption(Option.builder()
                .longOpt("check")
                .hasArg(false)
                .desc("calculate checkscores for all alignments\n" + "and output only incorrect alignments")
                //.numberOfArgs(Option.UNLIMITED_VALUES)
                .required(false)
                .build());
        options.addOption(Option.builder()
                .longOpt("pairs")
                .hasArg(true)
                .desc("path to pairs file")
                //.numberOfArgs(Option.UNLIMITED_VALUES)
                .required(true)
                .build());
        options.addOption(Option.builder()
                .longOpt("seqlib")
                .hasArg(true)
                .desc("path to sequence library file")
                //.numberOfArgs(Option.UNLIMITED_VALUES)
                .required(true)
                .build());
        options.addOption(Option.builder("m")
                .longOpt("matrix")
                .hasArg(true)
                .desc("path to substitution matrix file")
                //.numberOfArgs(Option.UNLIMITED_VALUES)
                .required(true)
                .build());
        options.addOption(Option.builder()
                .longOpt("mode")
                .hasArg(true)
                .argName("local|global|freeshift")
                //.numberOfArgs(Option.UNLIMITED_VALUES)
                .required(true)
                .build());
        options.addOption(Option.builder()
                .longOpt("nw")
                .hasArg(false)
                .desc("use NW algorithms")
                //.numberOfArgs(Option.UNLIMITED_VALUES)
                .required(false)
                .build());

        options.addOption(Option.builder()
                .longOpt("format")
                .hasArg(true)
                .argName("score|ali|html")
                .desc("output format" + "\n" + "scores: scores only" + "\n"+ " ali: scores + alignment in plaintext" + "\n"+ "scores + alignment in html" + "\n")
                //.numberOfArgs(Option.UNLIMITED_VALUES)
                .required(true)
                .build());



        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
            String pairs = cmd.getOptionValue("pairs");
            String seqlib = cmd.getOptionValue("seqlib");
            String matrix = cmd.getOptionValue("m");
            int ge = -1;
            if (cmd.getOptionValue("ge") != null){
                ge = Integer.parseInt(cmd.getOptionValue("ge"));
            }

            int go = -12;

            if (cmd.getOptionValue("go") != null){
                go = Integer.parseInt(cmd.getOptionValue("go"));
            }

            String mode = cmd.getOptionValue("mode");
            String format = cmd.getOptionValue("format");

            AlignmentNW needelman = new AlignmentNW(pairs, seqlib, matrix, ge);
            AlignmentSW smitty = new AlignmentSW(pairs, seqlib, matrix, ge);
            AlignmentSWfreeshift freeshift = new AlignmentSWfreeshift(pairs, seqlib, matrix, ge);
            GotohGlobal gotohGlobal = new GotohGlobal(pairs, seqlib, matrix, ge, go);
            GotohLocal gotohLocal = new GotohLocal(pairs, seqlib, matrix, ge, go);
            GotohFreeshift gotohFreeshift = new GotohFreeshift(pairs, seqlib, matrix, ge, go);

            if (cmd.hasOption("nw")){
                    switch (mode){
                        case "global":
                            needelman.processSequences(matrix, format, cmd.hasOption("check"), cmd.getOptionValue("dpmatrices"));
                            break;
                        case "local":
                            smitty.processSequences(matrix, format, cmd.hasOption("check"), cmd.getOptionValue("dpmatrices"));
                            break;
                        case  "freeshift":
                            freeshift.processSequences(matrix, format, cmd.hasOption("check"), cmd.getOptionValue("dpmatrices"));
                            break;
                    }
            }else {
                switch (mode){
                    case "global":
                        gotohGlobal.processSequences(matrix, format, cmd.hasOption("check"), cmd.getOptionValue("dpmatrices"));
                        break;
                    case "local":
                        gotohLocal.processSequences(matrix, format, cmd.hasOption("check"), cmd.getOptionValue("dpmatrices"));
                        break;
                    case "freeshift":
                        gotohFreeshift.processSequences(matrix, format, cmd.hasOption("check"), cmd.getOptionValue("dpmatrices"));
                        break;
                }

            }

        } catch (ParseException pe) {
            System.out.println("Error parsing command-line arguments!");
            System.out.println();
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "\n" +
                    "java -jar alignment.jar [--go <gapopen>] [--ge <gapextend>]\n" +
                    "[--dpmatrices <dir>] [--check]\n" +
                    "--pairs <pairfile> --seqlib <seqlibfile>\n" +
                    "-m <matrixname>\n" +
                    "--mode <local|global|freeshift> [--nw]\n" +
                    "--format <scores|ali|html>\n" + "\n", options );
            System.exit(1);
        }
    }
}
