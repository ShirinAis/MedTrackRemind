[README.md](https://github.com/user-attachments/files/35411845/README.md)
# MedTrackRemind

Дәрілердің үйлесімділігін тексеретін ақылды еске салғыш (Java ООП, 9-нұсқа).
Қолданушы дәрі қабылдау кестесін жүргізеді. Егер екі дәрі бір-біріне үйлеспесе, жүйе бірден дабыл береді.
Желілік нұсқада сервер базаны ұстайды, ал екі клиент (MedTracker 1 және 2) серверге қосылып, дабылды нақты уақытта алады.

## Мүмкіндіктер
- Дәрілерді, дозалау түрлерін (таблетка, инъекция, тамшы) және қабылдау кестесін сақтау
- Үйлеспейтін дәрі жұптарының графы
- Фондық ағын базаны тексеріп, жаңа қайшылық қосылса дереу дабыл береді
- Екі дәрі де кестеде тұрса, «қауіпті» дабыл (`ALERT_DANGER`)
- Көп клиентті сервер: дабыл барлық клиентке бірдей барады

## Архитектура
```
cli       Main, ServerMain, ClientMain            (консоль)
net       MedTrackServer, ClientHandler,
          MedTrackClient, NetworkNotifier         (желі, Socket)
reminder  ConflictMonitor, ReminderService,
          ReminderListener (Observer)             (фондық ағындар, дабыл)
graph     InteractionGraph: Map<Drug, Set<Drug>>  (қайшылық графы)
model     Drug (Builder), Dosage (Tablet/Injection/Drops), DosageFactory, ScheduleEntry
db        Database, DrugRepository, InteractionRepository, ScheduleRepository  (SQLite/JDBC)
```
Жоғарыдағы қабат төменгісін шақырады.

## Қолданылған технологиялар мен үлгілер
Java 17+, Maven, SQLite (JDBC), JUnit 5.
Үлгілер: Builder, Observer, Factory. Тақырыптар: полиморфизм, коллекциялар (Map/Set), Stream/Lambda, көп ағындылық, синхрондау, Socket, JDBC.

## Рөлдер
| Студент | Бөлігі |
|---|---|
| 1-студент | `Drug` + Builder, `InteractionGraph`, SQLite/JDBC (`db` пакеті), `DrugAndGraphTest`, `DatabaseTest`, `Benchmark` |
| 2-студент | `Dosage` түрлері, `DosageFactory`, `ScheduleEntry`, `ScheduleRepository`, Observer, `ReminderService`, `ConflictMonitor`, `Main`, `DosageTest`, `ConflictMonitorTest` |
| 3-студент | `net` пакеті: сервер, клиент, хаттама, `NetworkTest` |

## Іске қосу
Талап: JDK 17+ және Maven.
```
mvn test
mvn compile exec:java                                   # жергілікті нұсқа (Main)
mvn compile exec:java -Dexec.mainClass=medtrack.net.ServerMain
mvn compile exec:java -Dexec.mainClass=medtrack.net.ClientMain -Dexec.args="localhost 5050 MedTracker1"
mvn compile exec:java -Dexec.mainClass=medtrack.net.ClientMain -Dexec.args="localhost 5050 MedTracker2"
```
Әр команданы бөлек терминалда іске қосыңыз (сервер, 1-клиент, 2-клиент).

Клиент командалары: `conflict дәрі1 дәрі2`, `schedule дәрі ТҮРІ мөлшер HH:mm`, `list`, `plan`, `check`, `quit`.

## Желі хаттамасы
Мәтіндік, әр жол бір команда: `CONFLICT a|b`, `SCHEDULE дәрі|ТҮРІ|мөлшер|HH:mm`, `LIST_CONFLICTS`, `LIST_SCHEDULE`, `CHECK`, `QUIT`.
Жауап: `OK ...` немесе `ERR ...`. Сервер өзі жібереді: `ALERT ...`, `ALERT_DANGER ...`, `REMINDER ...`.

## Тесттер
`mvn test`: 23 JUnit-тест (DrugAndGraphTest 6, DatabaseTest 5, DosageTest 4, ConflictMonitorTest 4, NetworkTest 4). Барлығы өтеді.

## Өнімділікті өлшеу
```
mvn compile exec:java -Dexec.mainClass=medtrack.Benchmark
java -XX:StartFlightRecording=duration=30s,filename=rec.jfr -cp target/classes medtrack.Benchmark
```
Бенчмарк графқа 100 000 жұп қосып, іздеу уақытын өлшейді.

## Қауіпсіздік
Барлық SQL сұрау `PreparedStatement` арқылы жүреді (SQL-инъекциядан қорғау тесттелген), ресурстар `try-with-resources` арқылы жабылады, кодта құпиясөз немесе API-кілт жоқ.

## Қорғау сценарийі
1. Серверді және екі клиентті іске қосыңыз.
2. Оқытушы жаңа үйлеспейтін жұп қосады (`conflict aspirin warfarin` немесе тікелей базаға `INSERT INTO interactions(drug_a, drug_b) VALUES('aspirin','warfarin');`).
3. Екі клиент те бірден дабыл алады.
