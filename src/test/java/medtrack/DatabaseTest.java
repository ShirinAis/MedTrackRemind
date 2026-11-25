package medtrack;

import medtrack.db.*;
import medtrack.model.Drug;
import medtrack.model.TabletDosage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    @TempDir Path dir;
    Database db;

    @BeforeEach
    void setUp() { db = new Database(dir.resolve("test.db").toString()); }

    @Test
    void saveAndReadDrug() {
        DrugRepository repo = new DrugRepository(db);
        repo.save(new Drug.Builder().name("Ibuprofen").doseMg(200).build());
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void sqlInjectionIsHarmless() {
        DrugRepository repo = new DrugRepository(db);
        repo.save(new Drug.Builder().name("x'); DROP TABLE drugs;--").build());
        repo.save(new Drug.Builder().name("Normal").build());
        assertEquals(2, repo.findAll().size());   // кесте жойылған жоқ
    }

    @Test
    void conflictOrderDoesNotMatter() {
        InteractionRepository repo = new InteractionRepository(db);
        repo.addConflict("Aspirin", "Warfarin");
        repo.addConflict("warfarin", "aspirin");   // дубликат
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void graphLoadedFromDatabase() {
        InteractionRepository repo = new InteractionRepository(db);
        repo.addConflict("a", "b");
        assertTrue(repo.loadGraph().hasConflict(Drug.ofName("b"), Drug.ofName("a")));
    }

    @Test
    void scheduleRoundTrip() {
        ScheduleRepository repo = new ScheduleRepository(db);
        repo.add("Aspirin", new TabletDosage(1), LocalTime.of(8, 30));
        assertEquals(LocalTime.of(8, 30), repo.findAll().get(0).time());
    }
}
