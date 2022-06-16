# [기말고사] 최적화 알고리즘을 이용한 회귀식 추정
**201701685 윤동관 제출**

## 데이터 집합 선택
회귀식이란, 독립변수와 종속변수가 존재할 때, 종속변수로부터 오차가 가장 작은 직선의 방정식을 의미한다.

본 과제를 위해 독립변수와 종속변수의 데이터 집합을 고르는 과정에서, 지난 과제 중 정렬 알고리즘을 비교한 데이터가 있어 활용해보기로 하였다.

일반적으로 정렬 알고리즘의 소요 시간 그래프는 입력이 2의 17제곱부터 급격히 올라가는 형태이지만, Log2를 적용시킨 그래프는 선형적으로 표시되는 것을 확인했다.

### 버블정렬 그래프
![버블 로그x](https://user-images.githubusercontent.com/39906922/173866788-c7067a0a-5088-4e97-9223-5f08c3a285c9.png)

### 버블정렬 Log2 그래프
![버블 로그o](https://user-images.githubusercontent.com/39906922/173866873-cd70bc67-5963-4606-916a-59a22b90da3f.png)

### 정렬 알고리즘 Log2 그래프
![로그2무작위](https://user-images.githubusercontent.com/39906922/173868547-9ea57664-0db0-4914-a79c-bdaeb9fa5dd5.png)

따라서, 이번 과제에 사용할 데이터 집합은 Log2가 적용된 정렬 알고리즘들이다.

## 최적화 알고리즘
회귀식을 추정할 때, 사용할 알고리즘은 **유전 알고리즘**으로 선택하였다.

**유전 알고리즘**은 초기 세대의 해로부터 다음 세대를 생성해가며, 우수한 해를 선출하고, 마지막 세대에서 최적해 또는 최적해에 근접한 해를 선출하는 알고리즘이다.

유전 알고리즘의 수행 단계는 아래의 3단계로 나뉜다.

**1. 선택 연산**

**2. 교차 연산**

**3. 돌연변이 연산**  

첫 번째 선택 연산에서는, 현재 세대의 후보해 중 가장 우수한 후보해를 선택하는 연산으로, 무조건 최우수 후보해만 넣는 것이 아니라, 우수한 정도에 따라 가중치를 두어 선택될 확률을 조정하여 선택하는 방식이다.

두 번째 교차 연산에서는, 선택 연산에서 선택된 후보해들끼리 일련의 연산을 통해 새로운 후보해를 탄생시키는 연산이다.

세 번째 돌연변이 연산에서는, 교차 연산을 통해 탄생한 새로운 후보해를 아주 작은 확률이 일부 변형시키는 연산이다.   
돌연변이 연산은 후보해의 적합도를 오히려 낮추는 경우도 있는데, 다음 세대에 돌연변이가 이루어진 후보해와 다른 후보해의 교차 연산 후 탄생한 후보해가 우수한 후보해임을 기대하는 것에 의의를 두는 연산이다.


## 구현(JAVA)
#### 먼저 데이터 집합을 실수형 배열로 옮겨야 하는데, 여기선 버블 정렬을 선택하였다.
```
double[] bubble = {0, -8.0, -6.3, -6.1, -5.8, -5.0, -2.8, -0.8, 1.3, 2.6, 3.1, 3.3, 4.2, 5.7, 7.6, 10.2, 12.7, 14.5, 16.6, 18.6, 20.6};
```

---
#### 그 후 ax + b 형태의 방정식을 나타내기 위해, Problem 인터페이스를 생성하였다.
```
public interface Problem {
    double fit(double x, double a, double b);
}
```
---

#### 선택한 후보해 a와 b를 대입해 에러율을 구하는 함수를 구현하였다.
```
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
```
---
#### 선택 연산을 진행할 select 함수를 구현하였다.
```
private void select(double[] candidatesA, double[] candidatesB, double[] sortingAlgorithm, Problem p) {
        int n = candidatesA.length; // 후보해 집합의 길이
        double[] errorRate = new double[n]; // 에러율을 담을 배열
        Random random = new Random();
        double errorRateSum = 0;
        int[] probability = new int[n];

        for(int i = 0; i < n; i++){
            System.out.println("a : "+candidatesA[i]+" b : "+candidatesB[i]);

            errorRate[i] = errorRate(candidatesA[i], candidatesB[i], sortingAlgorithm, p); // 선택된 후보해 a, b와 그에 따른 에러율
            System.out.println("Error rate : "+errorRate[i]);

            errorRateSum += errorRate[i]; // 각 에러율에 비율의 따라 가중치를 두어 선택하기 위해, 에러율을 모두 더함.
        }


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
        } // 이 과정을 총 n번 반복하개 된다.

    }
```
선택연산을 구현함에 있어, 고민이 되었던 점은 원판의 비율을 어떻게 나눌 것인가였다.

그 이유는 에러율이 낮을 수록 선택될 확률이 높아져야 하기 때문이었는데, 에러율을 그대로 원판의 비율에 적용시키면, 에러율이 높을 수록 더 자주 선택되는 결과가 나온다.

따라서, 에러율을 그대로 비율에 적용시키되, 원판을 돌려 나오는 원소를 선택에서 제외하기로 하였다.

이렇게 한다면, 에러율이 가장 낮은 원소가 배열에 끝까지 남아있을 확률이 더 높게 되고, 에러율이 낮을 수록 선택되는 확률이 높아지게 되어 원하는 결과를 얻을 수 있었다.

---

#### 다음은, 교차 연산을 진행할 crossover 함수이다.
```
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
```
교차 연산을 구현할 때, 다음 세대의 해가 최적해에 더 가깝게 나올 수 있도록, 에러율에 따라 다음 세대의 후보해가 결정될 수 있도록 하였다.

첫번째 원소(a1, b1)의 에러율보다 두번째 원소(a2, b2)의 에러율이 더 낮다는 것은, a2, b2가 최적해에 더 가깝다는 의미이고,

따라서, a2, b2를 자식 세대에 담고, a1, a2 의 교차 연산 결과, b1, b2 의 교차 연산 결과를 자식 세대로 담아 새로운 후보해를 만들었다.

이 결과, 세대가 거듭될 수록 에러율이 감소하는 결과를 볼 수 있었다.

---
#### 다음은, 돌연변이 연산을 수행할 mutate 함수이다.
```
private void mutate(double[] candidatesA, double[] candidatesB) {
        for(int i = 0; i < candidatesA.length; i++) {
            if((int)(Math.random() * 100) < 10) { // 1% 확률로 돌연변이 발생
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
```
돌연변이 연산에 중요한 점은, 돌연변이가 얼마나 자주 혹은 가끔 생성되는가이다. 

돌연변이 확률이 100% 라면, 모든 후보해에 돌연변이가 일어나게 되어, 임의의 후보해가 탄생하게 된다. 

그렇게 되면, 다음 세대의 후보해가 더 우수한 후보해임을 기대하는 돌연변이 연산의 존재자체의 의의가 사실 없는 셈이다.

돌연변이 확률이 0% 라면, 모든 후보해에 돌연변이 연산이 일어나지 않고, 교차 연산으로 탄생한 후보해 그대로 이어질 것이다.

돌연변이 확률이 높을 때의 최종해와 돌연변이 확률이 낮을 떄의 최종적으로 나온 최적해(또는 최적해에 가까운 해)를 비교해본 결과 낮을 수록 최적해에 더 가까움을 확인할 수 있었다.

따라서, 본 과제에서는 돌연변이 확률을 1% 로 설정하였다.
