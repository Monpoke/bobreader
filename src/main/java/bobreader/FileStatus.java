package bobreader;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Pierre on 24/06/2016.
 */
public class FileStatus extends JLabel implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String s = value.toString();

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(Color.lightGray);
            setText(s+"  "+index);
        }else{
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            setText(s);
        }

        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
