package async;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Task2 {

    private static List<Integer> readFile(String filename) {
        List<Integer> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(Integer.parseInt(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static List<Integer> writeFile(String filename, List<Integer> list) {
        List<Integer> result = new ArrayList<>();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filename+"_res"), "UTF-8"))) {
            for(Integer i : list){
                writer.write(i.toString() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Integer>> reader1 = CompletableFuture
                .supplyAsync(()->{
                    return readFile("src/main/resources/file1");
                });
        CompletableFuture<List<Integer>> reader2 = CompletableFuture
                .supplyAsync(()->{
                    return readFile("src/main/resources/file2");
                });
        List<Integer> list1 = reader1.get();
        List<Integer> list2 = reader2.get();

        CompletableFuture<List<Integer>> sum = CompletableFuture
                .supplyAsync(()->{
                    List<Integer> sumList = new ArrayList<>();
                    for (int i = 0; i < list1.size(); i++) {
                        sumList.add(list1.get(i)+list2.get(i));
                    }
                   return sumList;
                });

        CompletableFuture<List<Integer>> mul = CompletableFuture
                .supplyAsync(()->{
                    List<Integer> sumList = new ArrayList<>();
                    for (int i = 0; i < list1.size(); i++) {
                        sumList.add(list1.get(i)*list2.get(i));
                    }
                    return sumList;
                });

        writeFile("src/main/resources/file1", sum.get());
        writeFile("src/main/resources/file2", mul.get());

     }
}
