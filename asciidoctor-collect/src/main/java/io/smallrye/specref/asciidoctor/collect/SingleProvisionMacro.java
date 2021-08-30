package io.smallrye.specref.asciidoctor.collect;

import java.util.Map;

import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.Cursor;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Format;
import org.asciidoctor.extension.FormatType;
import org.asciidoctor.extension.InlineMacroProcessor;
import org.asciidoctor.extension.Name;

import io.smallrye.specref.asciidoctor.collect.model.Location;
import io.smallrye.specref.asciidoctor.collect.model.Provision;
import io.smallrye.specref.asciidoctor.collect.model.Provisions;
import io.smallrye.specref.asciidoctor.collect.model.Section;

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
        provisions.add(findSection(parent), new Provision(target, findLocation(parent)));

        return createPhraseNode(parent, "quoted", (String) attributes.get("text"));
    }

    private static Section findSection(ContentNode node) {
        while (!(node instanceof org.asciidoctor.ast.Section)) {
            node = node.getParent();
        }
        return new Section(node.getId(), ((org.asciidoctor.ast.Section) node).getTitle());
    }

    private static Location findLocation(ContentNode node) {
        while (!(node instanceof StructuralNode)) {
            node = node.getParent();
        }

        Cursor cursor = ((StructuralNode) node).getSourceLocation();
        if (cursor == null) {
            return null;
        }
        return new Location(cursor.getPath(), cursor.getLineNumber());
    }
}
