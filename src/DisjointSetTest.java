/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        // TODO: add your own checks here


        DisjointSet single = new DisjointSet(1);
        check(!single.union(0, 0), "union with itself returns false");
        check(single.count() == 1, "self-union keeps the set count");


        int n = 100_000;
        DisjointSet chain = new DisjointSet(n);
        boolean allJoined = true;
        boolean countsCorrect = true;

        for (int i = 1; i < n; i++) {
            if (!chain.union(i - 1, i)) {
                allJoined = false;
            }
            if (chain.count() != n - i) {
                countsCorrect = false;
            }
        }

        check(allJoined, "every new chain union succeeds");
        check(countsCorrect, "count decreases after each successful union");
        check(chain.count() == 1, "one set after connecting the chain");

        boolean allConnected = true;
        for (int i = 0; i < n; i++) {
            if (!chain.connected(0, i)) {
                allConnected = false;
                break;
            }
        }

        check(allConnected, "all chain elements are connected");
        check(!chain.union(0, n - 1), "repeated union returns false");
        check(chain.count() == 1, "repeated union keeps the set count");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}
