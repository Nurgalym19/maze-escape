/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int cols = w.cols();
        int exit = n - 1;
        int[][] g = Graphs.open(w, broken);

        int deadEnds = 0;
        for (int c = 0; c < n; c++) {
            if (g[c].length == 1) deadEnds++;
        }

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        int[] next = new int[n];
        int[] stack = new int[n];
        int top = 0;
        int visitedCount;

        stack[top++] = 0;
        visited[0] = true;
        visitedCount = 1;
        parent[0] = -1;

        while (top > 0 && !visited[exit]) {
            int c = stack[top - 1];
            if (next[c] == 4) {
                top--;
                continue;
            }
            int d = next[c]++;
            int nb;
            switch (d) {
                case 0 -> nb = c - cols;   // up
                case 1 -> nb = c + 1;      // right
                case 2 -> nb = c + cols;   // down
                default -> nb = c - 1;     // left
            }
            if (!isOpen(g[c], nb) || visited[nb]) continue;

            visited[nb] = true;
            visitedCount++;
            parent[nb] = c;
            stack[top++] = nb;
        }

        // restore the path from the exit back to the start, then reverse it
        int len = 0;
        for (int c = exit; c != -1; c = parent[c]) len++;
        int[] path = new int[len];
        int i = len - 1;
        for (int c = exit; c != -1; c = parent[c]) path[i--] = c;

        return new Result(path, visitedCount, deadEnds);
    }

    private static boolean isOpen(int[] neighbours, int x) {
        for (int y : neighbours) {
            if (y == x) return true;
        }
        return false;
    }
}