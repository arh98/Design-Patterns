package org.ood;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    public List<JavaClass> parse(List<File> files) throws IOException {

        FCIExtractor extractor = new FCIExtractor();
        List<JavaClass> javaClassList = new ArrayList<>();

        for (File file : files) {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                String packageName = setPackageName(cu);

                for (ClassOrInterfaceDeclaration classOrInterface : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                    JavaClass javaClass = new JavaClass();
                    setSomeJClassFieldsForTable(javaClass, classOrInterface, packageName);
                    setChildren(javaClass, cu);
                    setVisibility(classOrInterface, javaClass);
                    setInheritance(javaClass, extractor, cu);
                    setFields(classOrInterface, javaClass);
                    setMethods(javaClass, cu);

                    javaClassList.add(javaClass);
                }
            } catch (ParseProblemException e) {
                System.out.println(e.getMessage());
            }
        }

        return javaClassList;
    }
    private void setType(JavaClass javaClass, ClassOrInterfaceDeclaration coid) {
        javaClass.type = 1;
        if (coid.isInterface()) {
            javaClass.type = 2;
        }
        if (coid.isNestedType()) {
            javaClass.type = 3;
        }
    }
    private String setPackageName(CompilationUnit cu) {
        return cu.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
    }
    private void setFields(ClassOrInterfaceDeclaration coid, JavaClass javaClass) {
        javaClass.fields = FCIExtractor.getFieldNames(coid);
    }
    private void setMethods(JavaClass javaClass, CompilationUnit cu) {
        MethodExtractor methodFinder = new MethodExtractor();
        var methodDecs = cu.findAll(MethodDeclaration.class);
        for (MethodDeclaration methodDec : methodDecs) {
            methodFinder.extractMethods(methodDec);
        }
        javaClass.methods = methodFinder.getAllMethods();
        javaClass.overrideMethods = methodFinder.getOverriddenMethods();
        javaClass.staticMethods = methodFinder.getStaticMethods();
        javaClass.abstractMethods = methodFinder.getAbstractMethods();
        javaClass.finalMethods = methodFinder.getFinalMethods();
    }

    private void setChildren(JavaClass javaClass, CompilationUnit cu) {
        javaClass.children.addAll(FCIExtractor.findChildClasses(cu));
        if (javaClass.children.isEmpty()) {
            javaClass.children.add("0");
        }
    }

    private void setInheritance(JavaClass javaClass, FCIExtractor extractor, CompilationUnit cu) {
        extractor.findExtendsAndImplements(cu, javaClass.name);
        javaClass.Implements = extractor.getImplementsList();
        javaClass.Extends = extractor.getExtendsList();
        //fix exception
        if (javaClass.Extends.isEmpty()) {
            javaClass.Extends = new ArrayList<>(List.of("0"));
        }
        if (javaClass.Implements.isEmpty()) {
            javaClass.Implements = new ArrayList<>(List.of("0"));
        }
//        if (javaClass.Extends.isEmpty()) {
//            javaClass.Extends.add("0");
//        }
//        if (javaClass.Implements.isEmpty()) {
//            javaClass.Implements.add("0");
//        }
    }

    private void setVisibility(ClassOrInterfaceDeclaration coid, JavaClass javaClass) {
//        0 = package_private
        javaClass.visibility = 0;
        if (!coid.getModifiers().isEmpty()) {
            Modifier.Keyword visibility = coid.getModifiers().get(0).getKeyword();
            switch (visibility) {
                case PUBLIC -> javaClass.visibility = 1;
                case PRIVATE -> javaClass.visibility = 2;
                case PROTECTED -> javaClass.visibility = 3;
            }
        }
    }
    private void setSomeJClassFieldsForTable(JavaClass javaClass, ClassOrInterfaceDeclaration coid, String packageName) {
        javaClass.packageName = packageName;
        javaClass.name = coid.getNameAsString();
        javaClass.isInterface = coid.isInterface();
        javaClass.isAbstract = (coid.isAbstract()) ? 1 : 0;
        javaClass.isFinal = coid.isFinal() ? 1 : 0;
        javaClass.isStatic = coid.isStatic() ? 1 : 0;
        setType(javaClass, coid);
    }
}
