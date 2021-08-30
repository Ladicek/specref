package io.smallrye.specref.asciidoctor.render;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.Format;
import org.asciidoctor.extension.FormatType;
import org.asciidoctor.extension.InlineMacroProcessor;
import org.asciidoctor.extension.Name;

import io.smallrye.specref.asciidoctor.render.model.Provision;
import io.smallrye.specref.asciidoctor.render.model.Provisions;
import io.smallrye.specref.asciidoctor.render.model.Section;
import io.smallrye.specref.asciidoctor.render.model.TestMethod;

@Name("specref")
@Format(FormatType.LONG)
@ContentModel(ContentModel.SIMPLE)
public class SingleProvisionMacro extends InlineMacroProcessor {
    private final Provisions provisions;

    SingleProvisionMacro(Provisions provisions) {
        this.provisions = provisions;
    }

    @Override
    public Object process(ContentNode parent, String target, Map<String, Object> attributes) {
        String baseUrl = (String) parent.getDocument().getAttribute("specref-base-url");

        Section section = new Section(findSectionId(parent));
        Provision provision = new Provision(target);
        Set<TestMethod> testMethods = provisions.get(section, provision);

        String html = "<span class=\"specref-bracket\">[</span>" + attributes.get("text")
                + "<span class=\"specref-bracket\">]</span>"
                + "<sub><a class=\"specref-link\" id=\"link-" + section.id + "-" + provision.id + "\">TCK</a></sub>"
                + "<span class=\"specref-list\" id=\"list-" + section.id + "-" + provision.id + "\">"
                + testMethods.stream()
                        .map(it -> "<a href=\"" + baseUrl + it.filePath + "#L" + it.startLine + "-L" + it.endLine
                                + "\" target=\"_blank\"><code>" + it.className + "." + it.methodName + "()</code></a>")
                        .collect(Collectors.joining())
                + "</span>";

        return createPhraseNode(parent, "quoted", html);
    }

    private static String findSectionId(ContentNode node) {
        while (!(node instanceof org.asciidoctor.ast.Section)) {
            node = node.getParent();
        }
        return node.getId();
    }
}
