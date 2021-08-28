import java.util.Arrays;

public class Recursion {

    private static int gap = -4;

    public int recursionAlginment(String align1, String align2) { //
        String seq1 = " " + align1;
        String seq2 = " " + align2;



        int[][] recursionScoreMatrix = new int[seq1.length()][seq2.length()];
        System.out.println(seq1.length());
        System.out.println(seq2.length());
        
        for (int[] row : recursionScoreMatrix)
            Arrays.fill(row, Integer.MIN_VALUE);

        return recursionScore(seq1.length() - 1, seq2.length() - 1,seq1, seq2, recursionScoreMatrix);
    }

    public static int recursionScore(int row, int column, String align1, String align2, int[][] recursionScoreMatrix) {

        //f(e,e) = 0
        if(row == 0 && column == 0){
            return 0;
        }

        //f(e,Px) = f(e,P)+gap
        if(row == 0) {
            return (column) * gap;
        }

        //f(Px,e) = f(P,e)+gap
        if(column == 0) {
            return (row) * gap;
        }

        
        if(recursionScoreMatrix[row][column] != Integer.MIN_VALUE) {
            return recursionScoreMatrix[row][column];
        }

        int matchScore;
        if (align2.charAt(column) == align1.charAt(row)) {
            matchScore = 3;
        } else{
            matchScore = -2;
        }

        //f(Px,P'y) = max{ f(P,P'}+score, f(P,P'y)+gap, f(Px,P')+gap } with align1=P and align2=P'
        int noGap = recursionScore(row-1, column-1, align1, align2, recursionScoreMatrix) + matchScore;
        int addGapAlign1 = recursionScore( row-1, column, align1, align2,recursionScoreMatrix) + gap;
        int addGapAlign2 = recursionScore(row, column-1, align1, align2, recursionScoreMatrix) + gap;

        return Math.max(noGap, Math.max(addGapAlign1, addGapAlign2));

    }

    public static void main(String[] args) {
        Recursion sequence = new Recursion();
        sequence.recursionAlginment("TTACGTAAGC","TATAAT");
    }

}



