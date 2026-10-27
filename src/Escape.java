import java.util.Arrays;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int cols = w.cols();
        int exit = n - 1;
        int[][] graph = Graphs.open(w, broken);

        int deadEnds = 0;
        for (int cell = 0; cell < n; cell++) {
            if (graph[cell].length == 1) {
                deadEnds++;
            }
        }

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        // Each cell remembers which direction DFS should try next.
        int[] nextDirection = new int[n];
        int[] stack = new int[n];
        int top = 0;
        stack[top] = 0;
        visited[0] = true;
        int visitedCount = 1;

        while (top >= 0 && !visited[exit]) {
            int cell = stack[top];

            if (nextDirection[cell] == 4) {
                top--;
                continue;
            }

            int direction = nextDirection[cell]++;
            int next = switch (direction) {
                case 0 -> cell >= cols ? cell - cols : -1;       // up
                case 1 -> cell % cols < cols - 1 ? cell + 1 : -1; // right
                case 2 -> cell + cols < n ? cell + cols : -1;     // down
                default -> cell % cols > 0 ? cell - 1 : -1;      // left
            };

            if (next == -1 || visited[next]) {
                continue;
            }

            boolean open = false;
            for (int neighbour : graph[cell]) {
                if (neighbour == next) {
                    open = true;
                    break;
                }
            }

            if (open) {
                parent[next] = cell;
                visited[next] = true;
                visitedCount++;
                stack[++top] = next;
            }
        }

        if (!visited[exit]) {
            return new Result(new int[0], visitedCount, deadEnds);
        }

        // Follow parents backwards, filling the path from its end.
        int length = 0;
        for (int cell = exit; cell != -1; cell = parent[cell]) {
            length++;
        }

        int[] path = new int[length];
        int cell = exit;
        for (int i = length - 1; i >= 0; i--) {
            path[i] = cell;
            cell = parent[cell];
        }

        return new Result(path, visitedCount, deadEnds);
    }
}
