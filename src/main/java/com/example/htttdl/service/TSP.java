package com.example.htttdl.service;

import java.util.ArrayList;
import java.util.List;

public class TSP {
    private static double minCost = Double.MAX_VALUE;
    public static List<Integer> bestPath = new ArrayList<>();

    public static List<Integer> solveTSP(double[][] graph, int start) {
        int n = graph.length;
        minCost = Double.MAX_VALUE;
        bestPath = new ArrayList<>();

        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();
        visited[start] = true;
        path.add(start);

        tspHelper(graph, start, 1, 0, visited, path);
        bestPath.add(start);
        return bestPath;
    }

    private static void tspHelper(double[][] graph, int current, int count, double cost, boolean[] visited,
            List<Integer> path) {
        int n = graph.length;
        if (count == n) {
            double totalCost = cost + graph[current][0];
            if (totalCost < minCost) {
                minCost = totalCost;
                bestPath = new ArrayList<>(path);
            }
            return;
        }

        for (int i = 0; i < n; i++) {
            if (!visited[i] && graph[current][i] > 0) {
                visited[i] = true;
                path.add(i);
                tspHelper(graph, i, count + 1, cost + graph[current][i], visited, path);
                path.remove(path.size() - 1);
                visited[i] = false;
            }
        }
    }

    public static double getMinCost() {
        return minCost;
    }
}