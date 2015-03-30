import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Color;
import javax.swing.JTextField;


public class Main {

    private JFrame frame;
    private JTextField textField;
    private JTextField zoomPercentage;
    private JTextField startPoint;
    private JTextField endPoint;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Main() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setBounds(100, 100, 800, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        
        // Tabbed panel
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(2, 11, 236, 669);
        frame.getContentPane().add(tabbedPane);
        
        // tab of file viewer
        JPanel file = new JPanel();
        tabbedPane.addTab("File", null, file, null);
        file.setLayout(null);
        
        JLabel lblBinaryFiles = new JLabel("Binary Files");
        lblBinaryFiles.setBounds(10, 11, 64, 14);
        file.add(lblBinaryFiles);
        
        // Panel for binary file explorer
        JPanel explorerBinary = new JPanel();
        // insert Binary File explorer here
        explorerBinary.setBackground(Color.WHITE);
        explorerBinary.setBounds(10, 28, 211, 174);
        file.add(explorerBinary);
        
        JLabel lblIbiFiles = new JLabel("IBI Files");
        lblIbiFiles.setBounds(10, 213, 46, 14);
        file.add(lblIbiFiles);
        
        // Panel for IBI file explorer
        JPanel explorerIBI = new JPanel();
        // insert IBI File explorer here
        explorerIBI.setBackground(Color.WHITE);
        explorerIBI.setBounds(10, 238, 211, 180);
        file.add(explorerIBI);
        
        JLabel lblEvenFiles = new JLabel("Event Files");
        lblEvenFiles.setBounds(10, 429, 96, 14);
        file.add(lblEvenFiles);
        
        // Panel for event file explorer
        JPanel explorerEvent = new JPanel();
        // insert Event File explorer here
        explorerEvent.setBackground(Color.WHITE);
        explorerEvent.setBounds(10, 454, 211, 180);
        file.add(explorerEvent);
        
        //tab for Channels viewer
        JPanel channels = new JPanel();
        // insert channels viewer here
        tabbedPane.addTab("Channels", null, channels, null);
        
        // tab for video viewer
        JPanel video = new JPanel();
        // insert video viewer here
        tabbedPane.addTab("Video", null, video, null);
        
        
        // Main panel area
        
        // panel displaying separate view
        JPanel seperatePanel = new JPanel();
        // insert graphs for separate view here
        seperatePanel.setBounds(248, 46, 536, 612);
        frame.getContentPane().add(seperatePanel);
        seperatePanel.setLayout(null);
        seperatePanel.setBackground(Color.WHITE);
        
        JLabel lblThePanelShowing = new JLabel("the panel showing seperate channels");
        lblThePanelShowing.setBounds(0, 0, 467, 14);
        seperatePanel.add(lblThePanelShowing);
        
        // panel displaying collapse view
        JPanel collapsePanel = new JPanel();
        // insert graphs for collapse view here
        collapsePanel.setBackground(Color.WHITE);
        collapsePanel.setBounds(248, 46, 536, 612);
        frame.getContentPane().add(collapsePanel);
        collapsePanel.setLayout(null);
        
        JLabel lbl1 = new JLabel("the panel showing overlaying channels");
        lbl1.setBounds(0, 0, 456, 14);
        collapsePanel.add(lbl1);
        
        // button toggle separate view
        JButton btnSeperate = new JButton("Seperate");
        btnSeperate.setBounds(586, 13, 94, 23);
        btnSeperate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                collapsePanel.setVisible(false);
                seperatePanel.setVisible(true);
            }
        });
        frame.getContentPane().add(btnSeperate);
        
        // button toggle separate view
        JButton btnCollapse = new JButton("Collapse");
        btnCollapse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                collapsePanel.setVisible(true);
                seperatePanel.setVisible(false);
            }
        });
        btnCollapse.setBounds(690, 13, 94, 23);
        frame.getContentPane().add(btnCollapse);
        
        // panel for zoom Controls
        JPanel zoomControl = new JPanel();
        zoomControl.setBounds(248, 668, 536, 23);
        frame.getContentPane().add(zoomControl);
        zoomControl.setLayout(null);
        
        // text field displaying zoomed percentage
        zoomPercentage = new JTextField();
        zoomPercentage.setEditable(false);
        zoomPercentage.setText("100%");
        zoomPercentage.setBounds(0, -1, 42, 20);
        zoomControl.add(zoomPercentage);
        zoomPercentage.setColumns(10);
        
        // button to reset zoom percentage
        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(50, -1, 73, 21);
        zoomControl.add(btnReset);
        
        JLabel lblStartPoint = new JLabel("Start:");
        lblStartPoint.setBounds(133, 2, 49, 14);
        zoomControl.add(lblStartPoint);
        
        // text field for input starting point of zooming
        startPoint = new JTextField();
        startPoint.setBounds(179, -1, 67, 20);
        zoomControl.add(startPoint);
        startPoint.setColumns(10);
        
        JLabel lblEndPoint = new JLabel("End:");
        lblEndPoint.setBounds(268, 2, 42, 14);
        zoomControl.add(lblEndPoint);
        
        // text field for input ending point of zooming
        endPoint = new JTextField();
        endPoint.setColumns(10);
        endPoint.setBounds(311, -1, 67, 20);
        zoomControl.add(endPoint);
        
        // button to zoom according to the entered points
        JButton btnZoom = new JButton("zoom");
        btnZoom.setBounds(390, -2, 89, 21);
        zoomControl.add(btnZoom);
        
       
        
        
    }
}
