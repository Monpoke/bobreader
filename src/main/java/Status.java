import javax.swing.*;

public class Status extends JDialog {
    private JPanel contentPane;
    private JList files;
    private JButton buttonOK;

    public Status() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
    }

    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    public void setContentPane(JPanel contentPane) {
        this.contentPane = contentPane;
    }

    public JList getFiles() {
        return files;
    }

    public void setFiles(JList files) {
        this.files = files;
    }

    public JButton getButtonOK() {
        return buttonOK;
    }

    public void setButtonOK(JButton buttonOK) {
        this.buttonOK = buttonOK;
    }
}
