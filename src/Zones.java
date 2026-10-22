/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
import java.util.*;

public class Zones {

    /** Mission 4. Kosaraju with explicit stacks. zoneOf[cell] = zone number, in the order of the second pass. */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);


        boolean[] seen = new boolean[n];
        int[] order = new int[n];
        int cnt = 0;
        int[] stack = new int[n];
        int[] pos = new int[n];
        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int top = 0;
            stack[top++] = start;
            seen[start] = true;
            while (top > 0) {
                int u = stack[top - 1];
                if (pos[u] < out[u].length) {
                    int v = s.to()[out[u][pos[u]++]];
                    if (!seen[v]) {
                        seen[v] = true;
                        stack[top++] = v;
                    }
                } else {
                    top--;
                    order[cnt++] = u;
                }
            }
        }


        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zones = 0;
        for (int i = n - 1; i >= 0; i--) {
            int root = order[i];
            if (zoneOf[root] != -1) continue;
            int top = 0;
            stack[top++] = root;
            zoneOf[root] = zones;
            while (top > 0) {
                int u = stack[--top];
                for (int slide : in[u]) {
                    int v = s.from()[slide];
                    if (zoneOf[v] == -1) {
                        zoneOf[v] = zones;
                        stack[top++] = v;
                    }
                }
            }
            zones++;
        }
        return zoneOf;
    }

    /** Mission 5. Zones in the order of their numbers. Returns {flow stages, fastest route cost}. */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n();


        int[] startIdx = new int[zoneCount + 1];
        for (int c = 0; c < n; c++) startIdx[zoneOf[c] + 1]++;
        for (int z = 0; z < zoneCount; z++) startIdx[z + 1] += startIdx[z];
        int[] cells = new int[n];
        int[] fill = Arrays.copyOf(startIdx, zoneCount);
        for (int c = 0; c < n; c++) cells[fill[zoneOf[c]]++] = c;

        int[][] out = Graphs.slidesFrom(s);
        final long INF = Long.MAX_VALUE;
        long[] dist = new long[zoneCount];
        long[] len = new long[zoneCount];
        Arrays.fill(dist, INF);
        Arrays.fill(len, 1);
        dist[zoneOf[0]] = 0;

        long stages = 1;
        for (int z = 0; z < zoneCount; z++) {
            stages = Math.max(stages, len[z]);
            for (int i = startIdx[z]; i < startIdx[z + 1]; i++) {
                int c = cells[i];
                for (int slide : out[c]) {
                    int y = zoneOf[s.to()[slide]];
                    if (y == z) continue;
                    len[y] = Math.max(len[y], len[z] + 1);
                    if (dist[z] != INF) {
                        dist[y] = Math.min(dist[y], dist[z] + s.time()[slide]);
                    }
                }
            }
        }
        return new long[]{stages, dist[zoneOf[n - 1]]};
    }
}