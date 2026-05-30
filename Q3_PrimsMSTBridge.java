import java.util.*;

/**
 * CO-3: Mumbai Distribution Grid
 * Prim's MST + Bridge Detection (Tarjan's) for Network Reliability
 */
public class Q3_PrimsMSTBridge {

    static final String[] VERTICES = {"A", "B", "C", "D", "E", "F", "G", "M"};

    static final int[][] EDGES = {
        // encoded as index pairs + weight; use vertex name map below
    };

    // Adjacency list: Map<vertex, List<[neighbour, weight]>>
    static Map<String, List<int[]>> buildAdj(List<int[]> edgeList, String[] verts) {
        Map<String, List<int[]>> adj = new HashMap<>();
        for (String v : verts) adj.put(v, new ArrayList<>());
        for (int[] e : edgeList) {
            String u = verts[e[0]], v = verts[e[1]];
            adj.get(u).add(new int[]{e[1], e[2]});
            adj.get(v).add(new int[]{e[0], e[2]});
        }
        return adj;
    }

    // ==================== Prim's Algorithm ====================
    static List<int[]> primsMST(Map<String, List<int[]>> adj, String[] verts, String start) {
        int n = verts.length;
        Map<String, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < n; i++) indexMap.put(verts[i], i);

        boolean[] visited = new boolean[n];
        List<int[]> mstEdges = new ArrayList<>();

        // PQ: {weight, fromIdx, toIdx}
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        int startIdx = indexMap.get(start);
        pq.offer(new int[]{0, startIdx, startIdx});

        int step = 1;
        System.out.println("=".repeat(60));
        System.out.println("  Prim's Algorithm — Step-by-Step");
        System.out.println("=".repeat(60));

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int cost = curr[0], from = curr[1], to = curr[2];
            if (visited[to]) continue;
            visited[to] = true;

            if (from != to) {
                mstEdges.add(new int[]{from, to, cost});
                System.out.printf("\n  Step %d: Add edge %s-%s = %d\n",
                        step, verts[from], verts[to], cost);
            } else {
                System.out.printf("\n  Step %d: Start at %s\n", step, verts[to]);
            }
            step++;

            System.out.print("    Visited: [");
            List<String> vis = new ArrayList<>();
            for (int i = 0; i < n; i++) if (visited[i]) vis.add(verts[i]);
            System.out.print(String.join(", ", vis) + "]\n");

            for (int[] nb : adj.get(verts[to])) {
                if (!visited[nb[0]])
                    pq.offer(new int[]{nb[1], to, nb[0]});
            }
        }
        return mstEdges;
    }

    // ==================== Bridge Detection (Tarjan's) ====================
    static int timer = 0;

    static List<int[]> findBridges(List<int[]> mstEdges, String[] verts) {
        int n = verts.length;
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : mstEdges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }

        int[] disc = new int[n];
        int[] low  = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(disc, -1);
        List<int[]> bridges = new ArrayList<>();
        timer = 0;

        for (int i = 0; i < n; i++)
            if (!visited[i])
                dfs(i, -1, adj, disc, low, visited, bridges);

        return bridges;
    }

    static void dfs(int u, int parent, List<List<Integer>> adj,
                    int[] disc, int[] low, boolean[] visited, List<int[]> bridges) {
        visited[u] = true;
        disc[u] = low[u] = timer++;
        for (int v : adj.get(u)) {
            if (v == parent) continue;
            if (!visited[v]) {
                dfs(v, u, adj, disc, low, visited, bridges);
                low[u] = Math.min(low[u], low[v]);
                if (low[v] > disc[u])
                    bridges.add(new int[]{u, v});
            } else {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    // ==================== Main ====================
    public static void main(String[] args) {
        // Edge list: [fromIdx, toIdx, weight]
        // A=0, B=1, C=2, D=3, E=4, F=5, G=6, M=7
        List<int[]> edgeList = Arrays.asList(
            new int[]{0, 1, 2},  // A-B
            new int[]{1, 2, 2},  // B-C
            new int[]{0, 7, 4},  // A-M
            new int[]{1, 7, 3},  // B-M
            new int[]{2, 7, 5},  // C-M
            new int[]{7, 6, 6},  // M-G
            new int[]{2, 6, 3},  // C-G
            new int[]{6, 5, 4},  // G-F
            new int[]{5, 4, 2},  // F-E
            new int[]{4, 7, 4},  // E-M
            new int[]{3, 4, 3},  // D-E
            new int[]{0, 3, 7}   // A-D
        );

        String[] verts = {"A", "B", "C", "D", "E", "F", "G", "M"};
        Map<String, List<int[]>> adj = buildAdj(edgeList, verts);

        System.out.println("=".repeat(60));
        System.out.println("  Mumbai Distribution Grid — Prim's MST + Bridge Detection");
        System.out.println("=".repeat(60));
        System.out.println("\n  Given Graph Edges:");
        String[] edgeNames = {"A-B","B-C","A-M","B-M","C-M","M-G","C-G","G-F","F-E","E-M","D-E","A-D"};
        int[]    edgeWts   = {2,2,4,3,5,6,3,4,2,4,3,7};
        for (int i = 0; i < edgeNames.length; i++)
            System.out.printf("    %s  =  %d crore\n", edgeNames[i], edgeWts[i]);

        // Prim's MST
        List<int[]> mstEdges = primsMST(adj, verts, "M");

        int totalCost = 0;
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  Final MST Edges");
        System.out.println("=".repeat(60));
        for (int[] e : mstEdges) {
            System.out.printf("    %s — %s  =  %d crore\n",
                    verts[e[0]], verts[e[1]], e[2]);
            totalCost += e[2];
        }
        System.out.println("\n  Total MST Cost = ₹" + totalCost + " crore");

        // Bridge Detection
        List<int[]> bridges = findBridges(mstEdges, verts);
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  Bridge Detection (Tarjan's Algorithm)");
        System.out.println("=".repeat(60));
        System.out.println("  All " + bridges.size()
                + " MST edges are bridges (removing any disconnects the grid):");
        for (int[] b : bridges)
            System.out.printf("    Bridge: %s — %s\n", verts[b[0]], verts[b[1]]);

        // Suggest extra edges for redundancy
        Set<String> mstSet = new HashSet<>();
        for (int[] e : mstEdges) {
            String key = Math.min(e[0], e[1]) + "-" + Math.max(e[0], e[1]);
            mstSet.add(key);
        }
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  N-1 Redundancy: Suggested Extra Edges");
        System.out.println("=".repeat(60));
        System.out.println("  Adding these non-MST edges creates alternate paths:");
        List<int[]> sorted = new ArrayList<>(edgeList);
        sorted.sort(Comparator.comparingInt(a -> a[2]));
        for (int[] e : sorted) {
            String key = Math.min(e[0], e[1]) + "-" + Math.max(e[0], e[1]);
            if (!mstSet.contains(key))
                System.out.printf("    %s — %s  =  %d crore\n",
                        verts[e[0]], verts[e[1]], e[2]);
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  Time Complexity Summary");
        System.out.println("=".repeat(60));
        System.out.printf("  %-35s %s\n", "Prim's MST (min-heap)",       "O((V + E) log V)");
        System.out.printf("  %-35s %s\n", "Bridge Detection (Tarjan's)", "O(V + E)");
        System.out.printf("  %-35s %s\n", "Space",                       "O(V + E)");
        System.out.println("\n  Conclusion: Prim's builds the minimum-cost network.");
        System.out.println("  Tarjan's detects all bridge edges (vulnerable cables).");
        System.out.println("  Extra edges provide N-1 redundancy for reliability.");
    }
}
