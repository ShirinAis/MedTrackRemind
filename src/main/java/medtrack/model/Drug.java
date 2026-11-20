package medtrack.model;

/** Дәрі. Builder үлгісі арқылы жасалады (1-студент). */
public class Drug {
    private final String name;
    private final String form;   // таблетка, инъекция, тамшы
    private final double doseMg;

    private Drug(Builder b) {
        this.name = b.name;
        this.form = b.form;
        this.doseMg = b.doseMg;
    }

    public String getName() { return name; }
    public String getForm() { return form; }
    public double getDoseMg() { return doseMg; }

    /** Тек аты бар қарапайым дәрі (графта іздеу үшін). */
    public static Drug ofName(String name) {
        return new Builder().name(name).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Drug)) return false;
        return name.equalsIgnoreCase(((Drug) o).name);
    }

    @Override
    public int hashCode() { return name.toLowerCase().hashCode(); }

    @Override
    public String toString() { return name; }

    public static class Builder {
        private String name;
        private String form = "таблетка";
        private double doseMg = 0;

        public Builder name(String name) { this.name = name; return this; }
        public Builder form(String form) { this.form = form; return this; }
        public Builder doseMg(double doseMg) { this.doseMg = doseMg; return this; }

        public Drug build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Дәрі аты бос болмауы керек");
            }
            if (doseMg < 0) {
                throw new IllegalArgumentException("Доза теріс болмауы керек");
            }
            name = name.trim();
            return new Drug(this);
        }
    }
}
