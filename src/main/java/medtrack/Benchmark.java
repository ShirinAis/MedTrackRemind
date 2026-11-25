package medtrack;

import medtrack.graph.InteractionGraph;
import medtrack.model.Drug;

/** Өлшенген дәлел: графқа 100 000 жұп қосу және іздеу уақыты. Есепке скриншот салыңыз. */
public class Benchmark {
    public static void main(String[] args) {
        int n = 100_000;
        InteractionGraph g = new InteractionGraph();

        long t0 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            g.addConflict(Drug.ofName("drug" + i), Drug.ofName("drug" + (i + 1)));
        }
        long t1 = System.nanoTime();

        int found = 0;
        for (int i = 0; i < n; i++) {
            if (g.hasConflict(Drug.ofName("drug" + i), Drug.ofName("drug" + (i + 1)))) found++;
        }
        long t2 = System.nanoTime();

        System.out.printf("Қосу: %d жұп, %.1f мс%n", n, (t1 - t0) / 1e6);
        System.out.printf("Іздеу: %d сұраныс (%d табылды), %.1f мс%n", n, found, (t2 - t1) / 1e6);
    }
}
