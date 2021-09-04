package gorI;

public class Sequence {

    public String seq;
    public String secStructure;
    public String titel = "";

    public String pH;
    public String pE;
    public String pC;

    public String predictedSecStructure;

    public Sequence(String seq, String titel) {
        this.seq = seq;
        this.titel = titel;
    }

    public Sequence() {
    }

    public Sequence(String seq, String secStructure, String titel) {
        this.seq = seq;
        this.secStructure = secStructure;
        this.titel = titel;
    }


}
