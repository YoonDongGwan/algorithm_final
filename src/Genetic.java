public class Genetic {

    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        double[] bubble = {0, -8.0, -6.3, -6.1, -5.8, -5.0, -2.8, -0.8, 1.3, 2.6, 3.1, 3.3, 4.2, 5.7, 7.6, 10.2, 12.7, 14.5, 16.6, 18.6, 20.6};

        double y = ga.solve(4, bubble, (x, a, b) -> a*x + b);
        System.out.println(y);
    }
}
