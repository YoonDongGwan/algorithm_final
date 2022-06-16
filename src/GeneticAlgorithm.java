import java.util.*;

public class GeneticAlgorithm {
    public double[] solve(int nCandidates, double[] sortingAlgorithm, Problem p){
        int nGenerations = 100000; // 10만번의 세대를 거침
        double[] candidatesA = new double[nCandidates];
        double[] candidatesB = new double[nCandidates];
        Random random = new Random();

        double minimumErrorRate = 0;
        int minimumIndex = 0;

        for(int i = 0; i < nCandidates; i++){
            candidatesA[i] = random.nextInt(100); // 랜덤 4개
            candidatesB[i] = random.nextInt(100) - 50;
        }
//        System.out.println("랜덤으로 선택된 처음 세대의 a, b들");
//        for(int i = 0; i < candidatesA.length; i++){
//            System.out.println("a["+(i)+"] : "+candidatesA[i]+"  b["+(i)+"] : "+candidatesB[i]);
//        }
        for(int i = 0; i < nGenerations; i++){
//            System.out.println("\n"+(i+1)+"번째 세대");
            select(candidatesA, candidatesB, sortingAlgorithm, p);
            crossover(candidatesA, candidatesB, sortingAlgorithm, p);
            mutate(candidatesA, candidatesB);
//            if(i%10000 == 0){
//                double averageErrorRate = 0;
//                for(int j = 0; j < nCandidates; j++) {
//                    averageErrorRate += errorRate(candidatesA[j], candidatesB[j], sortingAlgorithm, p);
//                }
//                System.out.println("Average Error rate : " + averageErrorRate / nCandidates);
//            }
//              for(int i = 0; i < candidatesA.length; i++){
//                  System.out.println("a["+(i)+"] : "+candidatesA[i]+"  b["+(i)+"] : "+candidatesB[i]);
//              }
        }

        for(int i = 0; i < nCandidates; i++){ // 마지막 세대에서 에러율이 가장 낮은 최적해(또는 최적해에 가까운 해)를 선택한다.
            double errorRate = errorRate(candidatesA[i], candidatesB[i], sortingAlgorithm, p);
            if(minimumErrorRate < errorRate){
                minimumErrorRate = errorRate;
                minimumIndex = i;
            }
        }
        System.out.println("유전 알고리즘 결과 : y = "+candidatesA[minimumIndex]+"x "+candidatesB[minimumIndex]);
        //System.out.println("a : "+candidatesA[minimumIndex]+" b : "+candidatesB[minimumIndex]);
        //System.out.println("Error rate : " + errorRate(candidatesA[minimumIndex], candidatesB[minimumIndex], sortingAlgorithm, p));

        double[] opt = {candidatesA[minimumIndex], candidatesB[minimumIndex]};
        return opt;
    }

    private void select(double[] candidatesA, double[] candidatesB, double[] sortingAlgorithm, Problem p) {
        int n = candidatesA.length; // 후보해 집합의 길이
        double[] errorRate = new double[n]; // 에러율을 담을 배열
        Random random = new Random();
        double errorRateSum = 0;
        int[] probability = new int[n];

        for(int i = 0; i < n; i++){
//            System.out.println("a : "+candidatesA[i]+" b : "+candidatesB[i]);

            errorRate[i] = errorRate(candidatesA[i], candidatesB[i], sortingAlgorithm, p); // 선택된 후보해 a, b와 그에 따른 에러율
//            System.out.println("Error rate : "+errorRate[i]);

            errorRateSum += errorRate[i]; // 각 에러율에 비율의 따라 가중치를 두어 선택하기 위해, 에러율을 모두 더함.
        }
//        System.out.println("Error rate sum : " + errorRateSum);


        probability[0] = (int)((errorRate[0] / errorRateSum) * 100);
        for(int i = 1; i < n; i++){
            probability[i] = (int)((errorRate[i] / errorRateSum) * 100) + probability[i-1]; // 에러율에 따라 확률 분포를 나누었다.
        }

        // 에러율이 작은 순서대로 선택 확률이 높아야 하기 때문에, 여기서 probability는 선택되지 않을 확률로 계산하였다.
        for(int i = 0; i < n; i++){
            HashSet<Integer> set = new HashSet<>();
            while(set.size() < 3) { // 해쉬셋에 선택하지 않을 후보해 3개를 담는다.
                int x = random.nextInt(probability[n - 1]); // 누적 확률에 따라 probability[n-1]은 100에 근접한 수가 되었을 것이다. 따라서 랜덤 변수의 최댓값을 그에 맞춰 설정해준다.
                for(int j = 0; j < n; j++){
                    if(x <= probability[j]){    // 랜덤 변수의 값이 probability[j]보다 크면 다음 인덱스로, 작거나 같으면 현재 인덱스의 값을 해쉬셋에 담는다.
                        set.add(j);
                        break;
                    }
                }
            }

            for(int j = 0; j < set.size(); j++){ // 해쉬셋에 담기지 않은 1개의 후보해가 선택될 것이다.
                if(!set.contains(j)){
                    candidatesA[i] = candidatesA[j];
                    candidatesB[i] = candidatesB[j];
                }
            }
        } // 이 과정을 총 4번 반복하개 된다.

    }



    private void crossover(double[] candidatesA, double[] candidatesB, double[] sortingAlgorithm, Problem p) {

        int n = candidatesA.length;

        for(int i = 0; i < n; i += 2){
            double a1 = candidatesA[i];
            double a2 = candidatesA[i+1];
            double b1 = candidatesB[i];
            double b2 = candidatesB[i+1];

            // 배열에서 a, b 원소를 각각 2개씩 뽑아 에러율을 계산하였다.
            double errorRate1 = errorRate(a1, b1, sortingAlgorithm, p);
            double errorRate2 = errorRate(a2, b2, sortingAlgorithm, p);

            // 에러율에 따라 교차 연산의 수행결과를 바꿔주기로 하였다.
            if(errorRate1 > errorRate2) { // 에러율이 2가 더 낮다는 것은 a2, b2 값이 더 최적해에 가깝다는 의미이다.
                candidatesA[i] = a2;
                candidatesA[i+1] = (a1+a2) / 2;
                candidatesB[i] = b2;
                candidatesB[i+1] = (b1+b2) / 2;
            }
            else{ // errorRate1 <= errorRate2 이 경우 a1, b1 이 더 최적해에 가깝다는 의미이다.
                candidatesA[i] = a1;
                candidatesA[i+1] = (a1+a2) / 2;
                candidatesB[i] = b1;
                candidatesB[i+1] = (b1+b2) / 2;
            }
        }
    }



    private void mutate(double[] candidatesA, double[] candidatesB) {
        for(int i = 0; i < candidatesA.length; i++) {
            if((int)(Math.random() * 100) < 1) { // 1% 확률로 돌연변이 발생
                switch((int)(Math.random() * 4)){ // 4가지의 돌연변이 경우
                    case 0:
                        candidatesA[i] += 1;    // a : +1
                        break;
                    case 1:
                        if(candidatesA[i] - 1 > 0) { // a : -1
                            candidatesA[i] -= 1;
                        }
                        break;
                    case 2:
                        candidatesB[i] += 1;    // b : +1
                        break;
                    case 3:
                        candidatesB[i] -= 1;    // b : -1
                        break;
                }
            }
        }

    }
    // 후보해로 선정된 a와 b를 ax + b 식에 대입해 에러율을 구하는 함수
    private double errorRate(double candidatesA, double candidatesB, double[] sortingAlgorithm, Problem p){
        int sort_length = sortingAlgorithm.length; // 입력된 정렬 알고리즘의 배열 길이
        double errorSum = 0; // 에러 값을 더할 변수

        for(int i = 0; i < sort_length; i++) {
            double result = p.fit(i, candidatesA, candidatesB);
            errorSum += Math.abs(sortingAlgorithm[i] - result); // 정렬 알고리즘의 i번째 결과와 ax+b의 결과를 서로 빼 절대값 연산을 취하여 에러 값의 합을 산출하였다.
        }

        return errorSum / sort_length; // 총 더해진 에러 값의 평균을 구하면, 선택된 a, b의 에러율이 나오게 된다.
    }


}
