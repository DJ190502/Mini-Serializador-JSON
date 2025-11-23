package com.mycompany.mavenproject1;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

class ObjectSerializerGUI extends JFrame {
    private Map<String, DynamicClass> definedClasses;
    private Map<String, Object> objectInstances;
    private JComboBox<String> classComboBox, instanceComboBox, typeComboBox;
    private JTextField classNameField, fieldNameField, instanceNameField;
    private JTable fieldsTable, instancesTable;
    private DefaultTableModel fieldsModel, instancesModel;
    private JTextArea jsonOutputArea;
    private JPanel valueInputPanel;
    private JButton addFieldBtn, createClassBtn, createInstanceBtn, serializeBtn, deleteInstanceBtn, modifyInstanceBtn;

    public ObjectSerializerGUI() {
        definedClasses = new HashMap<>();
        objectInstances = new HashMap<>();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Object Serializer GUI - Mini Framework");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel izquierdo - Definición de clases
        JPanel leftPanel = createClassDefinitionPanel();

        // Panel central - Instancias y serialización
        JPanel centerPanel = createInstancePanel();

        // Panel derecho - Resultado JSON
        JPanel rightPanel = createJsonOutputPanel();

        // Crear paneles divisores principales con ajuste de tamaño
        JSplitPane leftCenterSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
        leftCenterSplit.setDividerLocation(400);
        leftCenterSplit.setResizeWeight(0.33); // Permite redimensionar proporcionalmente
        leftCenterSplit.setOneTouchExpandable(true); // Botones para expandir/contraer

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCenterSplit, rightPanel);
        mainSplit.setDividerLocation(800);
        mainSplit.setResizeWeight(0.66); // Permite redimensionar proporcionalmente
        mainSplit.setOneTouchExpandable(true); // Botones para expandir/contraer

        add(mainSplit, BorderLayout.CENTER);

        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    private JPanel createClassDefinitionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Definición de Clases"));

        // Panel superior para crear nueva clase
        JPanel newClassPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        newClassPanel.add(new JLabel("Nombre Clase:"));
        classNameField = new JTextField();
        newClassPanel.add(classNameField);

        // Panel para agregar campos
        JPanel fieldDefinitionPanel = new JPanel(new BorderLayout(5, 5));
        fieldDefinitionPanel.setBorder(BorderFactory.createTitledBorder("Agregar Campos a la Clase"));

        JPanel fieldInputPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        fieldInputPanel.add(new JLabel("Nombre Campo:"));
        fieldNameField = new JTextField();
        fieldInputPanel.add(fieldNameField);

        fieldInputPanel.add(new JLabel("Tipo Campo:"));

        // Actualizar el tipoComboBox para incluir clases personalizadas y List
        Vector<String> typeOptions = new Vector<>();
        typeOptions.add("String");
        typeOptions.add("int");
        typeOptions.add("double");
        typeOptions.add("boolean");
        typeOptions.add("Object");
        typeOptions.add("List");
        // Las clases personalizadas se agregarán dinámicamente

        typeComboBox = new JComboBox<>(typeOptions);
        fieldInputPanel.add(typeComboBox);

        addFieldBtn = new JButton("Agregar Campo");
        fieldInputPanel.add(addFieldBtn);

        fieldDefinitionPanel.add(fieldInputPanel, BorderLayout.NORTH);

        // Tabla de campos definidos
        fieldsModel = new DefaultTableModel(new String[]{"Nombre", "Tipo", "Tipo Elemento", "Clase Personalizada"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        fieldsTable = new JTable(fieldsModel);
        fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Botón para eliminar campo seleccionado
        JButton removeFieldBtn = new JButton("Eliminar Campo Seleccionado");

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.add(new JLabel("Campos Definidos para la Nueva Clase:"), BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(fieldsTable), BorderLayout.CENTER);
        tablePanel.add(removeFieldBtn, BorderLayout.SOUTH);

        fieldDefinitionPanel.add(tablePanel, BorderLayout.CENTER);

        // Botón para CREAR LA CLASE
        createClassBtn = new JButton("CREAR NUEVA CLASE");
        createClassBtn.setBackground(new Color(0, 150, 0));
        createClassBtn.setForeground(Color.WHITE);
        createClassBtn.setFont(new Font("Arial", Font.BOLD, 14));

        // ComboBox para ver clases existentes
        JPanel existingClassesPanel = new JPanel(new BorderLayout(5, 5));
        existingClassesPanel.setBorder(BorderFactory.createTitledBorder("Clases Existentes"));
        classComboBox = new JComboBox<>();
        classComboBox.addItem("Seleccionar clase...");
        existingClassesPanel.add(classComboBox, BorderLayout.CENTER);

        // Botón para ver campos de clase existente
        JButton viewClassBtn = new JButton("Ver Campos de Clase Existente");
        existingClassesPanel.add(viewClassBtn, BorderLayout.SOUTH);

        // Organizar los paneles con divisores para hacerlos redimensionables
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(newClassPanel, BorderLayout.NORTH);
        topPanel.add(fieldDefinitionPanel, BorderLayout.CENTER);

        // Crear un split pane vertical para la sección izquierda
        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, createClassBtn);
        leftSplit.setDividerLocation(400);
        leftSplit.setResizeWeight(0.8);
        leftSplit.setOneTouchExpandable(true);

        JSplitPane leftBottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftSplit, existingClassesPanel);
        leftBottomSplit.setDividerLocation(500);
        leftBottomSplit.setResizeWeight(0.9);
        leftBottomSplit.setOneTouchExpandable(true);

        panel.add(leftBottomSplit, BorderLayout.CENTER);

        // Listeners
        addFieldBtn.addActionListener(e -> addFieldToClass());
        createClassBtn.addActionListener(e -> createNewClass());
        removeFieldBtn.addActionListener(e -> removeSelectedField());
        viewClassBtn.addActionListener(e -> viewSelectedClassFields());

        return panel;
    }

    private JPanel createInstancePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Gestión de Instancias"));

        // Panel para crear instancia
        JPanel instanceCreationPanel = new JPanel(new BorderLayout(5, 5));
        instanceCreationPanel.setBorder(BorderFactory.createTitledBorder("Crear/Modificar Instancia"));

        JPanel instanceInputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        instanceInputPanel.add(new JLabel("Clase:"));
        instanceComboBox = new JComboBox<>();
        instanceComboBox.addItem("Seleccionar clase...");
        instanceInputPanel.add(instanceComboBox);

        instanceInputPanel.add(new JLabel("Nombre Instancia:"));
        instanceNameField = new JTextField();
        instanceInputPanel.add(instanceNameField);

        instanceCreationPanel.add(instanceInputPanel, BorderLayout.NORTH);

        // Panel para valores de campos - INICIALIZACIÓN DE ATRIBUTOS
        JPanel valuesPanel = new JPanel(new BorderLayout(5, 5));
        valuesPanel.setBorder(BorderFactory.createTitledBorder("Inicialización de Atributos"));
        valueInputPanel = new JPanel();
        valueInputPanel.setLayout(new BoxLayout(valueInputPanel, BoxLayout.Y_AXIS));
        JScrollPane valuesScrollPane = new JScrollPane(valueInputPanel);
        valuesScrollPane.setPreferredSize(new Dimension(300, 200));
        valuesPanel.add(valuesScrollPane, BorderLayout.CENTER);

        instanceCreationPanel.add(valuesPanel, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        createInstanceBtn = new JButton("Crear Instancia");
        createInstanceBtn.setBackground(new Color(0, 100, 200));
        createInstanceBtn.setForeground(Color.WHITE);

        modifyInstanceBtn = new JButton("Modificar Instancia");
        deleteInstanceBtn = new JButton("Eliminar Instancia");
        serializeBtn = new JButton("Serializar Instancia Seleccionada");
        serializeBtn.setBackground(new Color(150, 0, 150));
        serializeBtn.setForeground(Color.WHITE);

        buttonPanel.add(createInstanceBtn);
        buttonPanel.add(modifyInstanceBtn);
        buttonPanel.add(deleteInstanceBtn);
        buttonPanel.add(serializeBtn);

        instanceCreationPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Tabla de instancias existentes
        JPanel instancesTablePanel = new JPanel(new BorderLayout(5, 5));
        instancesTablePanel.setBorder(BorderFactory.createTitledBorder("Instancias Existentes - Click para Serializar"));
        instancesModel = new DefaultTableModel(new String[]{"Nombre", "Clase", "Valores"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        instancesTable = new JTable(instancesModel);
        instancesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        instancesTable.setRowHeight(25);

        // Agregar listener para serialización automática al seleccionar
        instancesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                serializeSelectedInstance();
            }
        });

        instancesTablePanel.add(new JScrollPane(instancesTable), BorderLayout.CENTER);

        // Crear split pane vertical para la sección central
        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, instanceCreationPanel, instancesTablePanel);
        centerSplit.setDividerLocation(350);
        centerSplit.setResizeWeight(0.5);
        centerSplit.setOneTouchExpandable(true);

        panel.add(centerSplit, BorderLayout.CENTER);

        // Listeners
        createInstanceBtn.addActionListener(e -> createInstance());
        modifyInstanceBtn.addActionListener(e -> modifyInstance());
        deleteInstanceBtn.addActionListener(e -> deleteInstance());
        serializeBtn.addActionListener(e -> serializeSelectedInstance());
        instanceComboBox.addActionListener(e -> updateValueInputs());
        instancesTable.getSelectionModel().addListSelectionListener(e -> loadSelectedInstance());

        return panel;
    }

    private JPanel createJsonOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Resultado JSON - Serialización"));

        jsonOutputArea = new JTextArea();
        jsonOutputArea.setEditable(false);
        jsonOutputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        jsonOutputArea.setBackground(new Color(240, 240, 240));
        jsonOutputArea.setLineWrap(true);
        jsonOutputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(jsonOutputArea);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void addFieldToClass() {
        String fieldName = fieldNameField.getText().trim();
        String fieldType = (String) typeComboBox.getSelectedItem();

        if (fieldName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre para el campo");
            return;
        }

        // Verificar si el campo ya existe
        for (int i = 0; i < fieldsModel.getRowCount(); i++) {
            String existingName = (String) fieldsModel.getValueAt(i, 0);
            if (existingName.equals(fieldName)) {
                JOptionPane.showMessageDialog(this, "Ya existe un campo con el nombre: " + fieldName);
                return;
            }
        }

        if ("List".equals(fieldType)) {
            // Para campos de tipo List, mostrar diálogo para seleccionar tipo de elemento
            Vector<String> elementTypeOptions = new Vector<>();
            elementTypeOptions.add("String");
            elementTypeOptions.add("int");
            elementTypeOptions.add("double");
            elementTypeOptions.add("boolean");
            elementTypeOptions.add("Object");

            // Agregar clases personalizadas como opciones
            for (String className : definedClasses.keySet()) {
                elementTypeOptions.add(className);
            }

            String selectedElementType = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleccione el tipo de elemento para la lista '" + fieldName + "':",
                    "Seleccionar Tipo de Elemento",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    elementTypeOptions.toArray(),
                    elementTypeOptions.get(0)
            );

            if (selectedElementType != null) {
                String customClassName = null;
                if (definedClasses.containsKey(selectedElementType)) {
                    customClassName = selectedElementType;
                }
                fieldsModel.addRow(new Object[]{fieldName, fieldType, selectedElementType, customClassName});
            }
        } else if ("Object".equals(fieldType)) {
            // Para campos de tipo Object, mostrar opciones de clases personalizadas
            Vector<String> classOptions = new Vector<>();
            classOptions.add("java.lang.Object"); // Tipo base por defecto
            for (String className : definedClasses.keySet()) {
                classOptions.add(className);
            }

            String selectedClass = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleccione el tipo específico para el campo '" + fieldName + "':",
                    "Seleccionar Tipo de Campo",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    classOptions.toArray(),
                    classOptions.get(0)
            );

            if (selectedClass != null) {
                if (!"java.lang.Object".equals(selectedClass)) {
                    // Es una clase personalizada
                    fieldsModel.addRow(new Object[]{fieldName, "custom", "", selectedClass});
                } else {
                    // Es un Object genérico
                    fieldsModel.addRow(new Object[]{fieldName, fieldType, "", ""});
                }
            }
        } else {
            fieldsModel.addRow(new Object[]{fieldName, fieldType, "", ""});
        }

        fieldNameField.setText("");
        fieldNameField.requestFocus();

        JOptionPane.showMessageDialog(this,
                "Campo '" + fieldName + "' agregado a la definición de clase");
    }

    private void removeSelectedField() {
        int selectedRow = fieldsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un campo para eliminar");
            return;
        }

        String fieldName = (String) fieldsModel.getValueAt(selectedRow, 0);
        fieldsModel.removeRow(selectedRow);
        JOptionPane.showMessageDialog(this, "Campo '" + fieldName + "' eliminado");
    }

    private void viewSelectedClassFields() {
        String selectedClass = (String) classComboBox.getSelectedItem();
        if (selectedClass == null || selectedClass.equals("Seleccionar clase...")) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase para ver sus campos");
            return;
        }

        DynamicClass dynClass = definedClasses.get(selectedClass);
        if (dynClass != null) {
            StringBuilder fieldsInfo = new StringBuilder();
            fieldsInfo.append("Campos de la clase '").append(selectedClass).append("':\n\n");

            for (DynamicField field : dynClass.getFields()) {
                fieldsInfo.append("• ").append(field.getName())
                        .append(" : ").append(field.getType());
                if (field.isList()) {
                    fieldsInfo.append("<").append(field.getListElementType()).append(">");
                }
                if (field.isCustomClass()) {
                    fieldsInfo.append(" (").append(field.getCustomClassName()).append(")");
                }
                fieldsInfo.append("\n");
            }

            JOptionPane.showMessageDialog(this, fieldsInfo.toString(),
                    "Campos de " + selectedClass, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createNewClass() {
        String className = classNameField.getText().trim();

        if (className.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre para la clase");
            return;
        }

        if (definedClasses.containsKey(className)) {
            JOptionPane.showMessageDialog(this, "Ya existe una clase con el nombre: " + className);
            return;
        }

        if (fieldsModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un campo a la clase antes de crearla");
            return;
        }

        try {
            // Crear clase dinámica
            DynamicClass newClass = new DynamicClass(className);

            // Agregar campos desde la tabla
            for (int i = 0; i < fieldsModel.getRowCount(); i++) {
                String fieldName = (String) fieldsModel.getValueAt(i, 0);
                String fieldType = (String) fieldsModel.getValueAt(i, 1);
                String elementType = (String) fieldsModel.getValueAt(i, 2);
                String customClass = (String) fieldsModel.getValueAt(i, 3);

                if ("List".equals(fieldType)) {
                    newClass.addListField(fieldName, elementType, customClass);
                } else if ("custom".equals(fieldType) && customClass != null && !customClass.isEmpty()) {
                    newClass.addCustomField(fieldName, customClass);
                } else {
                    newClass.addField(fieldName, fieldType);
                }
            }

            // Guardar la clase en el mapa
            definedClasses.put(className, newClass);

            // Actualizar comboboxes
            classComboBox.addItem(className);
            instanceComboBox.addItem(className);

            // Actualizar el typeComboBox con las nuevas clases personalizadas
            updateTypeComboBox();

            // Mostrar mensaje de éxito con detalles
            StringBuilder message = new StringBuilder();
            message.append("✅ CLASE CREADA EXITOSAMENTE\n\n");
            message.append("Nombre: ").append(className).append("\n");
            message.append("Número de campos: ").append(fieldsModel.getRowCount()).append("\n\n");
            message.append("Campos definidos:\n");

            for (int i = 0; i < fieldsModel.getRowCount(); i++) {
                String fieldName = (String) fieldsModel.getValueAt(i, 0);
                String fieldType = (String) fieldsModel.getValueAt(i, 1);
                String elementType = (String) fieldsModel.getValueAt(i, 2);
                String customClass = (String) fieldsModel.getValueAt(i, 3);
                message.append("• ").append(fieldName).append(" : ").append(fieldType);
                if ("List".equals(fieldType)) {
                    message.append("<").append(elementType).append(">");
                }
                if (customClass != null && !customClass.isEmpty()) {
                    message.append(" (").append(customClass).append(")");
                }
                message.append("\n");
            }

            // Limpiar los campos después de crear la clase
            classNameField.setText("");
            fieldsModel.setRowCount(0); // Limpiar la tabla de campos

            JOptionPane.showMessageDialog(this, message.toString(),
                    "Clase Creada Exitosamente", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error creando la clase: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTypeComboBox() {
        // Guardar la selección actual
        String currentSelection = (String) typeComboBox.getSelectedItem();

        // Reconstruir el combobox con todas las opciones
        Vector<String> typeOptions = new Vector<>();
        typeOptions.add("String");
        typeOptions.add("int");
        typeOptions.add("double");
        typeOptions.add("boolean");
        typeOptions.add("Object");
        typeOptions.add("List");

        // Agregar las clases personalizadas
        for (String className : definedClasses.keySet()) {
            typeOptions.add(className);
        }

        typeComboBox.setModel(new DefaultComboBoxModel<>(typeOptions));

        // Restaurar la selección anterior si todavía existe
        if (currentSelection != null) {
            typeComboBox.setSelectedItem(currentSelection);
        }
    }

    private void updateValueInputs() {
        valueInputPanel.removeAll();

        String selectedClass = (String) instanceComboBox.getSelectedItem();
        if (selectedClass == null || selectedClass.equals("Seleccionar clase...")) {
            valueInputPanel.revalidate();
            valueInputPanel.repaint();
            return;
        }

        DynamicClass dynClass = definedClasses.get(selectedClass);
        if (dynClass != null) {
            for (DynamicField field : dynClass.getFields()) {
                JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
                fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

                String fieldLabelText = field.getName() + " (" + field.getType();
                if (field.isList()) {
                    fieldLabelText += "<" + field.getListElementType() + ">";
                } else if (field.isCustomClass()) {
                    fieldLabelText += " - " + field.getCustomClassName();
                }
                fieldLabelText += "):";

                JLabel fieldLabel = new JLabel(fieldLabelText);
                fieldLabel.setPreferredSize(new Dimension(200, 25));
                fieldPanel.add(fieldLabel, BorderLayout.WEST);

                if (field.isList()) {
                    // Para campos de tipo lista, crear un botón para configurar la lista
                    JButton configureListBtn = new JButton("Configurar Lista");
                    configureListBtn.setName(field.getName());
                    configureListBtn.addActionListener(e -> configureListField(field));
                    fieldPanel.add(configureListBtn, BorderLayout.CENTER);
                } else if (field.isCustomClass()) {
                    // Para campos de clase personalizada, crear un panel especial
                    JPanel customFieldPanel = new JPanel(new BorderLayout(5, 5));

                    JComboBox<String> customCombo = new JComboBox<>();
                    customCombo.addItem("<nueva instancia>");
                    customCombo.addItem("<null>");

                    // Agregar instancias existentes del tipo correcto
                    for (Map.Entry<String, Object> entry : objectInstances.entrySet()) {
                        if (entry.getValue() instanceof DynamicObject) {
                            DynamicObject obj = (DynamicObject) entry.getValue();
                            if (field.getCustomClassName().equals(obj.getClassName())) {
                                customCombo.addItem(entry.getKey());
                            }
                        }
                    }

                    customCombo.setName(field.getName());
                    customCombo.setPreferredSize(new Dimension(150, 25));
                    customFieldPanel.add(customCombo, BorderLayout.CENTER);

                    JButton configureBtn = new JButton("Configurar");
                    configureBtn.setName(field.getName());
                    configureBtn.addActionListener(e -> configureCustomField(field, customCombo));
                    customFieldPanel.add(configureBtn, BorderLayout.EAST);

                    fieldPanel.add(customFieldPanel, BorderLayout.CENTER);
                } else if (field.getType().equals("boolean")) {
                    JComboBox<String> boolCombo = new JComboBox<>(new String[]{"true", "false"});
                    boolCombo.setName(field.getName());
                    boolCombo.setPreferredSize(new Dimension(120, 25));
                    fieldPanel.add(boolCombo, BorderLayout.CENTER);
                } else if (field.getType().equals("Object")) {
                    JComboBox<String> objCombo = new JComboBox<>();
                    objCombo.addItem("null");
                    for (String instanceName : objectInstances.keySet()) {
                        String currentInstanceName = instanceNameField.getText().trim();
                        if (!instanceName.equals(currentInstanceName)) {
                            objCombo.addItem(instanceName);
                        }
                    }
                    objCombo.setName(field.getName());
                    objCombo.setPreferredSize(new Dimension(120, 25));
                    fieldPanel.add(objCombo, BorderLayout.CENTER);
                } else {
                    JTextField valueField = new JTextField();
                    valueField.setName(field.getName());
                    valueField.setPreferredSize(new Dimension(120, 25));

                    switch (field.getType()) {
                        case "int":
                            valueField.setText("0");
                            valueField.setToolTipText("Valor entero (ej: 42)");
                            break;
                        case "double":
                            valueField.setText("0.0");
                            valueField.setToolTipText("Valor decimal (ej: 3.14)");
                            break;
                        case "String":
                            valueField.setText("");
                            valueField.setToolTipText("Texto (ej: Hola Mundo)");
                            break;
                    }

                    fieldPanel.add(valueField, BorderLayout.CENTER);
                }

                valueInputPanel.add(fieldPanel);
            }
        }

        valueInputPanel.revalidate();
        valueInputPanel.repaint();
    }

    private void configureListField(DynamicField field) {
        JDialog dialog = new JDialog(this, "Configurar Lista: " + field.getName(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));

        // Panel para agregar elementos
        JPanel addPanel = new JPanel(new BorderLayout(5, 5));
        addPanel.setBorder(BorderFactory.createTitledBorder("Agregar Elemento"));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JTextField elementField = new JTextField();
        JButton addButton = new JButton("Agregar");

        inputPanel.add(new JLabel("Valor:"), BorderLayout.WEST);
        inputPanel.add(elementField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        addPanel.add(inputPanel, BorderLayout.NORTH);

        // Lista de elementos actuales
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> elementsList = new JList<>(listModel);

        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setBorder(BorderFactory.createTitledBorder("Elementos de la Lista"));
        listPanel.add(new JScrollPane(elementsList), BorderLayout.CENTER);

        // Botones para la lista
        JPanel listButtonsPanel = new JPanel(new FlowLayout());
        JButton removeButton = new JButton("Eliminar Seleccionado");
        JButton clearButton = new JButton("Limpiar Lista");

        listButtonsPanel.add(removeButton);
        listButtonsPanel.add(clearButton);

        listPanel.add(listButtonsPanel, BorderLayout.SOUTH);

        contentPanel.add(addPanel, BorderLayout.NORTH);
        contentPanel.add(listPanel, BorderLayout.CENTER);

        // Botones de diálogo
        JPanel dialogButtonsPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Guardar");
        JButton cancelButton = new JButton("Cancelar");

        dialogButtonsPanel.add(saveButton);
        dialogButtonsPanel.add(cancelButton);

        // Cargar elementos existentes si los hay
        int selectedRow = instancesTable.getSelectedRow();
        if (selectedRow != -1) {
            String instanceName = (String) instancesModel.getValueAt(selectedRow, 0);
            DynamicObject instance = (DynamicObject) objectInstances.get(instanceName);
            if (instance != null) {
                @SuppressWarnings("unchecked")
                List<Object> currentList = (List<Object>) instance.getFieldValue(field.getName());
                if (currentList != null) {
                    for (Object element : currentList) {
                        listModel.addElement(element != null ? element.toString() : "null");
                    }
                }
            }
        }

        // Listeners
        addButton.addActionListener(e -> {
            String value = elementField.getText().trim();
            if (!value.isEmpty()) {
                listModel.addElement(value);
                elementField.setText("");
            }
        });

        removeButton.addActionListener(e -> {
            int selectedIndex = elementsList.getSelectedIndex();
            if (selectedIndex != -1) {
                listModel.remove(selectedIndex);
            }
        });

        clearButton.addActionListener(e -> {
            listModel.clear();
        });

        saveButton.addActionListener(e -> {
            // Guardar la lista en el campo correspondiente
            List<Object> elementList = new ArrayList<>();
            for (int i = 0; i < listModel.size(); i++) {
                String elementStr = listModel.getElementAt(i);
                Object elementValue = convertStringToType(elementStr, field.getListElementType());
                elementList.add(elementValue);
            }

            // Actualizar el campo en la instancia actual si estamos modificando
            int currentRow = instancesTable.getSelectedRow();
            if (currentRow != -1) {
                String instanceName = (String) instancesModel.getValueAt(currentRow, 0);
                DynamicObject instance = (DynamicObject) objectInstances.get(instanceName);
                if (instance != null) {
                    instance.setListFieldValue(field.getName(), elementList);
                    updateInstancesTable();
                }
            }

            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(dialogButtonsPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private Object convertStringToType(String value, String type) {
        if ("null".equals(value)) {
            return null;
        }

        try {
            switch (type) {
                case "String":
                    return value;
                case "int":
                    return Integer.parseInt(value);
                case "double":
                    return Double.parseDouble(value);
                case "boolean":
                    return Boolean.parseBoolean(value);
                default:
                    // Para clases personalizadas, buscar la instancia por nombre
                    if (objectInstances.containsKey(value)) {
                        return objectInstances.get(value);
                    }
                    return value;
            }
        } catch (NumberFormatException e) {
            return value; // En caso de error, devolver como String
        }
    }

    private void configureCustomField(DynamicField field, JComboBox<String> combo) {
        String selectedOption = (String) combo.getSelectedItem();

        if ("<nueva instancia>".equals(selectedOption)) {
            // Crear una nueva instancia para este campo
            String instanceName = JOptionPane.showInputDialog(this,
                    "Ingrese un nombre para la nueva instancia de " + field.getCustomClassName() + ":",
                    "Crear Nueva Instancia",
                    JOptionPane.QUESTION_MESSAGE);

            if (instanceName != null && !instanceName.trim().isEmpty()) {
                if (objectInstances.containsKey(instanceName)) {
                    JOptionPane.showMessageDialog(this, "Ya existe una instancia con ese nombre");
                    return;
                }

                try {
                    DynamicClass customClass = definedClasses.get(field.getCustomClassName());
                    DynamicObject nestedInstance = new DynamicObject(customClass, objectInstances);
                    objectInstances.put(instanceName, nestedInstance);

                    // Actualizar el combo
                    combo.addItem(instanceName);
                    combo.setSelectedItem(instanceName);

                    updateInstancesTable();
                    JOptionPane.showMessageDialog(this, "Instancia '" + instanceName + "' creada exitosamente");

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error creando instancia: " + e.getMessage());
                }
            }
        } else if (!"<null>".equals(selectedOption)) {
            // Configurar una instancia existente
            String instanceName = selectedOption;
            DynamicObject nestedInstance = (DynamicObject) objectInstances.get(instanceName);

            if (nestedInstance != null) {
                // Mostrar un diálogo para configurar los campos de la instancia anidada
                showNestedInstanceDialog(nestedInstance, instanceName);
            }
        }
    }

    private void showNestedInstanceDialog(DynamicObject nestedInstance, String instanceName) {
        JDialog dialog = new JDialog(this, "Configurar Instancia: " + instanceName, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear campos para cada atributo de la instancia anidada
        for (DynamicField nestedField : nestedInstance.getDynamicClass().getFields()) {
            JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
            fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

            JLabel fieldLabel = new JLabel(nestedField.getName() + " (" + nestedField.getType() + "):");
            fieldLabel.setPreferredSize(new Dimension(150, 25));
            fieldPanel.add(fieldLabel, BorderLayout.WEST);

            if (nestedField.getType().equals("boolean")) {
                JComboBox<String> boolCombo = new JComboBox<>(new String[]{"true", "false"});
                boolCombo.setName(nestedField.getName());
                Object currentValue = nestedInstance.getFieldValue(nestedField.getName());
                if (currentValue != null) {
                    boolCombo.setSelectedItem(currentValue.toString());
                }
                fieldPanel.add(boolCombo, BorderLayout.CENTER);
            } else {
                JTextField valueField = new JTextField();
                valueField.setName(nestedField.getName());
                Object currentValue = nestedInstance.getFieldValue(nestedField.getName());
                if (currentValue != null) {
                    valueField.setText(currentValue.toString());
                }
                fieldPanel.add(valueField, BorderLayout.CENTER);
            }

            contentPanel.add(fieldPanel);
        }

        JButton saveButton = new JButton("Guardar Cambios");
        saveButton.addActionListener(e -> {
            // Guardar los valores modificados
            for (Component comp : contentPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    for (Component fieldComp : panel.getComponents()) {
                        if (fieldComp instanceof JTextField) {
                            JTextField textField = (JTextField) fieldComp;
                            String fieldName = textField.getName();
                            String value = textField.getText();
                            nestedInstance.setFieldValue(fieldName, value);
                        } else if (fieldComp instanceof JComboBox) {
                            @SuppressWarnings("unchecked")
                            JComboBox<String> combo = (JComboBox<String>) fieldComp;
                            String fieldName = combo.getName();
                            String value = (String) combo.getSelectedItem();
                            nestedInstance.setFieldValue(fieldName, value);
                        }
                    }
                }
            }
            updateInstancesTable();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Instancia '" + instanceName + "' actualizada exitosamente");
        });

        dialog.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        dialog.add(saveButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void createInstance() {
        String className = (String) instanceComboBox.getSelectedItem();
        String instanceName = instanceNameField.getText().trim();

        if (className == null || className.equals("Seleccionar clase...")) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase");
            return;
        }

        if (instanceName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre para la instancia");
            return;
        }

        if (objectInstances.containsKey(instanceName)) {
            JOptionPane.showMessageDialog(this, "Ya existe una instancia con ese nombre");
            return;
        }

        try {
            DynamicClass dynClass = definedClasses.get(className);
            DynamicObject instance = new DynamicObject(dynClass, objectInstances);

            // INICIALIZAR ATRIBUTOS con los valores de los campos de entrada
            setInstanceFieldValues(instance);

            objectInstances.put(instanceName, instance);
            updateInstancesTable();

            // Mostrar mensaje de éxito con detalles de inicialización
            StringBuilder successMessage = new StringBuilder();
            successMessage.append("✅ INSTANCIA CREADA EXITOSAMENTE\n\n");
            successMessage.append("Nombre: ").append(instanceName).append("\n");
            successMessage.append("Clase: ").append(className).append("\n");
            successMessage.append("Atributos inicializados:\n");

            for (DynamicField field : dynClass.getFields()) {
                Object value = instance.getFieldValue(field.getName());
                successMessage.append("• ").append(field.getName()).append(" = ");
                if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    successMessage.append("[size=").append(list.size()).append("]");
                } else if (value instanceof DynamicObject) {
                    successMessage.append("[Objeto anidado]");
                } else {
                    successMessage.append(value);
                }
                successMessage.append("\n");
            }

            JOptionPane.showMessageDialog(this, successMessage.toString(),
                    "Instancia Creada", JOptionPane.INFORMATION_MESSAGE);

            // Limpiar y actualizar
            instanceNameField.setText("");
            updateValueInputs(); // Actualizar combos de objetos

            // Serializar automáticamente la nueva instancia
            serializeInstance(instanceName, instance);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error creando instancia: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void modifyInstance() {
        int selectedRow = instancesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una instancia para modificar");
            return;
        }

        String instanceName = (String) instancesModel.getValueAt(selectedRow, 0);
        DynamicObject instance = (DynamicObject) objectInstances.get(instanceName);

        try {
            // Actualizar valores desde los campos de entrada
            setInstanceFieldValues(instance);
            updateInstancesTable();

            JOptionPane.showMessageDialog(this, "Instancia '" + instanceName + "' modificada exitosamente");

            // Serializar automáticamente después de modificar
            serializeInstance(instanceName, instance);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error modificando instancia: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setInstanceFieldValues(DynamicObject instance) {
        for (Component comp : valueInputPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel fieldPanel = (JPanel) comp;
                for (Component fieldComp : fieldPanel.getComponents()) {
                    if (fieldComp instanceof JTextField) {
                        JTextField textField = (JTextField) fieldComp;
                        String fieldName = textField.getName();
                        String value = textField.getText();
                        instance.setFieldValue(fieldName, value);
                    } else if (fieldComp instanceof JComboBox && !(fieldComp instanceof JComboBox && fieldComp.getParent().getComponents().length > 1 && fieldComp.getParent().getComponent(1) instanceof JButton)) {
                        // Solo procesar combos que no son de campos personalizados (esos se manejan separadamente)
                        @SuppressWarnings("unchecked")
                        JComboBox<String> combo = (JComboBox<String>) fieldComp;
                        String fieldName = combo.getName();
                        String value = (String) combo.getSelectedItem();
                        instance.setFieldValue(fieldName, value);
                    } else if (fieldComp instanceof JPanel) {
                        // Para campos personalizados, buscar el combo dentro del panel
                        JPanel customPanel = (JPanel) fieldComp;
                        for (Component customComp : customPanel.getComponents()) {
                            if (customComp instanceof JComboBox) {
                                @SuppressWarnings("unchecked")
                                JComboBox<String> customCombo = (JComboBox<String>) customComp;
                                String fieldName = customCombo.getName();
                                String selectedValue = (String) customCombo.getSelectedItem();

                                if ("<null>".equals(selectedValue)) {
                                    instance.setCustomFieldValue(fieldName, null);
                                } else if (!"<nueva instancia>".equals(selectedValue)) {
                                    // Es una instancia existente
                                    DynamicObject nestedInstance = (DynamicObject) objectInstances.get(selectedValue);
                                    instance.setCustomFieldValue(fieldName, nestedInstance);
                                }
                                // Para "<nueva instancia>" no hacemos nada aquí, ya se maneja en configureCustomField
                            }
                        }
                    }
                }
            }
        }
    }

    private void loadSelectedInstance() {
        int selectedRow = instancesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        String instanceName = (String) instancesModel.getValueAt(selectedRow, 0);
        instanceNameField.setText(instanceName);

        DynamicObject instance = (DynamicObject) objectInstances.get(instanceName);
        if (instance != null) {
            // Actualizar los valores en los campos de entrada
            updateValueInputsWithInstance(instance);
        }
    }

    private void updateValueInputsWithInstance(DynamicObject instance) {
        for (Component comp : valueInputPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel fieldPanel = (JPanel) comp;
                for (Component fieldComp : fieldPanel.getComponents()) {
                    if (fieldComp instanceof JTextField) {
                        JTextField textField = (JTextField) fieldComp;
                        String fieldName = textField.getName();
                        Object value = instance.getFieldValue(fieldName);
                        if (value != null) {
                            textField.setText(value.toString());
                        }
                    } else if (fieldComp instanceof JComboBox && !(fieldComp instanceof JComboBox && fieldComp.getParent().getComponents().length > 1 && fieldComp.getParent().getComponent(1) instanceof JButton)) {
                        @SuppressWarnings("unchecked")
                        JComboBox<String> combo = (JComboBox<String>) fieldComp;
                        String fieldName = combo.getName();
                        Object value = instance.getFieldValue(fieldName);
                        if (value != null) {
                            combo.setSelectedItem(value.toString());
                        }
                    } else if (fieldComp instanceof JPanel) {
                        // Para campos personalizados
                        JPanel customPanel = (JPanel) fieldComp;
                        for (Component customComp : customPanel.getComponents()) {
                            if (customComp instanceof JComboBox) {
                                @SuppressWarnings("unchecked")
                                JComboBox<String> customCombo = (JComboBox<String>) customComp;
                                String fieldName = customCombo.getName();
                                Object value = instance.getFieldValue(fieldName);

                                if (value == null) {
                                    customCombo.setSelectedItem("<null>");
                                } else if (value instanceof DynamicObject) {
                                    String refName = findInstanceName((DynamicObject) value);
                                    if (refName != null) {
                                        customCombo.setSelectedItem(refName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String findInstanceName(DynamicObject target) {
        for (Map.Entry<String, Object> entry : objectInstances.entrySet()) {
            if (entry.getValue() == target) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void deleteInstance() {
        int selectedRow = instancesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una instancia para eliminar");
            return;
        }

        String instanceName = (String) instancesModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la instancia '" + instanceName + "'?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            objectInstances.remove(instanceName);
            updateInstancesTable();
            updateValueInputs(); // Actualizar combos de objetos
            instanceNameField.setText("");
            jsonOutputArea.setText(""); // Limpiar JSON
            JOptionPane.showMessageDialog(this, "Instancia '" + instanceName + "' eliminada");
        }
    }

    private void serializeSelectedInstance() {
        int selectedRow = instancesTable.getSelectedRow();
        if (selectedRow == -1) {
            return; // No mostrar error, simplemente no hacer nada
        }

        String instanceName = (String) instancesModel.getValueAt(selectedRow, 0);
        Object instance = objectInstances.get(instanceName);

        serializeInstance(instanceName, instance);
    }

    private void serializeInstance(String instanceName, Object instance) {
        try {
            String json = JsonSerializer.serialize(instance);
            jsonOutputArea.setText(json);

            // Mostrar mensaje en la barra de estado
            setTitle("Object Serializer GUI - Mini Framework - Serializado: " + instanceName);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error serializando '" + instanceName + "': " + e.getMessage(),
                    "Error de Serialización", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateInstancesTable() {
        instancesModel.setRowCount(0);
        for (Map.Entry<String, Object> entry : objectInstances.entrySet()) {
            String instanceName = entry.getKey();
            DynamicObject obj = (DynamicObject) entry.getValue();
            instancesModel.addRow(new Object[]{
                    instanceName,
                    obj.getClassName(),
                    obj.getFieldValuesString()
            });
        }
   }
}