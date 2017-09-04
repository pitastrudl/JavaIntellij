import javax.swing.*;
import java.awt.*;

public class GuiPanel {

    public void showGUI(){
        JFrame window = new JFrame();

        JPanel panel = new JPanel();

        JPanel subPanel = new JPanel(new FlowLayout());

        JButton sButt = new JButton("Open Image");
        JButton seqButt = new JButton("Sequentally");
        JButton parButt = new JButton("Parallel");
        JButton distrButt = new JButton("Distributed");
        JButton exitButt = new JButton("Exit");

        startlistener listener = new startlistener(sButt, window);
        sButt.addActionListener(listener);
        startlistener listener2 = new startlistener(exitButt, window);
        exitButt.addActionListener(listener2);

        subPanel.add(sButt);
        subPanel.add(seqButt);
        subPanel.add(parButt);
        subPanel.add(distrButt);
        subPanel.add(exitButt);
        window.add(subPanel, BorderLayout.SOUTH);

        // window.setLayout(new BorderLayout());
        window.add(panel, BorderLayout.CENTER);

        ;
        window.setTitle("Konvulacija z jedrom");
		/*
		 * window.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		 */

        //window.setVisible(true);
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



    }
}
