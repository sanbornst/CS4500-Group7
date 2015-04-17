package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
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

    private final int CHANNELS_TAB = 1;

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

    private List<TogglePanel> togglePanels;

    public BaseUI() {
        initializeFrame();
        cm = new ChartManager();
        // for local testing:

        /*
         * cm = new ChartManager("data/PA_1.mw", "data/PA_1_event.txt",;
         * "data/PP01_ECG_Actiwave_PA_HRV_IBI_3_13 PM.txt"); try {
         * initializeCharts(); initializeOverlay(); } catch (IOException e) { //
         * TODO error handling System.out.println("oops"); }
         */

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
        scrollPanel
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPanel
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.setVisible(true);

        // hold the top buttons (for now, just switch view)
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(900, 60));

        // holds the bottom buttons (zoom, zoom out, etc)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(0, 40));

        mainPanel.add(scrollPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
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

        topPanel.setLayout(new FlowLayout());
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

    private void initializeSidebar() {
        // Tabbed panel
        sidebar = new JTabbedPane(JTabbedPane.TOP);
        sidebar.setPreferredSize(new Dimension(236, frame.getHeight()));
        frame.getContentPane().add(sidebar, BorderLayout.WEST);
        // tab of file viewer
        JPanel fileViewer = new JPanel();
        sidebar.addTab("File", null, fileViewer, null);
        fileViewer.setLayout(new FlowLayout());

        // add the file selector to the tabbed pane
        JLabel lblBinaryFiles = new JLabel("File Selectors:");
        lblBinaryFiles.setBounds(10, 11, 64, 14);
        fileViewer.add(lblBinaryFiles, BorderLayout.NORTH);
        final JFileChooser fileChooser = new JFileChooser();
        final JButton mwFileButton = new JButton("Select Binary File");
        mwFileButton.setPreferredSize(new Dimension(170, 30));
        mwFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(mwFileButton);

                if (result == JFileChooser.APPROVE_OPTION) {
                    System.out.println("File selected: "
                            + fileChooser.getSelectedFile());
                    mwFileSelected(fileChooser.getSelectedFile()
                            .getAbsolutePath());
                }
            }
        });
        final JButton eventFileButton = new JButton("Select Event File");
        eventFileButton.setPreferredSize(new Dimension(170, 30));
        eventFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(eventFileButton);

                if (result == JFileChooser.APPROVE_OPTION) {
                    System.out.println("File selected: "
                            + fileChooser.getSelectedFile());
                    eventFileSelected(fileChooser.getSelectedFile()
                            .getAbsolutePath());
                }
            }
        });
        final JButton ibiFileButton = new JButton("Select IBI File");
        ibiFileButton.setPreferredSize(new Dimension(170, 30));
        ibiFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(ibiFileButton);

                if (result == JFileChooser.APPROVE_OPTION) {
                    System.out.println("File selected: "
                            + fileChooser.getSelectedFile());
                    ibiFileSelected(fileChooser.getSelectedFile()
                            .getAbsolutePath());
                }
            }
        });
        fileViewer.add(mwFileButton);
        fileViewer.add(ibiFileButton);
        fileViewer.add(eventFileButton);
        // tab for Channels viewer
        JPanel channels = new JPanel();
        // insert channels viewer here
        sidebar.addTab("Channels", null, channels, null);
        // tab for video viewer
        JPanel video = new JPanel();
        // insert video viewer here
        sidebar.addTab("Video", null, video, null);

    }

    /**
     * Swaps the chart & overlay panels
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
        // revalidate and repaint
        mainPanel.revalidate();
        scrollPanel.revalidate();
        overlayPanel.revalidate();
        mainPanel.repaint();

    }

    /**
     * called when a user selects a mindware file, loads the file and updates
     * the charts
     * 
     * @param filename
     */
    private void mwFileSelected(String filename) {

        try {
            cm.openMwFile(filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error opening file: "
                    + filename + "\nPlease make sure you selected the correct file");
            e.printStackTrace();
        }
        updateCharts();
    }

    private void eventFileSelected(String filename) {
        try {
            cm.openEventFile(filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error opening file: "
                    + filename + "\nPlease make sure you selected the correct file");
        }
        updateCharts();

    }

    private void ibiFileSelected(String filename) {
        try {
            cm.openIbiFile(filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error opening file: "
                    + filename + "\nPlease make sure you selected the correct file");
        }
        updateCharts();
    }

    /**
     * Remove all points from chart, re-pull data, re-display charts
     */
    private void updateCharts() {

        chartPanel.removeAll();
        overlayPanel.removeAll();
        togglePanels = new ArrayList<TogglePanel>();
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
        chartPanel.repaint();
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
        togglePanels = new ArrayList<TogglePanel>();
        for (SynchronizedChart chart : charts) {
            ChartPanel tmpPanel = new ChartPanel(chart);
            tmpPanel.setPreferredSize(new Dimension(200,
                    ChartManager.MINIMUM_CHART_HEIGHT));
            TogglePanel tPanel = new TogglePanel(tmpPanel, true);
            togglePanels.add(tPanel);
            addChannel((JPanel) sidebar.getComponentAt(CHANNELS_TAB), tPanel);
        }

        addChartsToPanel(togglePanels);

        // be very very quiet... we're hunting zooms
        this.setZoomListener(new ZoomOutAdapter(cm));
        scrollPanel.setViewportView(chartPanel);
        scrollPanel.revalidate();
    }

    private void addChartsToPanel(List<TogglePanel> chartPanels) {
        chartPanel.setLayout(new GridLayout(chartPanels.size(), 1));
        // and add those chart panels to the main chart container
        for (TogglePanel panel : chartPanels) {
            if (panel.isVisible()) {
                chartPanel.add(panel.getPanel());
            }
        }
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void initializeOverlay() {
        SynchronizedChart overlaidChart;
        try {
            overlaidChart = cm.generateOverlay();
        } catch(NoSuchObjectException e) {
            return;
        }
        ChartPanel cPanel = new ChartPanel(overlaidChart);
        cPanel.setPreferredSize(new Dimension(500, 500));
        overlayPanel.setLayout(new GridLayout(1, 1));
        overlayPanel.add(cPanel);
    }

    private void addChannel(JPanel panel, TogglePanel togglePanel) {

        ChartPanel chartPanel = togglePanel.getPanel();
        JPanel formatPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        // Grid Bags. Fun.
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 10, 0, 0);
        JLabel lblChannel = new JLabel(chartPanel.getChart().getName() + ":");
        formatPanel.add(lblChannel, c);

        JPanel btnPanel = new JPanel();
        JButton colorPicker = new JButton("Choose color");

        btnPanel.add(colorPicker);
        JButton toggle = new JButton("Toggle");
        final TogglePanel tp = togglePanel;
        toggle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                toggleChart(tp);
            }

        });
        colorPicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(frame, "Choose a color",
                        Color.blue);
                tp.setColor(color);
            }
        });

        btnPanel.add(toggle);

        c = new GridBagConstraints();
        JButton exportButton = new JButton("export");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;
        c.gridx = 0;
        c.gridwidth = 3;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        formatPanel.add(btnPanel, c);

        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridx = 0;
        formatPanel.add(exportButton, c);
        panel.add(formatPanel);

    }

    /**
     * If the given <code>ChartPanel</code> is showing in the frame, remove it.
     * Otherwise, add it.
     * 
     * @param cPanel
     *            the <code>ChartPanel</code> to add/remove
     */
    private void toggleChart(TogglePanel tPanel) {
        tPanel.toggleVisibility();
        chartPanel.removeAll();
        addChartsToPanel(togglePanels);

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

    /**
     * As named.
     */
    public void display() {
        this.frame.setVisible(true);
    }

    /**
     * helper class to keep track of panels and their toggle state
     * 
     * @author jordanreedie
     * 
     */
    class TogglePanel {

        private ChartPanel panel;
        private boolean visible;

        public TogglePanel(ChartPanel panel, boolean visible) {
            this.panel = panel;
            this.visible = visible;
        }

        public void toggleVisibility() {
            this.visible = !this.visible;
        }

        /**
         * @return the panel
         */
        public ChartPanel getPanel() {
            return panel;
        }

        /**
         * @return the visible
         */
        public boolean isVisible() {
            return visible;
        }

        /**
         * @param panel
         *            the panel to set
         */
        public void setPanel(ChartPanel panel) {
            this.panel = panel;
        }

        /**
         * @param visible
         *            the visible to set
         */
        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        /**
         * sets the color of the chart
         * 
         * @param color
         */
        public void setColor(Color color) {
            panel.getChart().getTraces().first().setColor(color);
        }
    }

}
