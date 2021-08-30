package io.smallrye.specref.asciidoctor.render.model;

import java.util.Objects;

public final class Provision {
    public final String id; // unique inside a section

    public Provision(String id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Provision provision = (Provision) o;
        return id.equals(provision.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Provision{" +
                "id='" + id + '\'' +
                '}';
    }
}
