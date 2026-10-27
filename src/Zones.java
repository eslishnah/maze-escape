import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        // TODO
        int n = s.n();
        int[][] outgoing = Graphs.slidesFrom(s);
        int[][] incoming = Graphs.slidesInto(s);

        boolean[] visited = new boolean[n];
        int[] nextSlide = new int[n];
        int[] stack = new int[n];
        int[] finishOrder = new int[n];
        int finished = 0;

        // First pass: record cells only after all their outgoing slides are processed.
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            int top = 0;
            stack[top] = start;
            visited[start] = true;

            while (top >= 0) {
                int cell = stack[top];

                if (nextSlide[cell] < outgoing[cell].length) {
                    int slide = outgoing[cell][nextSlide[cell]++];
                    int next = s.to()[slide];

                    if (!visited[next]) {
                        visited[next] = true;
                        stack[++top] = next;
                    }
                } else {
                    finishOrder[finished++] = cell;
                    top--;
                }
            }
        }

        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zoneCount = 0;

        // Second pass: explore reversed slides, starting with the last finished cell.
        for (int i = finished - 1; i >= 0; i--) {
            int start = finishOrder[i];
            if (zoneOf[start] != -1) {
                continue;
            }

            int top = 0;
            stack[top] = start;
            zoneOf[start] = zoneCount;

            while (top >= 0) {
                int cell = stack[top--];

                for (int slide : incoming[cell]) {
                    int next = s.from()[slide];
                    if (zoneOf[next] == -1) {
                        zoneOf[next] = zoneCount;
                        stack[++top] = next;
                    }
                }
            }

            zoneCount++;
        }

        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        // TODO
        // Build the zone graph, keeping only the cheapest slide for each pair.
        ArrayList<HashMap<Integer, Integer>> graph = new ArrayList<>();
        for (int zone = 0; zone < zoneCount; zone++) {
            graph.add(new HashMap<>());
        }

        for (int slide = 0; slide < s.count(); slide++) {
            int from = zoneOf[s.from()[slide]];
            int to = zoneOf[s.to()[slide]];

            if (from == to) {
                continue;
            }

            HashMap<Integer, Integer> edges = graph.get(from);
            int time = s.time()[slide];
            Integer previous = edges.get(to);
            if (previous == null || time < previous) {
                edges.put(to, time);
            }
        }

        // A chain containing only one zone already has one stage.
        int[] stages = new int[zoneCount];
        Arrays.fill(stages, 1);

        long[] distance = new long[zoneCount];
        Arrays.fill(distance, Long.MAX_VALUE);
        distance[zoneOf[0]] = 0;

        int flowStages = 0;

        // Kosaraju numbered zones in topological order: every edge goes forwards.
        for (int zone = 0; zone < zoneCount; zone++) {
            flowStages = Math.max(flowStages, stages[zone]);

            for (Map.Entry<Integer, Integer> edge : graph.get(zone).entrySet()) {
                int next = edge.getKey();
                int time = edge.getValue();

                stages[next] = Math.max(stages[next], stages[zone] + 1);

                if (distance[zone] != Long.MAX_VALUE) {
                    distance[next] = Math.min(distance[next], distance[zone] + time);
                }
            }
        }

        int exitZone = zoneOf[s.n() - 1];
        return new long[]{flowStages, distance[exitZone]};
    }
}
