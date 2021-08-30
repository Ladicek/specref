package io.smallrye.specref.asciidoctor.render.model;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.stream.JsonReader;

public final class Provisions {
    private final Map<Section, Map<Provision, Set<TestMethod>>> map;

    private Provisions(Map<Section, Map<Provision, Set<TestMethod>>> map) {
        this.map = Collections.unmodifiableMap(map);
    }

    public Set<TestMethod> get(Section section, Provision provision) {
        return map.getOrDefault(section, Collections.emptyMap())
                .getOrDefault(provision, Collections.emptySet());
    }

    public static Provisions readJson(Reader reader) throws IOException {
        Map<Section, Map<Provision, Set<TestMethod>>> map = new HashMap<>();

        JsonReader json = new JsonReader(reader);

        json.beginObject();
        while (json.hasNext()) {
            String sectionId = json.nextName();
            Section section = new Section(sectionId);
            json.beginObject();
            while (json.hasNext()) {
                String provisionId = json.nextName();
                Provision provision = new Provision(provisionId);
                json.beginArray();
                while (json.hasNext()) {
                    json.beginObject();
                    String className = null;
                    String methodName = null;
                    String filePath = null;
                    long startLine = -1;
                    long endLine = -1;
                    while (json.hasNext()) {
                        switch (json.nextName()) {
                            case "className":
                                className = json.nextString();
                                break;
                            case "methodName":
                                methodName = json.nextString();
                                break;
                            case "filePath":
                                filePath = json.nextString();
                                break;
                            case "startLine":
                                startLine = json.nextLong();
                                break;
                            case "endLine":
                                endLine = json.nextLong();
                                break;
                            default:
                                json.skipValue();
                        }
                    }
                    json.endObject();
                    TestMethod testMethod = new TestMethod(className, methodName, filePath, startLine, endLine);

                    map.computeIfAbsent(section, ignored -> new HashMap<>())
                            .computeIfAbsent(provision, ignored -> new HashSet<>())
                            .add(testMethod);
                }
                json.endArray();
            }
            json.endObject();
        }
        json.endObject();

        return new Provisions(map);
    }
}
