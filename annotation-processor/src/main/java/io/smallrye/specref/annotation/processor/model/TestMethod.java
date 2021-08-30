package io.smallrye.specref.annotation.processor.model;

import java.util.Objects;

public final class TestMethod {
    public final String className;
    public final String methodName;

    public final String filePath;
    public final long startLine;
    public final long endLine;

    public TestMethod(String className, String methodName, String filePath, long startLine, long endLine) {
        this.className = Objects.requireNonNull(className);
        this.methodName = Objects.requireNonNull(methodName);

        this.filePath = Objects.requireNonNull(filePath);
        this.startLine = startLine;
        this.endLine = endLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TestMethod that = (TestMethod) o;
        return startLine == that.startLine && endLine == that.endLine && className.equals(that.className)
                && methodName.equals(that.methodName) && filePath.equals(that.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, methodName, filePath, startLine, endLine);
    }

    @Override
    public String toString() {
        return "TestMethod{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", startLine=" + startLine +
                ", endLine=" + endLine +
                '}';
    }
}
