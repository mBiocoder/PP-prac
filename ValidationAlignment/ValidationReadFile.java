import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ValidationReadFile{

    String testCasePath;

    public ValidationReadFile(String testCasePath){
        this.testCasePath = testCasePath;
        readFile(testCasePath);
    }

    public ArrayList<ArrayList<String>> refAndPredAlignments = new ArrayList<>(); //private
    public ArrayList<String> blockAlignments = new ArrayList<>(); //private

    private void readFile(String testCasesPairs){
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(testCasesPairs));
            BufferedReader brReader = new BufferedReader(reader);

            String header = "";
            String line = " ";
            String score = "";
            String seqId = "";
            String sequence = "";

            while ((line = brReader.readLine())!= null){

                if(line.startsWith(">")){
                    blockAlignments = new ArrayList<>();
                    refAndPredAlignments.add(blockAlignments);

                    header = line.replace(">", "").split(" ")[0];
                    score = line.replace(">", "").split(" ")[1];
                    blockAlignments.add(header);
                    blockAlignments.add(score);
                    continue;
                }

                String[] lineArray = line.replace(" ", "").split(":");

                if(lineArray.length>1){
                    seqId = lineArray[0];
                    sequence = lineArray[1];
                    blockAlignments.add(seqId);
                    blockAlignments.add(sequence);
                }
                else {
                    int index =blockAlignments.size()-1;
                    blockAlignments.set(index, blockAlignments.get(index) + line);

                }

            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
