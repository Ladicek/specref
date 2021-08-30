package io.smallrye.specref.annotation.processor.model;

import java.util.Objects;

public final class Section {
    public final String id; // globally unique

    public Section(String id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section) o;
        return id.equals(section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id='" + id + '\'' +
                '}';
    }
}
