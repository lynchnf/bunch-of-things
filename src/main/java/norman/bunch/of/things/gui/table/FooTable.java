package norman.bunch.of.things.gui.table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FooTable extends JPanel implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FooTable.class);
    private JButton addButton;
    private JButton removeButton;
    private JTable table;
    private JScrollPane scrollPane;

    public FooTable() {
        super(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setFloatable(false);
        addButton = new JButton("Add");
        toolBar.add(addButton);
        addButton.addActionListener(this);
        removeButton = new JButton("Remove");
        toolBar.add(removeButton);
        removeButton.addActionListener(this);

        table = new JTable();
        table.setAutoCreateRowSorter(true);
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        table.setFillsViewportHeight(true);
        scrollPane.setPreferredSize(table.getPreferredSize());
    }

    public void addTableModelListener(TableModelListener tableModelListener) {
        table.getModel().addTableModelListener(tableModelListener);
    }

    public void setColumnNames(String[] columnNames) {
        for (String columnName : columnNames) {
            TableColumn tableColumn = new TableColumn();
            tableColumn.setHeaderValue(columnName);
            table.getColumnModel().addColumn(tableColumn);
        }
    }

    public void setPreferredHeight(int preferredHeight) {
        scrollPane.setPreferredSize(new Dimension((int) table.getPreferredSize().getWidth(), preferredHeight));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(addButton)) {
            addRow();
        } else if (actionEvent.getSource().equals(removeButton)) {
            LOGGER.debug("Remove button pressed.");
        }
    }

    private void addRow() {
        LOGGER.debug("Add button pressed.");

        Container topLevelFrame = this;
        while (topLevelFrame.getParent() != null) {
            topLevelFrame = topLevelFrame.getParent();
        }

        String[] choices = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        Object choice = JOptionPane
                .showInputDialog(topLevelFrame, "Please select a skill", "Available Skills", JOptionPane.PLAIN_MESSAGE,
                        null, choices, null);
        LOGGER.debug("choice=\"" + choice + "\"");
    }
}
