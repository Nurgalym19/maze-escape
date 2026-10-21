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
        int visitedCount = 0;

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



            int r = c / cols;
            int col = c % cols;
            int nb = -1;
            if (d == 0) nb = (r - 1) * cols + col;
            else if (d == 1) nb = r * cols + (col + 1);
            else if (d == 2) nb = (r + 1) * cols + col;
            else if (d == 3) nb = r * cols + (col - 1);


            if (nb < 0 || nb >= n || visited[nb]) {
                continue;
            }
            boolean isOpen = false;
            for (int neighbor : g[c]) {
                if (neighbor == nb) {
                    isOpen = true;
                    break;
                }
            }
            if (!isOpen) {
                continue;
            }

            visited[nb] = true;
            visitedCount++;
            parent[nb] = c;
            stack[top++] = nb;
        }


        int tempCount = 0;
        int curr = exit;

        while (curr != -1) {
            tempCount++;
            if (curr == 0) break;
            curr = parent[curr];
        }

        int[] path = new int[tempCount];
        curr = exit;
        for (int i = tempCount - 1; i >= 0; i--) {
            path[i] = curr;
            curr = parent[curr];
        }

        return new Result(path, visitedCount, deadEnds);
    }
}