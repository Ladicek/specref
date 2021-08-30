package io.smallrye.specref.asciidoctor.render;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.DocinfoProcessor;
import org.asciidoctor.extension.Location;
import org.asciidoctor.extension.LocationType;

@Location(LocationType.HEADER)
public class StylesheetAndScripts extends DocinfoProcessor {
    @Override
    public String process(Document document) {
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("specref.html");
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
