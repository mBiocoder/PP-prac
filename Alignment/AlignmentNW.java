
import java.io.File;
import java.util.HashMap;

public class AlignmentNW extends Alignment{
    String seq1 = "";
    String seq2 = "";

    public AlignmentNW(String pairpath, String seqlibpath, String matrixpath, int gap) {
        super(pairpath, seqlibpath, matrixpath, gap);
    }

    private float [][] nwMatrix;
    private int rows;
    private int columns;

    //generate solution matrix
    public void findSolutionMatrix(){
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
                String asSeq1 = String.valueOf(seq1.charAt(row -1));
                String asSeq2 = String.valueOf(seq2.charAt(column -1));

                //matchvalue is the maximum of these 3 values of diagonal. left and up
                float matchValue = scorematrix.get(new Pair(asSeq1, asSeq2));
                float diagonal = nwMatrix[row -1][column -1] + matchValue;
                float up = nwMatrix[row -1][column] +gap;
                float left = nwMatrix[row][column -1] + gap;
                nwMatrix[row][column] = Math.max(Math.max(diagonal,up),left);
            }
        }
    }

    //get score of solution matrix
    public float getScoreNW(){
       return (float)Math.round(nwMatrix[rows-1][columns -1]*10)/10;
    }

    //Tracebackmatrix
    public String[] findTraceback(String seq1, String seq2){
        seq1 = " " + seq1;
        seq2 = " " + seq2;

        StringBuilder alignedStrand1 = new StringBuilder();
        StringBuilder alignedStrand2 = new StringBuilder();

        int row = nwMatrix.length -1;
        int column = nwMatrix[0].length -1;

        while (row > 0 || column > 0){

            if (column>0 && Math.abs(nwMatrix[row][column -1] - (nwMatrix[row][column] - gap)) < 0.0001) {

                alignedStrand1.append('-');
                alignedStrand2.append(seq2.charAt(column));
                column = column - 1;


            } else if(row>0 && Math.abs(nwMatrix[row -1][column] - (nwMatrix[row][column] - gap))< 0.0001 ){

                alignedStrand1.append(seq1.charAt(row));
                alignedStrand2.append('-');
                //move to new location
                row = row -1;

            } else {

                alignedStrand1.append(seq1.charAt(row));
                alignedStrand2.append(seq2.charAt(column));

                row = row - 1;
                column = column - 1;
            }


        }
        alignedStrand1.reverse();
        alignedStrand2.reverse();
        return new String[] {alignedStrand1.toString(), alignedStrand2.toString()};
    }

    public void printAlignment(String alignment1, String alignment2){
        System.out.println(alignment1 +"\n"+ alignment2 + "\n");
    }


    public float checkscoreNeedle(HashMap<Pair, Float> scorematrix, String [] alignment){
        String[] outputStrands = alignment;

        float matchValue = 0;
        float checkscore = 0;

        for (int index=0; index < outputStrands[0].length(); index++){
            if(outputStrands[0].charAt(index) == '-') {
                checkscore += gap;
            } else if (outputStrands[1].charAt(index) == '-'){
                checkscore += gap;
            } else {
                String asSeq1 = String.valueOf(outputStrands[0].charAt(index));
                String asSeq2 = String.valueOf(outputStrands[1].charAt(index));

                Pair substPair = new Pair(asSeq1, asSeq2);
                matchValue = scorematrix.get(substPair);

                checkscore += matchValue;

            }
        }checkscore = (float)Math.round(checkscore*10)/10;
        //System.out.println(checkscore);
        return checkscore;
    }

    //print the dynammic programming matrix
    public void printNWmatrix() {
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
                System.out.print(nwMatrix[i][j] +"  ");
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
            nwMatrix = new float[rows][columns];
            //System.out.println(pair);
            findSolutionMatrix();
            String [] alignedseq = findTraceback(seq1, seq2);

            if (check) {
                float score = checkscoreNeedle(scorematrix, alignedseq);
                if(score != getScoreNW()) {
                    printAlignment(alignedseq[0], alignedseq[1]); //print alignment if checkscore is not the same
                }
                continue;
            }

            switch (format){
                case "scores":
                    printScores(getScoreNW(), pair.seq1, pair.seq2);
                    break;
                case "ali":
                    printAli(getScoreNW(), pair.seq1, pair.seq2, alignedseq[0], alignedseq[1]);
                    break;
                case "html":
                    printHtml(getScoreNW(), pair.seq1, seq1, pair.seq2,seq2, alignedseq[0], alignedseq[1]);
                    break;
            }

            if (dpmatrices != null) {
                printDP_matrix(nwMatrix, seq1, seq2,new File(dpmatrices, "Nwmatrix"));
                System.out.println("Corresponding Matrix: ");
                printNWmatrix();
            }

        }
    }

    /*public static void main(String[] args) {
        //Alignment alignmentgeneral = new Alignment();

        AlignmentNW needlemanwunsch = new AlignmentNW("C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\sanity.pairs", "C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\domains.seqlib","C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\dayhoff.mat", -4);
        needlemanwunsch.processSequences();
        //needlemanwunsch.printAlignment();

        System.out.println(needlemanwunsch.checkscoreNeedle("C:\\Bachelor Bioinformatik\\Bachelor Bioinformatik\\ProPra\\Blockteil\\GenomeReport\\Alignment\\src\\dayhoff.mat"));

        //Alignment alignment = new Alignment();
        //System.out.println(needlemanwunsch.getScoreNW() + "\t" + alignmentgeneral.averageSequenceLength("TATAAT", "TTACGTAAGC"));
        //alignment.printDPMatrix("TTACGTAAGC", "TATAAT", needlemanwunsch.nwMatrix);
        /*alignment.printDPMatrix("GPLDVQVTEDAVRRYLTRKPMTTKDLLKKFQTKKTGLSSEQTVNVLAQILKRLNPERKMINDKMHFSLK", "MEEAKQKVVDFLNSKSKSKFYFNDFTDLFPDMKQREVKKILTALVNDEVLEYWSSGSTTMYGLKG", needlemanwunsch.nwMatrix);
        System.out.println();
        //needlemanwunsch.findTraceback("TTACGTAAGC", "TATAAT");
        needlemanwunsch.printAlignment("GPLDVQVTEDAVRRYLTRKPMTTKDLLKKFQTKKTGLSSEQTVNVLAQILKRLNPERKMINDKMHFSLK", "MEEAKQKVVDFLNSKSKSKFYFNDFTDLFPDMKQREVKKILTALVNDEVLEYWSSGSTTMYGLKG");

    }*/
}
