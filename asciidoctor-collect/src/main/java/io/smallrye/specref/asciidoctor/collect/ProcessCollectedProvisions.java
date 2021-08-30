package io.smallrye.specref.asciidoctor.collect;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.file.Paths;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import io.smallrye.specref.asciidoctor.collect.model.Provision;
import io.smallrye.specref.asciidoctor.collect.model.Provisions;
import io.smallrye.specref.asciidoctor.collect.model.Section;
import io.smallrye.specref.core.ProvisionId;
import io.smallrye.specref.core.SectionId;

public class ProcessCollectedProvisions extends Postprocessor {
    private final Provisions provisions;

    ProcessCollectedProvisions(Provisions provisions) {
        this.provisions = provisions;
    }

    @Override
    public String process(Document document, String output) {
        String targetDirectory = (String) document.getAttribute("specref-target-directory");
        String packageName = (String) document.getAttribute("specref-package-name");

        provisions.forEach((section, provisionsInSection) -> generateEnumAndAnnotation(section, provisionsInSection,
                targetDirectory, packageName));

        return output;
    }

    private void generateEnumAndAnnotation(Section section, List<Provision> provisions, String targetDirectory,
            String packageName) {
        try {
            doGenerateEnumAndAnnotation(section, provisions, targetDirectory, packageName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void doGenerateEnumAndAnnotation(Section section, List<Provision> provisions, String targetDirectory,
            String packageName) throws IOException {
        String enumName = idToTypeName(section.id) + "ID";
        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(enumName)
                .addJavadoc("Enumeration of all provisions in the <em>$L</em> section.", section.name)
                .addModifiers(Modifier.PUBLIC);
        for (Provision provision : provisions) {
            TypeSpec.Builder enumConstantBuilder = TypeSpec.anonymousClassBuilder("");
            if (provision.location != null) {
                enumConstantBuilder.addJavadoc("$L:$L", provision.location.fileName, provision.location.lineNumber);
            }
            enumConstantBuilder.addAnnotation(AnnotationSpec.builder(ProvisionId.class)
                    .addMember("value", "$S", provision.id)
                    .build());
            enumBuilder.addEnumConstant(idToConstantName(provision.id), enumConstantBuilder.build());
        }
        JavaFile.builder(packageName, enumBuilder.build())
                .indent("    ") // 4 spaces
                .build()
                .writeTo(Paths.get(targetDirectory));

        JavaFile.builder(packageName, TypeSpec.annotationBuilder(idToTypeName(section.id))
                .addJavadoc("Reference to one or more provisions in the <em>$L</em> section.", section.name)
                .addAnnotation(AnnotationSpec.builder(SectionId.class)
                        .addMember("value", "$S", section.id)
                        .build())
                .addAnnotation(AnnotationSpec.builder(Retention.class)
                        .addMember("value", "$T.RUNTIME", TypeNames.RETENTION_POLICY)
                        .build())
                .addAnnotation(AnnotationSpec.builder(Target.class)
                        .addMember("value", "$T.METHOD", TypeNames.ELEMENT_TYPE)
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("value")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(ArrayTypeName.of(ClassName.get(packageName, enumName)))
                        .build())
                .build())
                .indent("    ") // 4 spaces
                .build()
                .writeTo(Paths.get(targetDirectory));
    }

    private String idToTypeName(String id) {
        StringBuilder result = new StringBuilder();
        boolean nextToUpper = true;
        for (char ch : id.toCharArray()) {
            if (Character.isLetterOrDigit(ch)) {
                if (nextToUpper) {
                    result.append(Character.toUpperCase(ch));
                    nextToUpper = false;
                } else {
                    result.append(ch);
                }
            } else {
                nextToUpper = true;
            }
        }
        return result.toString();
    }

    private String idToConstantName(String id) {
        StringBuilder result = new StringBuilder();
        for (char ch : id.toCharArray()) {
            if (Character.isLetterOrDigit(ch)) {
                result.append(Character.toUpperCase(ch));
            } else {
                result.append('_');
            }
        }
        return result.toString();
    }
}
