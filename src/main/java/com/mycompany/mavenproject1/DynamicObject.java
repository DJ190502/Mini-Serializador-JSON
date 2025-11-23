package com.mycompany.mavenproject1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DynamicObject {
    private DynamicClass dynamicClass;
    private Map<String, Object> fieldValues;
    private Map<String, Object> objectInstances;

    public DynamicObject(DynamicClass dynamicClass, Map<String, Object> objectInstances) {
        this.dynamicClass = dynamicClass;
        this.fieldValues = new HashMap<>();
        this.objectInstances = objectInstances;

        // Inicializar valores por defecto
        for (DynamicField field : dynamicClass.getFields()) {
            if (field.isCustomClass()) {
                // Para campos de clase personalizada, crear una nueva instancia si es necesario
                if (field.getCustomClassName() != null && !field.getCustomClassName().isEmpty()) {
                    // Buscar la clase personalizada
                    DynamicClass customClass = findCustomClass(field.getCustomClassName());
                    if (customClass != null) {
                        // Crear una nueva instancia para este campo
                        DynamicObject nestedObject = new DynamicObject(customClass, objectInstances);
                        fieldValues.put(field.getName(), nestedObject);
                    } else {
                        fieldValues.put(field.getName(), null);
                    }
                } else {
                    fieldValues.put(field.getName(), null);
                }
            } else if (field.isList()) {
                // Para campos de tipo lista, crear una lista vacía
                fieldValues.put(field.getName(), new ArrayList<>());
            } else {
                switch (field.getType()) {
                    case "String": fieldValues.put(field.getName(), ""); break;
                    case "int": fieldValues.put(field.getName(), 0); break;
                    case "double": fieldValues.put(field.getName(), 0.0); break;
                    case "boolean": fieldValues.put(field.getName(), false); break;
                    case "Object": fieldValues.put(field.getName(), null); break;
                }
            }
        }
    }

    private DynamicClass findCustomClass(String className) {
        // Este método debería buscar en el mapa de clases definidas
        // Necesitamos acceso al mapa de clases definidas, lo pasaremos como parámetro
        // Por ahora retornamos null, se implementará en la GUI
        return null;
    }

    public void setFieldValue(String fieldName, String value) {
        DynamicField field = findField(fieldName);
        if (field != null) {
            try {
                if (field.isCustomClass()) {
                    // Para campos de clase personalizada, no se puede establecer con un String simple
                    // Esto se manejará de forma especial en la GUI
                    throw new IllegalArgumentException("Los campos de clase personalizada requieren configuración especial");
                } else if (field.isList()) {
                    // Para listas, no se puede establecer con un String simple
                    throw new IllegalArgumentException("Los campos de lista requieren configuración especial");
                } else {
                    switch (field.getType()) {
                        case "String":
                            fieldValues.put(fieldName, value);
                            break;
                        case "int":
                            fieldValues.put(fieldName, Integer.parseInt(value.isEmpty() ? "0" : value));
                            break;
                        case "double":
                            fieldValues.put(fieldName, Double.parseDouble(value.isEmpty() ? "0.0" : value));
                            break;
                        case "boolean":
                            fieldValues.put(fieldName, Boolean.parseBoolean(value));
                            break;
                        case "Object":
                            if ("null".equals(value)) {
                                fieldValues.put(fieldName, null);
                            } else {
                                Object referencedObject = objectInstances.get(value);
                                fieldValues.put(fieldName, referencedObject);
                            }
                            break;
                    }
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor inválido para campo " + fieldName + ": " + value);
            }
        }
    }

    // Nuevo método para establecer valores de campos de clase personalizada
    public void setCustomFieldValue(String fieldName, DynamicObject value) {
        DynamicField field = findField(fieldName);
        if (field != null && field.isCustomClass()) {
            fieldValues.put(fieldName, value);
        }
    }

    // Nuevo método para establecer valores de listas
    @SuppressWarnings("unchecked")
    public void setListFieldValue(String fieldName, List<Object> value) {
        DynamicField field = findField(fieldName);
        if (field != null && field.isList()) {
            fieldValues.put(fieldName, value);
        }
    }

    private DynamicField findField(String fieldName) {
        for (DynamicField field : dynamicClass.getFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    public String getClassName() {
        return dynamicClass.getName();
    }

    public String getFieldValuesString() {
        StringBuilder sb = new StringBuilder();
        for (DynamicField field : dynamicClass.getFields()) {
            Object value = fieldValues.get(field.getName());
            if (value instanceof DynamicObject) {
                sb.append(field.getName()).append("={...}; ");
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                sb.append(field.getName()).append("=[size=").append(list.size()).append("]; ");
            } else {
                sb.append(field.getName()).append("=").append(value).append("; ");
            }
        }
        return sb.toString();
    }

    public Map<String, Object> getFieldValues() {
        return fieldValues;
    }

    public Object getFieldValue(String fieldName) {
        return fieldValues.get(fieldName);
    }

    public DynamicClass getDynamicClass() {
        return dynamicClass;
    }
}