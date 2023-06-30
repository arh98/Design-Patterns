package org.ood;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class FCIExtractor extends VoidVisitorAdapter<Void> {
    private List<String> extendsList;
    private List<String> implementsList;
    public static List<String> getFieldNames(ClassOrInterfaceDeclaration cls) {
        return cls.getFields().stream()
                .map(field -> field.getVariable(0).getNameAsString() + ": " + field.getElementType())
                .collect(Collectors.toList());
    }
    public static List<String> findChildClasses(CompilationUnit cu) {
        return cu.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .filter(ClassOrInterfaceDeclaration::isNestedType)
                .map(ClassOrInterfaceDeclaration::getNameAsString)
                .collect(Collectors.toList());
    }

    public void findExtendsAndImplements(CompilationUnit cu, String className) {
        ClassOrInterfaceDeclaration cid = cu.getClassByName(className).orElse(null);
        if (cid == null) return;

        extendsList = cid.getExtendedTypes().stream()
                .filter(ClassOrInterfaceType.class::isInstance)
                .map(t -> t.asString())
                .collect(Collectors.toList());

        implementsList = cid.getImplementedTypes().stream()
                .filter(ClassOrInterfaceType.class::isInstance)
                .map(t -> t.asString())
                .collect(Collectors.toList());
    }

    public List<String> getExtendsList() {
        return extendsList != null ? extendsList : List.of();
    }

    public List<String> getImplementsList() {
        return implementsList != null ? implementsList : List.of();
    }
}
