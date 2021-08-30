package io.smallrye.specref.annotation.processor;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.Trees;

import io.smallrye.specref.annotation.processor.model.Provision;
import io.smallrye.specref.annotation.processor.model.Provisions;
import io.smallrye.specref.annotation.processor.model.Section;
import io.smallrye.specref.annotation.processor.model.TestMethod;
import io.smallrye.specref.core.ProvisionId;
import io.smallrye.specref.core.SectionId;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SpecRefProcessor extends AbstractProcessor {
    private final Provisions provisions = new Provisions();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            return doProcess(annotations, roundEnv);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unexpected error: " + e.getMessage());
            return false;
        }
    }

    private boolean doProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws IOException {
        for (TypeElement annotation : annotations) {
            SectionId sectionIdAnnotation = annotation.getAnnotation(SectionId.class);
            if (sectionIdAnnotation != null) {
                String sectionId = sectionIdAnnotation.value();

                for (Element annotated : roundEnv.getElementsAnnotatedWith(annotation)) {
                    AnnotationMirror annotationMirror = null;
                    for (AnnotationMirror it : annotated.getAnnotationMirrors()) {
                        if (it.getAnnotationType().asElement().equals(annotation)) {
                            annotationMirror = it;
                            break;
                        }
                    }
                    if (annotationMirror == null) { // should never happen
                        continue;
                    }

                    // the generated section annotation only has 1 member and that is mandatory
                    AnnotationValue provisions = annotationMirror.getElementValues().values().iterator().next();

                    List<AnnotationValue> provisionsArray = (List<AnnotationValue>) provisions.getValue();
                    for (AnnotationValue provisionValue : provisionsArray) {
                        VariableElement provisionEnum = (VariableElement) provisionValue.getValue();

                        String provisionId = provisionEnum.getAnnotation(ProvisionId.class).value();

                        Section section = new Section(sectionId);
                        Provision provision = new Provision(provisionId);
                        // the annotation can only be put on methods, so the cast here is safe
                        processAnnotatedMethod((ExecutableElement) annotated, section, provision);
                    }
                }
            }
        }

        if (roundEnv.processingOver()) {
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "specref.json");
            try (Writer writer = file.openWriter()) {
                provisions.writeJson(writer);
            }
        }

        return false;
    }

    private void processAnnotatedMethod(ExecutableElement method, Section section, Provision provision) {
        Element clazz = method;
        while (!(clazz instanceof TypeElement)) {
            clazz = clazz.getEnclosingElement();
        }

        String methodName = method.getSimpleName().toString();
        String className = clazz.getSimpleName().toString();

        Trees trees = Trees.instance(processingEnv);
        MethodTree methodTree = trees.getTree(method);
        CompilationUnitTree compilationUnit = trees.getPath(method).getCompilationUnit();
        long startPosition = trees.getSourcePositions().getStartPosition(compilationUnit, methodTree);
        long endPosition = trees.getSourcePositions().getEndPosition(compilationUnit, methodTree);

        long startLine = compilationUnit.getLineMap().getLineNumber(startPosition);
        long endLine = compilationUnit.getLineMap().getLineNumber(endPosition);

        String filePath = Paths.get(".").toUri().relativize(compilationUnit.getSourceFile().toUri()).getPath();

        TestMethod testMethod = new TestMethod(className, methodName, filePath, startLine, endLine);

        provisions.add(section, provision, testMethod);
    }
}
