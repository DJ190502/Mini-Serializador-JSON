package com.mycompany.mavenproject1;


import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class JsonSerializer {

    public static String serialize(Object obj) {
        if (obj == null) {
            return "null";
        }

        StringBuilder json = new StringBuilder();
        serializeValue(obj, json, new HashSet<>());
        return json.toString();
    }

    private static void serializeValue(Object value, StringBuilder json, HashSet<Object> visited) {
        if (value == null) {
            json.append("null");
        } else if (value instanceof String) {
            serializeString((String) value, json);
        } else if (value instanceof Number || value instanceof Boolean) {
            json.append(value);
        } else if (value instanceof List) {
            serializeList((List<?>) value, json, visited);
        } else if (value instanceof DynamicObject) {
            serializeDynamicObject((DynamicObject) value, json, visited);
        } else {
            serializeObject(value, json, visited);
        }
    }

    private static void serializeList(List<?> list, StringBuilder json, HashSet<Object> visited) {
        json.append("[");
        boolean first = true;
        for (Object element : list) {
            if (!first) {
                json.append(",");
            }
            serializeValue(element, json, visited);
            first = false;
        }
        json.append("]");
    }

    private static void serializeDynamicObject(DynamicObject dynObj, StringBuilder json, HashSet<Object> visited) {
        if (visited.contains(dynObj)) {
            json.append("\"<cyclic reference>\"");
            return;
        }
        visited.add(dynObj);

        json.append("{");

        Map<String, Object> fieldValues = dynObj.getFieldValues();
        boolean first = true;

        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            if (!first) {
                json.append(",");
            }

            json.append("\"").append(entry.getKey()).append("\":");
            serializeValue(entry.getValue(), json, visited);

            first = false;
        }

        json.append("}");
        visited.remove(dynObj);
    }

    private static void serializeObject(Object obj, StringBuilder json, HashSet<Object> visited) {
        if (visited.contains(obj)) {
            json.append("\"<cyclic reference>\"");
            return;
        }
        visited.add(obj);

        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();

            json.append("{");

            boolean firstField = true;
            for (Field field : fields) {
                if (java.lang.reflect.Modifier.isPublic(field.getModifiers())) {
                    if (!firstField) {
                        json.append(",");
                    }

                    String fieldName = field.getName();
                    Object fieldValue = field.get(obj);

                    json.append("\"").append(fieldName).append("\":");
                    serializeValue(fieldValue, json, visited);

                    firstField = false;
                }
            }

            json.append("}");

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accediendo a los campos del objeto", e);
        } finally {
            visited.remove(obj);
        }
    }

    private static void serializeString(String str, StringBuilder json) {
        json.append("\"").append(escapeJson(str)).append("\"");
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}