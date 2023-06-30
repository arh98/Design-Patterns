package org.ood;

import java.util.ArrayList;
import java.util.List;

public class JavaClass {
    public String packageName;
    public String name;
    public int type;
    public int visibility;
    public int isAbstract;
    public int isStatic;
    public int isFinal;
    public boolean isInterface;
    public List<MethodData> methods;
    public List<MethodData> overrideMethods;
    public List<String> staticMethods;
    public List<String> finalMethods;
    public List<String> abstractMethods;
    public List<String> Extends;
    public List<String> Implements;
    public List<String> children;
    public List<String> fields;
    public JavaClass(){
        this.children = new ArrayList<>();
        this.Extends = new ArrayList<>();
        this.Implements = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.overrideMethods = new ArrayList<>();
        this.abstractMethods = new ArrayList<>();
        this.finalMethods = new ArrayList<>();
        this.staticMethods = new ArrayList<>();
    }
}
