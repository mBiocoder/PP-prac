import org.apache.commons.cli.*;

public class ValidationRunner {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder()
                .longOpt("f")
                .hasArg(true)
                .desc("file containing alignment pairs")
                .required(true)
                .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
            String file = cmd.getOptionValue("f");

            ValidationTest validation = new ValidationTest(file);
            validation.processSequences();


        } catch (ParseException pe) {
            System.out.println("Error parsing command-line arguments!");
            System.out.println();
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "\n" +
                    "java -jar validateAli.jar -f <alignment-pairs>\n"+ "\n",  options );
            System.exit(1);
        }
    }
}
