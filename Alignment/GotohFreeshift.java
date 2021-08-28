
import java.io.File;
import java.util.HashMap;

public class GotohFreeshift extends Alignment {

    private int go;
    private int ge;
    String seq1 = " ";
    String seq2 = " ";


    public GotohFreeshift(String pairpath, String seqlibpath, String matrixpath, int ge, int go ) {
        super(pairpath, seqlibpath, matrixpath, ge);
        this.go = go;
        this.ge = ge;

        insertion = new float[rows][columns];
        deletion = new float[rows][columns];
        optAlignFree = new float[rows][columns];
    }

    private float [][] insertion;
    private float [][] deletion;
    private float [][] optAlignFree;
    private int rows;
    private int columns;

    //fill deletion matrix (D(i,j))
    public float[][] findSolutionMatrix(){
        insertion[0][0] = 0;
        deletion[0][0] = 0;
        optAlignFree[0][0] = 0;

        for (int row=1; row<rows; row++){
            deletion[row][0] = Integer.MIN_VALUE;
            insertion[row][0] = Integer.MIN_VALUE;
            optAlignFree[row][0] = 0; // A(k,0) = g(k) = go+ge*k
        }

        for (int column=1; column<columns; column++){
            deletion[0][column] = Integer.MIN_VALUE;
            insertion[0][column] = Integer.MIN_VALUE;
            optAlignFree[0][column] = 0; // A(k,0) = g(k) = go+ge*k
        }

        for(int row=1; row < rows; row++){
            for(int column=1; column < columns; column++){

                String asSeq1 = String.valueOf(seq1.charAt(row -1));
                String asSeq2 = String.valueOf(seq2.charAt(column -1));

                float matchValue = scorematrix.get(new Pair(asSeq1, asSeq2));
                insertion[row][column] = Math.max(optAlignFree[row-1][column]+go+ge, insertion[row-1][column]+ge);
                deletion[row][column] = Math.max(optAlignFree[row][column-1]+go+ge, deletion[row][column-1]+ge);
                optAlignFree[row][column] = Math.max(Math.max(optAlignFree[row-1][column-1] + matchValue, insertion[row][column]), deletion[row][column] );
            }
        }
        return optAlignFree;


    }


    //Tracebackmatrix
    public String[] findTraceback(String seq1, String seq2){
        seq1 = " " + seq1;
        seq2 = " " + seq2;

        StringBuilder alignedStrand1 = new StringBuilder();
        StringBuilder alignedStrand2 = new StringBuilder();

        int row = 0;
        int column = 0;

        int maxrow = optAlignFree.length-1;
        int maxcol = optAlignFree[0].length-1;



        //Start from  bottom right corner of the solution matrix
        float[] last_row = optAlignFree[rows -1];
        float[] last_col = new float[optAlignFree.length];

        for (int i = 1; i < optAlignFree.length; i++){
            last_col [i] = optAlignFree[i][columns -1];
        }


        if (findMaximumArrayValue(last_row) >= findMaximumArrayValue(last_col)){
            row = rows -1;
            column = findIndex(last_row);
        }else{
            row = findIndex(last_col);
            column = columns -1;
        }

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

        //while not reached top left corner of solution matrix, do the following:
        while (row > 0 && column > 0){
            if ((Math.abs(optAlignFree[row][column] - deletion[row][column])) < 0.0001){
                int extension = 1;
                for(int k=1; k<=column; k++){
                    if(Math.abs(optAlignFree[row][column-k] +go+ge*k - optAlignFree[row][column]) < 0.0001){
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

            else if ((Math.abs(optAlignFree[row][column] - insertion[row][column])) < 0.0001) {
                int extension = 1;
                for (int k=1; k<=row; k++){
                    if(Math.abs((optAlignFree[row-k][column]+go+ge*k) - optAlignFree[row][column]) < 0.0001){
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

    public float score(){

        float[] last_row = optAlignFree[rows -1];
        float[] last_col = new float[optAlignFree.length];

        for (int i = 1; i < optAlignFree.length; i++){
            last_col [i] = optAlignFree[i][columns -1];
        }

        int row=0;
        int column=0;


        //Print arrays and see
        if (findMaximumArrayValue(last_row) >= findMaximumArrayValue(last_col)){
            row = rows -1;
            column = findIndex(last_row);

        }else{
            row = findIndex(last_col);
            column = columns -1;

        }

        float max_score = optAlignFree[row][column];
        return (float)Math.round(max_score*10)/10;
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


    public void printAlignment(String alignment1, String alignment2){
        System.out.println(alignment1 +"\n"+ alignment2 + "\n");
    }

    public float checkscoreFree(HashMap<Pair, Float> scorematrix, String [] alignment){
        String[] outputStrands = alignment;

        float matchValue = 0;
        float checkscore = 0;
        int firstMatch = 0;
        int lastMatch = 0;

        boolean gapSeq1 = false;
        boolean gapSeq2 = false;


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
            if (outputStrands[0].charAt(i) == '-'){
                if (!gapSeq1){
                    checkscore += go+ge;
                    gapSeq1 = true;
                    gapSeq2 = false;
                } else {
                    checkscore += ge;
                }
            } else if (outputStrands[1].charAt(i) == '-'){
                if (!gapSeq2){
                    checkscore += go+ge;
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

        checkscore = (float)Math.round(checkscore*10)/10;
        return checkscore;
    }

    public void printGotohFree(float [][] matrix) {
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
            optAlignFree = new float[rows][columns];
            insertion = new float[rows][columns];
            deletion = new float[rows][columns];
            findSolutionMatrix();
            String [] alignedseq = findTraceback(seq1, seq2);

            if (check) {
                float score = checkscoreFree(scorematrix, alignedseq);
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
                printDP_matrix(optAlignFree, seq1, seq2, new File(dpmatrices, "GotohFreeshift"));
                System.out.println("Corresponding optimal alignment matrix: ");
                printGotohFree(optAlignFree);
                System.out.println("Corresponding insertion matrix: ");
                printGotohFree(insertion);
                System.out.println("Corresponding deletion matrix: ");
                printGotohFree(deletion);
            }

        }
    }

    /*public static void main(String[] args) {
        Alignment alignmentgeneral = new Alignment();
        String seq1 = "YGKDQQEAALVDMVNDGVEDLRCKYISLIYTNYEAGKDDYVKALPGQLKPFETLLSQNQGGKTFIVGDQISFADYNLLDLLLIHEVLAPGCLDAFPLLSAYVGRLSA";
        String seq2 = "MKRESHKHAEQARRNRLAVALHELASLIPAEWKQQNVSAAPSKATTVEAACRYIRHLQQNGS";

        GotohFreeshift gotohfreeshift = new GotohFreeshift(seq1, seq2);
        gotohfreeshift.findSolutionMatrix();
        //alignment.printDPMatrix("YGKDQQEAALVDMVNDGVEDLRCKYISLIYTNYEAGKDDYVKALPGQLKPFETLLSQNQGGKTFIVGDQISFADYNLLDLLLIHEVLAPGCLDAFPLLSAYVGRLSA", "MKRESHKHAEQARRNRLAVALHELASLIPAEWKQQNVSAAPSKATTVEAACRYIRHLQQNGS", gotohfreeshift.optAlignFree);
        //alignment.printDPMatrix("YGKDQQEAALVDMVNDGVEDLRCKYISLIYTNYEAGKDDYVKALPGQLKPFETLLSQNQGGKTFIVGDQISFADYNLLDLLLIHEVLAPGCLDAFPLLSAYVGRLSA", "MKRESHKHAEQARRNRLAVALHELASLIPAEWKQQNVSAAPSKATTVEAACRYIRHLQQNGS", gotohfreeshift.insertion);
        //alignment.printDPMatrix("YGKDQQEAALVDMVNDGVEDLRCKYISLIYTNYEAGKDDYVKALPGQLKPFETLLSQNQGGKTFIVGDQISFADYNLLDLLLIHEVLAPGCLDAFPLLSAYVGRLSA", "MKRESHKHAEQARRNRLAVALHELASLIPAEWKQQNVSAAPSKATTVEAACRYIRHLQQNGS", gotohfreeshift.deletion);

        gotohfreeshift.findTraceback(seq1, seq2);

        Matrices matrix = new Matrices();
        HashMap<Pair, Float> scoreMatrix = matrix.getscoreMatrices();
        gotohfreeshift.findSolutionMatrix();
        String[] alignedSequences = gotohfreeshift.findTraceback(seq1, seq2);
        float alignedScore = gotohfreeshift.score();
        gotohfreeshift.printAlignment(seq1, seq2);
        float checkScore = gotohfreeshift.checkscoreFree(scoreMatrix, alignedSequences[0], alignedSequences[1],alignedScore);
        System.out.println(alignedScore+ " " + checkScore);
    }*/




}

