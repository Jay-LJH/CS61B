package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        int[] N = new int[]{4000, 8000, 16000, 32000,64000};
        AList ns = new AList();
        AList times=new AList();
        AList opc=new AList();
        for (int i = 0; i < N.length; i++) {
            SLList temp = new SLList();
            for (int j = 0; j < N[i]; j++) {
                temp.addLast(1);
            }
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < 10000; j++) {
                temp.addLast(1);
            }
            double timeInSeconds = sw.elapsedTime();
            ns.addLast(N[i]);
            times.addLast(timeInSeconds);
            opc.addLast(temp.getOps()-N[i]);
        }
        printTimingTable(ns,times,opc);
    }

}
