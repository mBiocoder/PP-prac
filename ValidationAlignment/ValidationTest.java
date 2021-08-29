import java.util.ArrayList;

public class ValidationTest extends ValidationReadFile {


    public int correctAligned = 0;

    public int N = 0;
    public float sens = 0;
    public float spec = 0;
    public float cov = 0;
    public float meanShiftError = 0;
    public float inverseMeanShiftError = 0;
    public float counterCov = 0;

    public ArrayList<int[]> predictionAlignment = new ArrayList<>();
    public ArrayList<int[]> referenceAlignment = new ArrayList<>();

    public ArrayList<int[]> invPredictionAlignment = new ArrayList<>();
    public ArrayList<int[]> invReferenceAlignment = new ArrayList<>();

    public ValidationTest(String testCasePath) {
        super(testCasePath);
    }


    public void alignedPredictionINV(String[] prediction) {
        invPredictionAlignment = new ArrayList<>();

        String templatePred = prediction[1];
        String targetPred = prediction[0];

        int counterTemplatePred = 0;
        int counterTargetPred = 0;


        //check inverse prediction
        for (int index = 0; index < templatePred.length(); index++) {
            if (templatePred.charAt(index) != '-' && targetPred.charAt(index) != '-') {
                int[] yes = {counterTemplatePred, counterTargetPred};
                invPredictionAlignment.add(yes);
            }
            if (templatePred.charAt(index) == '-' && targetPred.charAt(index) != '-') {
                counterTemplatePred += 0;
                counterTargetPred += 1;
                continue;
            }
            if (targetPred.charAt(index) == '-' && templatePred.charAt(index) != '-') {
                counterTargetPred += 0;
                counterTemplatePred += 1;
                continue;
            }
            counterTargetPred += 1;
            counterTemplatePred += 1;
        }

    }

    public void alignedReferenceINV(String[] reference) {
        invReferenceAlignment = new ArrayList<>();

        String templateRef = reference[1];
        String targetRef = reference[0];

        int counterTargetRef = 0;
        int counterTemplateRef = 0;

        //check inverse reference
        for (int index = 0; index < templateRef.length(); index++) {
            if (templateRef.charAt(index) != '-' && targetRef.charAt(index) != '-') {
                int[] yes = {counterTemplateRef, counterTargetRef};
                invReferenceAlignment.add(yes);
            }
            if (templateRef.charAt(index) == '-' && targetRef.charAt(index) != '-') {
                counterTemplateRef += 0;
                counterTargetRef += 1;
                continue;

            }
            if (targetRef.charAt(index) == '-' && templateRef.charAt(index) != '-') {
                counterTargetRef += 0;
                counterTemplateRef += 1;
                continue;
            }
            counterTemplateRef += 1;
            counterTargetRef += 1;
        }

    }


    public void alignedPrediction(String[] prediction) {
        predictionAlignment = new ArrayList<>();

        String templatePred = prediction[0];
        String targetPred = prediction[1];

        int counterTemplatePred = 0;
        int counterTargetPred = 0;


        //check prediction
        for (int index = 0; index < templatePred.length(); index++) {
            if (templatePred.charAt(index) != '-' && targetPred.charAt(index) != '-') {
                int[] yes = {counterTemplatePred, counterTargetPred};
                predictionAlignment.add(yes);
            }
            if (templatePred.charAt(index) == '-' && targetPred.charAt(index) != '-') {
                counterTemplatePred += 0;
                counterTargetPred += 1;
                continue;
            }
            if (targetPred.charAt(index) == '-' && templatePred.charAt(index) != '-') {
                counterTargetPred += 0;
                counterTemplatePred += 1;
                continue;
            }
            counterTargetPred += 1;
            counterTemplatePred += 1;
        }

    }

    public void alignedReference(String[] reference) {
        referenceAlignment = new ArrayList<>();

        String templateRef = reference[0];
        String targetRef = reference[1];

        int counterTargetRef = 0;
        int counterTemplateRef = 0;

        //check reference
        for (int index = 0; index < templateRef.length(); index++) {
            if (templateRef.charAt(index) != '-' && targetRef.charAt(index) != '-') {
                int[] yes = {counterTemplateRef, counterTargetRef};
                referenceAlignment.add(yes);
            }
            if (templateRef.charAt(index) == '-' && targetRef.charAt(index) != '-') {
                counterTemplateRef += 0;
                counterTargetRef += 1;
                continue;

            }
            if (targetRef.charAt(index) == '-' && templateRef.charAt(index) != '-') {
                counterTargetRef += 0;
                counterTemplateRef += 1;
                continue;
            }
            counterTemplateRef += 1;
            counterTargetRef += 1;
        }

    }

    public void correctlyAlig() {
        //counter coverage (number of target residues (incor. or cor.) in both alignments
        counterCov = 0;
        //counter of correct aligned residues
        correctAligned = 0;
        for (int[] refPair : referenceAlignment) {
            for (int[] predPair : predictionAlignment) {
                if (refPair[0] == predPair[0] && refPair[1] == predPair[1]) {
                    correctAligned += 1;
                }
                if (refPair[1] == predPair[1]) {
                    counterCov += 1;
                }
            }
        }
    }

    public void sensAndSpecAndCov(String[] reference, String[] prediction) {
        sens = 0;
        spec = 0;
        cov = 0;

        String templateRef = reference[0];
        String targetRef = reference[1];

        String templatePred = prediction[0];
        String targetPred = prediction[1];

        //number of residues aligned by prediction
        int numberOfResiduesInPred = 0;

        //number of residues aligned by prediction
        int alignedResPred = 0;

        //number of residues aligned by reference
        int correctlyResRef = 0;

        //check reference
        for (int index = 0; index < templateRef.length(); index++) {
            if (templateRef.charAt(index) != '-' && targetRef.charAt(index) != '-') {
                correctlyResRef += 1;
            }
        }

        //check prediction
        for (int index = 0; index < templatePred.length(); index++) {
            if (templatePred.charAt(index) != '-' && targetPred.charAt(index) != '-') {
                //number of residues aligned by prediction
                alignedResPred += 1;
                numberOfResiduesInPred += 1;

            }
        }

        //specificity
        spec = (float) Math.round(((float) correctAligned / (float) alignedResPred) * 100000) / 100000;

        //sensitivity
        sens = (float) Math.round(((float) correctAligned / (float) correctlyResRef) * 100000) / 100000;

        //coverage
        cov = (float) counterCov / (float) (numberOfResiduesInPred);

    }

    public void invMSE() {
        ArrayList<Integer> residueShift = new ArrayList<>();
        for (int[] predPair : invPredictionAlignment) {
            for (int[] refPair : invReferenceAlignment) {
                if (refPair[1] == predPair[1]) {
                    residueShift.add(predPair[1]);
                }
            }
        }

        int sum = 0;
        for (Integer index : residueShift) {
            sum += Math.abs(shiftINV(index));
        }

        //inverse mean shift error
        inverseMeanShiftError = (float) Math.round((sum / (float) residueShift.size()) * 10000) / 10000;
    }

    private int shiftINV(int residue) {
        int referenceMatch = -1;
        int predictionMatch = -1;

        for (int[] refPairInverse : invReferenceAlignment) {
            if (refPairInverse[1] == residue) {
                referenceMatch = refPairInverse[0];
            }
        }

        for (int[] predPairInverse : invPredictionAlignment) {
            if (predPairInverse[1] == residue) {
                predictionMatch = predPairInverse[0];
            }
        }

        //calculate inverse shift
        if (referenceMatch != -1 && predictionMatch != -1) {
            return referenceMatch - predictionMatch;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public void mSE() {
        ArrayList<Integer> residueShift = new ArrayList<>();
        for (int[] predPair : predictionAlignment) {
            for (int[] refPair : referenceAlignment) {
                if (refPair[1] == predPair[1]) {
                    residueShift.add(predPair[1]);
                }
            }
        }
        int sum = 0;
        for (Integer i : residueShift) {
            sum += Math.abs(shift(i));
        }

        //mean shift error
        meanShiftError = (float) Math.round((sum / (float) residueShift.size()) * 10000) / 10000;

    }

    private int shift(int residue) { //residue position in the sequence
        int referenceMatch = -1;
        int predictionMatch = -1;

        for (int[] refPair : referenceAlignment) {
            if (refPair[1] == residue) {
                referenceMatch = refPair[0];
            }
        }

        for (int[] predPair : predictionAlignment) {
            if (predPair[1] == residue) {
                predictionMatch = predPair[0];
            }
        }

        //calculate shift
        if (referenceMatch != -1 && predictionMatch != -1) {
            return referenceMatch - predictionMatch;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public Float[] values() {
        //list all formulas
        Float[] allValuesInOrder = {sens, spec, cov, meanShiftError, inverseMeanShiftError};
        return allValuesInOrder;
    }

    public void printValiAli(String organism, float score, Float[] values, String seqIdTempPred, String sequenceTempPred, String seqIdTargPred, String sequenceTargPred, String seqIdTempRef, String sequenceTempRef, String seqIdTargRef, String sequenceTargRef) {
        //values[0] -> sens, values[1] -> spec, values[2] -> coverage, values[3] -> mean shift error, values[4] -> inverse mean shift error
        System.out.println(">" + organism + " " + score + " " + values[0] + " " + values[1] + " " + values[2] + " " + values[3] + " " + values[4] + "\n" + seqIdTempPred + ": " + sequenceTempPred + "\n" + seqIdTargPred + ": " + sequenceTargPred + "\n" + seqIdTempRef + ": " + sequenceTempRef + "\n" + seqIdTargRef + ": " + sequenceTargRef);
    }

    public void processSequences() {
        ValidationReadFile validationReadFile = new ValidationReadFile(testCasePath);
        ValidationTest validation = new ValidationTest(testCasePath);

        for (ArrayList<String> blockAli : refAndPredAlignments) {
            String organism = blockAli.get(0);

            float score = Float.parseFloat(blockAli.get(1));

            String seqIdTempPred = blockAli.get(2);
            String sequenceTempPred = blockAli.get(3);
            String seqIdTargPred = blockAli.get(4);
            String sequenceTargPred = blockAli.get(5);

            String[] prediction = {sequenceTempPred, sequenceTargPred};

            String seqIdTempRef = blockAli.get(6);
            String sequenceTempRef = blockAli.get(7);
            String seqIdTargRef = blockAli.get(8);
            String sequenceTargRef = blockAli.get(9);

            String[] reference = {sequenceTempRef, sequenceTargRef};


            //call methods
            validation.alignedReference(reference);
            validation.alignedPrediction(prediction);
            validation.alignedReferenceINV(reference);
            validation.alignedPredictionINV(prediction);
            validation.correctlyAlig();
            validation.sensAndSpecAndCov(reference, prediction);
            validation.mSE();
            validation.invMSE();


            validation.printValiAli(organism, score, validation.values(), seqIdTempPred, sequenceTempPred, seqIdTargPred, sequenceTargPred, seqIdTempRef, sequenceTempRef, seqIdTargRef, sequenceTargRef);

        }

    }
}
