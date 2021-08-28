
import java.io.File;
import java.util.HashMap;

public class AlignmentSW extends Alignment{
    //Attributes
    String seq1 = "";
    String seq2 = "";

    private float [][] swMatrix;
    private int rows;
    private int columns;

    public AlignmentSW(String pairpath, String seqlibpath, String matrixpath, int gap) {
        super(pairpath, seqlibpath, matrixpath, gap);
    }

    //Solution matrix
    public float[][] findSolutionMatrixSW(){
        //top-left corner
        swMatrix[0][0] = 0;

        //fill top row and first column and initialize
        for (int column= 1; column < columns; column++){
            swMatrix[0][column] = 0;
        }

        for (int row= 1; row < rows ; row++){
            swMatrix[row][0] = 0;
        }

        //fill in the rest of the matrix
        for (int row = 1; row < rows; row++){
            for (int column = 1; column < columns; column++){
                //if the chars in that psoition match for both strands, set the matchmalue to match, else to mismatch
                String asSeq1 = String.valueOf(seq1.charAt(row -1));
                String asSeq2 = String.valueOf(seq2.charAt(column -1));

                //matchvalue is the maximum of these 3 values of diagonal. left and up
                float matchValue = scorematrix.get(new Pair(asSeq1, asSeq2));
                float diagonal = swMatrix[row -1][column -1] + matchValue;
                float up = swMatrix[row -1][column] +gap;
                float left = swMatrix[row][column -1] + gap;
                swMatrix[row][column] = Math.max(Math.max(Math.max(diagonal,up),left),0);
            }
        }
        //System.out.println(swMatrix[rows -1][columns -1]);
        return swMatrix; //this is the solution matrix
    }

    //Traceback
    public String[] findTracebackSW(String seq1, String seq2){

        seq1 = " " + seq1;
        seq2 = " " + seq2;

        StringBuilder outputseq1 = new StringBuilder();
        StringBuilder outputseq2 = new StringBuilder();

        int row = getmaxrow();
        int column = getmaxcolumn();

        int maxrow = swMatrix.length-1;
        int maxcol = swMatrix[0].length-1;

        for (int i = maxrow; i > row; --i)
        {
            outputseq2.append("-");
            outputseq1.append(seq1.charAt(i));
        }

        for (int i = maxcol; i > column; --i)
        {
            outputseq2.append(seq2.charAt(i));
            outputseq1.append("-");
        }

        while (row > 0  | column > 0 && swMatrix[row][column] != 0) {
            if ((Math.abs(swMatrix[row][column -1] - (swMatrix[row][column] - gap)) < 0.0001)){
                //add to aligned sequences

                outputseq1.append('-');
                outputseq2.append(seq2.charAt(column));

                //move to new location
                column -= 1;
                continue;
            }
            else if ((Math.abs(swMatrix[row -1][column] - (swMatrix[row][column] - gap)) < 0.0001)) {

                //add to aligend sequences
                outputseq1.append(seq1.charAt(row));
                outputseq2.append('-');

                //move to new location
                row -= 1;
                continue;
            }

                //add to aligned sequences
                outputseq1.append(seq1.charAt(row));
                outputseq2.append(seq2.charAt(column));

                //move to new location
                row -= 1;
                column -= 1;

        }

        for (int i = row; i > 0; --i)
        {
            outputseq2.append("-");
            outputseq1.append(seq1.charAt(i));
        }

        for (int i = column; i > 0; --i)
        {
            outputseq2.append(seq2.charAt(i));
            outputseq1.append("-");
        }
        outputseq1.reverse();
        outputseq2.reverse();
        return new String[] {outputseq1.toString(), outputseq2.toString()};
    }

    public float getmaxscore(float[][] dp) {
        float max = 0;
        for(int i=0;i<dp.length;i++){
            for(int j=0;j<dp[0].length;j++){
                max = Math.max(max,dp[i][j]);
            }
        }
        return max;
    }

    //print result
    public void printAlignment(String seq1, String seq2){
        System.out.println(seq1 + "\n"+ seq2 + "\n");
    }

    //Helper function for returning the starting position of column
    public int getmaxcolumn() {
        float maxscore = getmaxscore(swMatrix);
        for(int i=0;i<swMatrix.length;i++){
            for(int j=0;j<swMatrix[0].length;j++){
                if(maxscore== swMatrix[i][j])
                    return j;
            }
        }
        return -1;
    }

    public float score(){
        float max = 0;
        for(int i=0;i<swMatrix.length;i++){
            for(int j=0;j<swMatrix[0].length;j++){
                max = Math.max(max,swMatrix[i][j]);
            }
        }

        return (float)Math.round(max*10)/10;
    }



    //method for returning the starting position of row
    public int getmaxrow() {
        float maxscore = getmaxscore(swMatrix);
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                if(maxscore==swMatrix[i][j])
                    return i;
            }
        }
        return -1;
    }

    public float checkscoreSmith(HashMap<Pair, Float> scorematrix, String [] alignment) {
        String[] outputStrands = alignment;

        float matchValue = 0;
        float checkscore = 0;
        int firstMatch = 0;
        int lastMatch = 0;


        for (int index = 0; index < outputStrands[0].length(); index++) {
            if (outputStrands[0].charAt(index) != '-' && outputStrands[1].charAt(index) != '-') {
                firstMatch = index;
                break;
            }
        }

        for (int revIndex = (outputStrands[0].length() - 1); revIndex > 0; --revIndex) {
            if (outputStrands[0].charAt(revIndex) != '-' && outputStrands[1].charAt(revIndex) != '-') {
                lastMatch = revIndex;
                break;
            }
        }

        for (int i = firstMatch; i <= lastMatch; i++) {
            if (outputStrands[0].charAt(i) == '-') {
                checkscore += gap;
            } else if (outputStrands[1].charAt(i) == '-') {
                checkscore += gap;
            } else {
                String asSeq1 = String.valueOf(outputStrands[0].charAt(i));
                String asSeq2 = String.valueOf(outputStrands[1].charAt(i));

                Pair substPair = new Pair(asSeq1, asSeq2);
                matchValue = scorematrix.get(substPair);

                checkscore += matchValue;

            }
        }
        checkscore = (float)Math.round(checkscore*10)/10;
        return checkscore;
    }

    //print the dynammic programming matrix
    public void printSWmatrix() {
        System.out.print("   ");
        for (int j=1; j<=seq2.length();j++)
            System.out.print ("     "+seq2.charAt(j-1));

        System.out.println();

        for (int i=0; i<=seq1.length(); i++) {
            if (i>0) {
                System.out.print(seq1.charAt(i - 1) + "  ");
            } else {
                System.out.print("   ");
            }
            for (int j=0; j<=seq2.length(); j++) {
                System.out.print(swMatrix[i][j] +"  ");
            }
            System.out.println("");
        }
    }

    public void processSequences(String  matrices, String format, boolean check, String dpmatrices){
        Matrices matrix = new Matrices();
        HashMap<Pair, Float> scorematrix = matrix.getscoreMatrices(matrices);
        for(Pair pair: namepairs){
            seq1 = seqlibseqs.get(pair.seq1);
            seq2 = seqlibseqs.get(pair.seq2);
            rows = seq1.length() +1;
            columns = seq2.length() +1;
            swMatrix = new float[rows][columns];
            findSolutionMatrixSW();
            String [] alignedseq = findTracebackSW(seq1, seq2);

            if (check) {
                float score = checkscoreSmith(scorematrix, alignedseq);
                if(score != score()) {
                    printAlignment(alignedseq[0], alignedseq[1]); //print alignment if checkscore is not the same
                }
                continue;
            }

            switch (format){
                case "scores":
                    printScores(score(), pair.seq1, pair.seq2);
                    break;
                case "ali":
                    printAli(score(), pair.seq1, pair.seq2, alignedseq[0], alignedseq[1]);
                    break;
                case "html":
                    printHtml(score(), pair.seq1, seq1, pair.seq2,seq2, alignedseq[0], alignedseq[1]);
                    break;
            }

            if (dpmatrices != null) {
                printDP_matrix(swMatrix, seq1, seq2, new File(dpmatrices, "Swmatrix"));
                System.out.println("Corresponding Matrix: ");
                printSWmatrix();
            }

        }
    }


    /*public static void main(String[] args) {
        AlignmentSW smithwaterman = new AlignmentSW("C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\sanity.pairs", "C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\domains.seqlib","C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\dayhoff.mat", -4);
        smithwaterman.processSequences();
        Alignment alignment = new Alignment();
        //System.out.println(smithwaterman.getScoreSW() + "\t" + alignment.averageSequenceLength("TATAAT", "TTACGTAAGC"));
        alignment.printDPMatrix("TATAAT", "TTACGTAAGC", smithwaterman.swMatrix);
        //smithwaterman.findTracebackSW("TATAAT", "TTACGTAAGC");
        //smithwaterman.getScoreSW();
        smithwaterman.printAlignment("TATAAT", "TTACGTAAGC");


    }*/

}