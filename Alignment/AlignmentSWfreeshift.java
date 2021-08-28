import java.io.BufferedWriter;
import java.io.File;
import java.util.HashMap;

public class AlignmentSWfreeshift extends Alignment {
    //Attributes
    String seq1 = "";
    String seq2 = "";

    public AlignmentSWfreeshift(String pairpath, String seqlibpath, String matrixpath, int gap) {
        super(pairpath, seqlibpath, matrixpath, gap);
    }

    private float [][] swfreeMatrix;
    private int rows;
    private int columns;

    //Solution matrix
    public float[][] findSolutionMatrixSWfree(){
        //top-left corner
        swfreeMatrix[0][0] = 0;

        //fill top row and first column and initialize with 0
        for (int column= 1; column < columns; column++){
            swfreeMatrix[0][column] = 0;
        }

        for (int row= 1; row < rows ; row++){
            swfreeMatrix[row][0] = 0;
        }

        //fill in the rest of the matrix
        for (int row = 1; row < rows; row++){
            for (int column = 1; column < columns; column++){
                //if the chars in that psoition match for both strands
                String asSeq1 = String.valueOf(seq1.charAt(row -1));
                String asSeq2 = String.valueOf(seq2.charAt(column -1));


                //matchvalue is the maximum of these 3 values of diagonal. left and up
                float matchValue = scorematrix.get(new Pair(asSeq1, asSeq2));
                float diagonal = swfreeMatrix[row -1][column -1] + matchValue;
                float up = swfreeMatrix[row -1][column] +gap;
                float left = swfreeMatrix[row][column -1] + gap;
                swfreeMatrix[row][column] = Math.max(Math.max(diagonal,up),left);
            }
        }
        return swfreeMatrix; //this is the solution matrix
    }

    public String[] findTracebackSWfree(String seq1, String seq2){

        seq1 = " " + seq1;
        seq2 = " " + seq2;

        StringBuilder outputseq1 = new StringBuilder();
        StringBuilder outputseq2 = new StringBuilder();

        float[] last_row = swfreeMatrix[rows -1];
        float[] last_col = new float[swfreeMatrix.length];

        for (int i = 1; i < swfreeMatrix.length; i++){
            last_col [i] = swfreeMatrix[i][columns -1];
        }

        int row = 0;
        int column = 0;

        int maxrow = swfreeMatrix.length-1;
        int maxcol = swfreeMatrix[0].length-1;

        if (findMaximumArrayValue(last_row) >= findMaximumArrayValue(last_col)){
            row = rows -1;
            column = findIndex(last_row);
        }else{
            row = findIndex(last_col);
            column = columns -1;
        }

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


        //Traceback to the matrix edge starting at the index of the max score
        while (row >0 && column>0) {
            //Case: left
            if ((Math.abs(swfreeMatrix[row][column -1] - (swfreeMatrix[row][column] - gap)) < 0.0001)){
                //add to aligned sequences
                outputseq1.append('-');
                outputseq2.append(seq2.charAt(column));

                //move to new location
                column -= 1;
                continue;
            }
            //Case: up
            else if ((Math.abs(swfreeMatrix[row -1][column] - (swfreeMatrix[row][column] - gap)) < 0.0001)) {
                //add to aligend sequences
                outputseq1.append(seq1.charAt(row));
                outputseq2.append('-');

                //move to new location
                row -= 1;
                continue;
            } //Case: diagonal
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

    public float findMaximumArrayValue(float[] array){
        float max = array[0];
        for(int i=1;i<array.length;i++)
            max = Math.max(array[i],max);
        return max;
    }

    public int findIndex(float arr[]) {
        // find length of array
        int len = arr.length;
        int i = 0;

        // traverse in the array
        while (i < len) {

            // if the i-th element is t then return the index
            if (arr[i] == findMaximumArrayValue(arr)) {
                return i;
            }
            else {
                i = i + 1;
            }
        }
        return -1;
    }

    //print result
    public void printAlignment(String alignment1, String alignment2){
        System.out.println(alignment1 +"\n"+ alignment2 + "\n");
    }

    public float checkscoreFree(HashMap<Pair, Float> scorematrix, String [] alignment){
        String[] outputStrands = alignment;

        float matchValue = 0;
        float checkscore = 0;
        int firstMatch = 0;
        int lastMatch = 0;


        if(outputStrands[0].charAt(0) == '-'){
            for(int index=0; index<outputStrands[0].length(); index++){
                if(outputStrands[0].charAt(index) != '-'){
                    firstMatch = index;
                    break;
                }
            }
        } else if(outputStrands[1].charAt(0) == '-'){
            for(int index=0; index<outputStrands[0].length(); index++){
                if(outputStrands[1].charAt(index) != '-'){
                    firstMatch = index;
                    break;
                }
            }
        } else {
            firstMatch = 0;
        }

        if(outputStrands[0].charAt(outputStrands[0].length()-1) == '-'){
            for(int index=(outputStrands[0].length()-1); index>0; --index){
                if(outputStrands[0].charAt(index) != '-'){
                    lastMatch = index;
                    break;
                }
            }
        } else if(outputStrands[1].charAt(outputStrands[1].length()-1) == '-'){
            for(int index=(outputStrands[0].length()-1); index>0; --index){
                if(outputStrands[1].charAt(index) != '-'){
                    lastMatch = index;
                    break;
                }
            }
        } else {
            lastMatch = outputStrands[0].length()-1;
        }

        for( int i=firstMatch; i<= lastMatch; i++){
            if(outputStrands[0].charAt(i) == '-') {
                checkscore += gap;
            } else if (outputStrands[1].charAt(i) == '-'){
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
    public void printSWfreematrix() {
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
                System.out.print(swfreeMatrix[i][j] +"  ");
            }
            System.out.println("");
        }
    }

    public float scoreFree(){

        float[] last_row = swfreeMatrix[rows -1];
        float[] last_col = new float[swfreeMatrix.length];

        for (int i = 1; i < swfreeMatrix.length; i++){
            last_col [i] = swfreeMatrix[i][columns -1];
        }

        int row;
        int column;

        //Print arrays and see
        if (findMaximumArrayValue(last_row) >= findMaximumArrayValue(last_col)){
            row = rows -1;
            column = findIndex(last_row);
            //System.out.println("row: " + row);
            //System.out.println("column: " + column);
        }else{
            row = findIndex(last_col);
            column = columns -1;
            //System.out.println("row: " + row);
            //System.out.println("column: " + column);
        }
        float max_score = swfreeMatrix[row][column];
        return (float)Math.round(max_score*10)/10;
    }


    public void processSequences(String  matrices, String format, boolean check, String dpmatrices){
        Matrices matrix = new Matrices();
        HashMap<Pair, Float> scorematrix = matrix.getscoreMatrices(matrices);
        for(Pair pair: namepairs){
            seq1 = seqlibseqs.get(pair.seq1);
            seq2 = seqlibseqs.get(pair.seq2);
            rows = seq1.length() +1;
            columns = seq2.length() +1;
            swfreeMatrix = new float[rows][columns];
            findSolutionMatrixSWfree();
            String [] alignedseq = findTracebackSWfree(seq1, seq2);

            if (check) {
                float score = checkscoreFree(scorematrix, alignedseq);
                if(score != scoreFree()) {
                    printAlignment(alignedseq[0], alignedseq[1]); //print alignment if checkscore is not the same
                }
                continue;
            }

            switch (format){
                case "scores":
                    printScores(scoreFree(), pair.seq1, pair.seq2);
                    break;
                case "ali":
                    printAli(scoreFree(), pair.seq1, pair.seq2, alignedseq[0], alignedseq[1]);
                    break;
                case "html":
                    printHtml(scoreFree(), pair.seq1, seq1, pair.seq2,seq2, alignedseq[0], alignedseq[1]);
                    break;
            }

            if (dpmatrices != null) {
                printDP_matrix(swfreeMatrix, seq1, seq2, new File(dpmatrices, "SwFreeMatrix"));
                System.out.println("Corresponding Matrix: ");
                printSWfreematrix();
            }

        }
    }


    /*public static void main(String[] args) {
        AlignmentSWfreeshift smitty = new AlignmentSWfreeshift("C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\sanity.pairs", "C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\domains.seqlib","C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\dayhoff.mat", -4);
        smitty.processSequences();
        Alignment alignment = new Alignment();
        //System.out.println(smithwaterman.getScoreSW() + "\t" + alignment.averageSequenceLength("TATAAT", "TTACGTAAGC"));
        alignment.printDPMatrix("TATAAT", "TTACGTAAGC", smitty.swfreeMatrix);
        //smithwaterman.findTracebackSW("TATAAT", "TTACGTAAGC");
        //smithwaterman.getScoreSW();
        smitty.printAlignment("TATAAT", "TTACGTAAGC");


    }*/

}
