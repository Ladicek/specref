package io.smallrye.specref.asciidoctor.collect;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

final class TypeNames {
    static final TypeName RETENTION_POLICY = ClassName.get(RetentionPolicy.class);
    static final TypeName ELEMENT_TYPE = ClassName.get(ElementType.class);
}
