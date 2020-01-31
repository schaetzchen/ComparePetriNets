package GUI;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

public class DisabledEditingJTable extends DefaultTableModel {

    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
