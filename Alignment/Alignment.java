import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.io.*;

public class Alignment {
    int gap;
    HashMap<Pair, Float> scorematrix;

    public Alignment(String pairpath, String seqlibpath, String matrixpath, int gap) {

        readPairsFile(pairpath);
        readSeqlibFile(seqlibpath);
        scorematrix = Matrices.getscoreMatrices(matrixpath);
        this.gap = gap;

    }

    //get .pairs and use this to receive sequences from .seqlib
    public List<Pair> namepairs =  new ArrayList<>();

    //read pairs file
    public void readPairsFile( String sanitypairs){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(sanitypairs));
            String line = reader.readLine();
            while (line != null){
                String[] columns = line.split(" ");
                String id1 = columns[0];
                String id2 = columns[1];
                Pair sanitypair = new Pair(id1, id2);
                namepairs.add(sanitypair);

                line = reader.readLine();
            }
            //namepairs.stream().forEach(elem -> System.out.println(elem));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String,String> seqlibseqs = new HashMap<>();

    //read seqlib file
    public void readSeqlibFile(String seqlib){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(seqlib));
            String line = reader.readLine();
            while (line != null){
                String[] column2 = line.split(":");
                String id = column2[0];
                String sequence = column2[1];
                seqlibseqs.put(id, sequence);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printDP_matrix(float matrix[][], String seq1, String seq2, File filename) {
        StringBuilder builder = new StringBuilder();
        builder.append("   ");
        for (int j = 1; j <= seq2.length(); j++) {
            builder.append("     " + seq2.charAt(j - 1));
        }
        builder.append("\n");

        for (int i = 0; i <= seq1.length(); i++) {
            if (i > 0) {
                builder.append(seq1.charAt(i - 1) + "  ");
            } else {
                builder.append("   ");
            }
            for (int j = 0; j <= seq2.length(); j++) {
                builder.append(matrix[i][j] + "  ");
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            builder.append("\n");
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printScores(float scores, String header1, String header2){
        System.out.println(header1 + " " + header2 + " " + scores);
    }

    public static void printAli(float scores, String header1, String header2, String alignedSeq1, String alignedSeq2){
        System.out.println(">"+header1 + " " + header2 + " " + scores + "\n" + header1 + ":" + alignedSeq1+"\n" + header2 + ":" + alignedSeq2);
    }

    public static void printHtml(float scores, String header1, String seq1, String header2, String seq2, String alignedSeq1, String alignedSeq2){
        int seqDiff = 0;
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("Score: " + scores + "bits");
        System.out.println("Length of first sequence: " + seq1.length() + " " + "Length of second sequence: " + seq2.length());
        System.out.println("Length of alignment: " + alignedSeq1.length());
        int identity = 1 - (hammingDist(alignedSeq1, alignedSeq2) / alignedSeq1.length());
        System.out.println("Identity in %: " + identity);
        System.out.println("Matches: " + getNumofMatches(alignedSeq1, alignedSeq2));
        System.out.println("--------------------------------------------------------------------------------------------------");


        //print alignment in human readable format
        int i = 0;
        int seqlength = Math.max(header1.length(), header2.length());

        String matches = "";
        while (i < alignedSeq1.length()) {
            if (alignedSeq1.charAt(i) == alignedSeq2.charAt(i)){
                matches += "|";
            }else {
                matches += " ";
            }

            i++;
        }

        if(header1.length()  > header2.length()){
            seqDiff = header1.length() - header2.length();
            System.out.print(header1 + "\t" + alignedSeq1 + "\n");
            System.out.print(" ".repeat(seqlength) + "\t" + matches +"\n");
            System.out.print(header2 + " ".repeat(seqDiff) + "\t" + alignedSeq2 + "\n");
        }
        else {
            seqDiff = header2.length() - header1.length();
            System.out.println(header1 + " ".repeat(seqDiff) +"\t" + alignedSeq1);
            System.out.print(" ".repeat(seqlength) + "\t" + matches + "\n");
            System.out.println(header2 + "\t" + alignedSeq2 );
        }

    }

    public static int getNumofMatches(String str1, String str2){
        int i = 0, match = 0;
        while (i < str1.length())
        {
            if (str1.charAt(i) == str2.charAt(i))
                match++;
            i++;
        }
        return match;
    }


    public static int hammingDist(String str1, String str2)
    {
        int i = 0, count = 0;
        while (i < str1.length())
        {
            if (str1.charAt(i) != str2.charAt(i))
                count++;
            i++;
        }
        return count;
    }



    public String getSequence(String seqid){
        return seqlibseqs.get(seqid);
    }

    //find average sequence length of two sequences
    public int averageSequenceLength(String sequence1, String sequence2){
        return (sequence1.length() + sequence2.length()) / 2;
    }

    public String generateRandomString( int targetStringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        return generatedString.toUpperCase();
    }


}
