import java.util.Random;

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
            candidatesB[i] = random.nextInt(30);
        }
        for(int i = 0; i < candidatesA.length; i++){
            System.out.println("a["+(i)+"] : "+candidatesA[i]+"  b["+(i)+"] : "+candidatesB[i]);
        }
//        for(int i = 0; i < nGenerations; i++){
              select(candidatesA, candidatesB, sortingAlgorithm, p);
              for(int i = 0; i < candidatesA.length; i++){
                  System.out.println("a["+(i)+"] : "+candidatesA[i]+"  b["+(i)+"] : "+candidatesB[i]);
              }



//            candidatesA = crossover(candidatesA, p);
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

//        for (int i=5; i<res.length; i++) {
//            System.out.println("res["+i+"] : "+res[i]);
//            System.out.println("error["+i+"] : "+error[i]);
//        }



        for (int i = 0; i < n; i++) {
            int x = random.nextInt(probability[n-1]);

            for(int j = 0; j < n; j++){
                if(x <= probability[j]){
                    candidatesA[i] = candidatesA[j];
                    candidatesB[i] = candidatesB[j];
                    break;
                }
            }
        }
    }



//    private double[] crossover(double[] candidates, Problem p) {
//
//        int n = candidates.length;
//
//        for(int i = 0; i < n; i += 2){
//            double x1 = candidates[i];
//            double x2 = candidates[i+1];
//            double y1 = p.fit(x1);
//            double y2 = p.fit(x2);
//
//            double in = (y2-y1) / (x2-x1);
//            if(in > 0) {
//                candidates[i] += 1;
//                candidates[i+1] += 1;
//            }
//            else if(in < 0) {
//                candidates[i] -= 1;
//                candidates[i+1] -= 1;
//            }
//        }
//
//        return candidates;
//    }
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



}
