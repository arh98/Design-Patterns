package org.ood;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

public class MethodExtractor {
    private final List<MethodData> allMethods = new ArrayList<>();
    private final List<MethodData> overriddenMethods = new ArrayList<>();
    private final List<String> staticMethods = new ArrayList<>();
    private final List<String> finalMethods = new ArrayList<>();
    private final List<String> abstractMethods = new ArrayList<>();
    public void extractMethods(MethodDeclaration methodDec) {
        MethodData methodData = getMethodData(methodDec);
        String methodName = methodData.getName();

        allMethods.add(methodData);

        Optional<MethodDeclaration> overriddenMethod = methodDec.getAnnotationByName("Override")
                .flatMap(annotationExpr -> annotationExpr.getParentNode().filter(MethodDeclaration.class::isInstance))
                .map(MethodDeclaration.class::cast);

        overriddenMethod.ifPresent(method -> {
            MethodData overriddenMethodData = getMethodData(method);
            overriddenMethods.add(overriddenMethodData);
        });
        if (methodDec.isStatic()) {
            staticMethods.add(methodName);
        } else if (methodDec.isFinal()) {
            finalMethods.add(methodName);
        } else if (methodDec.isAbstract()) {
            abstractMethods.add(methodName);
        }
    }

    private MethodData getMethodData(MethodDeclaration methodDec) {
        var methodData = new MethodData();
        methodData.setName(methodDec.getNameAsString());
        if (methodDec.getNameAsString().equals(methodDec.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString())) {
            methodData.setConstructor(true);
        }
        methodData.setReturnType(methodDec.getTypeAsString());
        methodData.getParameters().addAll(methodDec.getParameters().stream().map(p -> p.getTypeAsString()).collect(Collectors.toList()));
        methodData.setModifiers(methodDec.getModifiers());
        return methodData;
    }

    public List<MethodData> getAllMethods() {
        return allMethods;
    }

    public List<MethodData> getOverriddenMethods() {
        return overriddenMethods;
    }

    public List<String> getStaticMethods() {
        return staticMethods;
    }

    public List<String> getFinalMethods() {
        return finalMethods;
    }

    public List<String> getAbstractMethods() {
        return abstractMethods;
    }
}
class MethodData {
    private String name;
    private List<String> parameters;
    private NodeList<Modifier> modifiers;
    private String returnType;
    private boolean isConstructor ;
    public MethodData() {
        parameters = new ArrayList<>();
        modifiers = new NodeList<>();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<String> getParameters() {
        return parameters;
    }
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
    public NodeList<Modifier> getModifiers() {
        return modifiers;
    }
    public void setModifiers(NodeList<Modifier> modifiers) {
        this.modifiers = modifiers;
    }
    public String getReturnType() {
        return returnType;
    }
    public MethodData setReturnType(String returnType) {
        this.returnType = returnType;
        return this;
    }
    public String getSignature(){
        if(isConstructor){
            return " Constructor :" + String.join((CharSequence) ",", (CharSequence) modifiers.get(0)) ;
        }
        return name + "{ Params : " + String.join(",",parameters) + " Returns : " + returnType;
    }
    public boolean isConstructor() {
        return isConstructor;
    }
    public void setConstructor(boolean constructor) {
        this.isConstructor = constructor;
    }
}
