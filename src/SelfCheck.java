import java.util.*;

/** My own extra checks. Run:  java -cp out SelfCheck */
public class SelfCheck {
    static int bad = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "ok    " : "FAIL  ") + name);
        if (!ok) bad++;
    }

    public static void main(String[] args) throws Exception {
        String dir = args.length > 0 ? args[0] : "data";
        Input.Walls w = Input.readWalls(dir + "/walls.txt");
        Input.Slides s = Input.readSlides(dir + "/slides.txt");
        int R = w.rows(), C = w.cols(), n = w.cells();

        System.out.println("--- Missions 1 and 2");
        int[] broken = Maze.breakWalls(w);
        check("Broken = R*C - 1", broken.length == n - 1);
        check("Kept = (R-1)*(C-1)", w.count() - broken.length == (R - 1) * (C - 1));
        DisjointSet ds = new DisjointSet(n);
        for (int k : broken) ds.union(w.a()[k], w.b()[k]);
        check("broken walls connect all cells", ds.count() == 1);

        int[] mst = Maze.cheapestMaze(w);
        long kruskal = 0, maze1 = 0;
        for (int k : mst) kruskal += w.cost()[k];
        for (int k : broken) maze1 += w.cost()[k];
        check("Kruskal cost = Prim cost", kruskal == Maze.primCost(w));
        check("MST cost <= cost of the maze of Mission 1", kruskal <= maze1);

        System.out.println("--- Mission 3");
        Escape.Result e = Escape.escape(w, broken);
        int[][] g = Graphs.open(w, broken);
        int[] p = e.path();
        boolean ok = p.length > 0 && p[0] == 0 && p[p.length - 1] == n - 1;
        for (int i = 0; ok && i + 1 < p.length; i++) {
            boolean door = false;
            for (int x : g[p[i]]) if (x == p[i + 1]) door = true;
            ok = door;
        }
        check("path goes from 0 to the exit through open doors", ok);
        check("path has no repeated cells", Arrays.stream(p).distinct().count() == p.length);
        check("visited >= number of cells on the path", e.visited() >= p.length);
        check("path length >= (R-1)+(C-1)", p.length - 1 >= (R - 1) + (C - 1));
        int de = 0;
        for (int[] x : g) if (x.length == 1) de++;
        check("dead ends counted again = " + de, e.deadEnds() == de && de >= 2);

        System.out.println("--- Missions 4 and 5");
        int[] z = Zones.kosaraju(s);
        int zones = 0;
        for (int x : z) zones = Math.max(zones, x + 1);
        boolean topo = true;
        for (int i = 0; i < s.count(); i++) if (z[s.from()[i]] > z[s.to()[i]]) topo = false;
        check("no slide goes to a smaller zone number (topological order)", topo);

        int[][] out = Graphs.slidesFrom(s);
        if (n <= 2000) {
            boolean[][] r = new boolean[n][n];
            for (int u = 0; u < n; u++) {
                ArrayDeque<Integer> q = new ArrayDeque<>();
                q.add(u);
                r[u][u] = true;
                while (!q.isEmpty()) {
                    int c = q.poll();
                    for (int i : out[c]) {
                        int v = s.to()[i];
                        if (!r[u][v]) { r[u][v] = true; q.add(v); }
                    }
                }
            }
            boolean same = true;
            for (int u = 0; u < n && same; u++)
                for (int v = 0; v < n; v++)
                    if ((z[u] == z[v]) != (r[u][v] && r[v][u])) { same = false; break; }
            check("same zone <=> reach each other (brute force)", same);
        }

        long[] res = Zones.routes(s, z, zones);
        long INF = Long.MAX_VALUE / 4;
        long[] d = new long[n];
        Arrays.fill(d, INF);
        d[0] = 0;
        for (int it = 0; it < n; it++) {
            boolean changed = false;
            for (int i = 0; i < s.count(); i++) {
                int a = s.from()[i], b = s.to()[i];
                long wg = z[a] == z[b] ? 0 : s.time()[i];
                if (d[a] + wg < d[b]) { d[b] = d[a] + wg; changed = true; }
            }
            if (!changed) break;
        }
        check("Fastest route cost = " + res[1] + " (Bellman-Ford check)", res[1] == d[n - 1]);

        long[] len = new long[zones];
        Arrays.fill(len, 1);
        for (int it = 0; it < zones; it++)
            for (int i = 0; i < s.count(); i++) {
                int a = z[s.from()[i]], b = z[s.to()[i]];
                if (a != b) len[b] = Math.max(len[b], len[a] + 1);
            }
        long st = 0;
        for (long x : len) st = Math.max(st, x);
        check("Flow stages = " + res[0] + " (brute force check)", res[0] == st);

        System.out.println("--- Big inputs (stack overflow test)");
        Input.Walls big = Generator.generate(1000, 1000, 1);
        Escape.Result be = Escape.escape(big, Maze.breakWalls(big));
        check("1000 x 1000 maze: DFS finishes", be.path().length > 0);

        int N = 1_000_000;
        int[] f = new int[N - 1], t = new int[N - 1], tm = new int[N - 1];
        for (int i = 0; i < N - 1; i++) { f[i] = i; t[i] = i + 1; tm[i] = 1; }
        Input.Slides chain = new Input.Slides(N, f, t, tm);
        int[] zz = Zones.kosaraju(chain);
        long[] rr = Zones.routes(chain, zz, N);
        check("chain of 1,000,000 cells: Kosaraju finishes", zz.length == N);
        check("chain: Flow stages = N and cost = N-1", rr[0] == N && rr[1] == N - 1);

        System.out.println(bad == 0 ? "\nALL GOOD" : "\n" + bad + " check(s) FAILED");
    }
}