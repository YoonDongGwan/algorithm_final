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

따라서, 이번 과제에 사용할 데이터 집합은 Log2가 적용된 정렬 알고리즘들로 선택하였다.

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
### 먼저 데이터 집합을 실수형 배열로 옮겨야 하는데, 여기선 버블 정렬을 선택하였다.
```
double[] bubble = {0, -8.0, -6.3, -6.1, -5.8, -5.0, -2.8, -0.8, 1.3, 2.6, 3.1, 3.3, 4.2, 5.7, 7.6, 10.2, 12.7, 14.5, 16.6, 18.6, 20.6};
```
여기서 bubble[i] 는, 2의 i제곱의 랜덤 입력이 주어질 때, 정렬에 소요되는 시간(ms)에 Log2를 취한 값이다.

---
### 그 후 ax + b 형태의 방정식을 나타내기 위해, Problem 인터페이스를 생성하였다.
```
public interface Problem {
    double fit(double x, double a, double b);
}
```
이후 메인 함수에선 (x, a, b) -> a * x + b 의 형태로 return 해주었다.

---

### 선택한 후보해 a와 b를 대입해 에러율을 구하는 함수를 구현하였다.
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
### 선택 연산을 진행할 select 함수를 구현하였다.
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

### 다음은, 교차 연산을 진행할 crossover 함수이다.
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

<img width="292" alt="스크린샷 2022-06-16 오후 4 17 27" src="https://user-images.githubusercontent.com/39906922/174014312-3e5e7dc4-467d-4393-8903-4509b2056d6f.png">

_5번의 유전 알고리즘 수행 결과 Error rate sum이 점차 감소하는 경향임을 볼 수 있다._

---
### 다음은, 돌연변이 연산을 수행할 mutate 함수이다.
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
돌연변이는 a 또는 b 중 하나의 원소만 +1 혹은 -1 연산이 이루어지도록 정하였다.

돌연변이 연산에 중요한 점은, 돌연변이가 얼마나 자주 혹은 가끔 생성되는가이다. 

돌연변이 확률이 100% 라면, 모든 후보해에 돌연변이가 일어나게 되어, 임의의 후보해가 탄생하게 된다. 

그렇게 되면, 다음 세대의 후보해가 더 우수한 후보해임을 기대하는 돌연변이 연산의 존재자체의 의의가 사실 없는 셈이다.

돌연변이 확률이 0% 라면, 모든 후보해에 돌연변이 연산이 일어나지 않고, 교차 연산으로 탄생한 후보해 그대로 이어질 것이다.

돌연변이 확률이 높을 때의 마지막 세대 해와 돌연변이 확률이 낮을 때의 마지막 세대의 최적해(또는 최적해에 가까운 해)를 비교해본 결과 낮을 수록 최적해에 더 가까움을 확인할 수 있었다.

따라서, 본 과제에서는 돌연변이 확률을 1% 로 설정하였다.
![돌연변이확률](https://user-images.githubusercontent.com/39906922/174019186-873c4c6f-7296-4e36-9881-a486d90a83e1.png)  
<img width="476" alt="스크린샷 2022-06-16 오후 4 46 09" src="https://user-images.githubusercontent.com/39906922/174019485-811cc9cd-966c-4e1f-9c5a-ede583eb3801.png">


_돌연변이 확률이 낮을 수록 평균 에러율이 감소하는 경향이다._


---
### 최종적인 solve 함수
```
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
        for(int i = 0; i < nGenerations; i++){ // 10만번의 select - crossover - mutate 과정을 거치며 세대를 내려간다.
            select(candidatesA, candidatesB, sortingAlgorithm, p);
            crossover(candidatesA, candidatesB, sortingAlgorithm, p);
            mutate(candidatesA, candidatesB);

        }

        for(int i = 0; i < nCandidates; i++){ // 마지막 세대에서 에러율이 가장 낮은 최적해(또는 최적해에 가까운 해)를 선택한다.
            double errorRate = errorRate(candidatesA[i], candidatesB[i], sortingAlgorithm, p);
            if(minimumErrorRate < errorRate){
                minimumErrorRate = errorRate;
                minimumIndex = i;
            }
        }
        double[] opt = {candidatesA[minimumIndex], candidatesB[minimumIndex]};
        return opt;
    }
```
총 100,000 번의 세대를 거치며, 마지막 100,000번째 세대의 후보해 중, 에러율이 가장 낮은 해를 return 한다.

참고로 초기 배열에 넣을 a와 b를 정할 때엔, 랜덤 함수를 사용해 임의의 정수 값을 넣기로 하였다.

알고리즘의 그래프가 모두 우상향하는 그래프이기 때문에,

회귀식의 기울기가 음수가 되는 경우는 없을 것이라 보았고, y절편은 양수, 음수 모두 될 수도 있기 때문에, 

a의 범위는 0 ~ 100, b의 범위는 -50 ~ 50 으로 설정하였다.

## 결과  
<img width="285" alt="스크린샷 2022-06-16 오후 4 56 26" src="https://user-images.githubusercontent.com/39906922/174021495-e5bb82d1-80c8-4eab-8d31-e7a9e1efcd8d.png">

10만번의 세대를 거치게 설정하였고 5번을 시행하였다.

그 결과, ax + b 의 방정식에서 a는 주로 1.3 &#126; 1.5 사이의 실수 값을, b는 주로 -9.0 ~ -13.0 사이의 실수 값이 나오게 된다.

이 결과가 과연 최적해에 가까울지 확인하기 위해, 엑셀의 추세선 기능을 이용하였다.

![추세선](https://user-images.githubusercontent.com/39906922/174022396-29dc0bc5-5741-48ce-8ac5-8c216c7b17e0.png)

엑셀에서 도출해낸 회귀식은 y = 1.4843x - 11.266 이며, 이와 완벽하게 같지는 않더라도 최적해에 가까운 해가 도출되었음을 알 수 있다.

<img width="312" alt="스크린샷 2022-06-16 오후 5 16 00" src="https://user-images.githubusercontent.com/39906922/174025181-617bc122-c744-4892-a20e-3a84bd2676d1.png">


n * 10000번째 세대마다 평균 에러율을 확인해보았고, 세대가 거듭될 수록 에러율이 확실히 감소하고 있음을 볼 수 있었다.

아래의 정렬 알고리즘에도 같은 방식의 유전 알고리즘을 적용하였고, 그 결과 도출된 회귀식은 아래와 같다.

### Selection Sort
<img width="341" alt="스크린샷 2022-06-16 오후 5 38 20" src="https://user-images.githubusercontent.com/39906922/174029698-0d7fe1d6-17ac-42bb-aa1b-ae159ec12bd2.png">
  
### Insertion Sort
<img width="384" alt="스크린샷 2022-06-16 오후 5 43 24" src="https://user-images.githubusercontent.com/39906922/174030745-1ce6bfab-3699-4da9-9f68-bf03f1efb0a3.png">

### Shell Sort
<img width="384" alt="스크린샷 2022-06-16 오후 5 49 48" src="https://user-images.githubusercontent.com/39906922/174031970-db5e69c5-f34b-4dfc-9779-f116536b4171.png">

### Heap Sort
<img width="384" alt="스크린샷 2022-06-16 오후 5 56 00" src="https://user-images.githubusercontent.com/39906922/174033180-bebe7dec-a1c8-43ec-ace4-342e6c03a5fd.png">

### Quick Sort
<img width="384" alt="스크린샷 2022-06-16 오후 6 00 11" src="https://user-images.githubusercontent.com/39906922/174034051-f53f6580-8605-40f5-8db5-e1e3e43d2d42.png">

위 결과를 바탕으로, 성능이 우수한 정렬(퀵, 힙)일수록 회귀식의 기울기와 y절편의 절댓값이 낮게 도출된다는 것을 알 수 있었다.
