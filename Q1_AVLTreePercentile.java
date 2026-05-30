/**
 * CO-1: Codeforces/LeetCode-style Online Judge
 * Fast 'percentile of submission' computation
 * using AVL Tree with subtree-size augmentation
 */
public class Q1_AVLTreePercentile {

    // ==================== Node ====================
    static class Node {
        int key, height, size;
        Node left, right;

        Node(int key) {
            this.key    = key;
            this.height = 1;
            this.size   = 1;
        }
    }

    // ==================== AVL Tree ====================
    static class AVLTree {

        // ---- Utility ----
        int height(Node n) { return n == null ? 0 : n.height; }
        int size(Node n)   { return n == null ? 0 : n.size;   }

        int getBalance(Node n) {
            return n == null ? 0 : height(n.left) - height(n.right);
        }

        void update(Node n) {
            if (n == null) return;
            n.height = 1 + Math.max(height(n.left), height(n.right));
            n.size   = 1 + size(n.left) + size(n.right);
        }

        // ---- Rotations ----
        Node rightRotate(Node z) {
            Node y  = z.left;
            Node T3 = y.right;
            y.right = z;
            z.left  = T3;
            update(z);
            update(y);
            return y;
        }

        Node leftRotate(Node z) {
            Node y  = z.right;
            Node T2 = y.left;
            y.left  = z;
            z.right = T2;
            update(z);
            update(y);
            return y;
        }

        // ---- Insertion ----
        Node insert(Node root, int key) {
            if (root == null) return new Node(key);

            if (key < root.key)
                root.left  = insert(root.left,  key);
            else
                root.right = insert(root.right, key);

            update(root);
            int balance = getBalance(root);

            // Left-Left
            if (balance > 1 && key < root.left.key)
                return rightRotate(root);
            // Right-Right
            if (balance < -1 && key > root.right.key)
                return leftRotate(root);
            // Left-Right
            if (balance > 1 && key > root.left.key) {
                root.left = leftRotate(root.left);
                return rightRotate(root);
            }
            // Right-Left
            if (balance < -1 && key < root.right.key) {
                root.right = rightRotate(root.right);
                return leftRotate(root);
            }
            return root;
        }

        // ---- Rank / Percentile queries ----
        int countLessEqual(Node root, int x) {
            if (root == null) return 0;
            if (x == root.key)
                return size(root.left) + 1;
            else if (x < root.key)
                return countLessEqual(root.left, x);
            else
                return size(root.left) + 1 + countLessEqual(root.right, x);
        }

        double percentile(Node root, int x) {
            int rank  = countLessEqual(root, x);
            int total = size(root);
            return (rank * 100.0) / total;
        }

        // ---- In-order traversal ----
        void inorder(Node root) {
            if (root == null) return;
            inorder(root.left);
            System.out.print(root.key + " ");
            inorder(root.right);
        }

        // ---- Tree print ----
        void printTree(Node root, int level, String prefix) {
            if (root == null) return;
            System.out.println("    ".repeat(level) + prefix + root.key
                    + "  [h=" + root.height + ", sz=" + root.size + "]");
            if (root.left != null || root.right != null) {
                if (root.left  != null) printTree(root.left,  level + 1, "L--- ");
                else System.out.println("    ".repeat(level + 1) + "L--- null");
                if (root.right != null) printTree(root.right, level + 1, "R--- ");
                else System.out.println("    ".repeat(level + 1) + "R--- null");
            }
        }
    }

    // ==================== Main ====================
    public static void main(String[] args) {
        int[] submissions = {300, 200, 180, 250, 220};
        AVLTree avl  = new AVLTree();
        Node    root = null;

        System.out.println("=".repeat(55));
        System.out.println("  AVL Tree — Online Judge Percentile System");
        System.out.println("=".repeat(55));

        for (int t : submissions) {
            root = avl.insert(root, t);
            System.out.print("\nInserted: " + t + " ms  |  In-order: ");
            avl.inorder(root);
            System.out.println("|  Size: " + avl.size(root));
        }

        System.out.println("\n" + "=".repeat(55));
        System.out.println("  Final AVL Tree Structure");
        System.out.println("=".repeat(55));
        avl.printTree(root, 0, "Root: ");

        System.out.println("\n" + "=".repeat(55));
        System.out.println("  Percentile Queries");
        System.out.println("=".repeat(55));
        int[] queries = {180, 220, 300};
        for (int q : queries) {
            int rank   = avl.countLessEqual(root, q);
            double pct = avl.percentile(root, q);
            System.out.printf("  Time %d ms → rank = %d/%d  → percentile = %.1f%%\n",
                    q, rank, avl.size(root), pct);
        }

        System.out.println("\n" + "=".repeat(55));
        System.out.println("  Time Complexity Summary");
        System.out.println("=".repeat(55));
        String[][] rows = {
            {"AVL Insertion",          "O(log n)"},
            {"AVL Rotation",           "O(1)"},
            {"Balance Factor Calc",    "O(1)"},
            {"Size Field Update",      "O(1)"},
            {"Rank Query (count ≤ x)", "O(log n)"},
            {"Percentile Query",       "O(log n)"},
        };
        for (String[] r : rows)
            System.out.printf("  %-30s %s\n", r[0], r[1]);
    }
}
