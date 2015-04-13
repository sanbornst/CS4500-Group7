package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import MindChart.ChartManager;
import MindChart.SynchronizedChart;
import MindChart.ZoomOutAdapter;
import info.monitorenter.gui.chart.views.ChartPanel;

/**
 * Class to represent the UI
 * 
 * @author jordanreedie
 * 
 */
public class BaseUI {

    private JButton btnReset;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel chartPanel;
    private JPanel overlayPanel;
    private JTabbedPane sidebar;
    private JTextField startPoint;
    private JTextField endPoint;
    private ChartManager cm;
    private JScrollPane scrollPanel;
    private boolean isOverlay;

    public BaseUI() {
        initializeFrame();
        cm = new ChartManager("data/PA_1.mw", "data/PA_1_event.txt", "data/PP01_ECG_Actiwave_PA_HRV_IBI_3_13 PM.txt");
        try {
            initializeCharts();
            initializeOverlay();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Creates the frame and adds some stuff to it
     */
    private void initializeFrame() {

        frame = new JFrame();
        frame.setBounds(100, 100, 800, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setResizable(true);

        // frame is divided into two main pieces:
        // the sidebar, and the main panel
        initializeSidebar();

        // Main panel area
        mainPanel = new JPanel(new BorderLayout());
        frame.getContentPane().add(mainPanel);

        // insert graphs for separate view here
        chartPanel = new JPanel();
        overlayPanel = new JPanel();
        scrollPanel = new JScrollPane();
        scrollPanel.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPanel.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.setVisible(true);
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(900, 60));
        // holds the bottom buttons (zoom, zoom out, etc)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(0, 40));
        mainPanel.add(scrollPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        topPanel.setLayout(new FlowLayout());
        overlayPanel.setVisible(false);
        
        // button toggle separate view
        JButton btnSeparate = new JButton("Switch View");
        btnSeparate.setPreferredSize(new Dimension(120, 30));
        btnSeparate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                // swap 'em
                swapChartPanels();
            }
        });

        topPanel.add(btnSeparate);
        // button toggle separate view
        JPanel buttonPanel = new JPanel();
        bottomPanel.add(buttonPanel, BorderLayout.WEST);

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
        
        // button to reset zoom percentage
        btnReset = new JButton("Reset");
        btnReset.setPreferredSize(new Dimension(73, 21));
        buttonPanel.add(btnReset);
        
        btnZoom.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                double start = 0;
                double end = 0;
                try {
                    start = Double.parseDouble(startPoint.getText());
                    end = Double.parseDouble(endPoint.getText());

                } catch (NumberFormatException oops) {
                    JOptionPane.showMessageDialog(null,
                            "Inputted bounds must be numbers!");
                    return;
                }

                if (start >= end) {
                    JOptionPane.showMessageDialog(null,
                            "End value must be larger than start value!");
                } else {
                    try {
                        cm.setRange(start, end);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
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
        fileViewer.setLayout(new BorderLayout());

        // add the file selector to the tabbed pane
        JLabel lblBinaryFiles = new JLabel("Binary Files");
        lblBinaryFiles.setBounds(10, 11, 64, 14);
        fileViewer.add(lblBinaryFiles, BorderLayout.NORTH);
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    System.out.println("File selected: "
                            + fileChooser.getSelectedFile());
                    mwFileSelected(fileChooser.getSelectedFile()
                            .getAbsolutePath());
                }
            }
        });
        fileViewer.add(fileChooser, BorderLayout.CENTER);
        // tab for Channels viewer
        JPanel channels = new JPanel();
        // insert channels viewer here
        sidebar.addTab("Channels", null, channels, null);
        channels.setLayout(null);
        addChannel(channels, 0);
        addChannel(channels, 1);

        // tab for video viewer
        JPanel video = new JPanel();
        // insert video viewer here
        sidebar.addTab("Video", null, video, null);

    }
    
    /**
     *  Swaps the chart & overlay panels
     */
    private void swapChartPanels() {

        if (scrollPanel.isVisible()) {
            mainPanel.remove(scrollPanel);
            scrollPanel.setVisible(false);
            overlayPanel.setVisible(true);
            mainPanel.add(overlayPanel, BorderLayout.CENTER);
        } else {
            mainPanel.remove(overlayPanel);
            overlayPanel.setVisible(false);
            scrollPanel.setVisible(true);
            mainPanel.add(scrollPanel, BorderLayout.CENTER);
        }
        
        frame.revalidate();
        //revalidate and repaint
        mainPanel.revalidate();
        scrollPanel.revalidate();
        overlayPanel.revalidate();
        mainPanel.repaint();
        
    }

    /**
     * called when a user selects a mindware file, loads the file and updates the charts
     * 
     * @param filename
     */
    private void mwFileSelected(String filename) {

        try {
            cm.setMwPath(filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error opening file: "
                    + filename);
            e.printStackTrace();
        }
        updateCharts();
    }

    /**
     * Remove all points from chart, re-pull data, re-display charts
     */
    private void updateCharts() {

        scrollPanel.removeAll();
        chartPanel.removeAll();
        overlayPanel.removeAll();
        try {
            this.initializeCharts();
            this.initializeOverlay();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // required for redraw
        scrollPanel.revalidate();
        chartPanel.revalidate();
        overlayPanel.revalidate();
    }

    /**
     * Creates charts & adds them to the panel
     * 
     * @throws IOException
     */
    private void initializeCharts() throws IOException {

        // let's make some charts
        List<SynchronizedChart> charts = cm.generateCharts();

        // now add those charts to panels
        List<ChartPanel> cPanels = new ArrayList<ChartPanel>();
        for (SynchronizedChart chart : charts) {
            ChartPanel tmpPanel = new ChartPanel(chart);
            tmpPanel.setPreferredSize(new Dimension(200, ChartManager.MINIMUM_CHART_HEIGHT));
            cPanels.add(tmpPanel);
        }

        chartPanel.setLayout(new GridLayout(charts.size(), 1));
        // and add those chart panels to the main chart container
        for (ChartPanel panel : cPanels) {
            chartPanel.add(panel);
        }

        // be very very quiet... we're hunting zooms
        this.setZoomListener(new ZoomOutAdapter(cm));
        scrollPanel.setViewportView(chartPanel);
        scrollPanel.revalidate();
    }
    
    private void initializeOverlay() {
       SynchronizedChart overlaidChart = cm.generateOverlay();
       ChartPanel cPanel = new ChartPanel(overlaidChart);
       cPanel.setPreferredSize(new Dimension(500, 500));
       overlayPanel.setLayout(new GridLayout(1, 1));
       overlayPanel.add(cPanel);
    }
    
    // method to add channels to channelView tab
    // maybe need more variables from reading channels...
    private void addChannel(JPanel panel, int nth){
        
        JLabel lblChennalPreview = new JLabel("Channel 1");
        lblChennalPreview.setBounds(10, 7 + (nth * 105), 61, 18);
        panel.add(lblChennalPreview);
        
        JCheckBox chckbxShow = new JCheckBox("Show");
        chckbxShow.setBounds(165, 7 + (nth * 105), 61, 18);
        // add listener here to toggle
        panel.add(chckbxShow);
        
        // drop down menu for color selection
        JComboBox colorSelector = new JComboBox();
        colorSelector.setBounds(10, 76 + (nth * 105), 216, 20);
        colorSelector.addItem("red");
        colorSelector.addItem("green");
        colorSelector.addItem("blue");
        colorSelector.addItem("cyan");
        colorSelector.addItem("yellow");
        colorSelector.addItem("magenta");
        colorSelector.addItem("black");
        colorSelector.addActionListener(new ActionListener() {
            // add action here to change color
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        panel.add(colorSelector);
        
        // panel for preview of channel
        JPanel chennelPreview = new JPanel();
        chennelPreview.setBackground(Color.WHITE);
        chennelPreview.setBounds(10, 30 + (nth * 105), 216, 42);
        panel.add(chennelPreview);
        
    }

    /**
     * Sets the zoom listener for the charts
     * 
     * @param l
     *            The provided zoom listener
     */
    public void setZoomListener(ActionListener l) {
        btnReset.addActionListener(l);
    }

    public void display() {
        this.frame.setVisible(true);
    }
}
