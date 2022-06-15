import java.util.*;

public class GeneticAlgorithm {
    public double solve(int nCandidates, double[] sortingAlgorithm, Problem p){
        int nGenerations = 1000;
        double[] candidatesA = new double[nCandidates];
        double[] candidatesB = new double[nCandidates];
        Random random = new Random();

        double max = 0;
        int x = 0;

        for(int i = 0; i < nCandidates; i++){
            candidatesA[i] = random.nextInt(30); // 랜덤 4개
            candidatesB[i] = random.nextInt(30) - 15;
        }
        for(int i = 0; i < candidatesA.length; i++){
            System.out.println("a["+(i)+"] : "+candidatesA[i]+"  b["+(i)+"] : "+candidatesB[i]);
        }
//        for(int i = 0; i < nGenerations; i++){
              select(candidatesA, candidatesB, sortingAlgorithm, p);
              for(int i = 0; i < candidatesA.length; i++){
                  System.out.println("a["+(i)+"] : "+candidatesA[i]+"  b["+(i)+"] : "+candidatesB[i]);
              }
              crossover(candidatesA, candidatesB, sortingAlgorithm, p);
        for(int i = 0; i < candidatesA.length; i++){
            System.out.println("a["+(i)+"] : "+candidatesA[i]+"  b["+(i)+"] : "+candidatesB[i]);
        }
//            candidatesA = mutate(candidatesA);
//        }

//        for(int i = 0; i < nCandidates; i++){
//            double fx = p.fit(candidatesA[i]);
//            if(max < fx){
//                max = fx;
//                x = i;
//            }
//        }

        return candidatesA[x];
    }

    private void select(double[] candidatesA, double[] candidatesB, double[] sortingAlgorithm, Problem p) {
        int n = candidatesA.length;
        int sort_length = sortingAlgorithm.length;
        double[] res = new double[sort_length];
        double[] error = new double[sort_length];
        double[] errorRate = new double[n];
        Random random = new Random();
        double errorRateSum = 0;
        int[] probability = new int[n];

        for(int i = 0; i < n; i++){
            System.out.println("a : "+candidatesA[i]+" b : "+candidatesB[i]);
            for(int j = 5; j < sort_length; j++){
                res[j] = p.fit(j, candidatesA[i], candidatesB[i]);

                error[j] = Math.abs(sortingAlgorithm[j] - res[j]);

//                System.out.println("res["+j+"] : "+res[j]);
//                System.out.println("error["+j+"] : "+error[j]);


            }
            double errorSum = 0;

            for(int k = 5; k < sort_length; k++){
                errorSum += error[k];
            }
            errorRate[i] = errorSum / 15;
            System.out.println("Error rate : "+errorRate[i]);
            errorRateSum += errorRate[i];
        }
        System.out.println(errorRateSum);

        probability[0] = (int)((errorRate[0] / errorRateSum) * 100);
        System.out.println("probability "+ (1) + " : "+ probability[0]);
        for(int i = 1; i < n; i++){
            probability[i] = (int)((errorRate[i] / errorRateSum) * 100) + probability[i-1];
            System.out.println("probability "+ (i+1) + " : "+ probability[i]);
        }


        for(int i = 0; i < n; i++){
            HashSet<Integer> set = new HashSet<>();
            while(set.size() < 3) {
                int x = random.nextInt(probability[n - 1]);
                for(int j = 0; j < n; j++){
                    if(x <= probability[j]){
                        set.add(j);
                        break;
                    }
                }
            }

            for(int j = 0; j < set.size(); j++){
                if(!set.contains(j)){
                    candidatesA[i] = candidatesA[j];
                    candidatesB[i] = candidatesB[j];
                }
            }
        }

    }



    private void crossover(double[] candidatesA, double[] candidatesB, double[] sortingAlgorithm, Problem p) {

        int n = candidatesA.length;

        for(int i = 0; i < n; i += 2){
            double a1 = candidatesA[i];
            double a2 = candidatesA[i+1];
            double b1 = candidatesB[i];
            double b2 = candidatesB[i+1];

            double errorRate1 = errorRate(a1, b1, sortingAlgorithm, p);
            double errorRate2 = errorRate(a2, b2, sortingAlgorithm, p);


            if(errorRate1 > errorRate2) {
                if(a1 > a2){ // 1/ 10
                    candidatesA[i] = (int)((a1 + a2) / 2) - 1;
                    candidatesA[i+1] = (int)((a1 + a2) / 2);
                }
                else{ // 10 1
                    candidatesA[i] = (int)((a1 + a2) / 2) + 1;
                    candidatesA[i+1] = (int)((a1 + a2) / 2);
                }
                if(b1 > b2) {
                    candidatesB[i] = (int)((b1 + b2) / 2) - 1;
                    candidatesB[i+1] = (int)((b1 + b2) / 2);
                }
                else{
                    candidatesB[i] = (int)((b1 + b2) / 2) + 1;
                    candidatesB[i+1] = (int)((b1 + b2) / 2);
                }
            }
            else{
                if(a1 > a2){ // 1/ 10
                    candidatesA[i] = (int)((a1 + a2) / 2) + 1;
                    candidatesA[i+1] = (int)((a1 + a2) / 2);
                }
                else{ // 10 1
                    candidatesA[i] = (a1 + a2) / 2 - 1;
                    candidatesA[i+1] = (int)((a1 + a2) / 2);
                }
                if(b1 > b2) {
                    candidatesB[i] = (b1 + b2) / 2 + 1;
                    candidatesB[i+1] = (int)((b1 + b2) / 2);
                }
                else{
                    candidatesB[i] = (b1 + b2) / 2 - 1;
                    candidatesB[i+1] = (int)((b1 + b2) / 2);
                }
            }
        }

    }
//
//
//
//    private double[] mutate(double[] candidates) {
//        for(int i = 0; i < candidates.length; i++) {
//            if((int)(Math.random() * 100) < 5) {
//                if((int) (Math.random() * 2) == 0){
//                    candidates[i] += 1;
//                }
//                else{
//                    candidates[i] -= 1;
//                }
//            }
//        }
//        return candidates;
//    }
    private double errorRate(double candidatesA, double candidatesB, double[] sortingAlgorithm, Problem p){
        int sort_length = sortingAlgorithm.length;
        double[] res = new double[sort_length];
        double errorSum = 0;

        for(int i = 5; i < sort_length; i++) {
            res[i] = p.fit(i, candidatesA, candidatesB);
            errorSum += Math.abs(sortingAlgorithm[i] - res[i]);
        }

        return errorSum / 15;
    }


}
