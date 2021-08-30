package io.smallrye.specref.asciidoctor.collect.model;

import java.util.Objects;

public final class Provision {
    public final String id; // unique inside a section
    public final Location location; // may be null if source location info is missing

    public Provision(String id, Location location) {
        this.id = Objects.requireNonNull(id);
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Provision provision = (Provision) o;
        return id.equals(provision.id) && Objects.equals(location, provision.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location);
    }

    @Override
    public String toString() {
        return "Provision{" +
                "id='" + id + '\'' +
                ", location=" + location +
                '}';
    }
}
