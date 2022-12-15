package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> M = new BuggyAList<>();
        int N = 10000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                M.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int Msize = M.size();
                assertEquals(size,Msize);
                System.out.println("size: " + size);
            } else if (operationNumber == 2) {
                if (L.size() > 0) {
                    assertEquals(L.getLast(),M.getLast());
                    System.out.println("getLast(" + L.getLast() + ")");
                }
            } else if (operationNumber == 3) {
                if (L.size() > 0) {
                    int Llast=L.removeLast();
                    int Mlast=M.removeLast();
                    assertEquals(Llast,Mlast);
                    System.out.println("removeLast(" + Llast + ")");
                }
            }
        }
    }
}
