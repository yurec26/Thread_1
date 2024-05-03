package org.example;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        long startTs = System.currentTimeMillis(); // start time
        List<Future> futures = new ArrayList<>(); //*

        ExecutorService service = Executors.newFixedThreadPool(25);
        // ничего не говорится о кол-ве потоков в задании, пусть будет 25

        for (String text : texts) {
            Callable runnable = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                return maxSize;
            };
            Future<Integer> task = service.submit(runnable);
            futures.add(task);
        }

        List<Integer> results = new ArrayList<>();
        for (Future future : futures) {
            results.add((Integer) future.get());
        }

        Integer max = results
                .stream()
                .mapToInt(v -> v)
                .max().getAsInt();
        System.out.println(max);

        service.shutdown();

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}