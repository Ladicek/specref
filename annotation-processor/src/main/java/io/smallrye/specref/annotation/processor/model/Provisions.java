package io.smallrye.specref.annotation.processor.model;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.stream.JsonWriter;

public final class Provisions {
    private final Map<Section, Map<Provision, Set<TestMethod>>> map = new HashMap<>();

    public void add(Section section, Provision provision, TestMethod testMethod) {
        map.computeIfAbsent(section, ignored -> new HashMap<>())
                .computeIfAbsent(provision, ignored -> new HashSet<>())
                .add(testMethod);
    }

    public void writeJson(Writer writer) throws IOException {
        JsonWriter json = new JsonWriter(writer);
        json.setIndent("  "); // 2 spaces

        json.beginObject();
        for (Map.Entry<Section, Map<Provision, Set<TestMethod>>> sectionEntry : map.entrySet()) {
            Section section = sectionEntry.getKey();
            json.name(section.id);
            json.beginObject();
            for (Map.Entry<Provision, Set<TestMethod>> provisionEntry : sectionEntry.getValue().entrySet()) {
                Provision provision = provisionEntry.getKey();
                json.name(provision.id);
                json.beginArray();
                for (TestMethod testMethod : provisionEntry.getValue()) {
                    json.beginObject();
                    json.name("className").value(testMethod.className);
                    json.name("methodName").value(testMethod.methodName);
                    json.name("filePath").value(testMethod.filePath);
                    json.name("startLine").value(testMethod.startLine);
                    json.name("endLine").value(testMethod.endLine);
                    json.endObject();
                }
                json.endArray();
            }
            json.endObject();
        }
        json.endObject();
    }
}
