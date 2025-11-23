package com.mycompany.mavenproject1;

class DynamicField {
    private String name;
    private String type;
    private String customClassName; // Para campos de tipo clase personalizada
    private String listElementType; // Para campos de tipo lista

    public DynamicField(String name, String type) {
        this.name = name;
        this.type = type;
        this.customClassName = null;
        this.listElementType = null;
    }

    public DynamicField(String name, String type, String customClassName) {
        this.name = name;
        this.type = type;
        this.customClassName = customClassName;
        this.listElementType = null;
    }

    public DynamicField(String name, String type, String customClassName, String listElementType) {
        this.name = name;
        this.type = type;
        this.customClassName = customClassName;
        this.listElementType = listElementType;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getCustomClassName() { return customClassName; }
    public String getListElementType() { return listElementType; }

    public boolean isCustomClass() {
        return "custom".equals(type);
    }

    public boolean isList() {
        return "List".equals(type);
    }
}