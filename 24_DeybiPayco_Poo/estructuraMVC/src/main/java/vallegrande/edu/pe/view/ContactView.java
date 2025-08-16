package vallegrande.edu.pe.view;

import vallegrande.edu.pe.controller.ContactController;
import vallegrande.edu.pe.model.Contact;
import vallegrande.edu.pe.util.NotificationManager; // Nueva clase para notificaciones

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Vista principal con Swing que muestra la lista de contactos y botones.
 * Ventana maximizada, botones con estilo moderno y colores.
 */
public class ContactView extends JFrame {
    private final ContactController controller;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField filterField; // Campo de filtro para la tabla

    public ContactView(ContactController controller) {
        super("Agenda MVC Swing - Vallegrande");
        this.controller = controller;
        initUI();
        loadContacts();
        showWelcomeMessage(); // Muestra el mensaje de bienvenida al iniciar
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Paleta de colores consistente
        Color primaryColor = new Color(52, 152, 219); // Azul suave
        Color secondaryColor = new Color(46, 204, 113); // Verde esmeralda
        Color accentColor = new Color(231, 76, 60); // Rojo coral
        Color backgroundColor = new Color(240, 243, 245); // Gris claro
        Color panelColor = Color.WHITE;
        Color fontColor = new Color(50, 50, 50);

        // Fuente moderna y legible
        Font baseFont = new Font("Roboto", Font.PLAIN, 16);
        Font boldFont = baseFont.deriveFont(Font.BOLD);
        Font headerFont = baseFont.deriveFont(Font.BOLD, 18f);

        // Configuramos layout principal
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        setContentPane(contentPanel);

        // Panel superior para título y filtro
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // Transparente para que se vea el fondo
        JLabel titleLabel = new JLabel("Mis Contactos");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 36));
        titleLabel.setForeground(primaryColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        filterField = new JTextField("Buscar contacto...");
        filterField.setFont(baseFont);
        filterField.setForeground(new Color(150, 150, 150));
        filterField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(7, 7, 7), 1, true),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        filterField.putClientProperty("JComponent.roundedCorners", true);

        // Listener para el campo de filtro
        filterField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });

        filterField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (filterField.getText().equals("Buscar contacto...")) {
                    filterField.setText("");
                    filterField.setForeground(fontColor);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (filterField.getText().isEmpty()) {
                    filterField.setForeground(new Color(7, 7, 7));
                    filterField.setText("Buscar contacto...");
                }
            }
        });

        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setOpaque(false);
        filterPanel.add(filterField, BorderLayout.CENTER);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        headerPanel.add(filterPanel, BorderLayout.SOUTH);

        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel de la tabla con estilos
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(panelColor);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Teléfono"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(baseFont);
        table.setForeground(fontColor);
        table.setRowHeight(40); // Filas más altas
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(primaryColor.brighter());
        table.setSelectionForeground(Color.WHITE);

        // Colores alternos


        // Cabecera de la tabla
        table.getTableHeader().setFont(headerFont);
        table.getTableHeader().setForeground(fontColor);
        table.getTableHeader().setBackground(new Color(235, 235, 235));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        // Ordenamiento de columnas
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tablePanel, BorderLayout.CENTER);

        // Panel para botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setOpaque(false);

        JButton addBtn = createStyledButton("Agregar Contacto", secondaryColor, "add_icon.png");
        JButton deleteBtn = createStyledButton("Eliminar Contacto", accentColor, "delete_icon.png");

        buttonsPanel.add(addBtn);
        buttonsPanel.add(deleteBtn);

        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            showAddContactDialog();
            NotificationManager.showToast(this, "Contacto agregado con éxito!");
        });
        deleteBtn.addActionListener(e -> {
            deleteSelectedContact();
            NotificationManager.showToast(this, "Contacto eliminado con éxito!");
        });
    }

    private void showWelcomeMessage() {
        // Mensaje de bienvenida atractivo y moderno
        JDialog welcomeDialog = new JDialog(this, "Bienvenido", true);
        welcomeDialog.setLayout(new BorderLayout());
        welcomeDialog.setSize(400, 250);
        welcomeDialog.setLocationRelativeTo(this);
        welcomeDialog.setUndecorated(true);
        welcomeDialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 3));

        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel iconLabel = new JLabel(new ImageIcon(new ImageIcon("welcome_icon.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH)));
        JLabel welcomeLabel = new JLabel("¡Bienvenido a tu Agenda!");
        JLabel descriptionLabel = new JLabel("<html><div style='text-align: center;'>Gestione sus contactos de forma fácil y rápida.<br>¡Estamos listos para empezar!</div></html>");

        welcomeLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(52, 152, 219));
        descriptionLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(100, 100, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        welcomePanel.add(iconLabel, gbc);

        gbc.gridy++;
        welcomePanel.add(welcomeLabel, gbc);

        gbc.gridy++;
        welcomePanel.add(descriptionLabel, gbc);

        JButton closeBtn = createStyledButton("Entendido", new Color(46, 204, 113), "");
        closeBtn.addActionListener(e -> welcomeDialog.dispose());
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        welcomePanel.add(closeBtn, gbc);

        welcomeDialog.add(welcomePanel, BorderLayout.CENTER);
        welcomeDialog.setVisible(true);
    }

    /**
     * Crea un botón con estilos modernos, bordes redondeados e ícono.
     */
    private JButton createStyledButton(String text, Color baseColor, String iconPath) {
        JButton button = new JButton(text);
        if (!iconPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
                button.setIcon(icon);
            } catch (Exception e) {
                // Manejar error si el ícono no se encuentra
            }
        }
        button.setFont(new Font("Roboto", Font.BOLD, 16));
        button.setForeground(Color.BLACK);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(baseColor, 2, true)); // Borde redondeado
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(false); // Necesario para que el borde redondeado se vea bien

        // Listener para el hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }

    private void loadContacts() {
        tableModel.setRowCount(0);
        List<Contact> contacts = controller.list();
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{c.id(), c.name(), c.email(), c.phone()});
        }
    }

    private void showAddContactDialog() {
        // Asumiendo que AddContactDialog se maneja de forma similar,
        // el código para manejar el resultado debería ser similar al original
        AddContactDialog dialog = new AddContactDialog(this, controller);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadContacts();
        }
    }

    private void deleteSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el ID de la fila seleccionada, considerando el ordenamiento
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String id = (String) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este contacto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadContacts();
        }
    }

    /**
     * Aplica el filtro a la tabla según el texto del campo de búsqueda.
     */
    private void filterTable() {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        String text = filterField.getText();
        if (text.isEmpty() || text.equals("Buscar contacto...")) {
            sorter.setRowFilter(null);
        } else {
            // Filtro insensible a mayúsculas y minúsculas
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
}