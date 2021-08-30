package io.smallrye.specref.asciidoctor.collect.model;

import java.util.Objects;

public final class Location {
    public final String fileName;
    public final int lineNumber;

    public Location(String fileName, int lineNumber) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Location that = (Location) o;
        return lineNumber == that.lineNumber && Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, lineNumber);
    }

    @Override
    public String toString() {
        return "Location{" +
                "fileName='" + fileName + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
