package io.smallrye.specref.asciidoctor.collect.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class Provisions {
    private final Map<Section, List<Provision>> map = new HashMap<>();

    public void add(Section section, Provision provision) {
        map.computeIfAbsent(section, ignored -> new ArrayList<>()).add(provision);
    }

    public void forEach(BiConsumer<Section, List<Provision>> function) {
        map.forEach(function);
    }
}
