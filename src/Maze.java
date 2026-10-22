/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Maze {

    /** Mission 1. Walls in the order of the file, break if union returns true. */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet ds = new DisjointSet(w.cells());
        int[] result = new int[w.cells() - 1];
        int n = 0;
        for (int k = 0; k < w.a().length; k++) {
            if (ds.union(w.a()[k], w.b()[k])) {
                result[n++] = k;
            }
        }
        return Arrays.copyOf(result, n);
    }

    /** Mission 2. Kruskal: walls from the cheapest to the most expensive. */
    public static int[] cheapestMaze(Input.Walls w) {
        int m = w.a().length;
        Integer[] order = new Integer[m];
        for (int i = 0; i < m; i++) order[i] = i;
        Arrays.sort(order, (x, y) -> Long.compare(w.cost()[x], w.cost()[y]));

        DisjointSet ds = new DisjointSet(w.cells());
        int[] result = new int[w.cells() - 1];
        int n = 0;
        for (int k : order) {
            if (ds.union(w.a()[k], w.b()[k])) {
                result[n++] = k;
            }
        }
        return Arrays.copyOf(result, n);
    }

    /** Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze. */
    @SuppressWarnings("unchecked")
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        int m = w.a().length;

        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int k = 0; k < m; k++) {
            adj[w.a()[k]].add(k);
            adj[w.b()[k]].add(k);
        }

        boolean[] visited = new boolean[n];
        PriorityQueue<Integer> pq =
                new PriorityQueue<>((x, y) -> Long.compare(w.cost()[x], w.cost()[y]));

        long total = 0;
        visited[0] = true;
        for (int k : adj[0]) pq.add(k);

        while (!pq.isEmpty()) {
            int k = pq.poll();
            int a = w.a()[k], b = w.b()[k];
            if (visited[a] && visited[b]) continue;
            int c = visited[a] ? b : a;
            total += w.cost()[k];
            visited[c] = true;
            for (int e : adj[c]) {
                int other = (w.a()[e] == c) ? w.b()[e] : w.a()[e];
                if (!visited[other]) pq.add(e);
            }
        }
        return total;
    }
}