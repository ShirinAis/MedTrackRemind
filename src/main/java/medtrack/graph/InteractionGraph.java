package medtrack.graph;

import medtrack.model.Drug;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Дәрілердің өзара әрекеттесу графы: Map<Drug, Set<Drug>> (1-студент). Көп ағынға қауіпсіз. */
public class InteractionGraph {
    private final Map<Drug, Set<Drug>> conflicts = new ConcurrentHashMap<>();

    public void addConflict(Drug a, Drug b) {
        conflicts.computeIfAbsent(a, k -> ConcurrentHashMap.newKeySet()).add(b);
        conflicts.computeIfAbsent(b, k -> ConcurrentHashMap.newKeySet()).add(a);
    }

    public boolean hasConflict(Drug a, Drug b) {
        return conflicts.getOrDefault(a, Set.of()).contains(b);
    }

    public Set<Drug> conflictsOf(Drug d) {
        return Set.copyOf(conflicts.getOrDefault(d, Set.of()));
    }

    public int drugCount() { return conflicts.size(); }
}
