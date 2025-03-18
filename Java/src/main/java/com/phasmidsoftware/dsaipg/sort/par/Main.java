package com.phasmidsoftware.dsaipg.sort.par;

import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

/**
 * Parallel Sorting Experiment Main Entry
 * This program runs 3 experiments:
 * 1. Only change cutoff value
 * 2. Only change recursion depth (max depth)
 * 3. Change both cutoff and recursion depth
 * and saves each result to its own CSV file.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());

        runCutoffOnlyExperiment();
        runDepthOnlyExperiment();
        runCombinedExperiment();

    }

    /**
     * Experiment 1: only vary cutoff, fix recursion depth to a reasonable value
     */
    private static void runCutoffOnlyExperiment() {
        System.out.println("\n EXP1: only change cutoff (fixed maxDepth)");
        int fixedDepth = (int) (Math.log(ForkJoinPool.getCommonPoolParallelism()) / Math.log(2));
        int[] array = new int[2_000_000];
        Random random = new Random();
        List<Long> timeList = new ArrayList<>();

        int[] cutoffValues = {
            20000, 30000, 40000, 50000, 60000,
            80000, 100000, 120000, 150000, 180000, 200000
        };

        for (int cutoff : cutoffValues) {
            ParSort.cutoff = cutoff;
            ParSort.setMaxDepth(fixedDepth);

            long total = 0;
            for (int t = 0; t < 10; t++) {
                for (int i = 0; i < array.length; i++)
                    array[i] = random.nextInt(10_000_000);
                long start = System.currentTimeMillis();
                ParSort.sort(array, 0, array.length);
                total += System.currentTimeMillis() - start;
            }

            System.out.printf("cutoff = %-7d, avgTime = %.2f ms%n", cutoff, total / 10.0);
            timeList.add(total / 10);
        }

    }

    /**
     * Experiment 2: only vary recursion depth, fix cutoff to a reasonable value
     */
    private static void runDepthOnlyExperiment() {
        System.out.println("\n EXP2: only change maxDepth (fixed cutoff)");
        int fixedCutoff = 100000;  
        int[] array = new int[2_000_000];
        Random random = new Random();
        List<Long> timeList = new ArrayList<>();

        int sysDepth = (int) (Math.log(ForkJoinPool.getCommonPoolParallelism()) / Math.log(2));

        for (int depth = 1; depth <= sysDepth + 2; depth++) {
            ParSort.cutoff = fixedCutoff;
            ParSort.setMaxDepth(depth);

            long total = 0;
            for (int t = 0; t < 10; t++) {
                for (int i = 0; i < array.length; i++)
                    array[i] = random.nextInt(10_000_000);
                long start = System.currentTimeMillis();
                ParSort.sort(array, 0, array.length);
                total += System.currentTimeMillis() - start;
            }

            System.out.printf("maxDepth = %-2d, avgTime = %.2f ms%n", depth, total / 10.0);
            timeList.add(total / 10);
        }

    }

    /**
     * Experiment 3: vary both cutoff and maxDepth (combined strategy)
     */
    private static void runCombinedExperiment() {
        System.out.println("\n EXP3: combined (cutoff and maxDepth)");
        int[] array = new int[2_000_000];
        Random random = new Random();

        int[] cutoffValues = {
            20000, 30000, 40000, 50000, 60000,
            80000, 100000, 120000, 150000, 180000, 200000
        };

        for (int cutoff : cutoffValues) {
            for (int depth = 2; depth <= 6; depth++) {
                ParSort.cutoff = cutoff;
                ParSort.setMaxDepth(depth);

                long total = 0;
                for (int t = 0; t < 10; t++) {
                    for (int i = 0; i < array.length; i++)
                        array[i] = random.nextInt(10_000_000);
                    long start = System.currentTimeMillis();
                    ParSort.sort(array, 0, array.length);
                    total += System.currentTimeMillis() - start;
                }

                double avg = total / 10.0;
                System.out.printf("cutoff = %-7d | depth = %-2d | avgTime = %.2f ms%n", cutoff, depth, avg);
            }
        }
    }
}
