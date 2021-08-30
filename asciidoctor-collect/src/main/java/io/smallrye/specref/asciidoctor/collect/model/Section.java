package io.smallrye.specref.asciidoctor.collect.model;

import java.util.Objects;

public final class Section {
    public final String id; // globally unique
    public final String name;

    public Section(String id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = name != null ? name : id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section) o;
        return id.equals(section.id) && name.equals(section.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
