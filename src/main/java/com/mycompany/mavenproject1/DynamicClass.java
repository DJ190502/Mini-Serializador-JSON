package com.mycompany.mavenproject1;
import java.util.ArrayList;
import java.util.List;

class DynamicClass {
    private String name;
    private List<DynamicField> fields;

    public DynamicClass(String name) {
        this.name = name;
        this.fields = new ArrayList<>();
    }

    public void addField(String name, String type) {
        fields.add(new DynamicField(name, type));
    }

    public void addCustomField(String name, String customClassName) {
        fields.add(new DynamicField(name, "custom", customClassName));
    }

    public void addListField(String name, String elementType, String customClassName) {
        fields.add(new DynamicField(name, "List", customClassName, elementType));
    }

    public List<DynamicField> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }
}