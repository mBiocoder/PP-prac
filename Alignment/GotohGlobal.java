import java.io.BufferedWriter;
import java.io.File;
import java.util.HashMap;

public class GotohGlobal extends Alignment{

    private int go;
    private int ge;
    String seq1 = " ";
    String seq2 = " ";


    public GotohGlobal(String pairpath, String seqlibpath, String matrixpath, int ge, int go ) {
        super(pairpath, seqlibpath, matrixpath, ge);
        this.go = go;
        this.ge = ge;

        insertion = new float[rows][columns];
        deletion = new float[rows][columns];
        optAlignGlob = new float[rows][columns];
    }

    private int rows;
    private int columns;

    private float [][] insertion;
    private float [][] deletion;
    private float [][] optAlignGlob;


    public float[][] findSolutionMatrix(){
        insertion[0][0] = 0;
        deletion[0][0] = 0;
        optAlignGlob[0][0] = 0;

        for (int row=1; row<rows; row++){
            deletion[row][0] = Integer.MIN_VALUE;
            insertion[row][0] = Integer.MIN_VALUE;
            optAlignGlob[row][0] = go + ge*row; // A(k,0) = g(k) = go+ge*k
        }

        for (int column=1; column<columns; column++){
            deletion[0][column] = Integer.MIN_VALUE;
            insertion[0][column] = Integer.MIN_VALUE;
            optAlignGlob[0][column] = go + ge*column; // A(k,0) = g(k) = go+ge*k
        }


        for(int row=1; row < rows; row++){
            for(int column=1; column < columns; column++) {

                String asSeq1 = String.valueOf(seq1.charAt(row - 1));
                String asSeq2 = String.valueOf(seq2.charAt(column - 1));

                float matchValue = scorematrix.get(new Pair(asSeq1, asSeq2));
                insertion[row][column] = Math.max(optAlignGlob[row - 1][column] + go + ge, insertion[row - 1][column] + ge);
                deletion[row][column] = Math.max(optAlignGlob[row][column - 1] + go + ge, deletion[row][column - 1] + ge);
                optAlignGlob[row][column] = Math.max(Math.max(optAlignGlob[row - 1][column - 1] + matchValue, insertion[row][column]), deletion[row][column]);
            }
        }
        return optAlignGlob;
    }

    public float getScoreGotohGlob(){
        return (float)Math.round(optAlignGlob[rows-1][columns-1]*10)/10;
    }

    //Tracebackmatrix
    public String[] findTraceback(String seq1, String seq2){
        seq1 = " " + seq1;
        seq2 = " " + seq2;

        StringBuilder alignedStrand1 = new StringBuilder();
        StringBuilder alignedStrand2 = new StringBuilder();

        int row = seq1.length()-1 ;
        int column = seq2.length()-1;


        //while not reached top left corner of solution matrix, do the following:
        while (row > 0 | column > 0){


            if (Math.abs(optAlignGlob[row][column] - (deletion[row][column])) < 0.0001|| row == 0){

                int extension = -1;
                for(int k=1; k<=column; k++){
                    if(Math.abs(optAlignGlob[row][column-k] +go+ge*k - optAlignGlob[row][column]) < 0.0001){
                        extension = k;
                    }
                }

                for(int ex=0; ex<extension; ex++){
                    alignedStrand1.append('-');
                    alignedStrand2.append(seq2.charAt(column));
                    column = column-1;
                }
                continue;

            } else if ( Math.abs(optAlignGlob[row][column] - (insertion[row][column])) < 0.0001|| column == 0){
                int extension = -1;
                for (int k=1; k<=row; k++){
                    if(Math.abs(optAlignGlob[row-k][column]+go+ge*k - optAlignGlob[row][column]) < 0.0001){
                        extension = k;
                    }
                }


                for (int ex=0; ex<extension; ex++){
                    alignedStrand1.append(seq1.charAt(row));
                    alignedStrand2.append('-');
                    row = row-1;
                }

                continue;
            } else {
                alignedStrand2.append(seq2.charAt(column));
                alignedStrand1.append(seq1.charAt(row));

                row = row -1;
                column = column -1;
            }

        }
        alignedStrand1.reverse();
        alignedStrand2.reverse();
        return new String[] {alignedStrand1.toString(), alignedStrand2.toString()};
    }

    public void printAlignment(String alignment1, String alignment2){
        System.out.println(alignment1 +"\n"+ alignment2 + "\n");
    }

    //checkscore GotohGlobal
    public float checkscoreGotohGlob(HashMap<Pair, Float> scorematrix, String [] alignment){
        String[] outputStrands = alignment;
        float matchValue = 0;
        float checkscore = 0;
        boolean gapSeq1 = false;
        boolean gapSeq2 = false;

        for(int index=0; index<outputStrands[0].length(); index++){
            if (outputStrands[0].charAt(index) == '-'){
                if (!gapSeq1){
                    checkscore += go+ge;
                    gapSeq1 = true;
                    gapSeq2 = false;
                } else {
                    checkscore += ge;
                }
            } else if (outputStrands[1].charAt(index) == '-'){
                if (!gapSeq2){
                    checkscore += go+ge;
                    gapSeq2 = true;
                    gapSeq1 = false;
                } else {
                    checkscore += ge;
                }
            } else {
                String asSeq1 = String.valueOf(outputStrands[0].charAt(index));
                String asSeq2 = String.valueOf(outputStrands[1].charAt(index));

                Pair substPair = new Pair(asSeq1, asSeq2);
                matchValue = scorematrix.get(substPair);
                checkscore += matchValue;
                gapSeq1 = false;
                gapSeq2 = false;
            }
        }
        checkscore = (float)Math.round(checkscore*10)/10;
        return checkscore;
    }

    public void printGotohGlob(float[][] matrix) {
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
            //System.out.println(pair);
            optAlignGlob = new float[rows][columns];
            insertion = new float[rows][columns];
            deletion = new float[rows][columns];
            findSolutionMatrix();
            String [] alignedseq = findTraceback(seq1, seq2);

            if (check) {
                float score = checkscoreGotohGlob(scorematrix, alignedseq);
                if(score != getScoreGotohGlob()) {
                    printAlignment(alignedseq[0], alignedseq[1]); //print alignment if checkscore is not the same
                }
                continue;
            }

            switch (format){
                case "scores":
                    printScores(getScoreGotohGlob(), pair.seq1, pair.seq2);
                    break;
                case "ali":
                    printAli(getScoreGotohGlob(), pair.seq1, pair.seq2, alignedseq[0], alignedseq[1]);
                    break;
                case "html":
                    printHtml(getScoreGotohGlob(), pair.seq1, seq1, pair.seq2,seq2, alignedseq[0], alignedseq[1]);
                    break;
            }

            if (dpmatrices != null) {
                printDP_matrix(optAlignGlob, seq1, seq2, new File(dpmatrices, "GotohGlobalMatrix"));
                System.out.println("Corresponding optimal alignment matrix: ");
                printGotohGlob(optAlignGlob);
                System.out.println("Corresponding insertion matrix: ");
                printGotohGlob(insertion);
                System.out.println("Corresponding deletion matrix: ");
                printGotohGlob(deletion);
            }
        }
    }



    /*public static void main(String[] args) {
        Alignment alignmentgeneral = new Alignment();
        String seq1 = "GPLDVQVTEDAVRRYLTRKPMTTKDLLKKFQTKKTGLSSEQTVNVLAQILKRLNPERKMINDKMHFSLK";
        String seq2 = "MEEAKQKVVDFLNSKSKSKFYFNDFTDLFPDMKQREVKKILTALVNDEVLEYWSSGSTTMYGLKG";

        GotohGlobal gotohglobal = new GotohGlobal(seq1, seq2);
        Alignment alignment = new Alignment();
        gotohglobal.findSolutionMatrix();
        System.out.println(gotohglobal.getScoreGotohGlob() + "\t" + alignmentgeneral.averageSequenceLength(seq1, seq2));
        //alignment.printDPMatrix(seq1, seq2, gotohglobal.optAlignGlob);
        //alignment.printDPMatrix("EEKRNRAITARRQHLKSVMLQIAATELEKE", "GERRRSQLDRDQCAYCKEKGHWAKDCPKKPRGPRGPRPQ", gotohglobal.insertion);
        //alignment.printDPMatrix("EEKRNRAITARRQHLKSVMLQIAATELEKE", "GERRRSQLDRDQCAYCKEKGHWAKDCPKKPRGPRGPRPQ", gotohglobal.deletion);

        gotohglobal.findTraceback(seq1, seq2);
        gotohglobal.printAlignment(seq1, seq2);

        Matrices matrix = new Matrices();
        HashMap<Pair, Float> scoreMatrix = matrix.getscoreMatrices();

        gotohglobal.findSolutionMatrix();
        System.out.println(gotohglobal.getScoreGotohGlob() + "\t" + alignmentgeneral.averageSequenceLength(seq1, seq2));

        String[] alignedSequences = gotohglobal.findTraceback(seq1, seq2);
        float alignedScore = gotohglobal.getScoreGotohGlob();
        gotohglobal.printAlignment(seq1, seq2);
        float checkScore = gotohglobal.checkscoreGotohGlob(scoreMatrix, alignedSequences[0], alignedSequences[1],alignedScore);
        System.out.println(alignedScore+ " " + checkScore);
    }*/




}

