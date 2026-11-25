package medtrack;

import medtrack.graph.InteractionGraph;
import medtrack.model.Drug;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DrugAndGraphTest {
    @Test
    void builderCreatesDrug() {
        Drug d = new Drug.Builder().name("Aspirin").form("таблетка").doseMg(500).build();
        assertEquals("Aspirin", d.getName());
        assertEquals(500, d.getDoseMg());
    }

    @Test
    void builderRejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new Drug.Builder().name("  ").build());
    }

    @Test
    void builderRejectsNegativeDose() {
        assertThrows(IllegalArgumentException.class,
                () -> new Drug.Builder().name("X").doseMg(-1).build());
    }

    @Test
    void drugEqualityIgnoresCase() {
        assertEquals(Drug.ofName("Aspirin"), Drug.ofName("aspirin"));
    }

    @Test
    void conflictIsSymmetric() {
        InteractionGraph g = new InteractionGraph();
        g.addConflict(Drug.ofName("aspirin"), Drug.ofName("warfarin"));
        assertTrue(g.hasConflict(Drug.ofName("warfarin"), Drug.ofName("aspirin")));
    }

    @Test
    void noConflictForUnrelatedDrugs() {
        InteractionGraph g = new InteractionGraph();
        g.addConflict(Drug.ofName("a"), Drug.ofName("b"));
        assertFalse(g.hasConflict(Drug.ofName("a"), Drug.ofName("c")));
    }
}
