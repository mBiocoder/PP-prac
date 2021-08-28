import java.util.HashMap;

public class CodingNW extends Alignment{

    //Attributes
    String seq1 = "";
    String seq2 = "";

    private float [][] nwMatrix;
    private int rows;
    private int columns;

    int gap = -4;
    int MATCH = 3;
    int MISMATCH = -2;

    //Constructor
    public CodingNW(String sequence1, String sequence2) {

        seq1 = sequence1;
        seq2 = sequence2;

        rows = seq1.length() +1;
        columns = seq2.length() +1;
        nwMatrix = new float[rows][columns];

    }

    //generate solution matrix
    public float[][] findSolutionMatrix(){
        //nullpoint and top-left corner
        nwMatrix[0][0] = 0;

        //fill top row and first column and initialize
        for (int column= 1; column < columns; column++){
            nwMatrix[0][column] = nwMatrix[0][column -1] + gap;
        }

        for (int row= 1; row < rows ; row++){
            nwMatrix[row][0] = nwMatrix[row -1][0] +gap;
        }

        //fill in the rest of the matrix
        for (int row = 1; row < rows; row++){
            for (int column = 1; column < columns; column++){
                //if the chars in that psoition match for both strands, set the matchmalue to match, else to mismatch
                int matchValue;
                if (seq1.charAt(row -1) == seq2.charAt(column -1)){
                    matchValue = MATCH;
                }else{
                    matchValue = MISMATCH;
                }
                //matchvalue is the maximum of these 3 values of diagonal. left and up
                float diagonal = nwMatrix[row -1][column -1] + matchValue;
                float up = nwMatrix[row -1][column] +gap;
                float left = nwMatrix[row][column -1] + gap;
                nwMatrix[row][column] = Math.max(Math.max(diagonal,up),left);
            }
        }
        return nwMatrix; //this is the solution matrix
    }

    //get score of solution matrix
    public float getScoreNW(){
        return nwMatrix[rows-1][columns -1];
    }

    public static void main(String[] args) {
        Alignment alignmentgeneral = new Alignment();
        for ( int i = 0; i <100; i++){
            String randomseq1 = alignmentgeneral.generateRandomString(Integer.parseInt(args[0]));
            String randomseq2 = alignmentgeneral.generateRandomString(Integer.parseInt(args[0]));

             CodingNW needlemanwunsch = new CodingNW(randomseq1, randomseq2);
             needlemanwunsch.findSolutionMatrix();
             System.out.println(needlemanwunsch.getScoreNW());
        }

        /*CodingNW needlemanwunsch = new CodingNW("TATAAT", "TTACGTAAGC");
        Alignment alignment = new Alignment();
        needlemanwunsch.findSolutionMatrix();
        System.out.println(needlemanwunsch.getScoreNW() + "\t" + alignmentgeneral.averageSequenceLength("TATAAT", "TTACGTAAGC"));
        alignment.printDPMatrix("TATAAT", "TTACGTAAGC", needlemanwunsch.nwMatrix);*/
    }
}
