package io.smallrye.specref.asciidoctor.collect;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;

import io.smallrye.specref.asciidoctor.collect.model.Provisions;

public class CollectExtensionRegistry implements ExtensionRegistry {
    @Override
    public void register(Asciidoctor asciidoctor) {
        Provisions provisions = new Provisions();

        asciidoctor.javaExtensionRegistry().inlineMacro(new SingleProvisionMacro(provisions));
        asciidoctor.javaExtensionRegistry().postprocessor(new ProcessCollectedProvisions(provisions));
    }
}
