package timingtest;

import edu.princeton.cs.algs4.Stopwatch;
import org.checkerframework.checker.units.qual.A;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        int[] N = new int[]{4000, 8000, 16000, 32000,64000};
        AList<Integer> ns = new AList<>();
        AList<Double> times=new AList<>();
        AList<Integer> opc=new AList<>();
        for (int k : N) {
            AList<Integer> temp = new AList<>();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < k; j++) {
                temp.addLast(1);
            }
            double timeInSeconds = sw.elapsedTime();
            ns.addLast(k);
            times.addLast(timeInSeconds);
            opc.addLast(temp.opc());
        }
        printTimingTable(ns,times,opc);
    }

}
