import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Pierre on 24/06/2016.
 */
public class MainApp extends JFrame {
    private static final int REFRESH_TIME = 400;
    private static final String FILENAME = "db.ser";
    private static boolean BLOCK_RECOGNITION = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainApp();
            }
        });
    }

    public MainApp() {
        setTitle("BOBReader");

        loadCam();
        loadFiles();
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void loadFiles() {
        final JDialog frame = new JDialog();
        final Status window = new Status();
        frame.setContentPane(window.getContentPane());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // VISIBLE
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setSize(300,400);

        final JList files = window.getFiles();
        files.setCellRenderer(new FileStatus());

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    HashMap<String, HashMap<String, String>> allData = getAllData();
                    Set<String> strings = allData.keySet();


                    List<String> list = new ArrayList<>();

                    for (String fileName : strings) {
                        list.add(fileName + " FACE " + getFaceName(fileName) + " => " + allData.get(fileName).get("status"));
                    }

                    files.setListData(list.toArray());
                    files.repaint();

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {

                    }
                } while (true);
            }
        });
        t.start();

    }

    private void loadCam() {

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        getContentPane().add(panel);

        registerCamEvent(webcam, REFRESH_TIME);
    }

    private void registerCamEvent(final Webcam webcam, final int refresh) {

        final MainApp target = this;

        Thread thread = new Thread(new Runnable() {
            public void run() {

                do {
                    Result result = null;
                    BufferedImage image = null;

                    if (webcam.isOpen() && BLOCK_RECOGNITION == false) {
                        if ((image = webcam.getImage()) != null) {


                            LuminanceSource source = new BufferedImageLuminanceSource(image);
                            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                            try {
                                result = new MultiFormatReader().decode(bitmap);
                            } catch (NotFoundException e) {
                                // fall thru, it means there is no QR code in image
                                //System.out.println("Nothing");
                            }
                        } else {
                            System.out.println("null");
                        }
                    }

                    if (result != null) {
                        BLOCK_RECOGNITION = true;
                        System.out.println("Found:" + result.getText());
                        target.openModalFor(result.getText());
                        result=null;
                    }

                    // SLEEP TIME
                    try {
                        Thread.sleep(refresh);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                while (true);
            }
        });

        thread.start();
    }

    /**
     * Have to load some data in the modal
     * @param tapeQRCODE
     */
    private void openModalFor(final String tapeQRCODE) {

        // analyse and get data
        String faceName = getFaceName(tapeQRCODE);

        final HashMap<String, String> data = loadData(tapeQRCODE);

        // have to change the name


        final JDialog frame = new JDialog();
        final Window window = new Window();
        frame.setContentPane(window.getContentPane());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // SET DATA
        window.getTapeName().setText(tapeQRCODE);
        frame.setTitle("Tape " + tapeQRCODE + " Face " + faceName);

        // STATUS
        if(data.containsKey("status")){
            switch(data.get("status")){
                case "recording":
                    window.getRecording().setSelected(true);
                    break;
                case "done":
                    window.getDone().setSelected(true);
                    break;
                default:
                    window.getNoRecorded().setSelected(true);
                    break;
            }
        }

        // COMMENTS
        if(data.containsKey("comments")){
            window.getComments().setText(data.get("comments"));
        }


        // VISIBLE
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent evt) {
                BLOCK_RECOGNITION=false;
            }
        });

        // SAVE REGISTER
        window.getButtonOK().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // GET STATUS
                String status = "no";
                if(window.getDone().isSelected()){
                    status = "done";
                } else if(window.getRecording().isSelected()){
                    status = "recording";
                }

                String comments = window.getComments().getText();

                HashMap<String,String> data = new HashMap<String, String>();
                data.put("status",status);
                data.put("comments",comments);

                saveData(tapeQRCODE,data);
                frame.dispose();
                BLOCK_RECOGNITION=false;

            }
        });

        window.getButtonCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
    }

    private String getFaceName(String text) {
        String[] split = text.split("_");
        if(split.length != 2){
            return "A";
        }
        else if(split[1].equals("B")){
            return "B";
        }

        return "Unk";
    }


    private HashMap<String,String> loadData(String name){

        HashMap<String, HashMap<String, String>> map = getAllData();
        if(map != null && map.containsKey(name)){
            return map.get(name);
        } else {
            return new HashMap<>();
        }
    }

    private void saveData(String name, HashMap<String,String> data){
        HashMap<String, HashMap<String, String>> map = getAllData();
        if(map==null){
            map = new HashMap<>();
        }

        map.put(name,data);

        // WRITE TO FILE
        //serialize the List
        try (
                OutputStream file = new FileOutputStream(FILENAME);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer);
        ){
            output.writeObject(map);
        }
        catch(IOException ex){
            System.out.println("Can't write...");
        }

    }

    private HashMap<String, HashMap<String, String>> getAllData(){

        HashMap<String,HashMap<String,String>> map = null;
        try
        {
            FileInputStream fis = new FileInputStream(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe)
        {
            System.out.println("File doesn't exist");
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
        }

        return map;
    }
}
