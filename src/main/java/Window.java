import javax.swing.*;

/**
 * Created by Pierre on 24/06/2016.
 */
public class Window extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel buttons;
    private JLabel tapeName;
    private JRadioButton done;
    private JRadioButton recording;
    private JRadioButton noRecorded;
    private JTextArea comments;
    private JButton faceNameButton;


    public Window(){

    }


    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    public void setContentPane(JPanel contentPane) {
        this.contentPane = contentPane;
    }

    public JButton getButtonOK() {
        return buttonOK;
    }

    public void setButtonOK(JButton buttonOK) {
        this.buttonOK = buttonOK;
    }

    public JButton getButtonCancel() {
        return buttonCancel;
    }

    public void setButtonCancel(JButton buttonCancel) {
        this.buttonCancel = buttonCancel;
    }

    public JPanel getButtons() {
        return buttons;
    }

    public void setButtons(JPanel buttons) {
        this.buttons = buttons;
    }

    public JLabel getTapeName() {
        return tapeName;
    }

    public void setTapeName(JLabel tapeName) {
        this.tapeName = tapeName;
    }

    public JRadioButton getDone() {
        return done;
    }

    public void setDone(JRadioButton done) {
        this.done = done;
    }

    public JRadioButton getRecording() {
        return recording;
    }

    public void setRecording(JRadioButton recording) {
        this.recording = recording;
    }

    public JRadioButton getNoRecorded() {
        return noRecorded;
    }

    public void setNoRecorded(JRadioButton noRecorded) {
        this.noRecorded = noRecorded;
    }

    public JTextArea getComments() {
        return comments;
    }

    public void setComments(JTextArea comments) {
        this.comments = comments;
    }

    public JButton getFaceNameButton() {
        return faceNameButton;
    }

    public void setFaceNameButton(JButton faceNameButton) {
        this.faceNameButton = faceNameButton;
    }
}
