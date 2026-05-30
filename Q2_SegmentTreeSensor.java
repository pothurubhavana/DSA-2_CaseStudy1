/**
 * CO-2: Foxconn Factory Floor — Segment Tree for Sensor Range-Max Alerts
 * Supports efficient range-maximum queries and point updates.
 * Sensor readings: [71, 73, 78, 75, 82, 79, 77, 80]
 */
public class Q2_SegmentTreeSensor {

    // ==================== Segment Tree ====================
    static class SegmentTree {
        int n;
        int[] data;
        int[] tree;

        SegmentTree(int[] arr) {
            this.n    = arr.length;
            this.data = arr.clone();
            this.tree = new int[4 * n];
            build(0, 0, n - 1);
        }

        // ---- Build ----
        void build(int node, int start, int end) {
            if (start == end) {
                tree[node] = data[start];
            } else {
                int mid = (start + end) / 2;
                build(2 * node + 1, start, mid);
                build(2 * node + 2, mid + 1, end);
                tree[node] = Math.max(tree[2 * node + 1], tree[2 * node + 2]);
            }
        }

        // ---- Range-Max Query ----
        int query(int l, int r, int node, int start, int end) {
            if (r < start || end < l)          // completely outside
                return Integer.MIN_VALUE;
            if (l <= start && end <= r)        // completely inside
                return tree[node];
            int mid = (start + end) / 2;
            int leftMax  = query(l, r, 2 * node + 1, start, mid);
            int rightMax = query(l, r, 2 * node + 2, mid + 1, end);
            return Math.max(leftMax, rightMax);
        }

        int query(int l, int r) {
            return query(l, r, 0, 0, n - 1);
        }

        // ---- Point Update ----
        void update(int idx, int newVal, int node, int start, int end) {
            if (start == end) {
                data[idx]  = newVal;
                tree[node] = newVal;
            } else {
                int mid = (start + end) / 2;
                if (idx <= mid)
                    update(idx, newVal, 2 * node + 1, start, mid);
                else
                    update(idx, newVal, 2 * node + 2, mid + 1, end);
                tree[node] = Math.max(tree[2 * node + 1], tree[2 * node + 2]);
            }
        }

        void update(int idx, int newVal) {
            update(idx, newVal, 0, 0, n - 1);
        }

        // ---- Display ----
        void printArray(String label) {
            System.out.print("  " + label + ": [");
            for (int i = 0; i < n; i++)
                System.out.print(data[i] + (i < n - 1 ? ", " : ""));
            System.out.println("]");
        }
    }

    // ==================== Main ====================
    public static void main(String[] args) {
        int[] readings = {71, 73, 78, 75, 82, 79, 77, 80};

        System.out.println("=".repeat(55));
        System.out.println("  Segment Tree — Factory IoT Sensor Monitor");
        System.out.println("=".repeat(55));
        System.out.print("  Initial readings (1-indexed): [");
        for (int i = 0; i < readings.length; i++)
            System.out.print(readings[i] + (i < readings.length - 1 ? ", " : "]\n"));

        SegmentTree st = new SegmentTree(readings);

        // Step 1: Build info
        System.out.println("\n" + "=".repeat(55));
        System.out.println("  Step 1: Build Segment Tree");
        System.out.println("=".repeat(55));
        System.out.println("  Left Subtree:");
        System.out.println("    [1..2] = max(71,73) = 73");
        System.out.println("    [3..4] = max(78,75) = 78");
        System.out.println("    [1..4] = max(73,78) = 78");
        System.out.println("  Right Subtree:");
        System.out.println("    [5..6] = max(82,79) = 82");
        System.out.println("    [7..8] = max(77,80) = 80");
        System.out.println("    [5..8] = max(82,80) = 82");
        System.out.println("  Root:");
        System.out.println("    [1..8] = max(78,82) = 82");

        // Step 2: Range-Max Query [3..7] → 0-based [2..6]
        System.out.println("\n" + "=".repeat(55));
        System.out.println("  Step 2: Range-Max Query [3..7]");
        System.out.println("=".repeat(55));
        int l = 2, r = 6;   // 0-based
        int maxVal = st.query(l, r);
        System.out.println("  Elements: 78, 75, 82, 79, 77");
        System.out.println("  Visited nodes: [3..4]=78, [5..6]=82, [7]=77");
        System.out.println("  max(78, 82, 77) = " + maxVal);

        // Step 3: Point Update index 4 (1-based) → idx 3 (0-based): 75 → 88
        System.out.println("\n" + "=".repeat(55));
        System.out.println("  Step 3: Point Update — index 4 (1-based): 75 → 88");
        System.out.println("=".repeat(55));
        st.update(3, 88);
        st.printArray("Updated array");
        System.out.println("  Propagation:");
        System.out.println("    Leaf [4]   = 88");
        System.out.println("    [3..4]     = max(78, 88) = 88");
        System.out.println("    [1..4]     = max(73, 88) = 88");
        System.out.println("    Root[1..8] = max(88, 82) = 88");

        // Verify
        int maxAfter = st.query(l, r);
        System.out.println("\n  Range-Max [3..7] after update = " + maxAfter);

        System.out.println("\n" + "=".repeat(55));
        System.out.println("  Time Complexity Summary");
        System.out.println("=".repeat(55));
        String[][] rows = {
            {"Build Segment Tree", "O(n)"},
            {"Range-Max Query",    "O(log n)"},
            {"Point Update",       "O(log n)"},
            {"Space",              "O(n)"},
        };
        for (String[] row : rows)
            System.out.printf("  %-30s %s\n", row[0], row[1]);
    }
}
