/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */

import java.util.Arrays;
import java.util.PriorityQueue;

public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        // TODO
        int n = w.rows() * w.cols();
        DisjointSet dsu = new DisjointSet(n);

        int[] broken = new int[n - 1];
        int brokenCount = 0;

        for (int k = 0; k < w.count(); k++) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[brokenCount] = k;
                brokenCount++;
            }
        }

        return broken;
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        // TODO
        int n = w.rows() * w.cols();
        DisjointSet dsu = new DisjointSet(n);

        Integer[] order = new Integer[w.count()];
        for (int k = 0; k < w.count(); k++) {
            order[k] = k;
        }

        Arrays.sort(order, (a, b) ->
                Long.compare(w.cost()[a], w.cost()[b]));

        int[] broken = new int[n - 1];
        int brokenCount = 0;

        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[brokenCount] = k;
                brokenCount++;

                if (brokenCount == n - 1) {
                    break;
                }
            }
        }

        return broken;
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        // TODO
        int n = w.rows() * w.cols();


        int[] degree = new int[n];
        for (int k = 0; k < w.count(); k++) {
            degree[w.a()[k]]++;
            degree[w.b()[k]]++;
        }


        int[][] adjacent = new int[n][];
        for (int cell = 0; cell < n; cell++) {
            adjacent[cell] = new int[degree[cell]];
        }

        int[] position = new int[n];
        for (int k = 0; k < w.count(); k++) {
            int a = w.a()[k];
            int b = w.b()[k];

            adjacent[a][position[a]++] = k;
            adjacent[b][position[b]++] = k;
        }

        PriorityQueue<Integer> queue = new PriorityQueue<>(
                (a, b) -> Long.compare(w.cost()[a], w.cost()[b]));

        boolean[] visited = new boolean[n];
        visited[0] = true;
        int visitedCount = 1;
        long totalCost = 0;

        for (int k : adjacent[0]) {
            queue.add(k);
        }

        while (!queue.isEmpty() && visitedCount < n) {
            int k = queue.poll();
            int a = w.a()[k];
            int b = w.b()[k];

            if (visited[a] && visited[b]) {
                continue;
            }

            int next = visited[a] ? b : a;
            visited[next] = true;
            visitedCount++;
            totalCost += w.cost()[k];

            for (int wall : adjacent[next]) {
                int other = w.a()[wall] == next
                        ? w.b()[wall]
                        : w.a()[wall];

                if (!visited[other]) {
                    queue.add(wall);
                }
            }
        }

        return totalCost;
    }
}