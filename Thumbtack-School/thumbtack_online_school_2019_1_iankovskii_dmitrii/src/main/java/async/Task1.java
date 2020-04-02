package async;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Task1 {

    static double[] arrX = new double[]{1, 2, 4, 6, 7};
    static double[] arrY = new double[]{1, 2, 4, 6, 7};
    static double averageX;
    static double averageY;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CompletableFuture<Double> avgX = CompletableFuture
                .supplyAsync(()-> Arrays.stream(arrX).sum()/arrX.length);

        CompletableFuture<Double> avgY = CompletableFuture
                .supplyAsync(()-> Arrays.stream(arrY).sum()/arrY.length);

        CompletableFuture<Double> sumXY = CompletableFuture
                .supplyAsync(()->{
                    double sum = 0;
                    for (int i = 0; i < arrX.length; i++){
                        sum += (arrX[i] - averageX)*(arrY[i] - averageY);
                    }
                    return sum;
                });

        CompletableFuture<Double> sumSqrX = CompletableFuture
                .supplyAsync(()->{
                    double sum = 0;
                    for (double i : arrX){
                        sum += Math.pow(i - averageX, 2);
                    }
                    return sum;
                });

        CompletableFuture<Double> sumSqrY = CompletableFuture
                .supplyAsync(()->{
                    double sum = 0;
                    for (double i : arrY){
                        sum += Math.pow(i - averageY, 2);
                    }
                    return sum;
                });
        averageY = avgY.get();
        averageX = avgX.get();
        CompletableFuture<Double> bottomSqrt = sumSqrX.thenCombine(sumSqrY, (a, b)-> a * b).thenApply(Math::sqrt);
        CompletableFuture<Double> coef = sumXY.thenCombine(bottomSqrt, (a,b)-> a/b);
        System.out.println("Correlation : " + coef.get());
    }

}
