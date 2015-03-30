package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import MindChart.ChartManager;
import MindChart.SynchronizedChart;
import MindChart.ZoomOutAdapter;
import MindReader.BinaryIO;
import info.monitorenter.gui.chart.views.ChartPanel;

public class BaseUI {

    private JButton btnReset;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel chartPanel;
    private JTabbedPane sidebar;
    private JTextField textField;
    private JTextField startPoint;
    private JTextField endPoint;
    private ChartManager cm;

    public BaseUI() {
        initializeFrame();
        cm = new ChartManager("data/PA_1.mw");
        try {
            initializeCharts(10);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void initializeFrame() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setResizable(true);
        // frame is divided into two main pieces:
        // the sidebar, and the main panel

        initializeSidebar();

        // Main panel area
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.getContentPane().add(mainPanel);

        // insert graphs for separate view here
        chartPanel = new JPanel();
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(0, 40));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(0, 40));
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        /*
         * useless for now // panel displaying collapse view final JPanel
         * collapsePanel = new JPanel();d // insert graphs for collapse view
         * here collapsePanel.setBackground(Color.WHITE);
         * collapsePanel.setBounds(248, 46, 536, 612);
         * frame.getContentPane().add(collapsePanel);
         * collapsePanel.setLayout(null);
         * 
         * // button toggle separate view JButton btnSeperate = new
         * JButton("Seperate"); btnSeperate.setBounds(586, 13, 94, 23);
         * btnSeperate.addActionListener(new ActionListener() { public void
         * actionPerformed(ActionEvent arg0) { collapsePanel.setVisible(false);
         * chartPanel.setVisible(true); } });
         * frame.getContentPane().add(btnSeperate);
         * 
         * // button toggle separate view JButton btnCollapse = new
         * JButton("Collapse"); btnCollapse.addActionListener(new
         * ActionListener() { public void actionPerformed(ActionEvent arg0) {
         * collapsePanel.setVisible(true); chartPanel.setVisible(false); } });
         * btnCollapse.setBounds(690, 13, 94, 23);
         * frame.getContentPane().add(btnCollapse);
         */

        JPanel buttonPanel = new JPanel();
        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        // button to reset zoom percentage
        btnReset = new JButton("Reset");
        btnReset.setPreferredSize(new Dimension(73, 21));
        buttonPanel.add(btnReset);

        JLabel lblStartPoint = new JLabel("Start:");
        lblStartPoint.setPreferredSize(new Dimension(49, 14));
        buttonPanel.add(lblStartPoint);

        // text field for input starting point of zooming
        startPoint = new JTextField();
        startPoint.setPreferredSize(new Dimension(40, 20));
        buttonPanel.add(startPoint);
        startPoint.setColumns(10);

        JLabel lblEndPoint = new JLabel("End:");
        lblEndPoint.setPreferredSize(new Dimension(42, 14));
        buttonPanel.add(lblEndPoint);

        // text field for input ending point of zooming
        endPoint = new JTextField();
        endPoint.setColumns(10);
        endPoint.setPreferredSize(new Dimension(40, 20));
        buttonPanel.add(endPoint);

        // button to zoom according to the entered points
        JButton btnZoom = new JButton("zoom");
        btnZoom.setPreferredSize(new Dimension(89, 21));
        buttonPanel.add(btnZoom);
        btnZoom.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int start = 0;
                int end = 0;
                try {
                   start = Integer.parseInt(startPoint.getText());
                   end = Integer.parseInt(endPoint.getText());

                } catch (NumberFormatException oops) {
                    JOptionPane.showMessageDialog(null, "Inputted bounds must be numbers!");
                    return;
                }
                
                cm.setRange(start, end);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        });
    }

    public void initializeSidebar() {
        // Tabbed panel
        sidebar = new JTabbedPane(JTabbedPane.TOP);
        sidebar.setPreferredSize(new Dimension(236, frame.getHeight()));
        frame.getContentPane().add(sidebar, BorderLayout.WEST);
        // tab of file viewer
        JPanel fileViewer = new JPanel();
        sidebar.addTab("File", null, fileViewer, null);
        fileViewer.setLayout(null);

        JLabel lblBinaryFiles = new JLabel("Binary Files");
        lblBinaryFiles.setBounds(10, 11, 64, 14);
        fileViewer.add(lblBinaryFiles);

        // Panel for binary file explorer
        JPanel explorerBinary = new JPanel();
        // insert Binary File explorer here
        explorerBinary.setBackground(Color.WHITE);
        explorerBinary.setBounds(10, 28, 211, 174);
        fileViewer.add(explorerBinary);
        JLabel lblIbiFiles = new JLabel("IBI Files");
        lblIbiFiles.setBounds(10, 213, 46, 14);
        fileViewer.add(lblIbiFiles);

        // Panel for IBI file explorer
        JPanel explorerIBI = new JPanel();
        // insert IBI File explorer here
        explorerIBI.setBackground(Color.WHITE);
        explorerIBI.setBounds(10, 238, 211, 180);
        fileViewer.add(explorerIBI);

        JLabel lblEvenFiles = new JLabel("Event Files");
        lblEvenFiles.setBounds(10, 429, 96, 14);
        fileViewer.add(lblEvenFiles);

        // Panel for event file explorer
        JPanel explorerEvent = new JPanel();
        // insert Event File explorer here
        explorerEvent.setBackground(Color.WHITE);
        explorerEvent.setBounds(10, 454, 211, 180);
        fileViewer.add(explorerEvent);

        // tab for Channels viewer
        JPanel channels = new JPanel();
        // insert channels viewer here
        sidebar.addTab("Channels", null, channels, null);

        // tab for video viewer
        JPanel video = new JPanel();
        // insert video viewer here
        sidebar.addTab("Video", null, video, null);

    }

    public void initializeCharts(int freq) throws IOException {
        List<SynchronizedChart> charts = cm.generateCharts(freq);
        List<ChartPanel> cPanels = new ArrayList<ChartPanel>();
        for (SynchronizedChart chart : charts) {
            ChartPanel tmpPanel = new ChartPanel(chart);
            cPanels.add(tmpPanel);
        }
        chartPanel.setLayout(new GridLayout(charts.size(), 1));
        for (ChartPanel panel : cPanels) {
            chartPanel.add(panel);
        }

        // aaaand set the zoom listener
        this.setZoomListener(new ZoomOutAdapter(charts));
    }

    public void setZoomListener(ActionListener l) {
        btnReset.addActionListener(l);
    }

    public void display() {
        this.frame.setVisible(true);
    }
}
