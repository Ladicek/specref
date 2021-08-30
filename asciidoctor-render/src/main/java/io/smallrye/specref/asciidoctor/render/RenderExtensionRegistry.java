package io.smallrye.specref.asciidoctor.render;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;

import io.smallrye.specref.asciidoctor.render.model.Provisions;

public class RenderExtensionRegistry implements ExtensionRegistry {
    @Override
    public void register(Asciidoctor asciidoctor) {
        try {
            // TODO read all specref.json files available on classpath and merge the data!
            InputStream jsonStream = this.getClass().getClassLoader().getResourceAsStream("specref.json");
            Provisions provisions = Provisions.readJson(new InputStreamReader(jsonStream, StandardCharsets.UTF_8));

            asciidoctor.javaExtensionRegistry().inlineMacro(new SingleProvisionMacro(provisions));
            asciidoctor.javaExtensionRegistry().docinfoProcessor(StylesheetAndScripts.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
