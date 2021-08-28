import java.io.BufferedWriter;
import java.io.File;
import java.util.HashMap;

public class GotohLocal extends Alignment{

    String seq1 = " ";
    String seq2 = " ";

    private int go;
    private int ge;

    public GotohLocal(String pairpath, String seqlibpath, String matrixpath, int ge, int go) {
        super(pairpath, seqlibpath, matrixpath, ge);

        this.ge = ge;
        this.go = go;

        insertion = new float[rows][columns];
        deletion = new float[rows][columns];
        optAlignLoc = new float[rows][columns];
    }

    private float [][] insertion;
    private float [][] deletion;
    private float [][] optAlignLoc;
    private int rows;
    private int columns;

    //fill deletion matrix (D(i,j))
    public float[][] findSolutionMatrix(){
        insertion[0][0] = 0;
        deletion[0][0] = 0;
        optAlignLoc[0][0] = 0;

        for (int row=1; row<rows; row++){
            deletion[row][0] = Integer.MIN_VALUE;
            insertion[row][0] = Integer.MIN_VALUE;
            optAlignLoc[row][0] = 0; // A(k,0) = g(k) = go+ge*k
        }

        for (int column=1; column<columns; column++){
            deletion[0][column] = Integer.MIN_VALUE;
            insertion[0][column] = Integer.MIN_VALUE;
            optAlignLoc[0][column] = 0; // A(k,0) = g(k) = go+ge*k
        }

        for(int row=1; row < rows; row++){
            for(int column=1; column < columns; column++){

                String asSeq1 = String.valueOf(seq1.charAt(row -1));
                String asSeq2 = String.valueOf(seq2.charAt(column -1));

                float matchValue = scorematrix.get(new Pair(asSeq1, asSeq2));
                insertion[row][column] = Math.max(optAlignLoc[row-1][column]+go+ge, insertion[row-1][column]+ge);
                deletion[row][column] = Math.max(optAlignLoc[row][column-1]+go+ge, deletion[row][column-1]+ge);
                optAlignLoc[row][column] = Math.max(Math.max(Math.max(optAlignLoc[row-1][column-1] + matchValue, insertion[row][column]), deletion[row][column] ),0);
            }
        }
        return optAlignLoc;
    }


    public float getScoreGotohLoc(){
        float maxScore = 0;
        for(int row=0; row<rows; row++){
            for(int column=0; column<columns; column++){
                if (optAlignLoc[row][column]>maxScore){
                    maxScore = optAlignLoc[row][column];
                }
            }
        }
        return maxScore;
    }

    //Traceback
    public String[] findTraceback(String seq1, String seq2){

        seq1 = " " + seq1;
        seq2 = " " + seq2;

        StringBuilder alignedStrand1 = new StringBuilder();
        StringBuilder alignedStrand2 = new StringBuilder();

        //start from the highest score in your solution matrix optAlignLoc
        int row = getmaxrow();
        int column = getmaxcolumn();

        int maxrow = optAlignLoc.length-1;
        int maxcol = optAlignLoc[0].length-1;

        for (int i = maxrow; i > row; --i)
        {
            alignedStrand2.append("-");
            alignedStrand1.append(seq1.charAt(i));
        }

        for (int i = maxcol; i > column; --i)
        {
            alignedStrand2.append(seq2.charAt(i));
            alignedStrand1.append("-");
        }

        while ((row > 0  || column > 0) && optAlignLoc[row][column] != 0) {
            if (Math.abs(optAlignLoc[row][column] - deletion[row][column]) < 0.0001){
                int extension = -1;
                for(int k=1; k<=column; k++){
                    if(Math.abs(optAlignLoc[row][column-k] +go+ge*k - optAlignLoc[row][column]) < 0.0001){
                        extension = k;
                    }
                }

                for(int ex=0; ex<extension; ex++){
                    alignedStrand2.append(seq2.charAt(column));
                    alignedStrand1.append('-');
                    column = column-1;
                }

                continue;
            }

            else if ((Math.abs(optAlignLoc[row][column] - insertion[row][column])) < 0.0001) {
                int extension = -1;
                for (int k=1; k<=row; k++){
                    if(Math.abs((optAlignLoc[row-k][column]+go+ge*k) - optAlignLoc[row][column]) < 0.0001){
                        extension = k;
                    }
                }

                for (int ex=0; ex<extension; ex++){
                    alignedStrand1.append(seq1.charAt(row));
                    alignedStrand2.append('-');
                    row = row-1;
                }

                continue;
            }

            alignedStrand2.append(seq2.charAt(column));
            alignedStrand1.append(seq1.charAt(row));

            row = row -1;
            column = column -1;

        }
        for (int i = row; i > 0; --i)
        {
            alignedStrand2.append("-");
            alignedStrand1.append(seq1.charAt(i));
        }

        for (int i = column; i > 0; --i)
        {
            alignedStrand2.append(seq2.charAt(i));
            alignedStrand1.append("-");
        }
        alignedStrand1.reverse();
        alignedStrand2.reverse();
        return new String[] {alignedStrand1.toString(), alignedStrand2.toString()};
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

    public float score(){
        float max = 0;
        for(int i=0;i<optAlignLoc.length;i++){
            for(int j=0;j<optAlignLoc[0].length;j++){
                max = Math.max(max,optAlignLoc[i][j]);
            }
        }
        return max;
    }

    //Helper function for returning the starting position of column
    public int getmaxcolumn() {
        float maxscore = getmaxscore(optAlignLoc);
        for(int i=0;i<optAlignLoc.length;i++){
            for(int j=0;j<optAlignLoc[0].length;j++){
                if(maxscore== optAlignLoc[i][j])
                    return j;
            }
        }
        return -1;
    }

    //method for returning the starting position of row
    public int getmaxrow() {
        float maxscore = getmaxscore(optAlignLoc);
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                if(maxscore==optAlignLoc[i][j])
                    return i;
            }
        }
        return -1;
    }

    public float checkscoreGotohLoc(HashMap<Pair, Float> scorematrix, String [] alignment) {
        String[] outputStrands = alignment;

        float matchValue = 0;
        int firstMatch = 0;
        int lastMatch = 0;
        boolean gapSeq1 = false;
        boolean gapSeq2 = false;

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

        float checkscore = 0;
        for (int i = firstMatch; i <= lastMatch; i++) {
            if (outputStrands[0].charAt(i) == '-') {
                if (!gapSeq1) {
                    checkscore += go + ge;
                    gapSeq1 = true;
                    gapSeq2 = false;
                } else {
                    checkscore += ge;
                }
            } else if (outputStrands[1].charAt(i) == '-') {
                if (!gapSeq2) {
                    checkscore += go + ge;
                    gapSeq2 = true;
                    gapSeq1 = false;
                } else {
                    checkscore += ge;
                }
            } else {
                String asSeq1 = String.valueOf(outputStrands[0].charAt(i));
                String asSeq2 = String.valueOf(outputStrands[1].charAt(i));

                Pair substPair = new Pair(asSeq1, asSeq2);
                matchValue = scorematrix.get(substPair);
                checkscore += matchValue;
                gapSeq1 = false;
                gapSeq2 = false;
            }
        }
        checkscore = (float) Math.round(checkscore * 10) / 10;
        return checkscore;
    }



    public void printAlignment(String alignment1, String alignment2){
        System.out.println(alignment1 +"\n"+ alignment2 + "\n");
    }

    public void printGotohLoc(float[][] matrix) {
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
                System.out.print(matrix[i][j] +"  ");
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
            optAlignLoc = new float[rows][columns];
            insertion = new float[rows][columns];
            deletion = new float[rows][columns];
            findSolutionMatrix();
            String [] alignedseq = findTraceback(seq1, seq2);

            if (check) {
                float score = checkscoreGotohLoc(scorematrix, alignedseq);
                if(score != getScoreGotohLoc()) {
                    printAlignment(alignedseq[0], alignedseq[1]); //print alignment if checkscore is not the same
                }
                continue;
            }

            switch (format){
                case "scores":
                    printScores(getScoreGotohLoc(), pair.seq1, pair.seq2);
                    break;
                case "ali":
                    printAli(getScoreGotohLoc(), pair.seq1, pair.seq2, alignedseq[0], alignedseq[1]);
                    break;
                case "html":
                    printHtml(getScoreGotohLoc(), pair.seq1, seq1, pair.seq2,seq2, alignedseq[0], alignedseq[1]);
                    break;
            }

            if (dpmatrices != null) {
                printDP_matrix(optAlignLoc, seq1, seq2, new File(dpmatrices, "GotohLocalMatrix"));
                System.out.println("Corresponding optimal alignment matrix: ");
                printGotohLoc(optAlignLoc);
                System.out.println("Corresponding insertion matrix: ");
                printGotohLoc(insertion);
                System.out.println("Corresponding deletion matrix: ");
                printGotohLoc(deletion);
            }

        }
    }

    /*public static void main(String[] args) {
        String seq1 = "EEKRNRAITARRQHLKSVMLQIAATELEKE";
        String seq2 = "GERRRSQLDRDQCAYCKEKGHWAKDCPKKPRGPRGPRPQ";
        GotohLocal gotohLocal = new GotohLocal("C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\sanity.pairs", "C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\domains.seqlib","C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\dayhoff.mat", -4);
        //gotohLocal.findSolutionMatrix();

        Alignment alignmentgeneral = new Alignment("C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\sanity.pairs", "C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\domains.seqlib","C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\dayhoff.mat", -4);
        System.out.println(gotohLocal.score() + "\t" + alignmentgeneral.averageSequenceLength(seq1, seq2));
        alignmentgeneral.printDPMatrix(seq1, seq2, gotohLocal.optAlignLoc);
        alignmentgeneral.printDPMatrix(seq1, seq2, gotohLocal.insertion);
        alignmentgeneral.printDPMatrix(seq1, seq2, gotohLocal.deletion);

        gotohLocal.processSequences();*/

        /*Matrices matrix = new Matrices();
        HashMap<Pair, Float> scoreMatrix = matrix.getscoreMatrices();


        String[] alignedSequences = gotohLocal.findTraceback(seq1, seq2);
        float alignedScore = gotohLocal.getScoreGotohLoc();
        gotohLocal.printAlignment(seq1, seq2);
        float checkScore = gotohLocal.checkscoreGotohLoc(scoreMatrix, alignedSequences[0], alignedSequences[1],alignedScore);

        System.out.println(alignedScore+ " " + checkScore); //checkscore



    }*/





}

