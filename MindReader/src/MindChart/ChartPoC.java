package MindChart;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ZoomableChart;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSorted;
import info.monitorenter.gui.chart.views.ChartPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;


public class ChartPoC {
	
	
  
  public static void main(String[]args){
	
	  	// create charts
	   	List<SynchronizedChart> charts;
	   	List<ITrace2D> traces = new ArrayList<ITrace2D>();
	    // Create an ITrace: 
	    ITrace2D trace1 = new Trace2DSorted();
	    ITrace2D trace2 = new Trace2DSorted(); 
	    // Add the trace to the chart. This has to be done before adding points (deadlock prevention): 
	    traces.add(trace1);
	    traces.add(trace2);
	    charts = ChartGenerator.generateCharts(traces);
	    // Add all points, sin & cos because why not
	    for(int i=1; i < 10000; i++) {
	      trace1.addPoint(i, Math.sin(i * .01));
	      trace2.addPoint(i, Math.cos(i * .01));
	    }
	    // Create a frame.
	    JFrame frame = new JFrame("MinimalStaticChart");
	    frame.setLayout(new FlowLayout());
	    ChartPanel panel1 = new ChartPanel(charts.get(0));
	    ChartPanel panel2 = new ChartPanel(charts.get(1));
	    // add the panels to the frame: 
	    panel1.setPreferredSize(new Dimension(800,300));
	    panel2.setPreferredSize(new Dimension(800,300));
	    frame.getContentPane().add(panel1);
	    frame.getContentPane().add(panel2);
	    frame.setSize(800,700);
	    
	    
	    // Enable the termination button [cross on the upper right edge]: 
	    frame.addWindowListener(
	        new WindowAdapter() {	
	          public void windowClosing(WindowEvent e) {
	              System.exit(0);
	          }
	        }
	      );
	    
	    // class to listen for zoom out button
	    class ZoomOutAdapter implements ActionListener {
	        /** The zoomable charts to act upon. */
	        private List<SynchronizedChart> m_zoomableCharts;

	        /**
	         * Creates an instance that will reset zooming on the given zoomable charts upon the triggered
	         * action.
	         * <p>
	         * 
	         * @param charts
	         *            the charts to reset zooming on.
	         */
	        public ZoomOutAdapter(final List<SynchronizedChart> charts) {
	          this.m_zoomableCharts = charts;
	        }

	        
	        /**
	         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	         */
	        public void actionPerformed(final ActionEvent event) {
	          for (SynchronizedChart chart : m_zoomableCharts) {
	        	  chart.zoomAll();
	          }
        	}
	       
    	}	
	    // class to listen for sync button
	    class SyncAdapter implements ActionListener {
	        /** The syncable charts to act upon. */
	        private List<SynchronizedChart> m_zoomableCharts;

	        /**
	         * Creates an instance that will synchronize or desynchronize the given charts
	         * 	when invoked
	         * <p>
	         * 
	         * @param charts
	         *            the charts to modify synchronization on.
	         */
	        public SyncAdapter(final List<SynchronizedChart> charts) {
	          this.m_zoomableCharts = charts;
	        }

	        
	        /**
	         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	         */
	        public void actionPerformed(final ActionEvent event) {
	          for (SynchronizedChart chart : m_zoomableCharts) {
	        	  chart.setSynced(!chart.isSynced());
	          }
        	}
	       
    	}	
	    
	    for(SynchronizedChart chart : charts) {
	    	chart.setFocusable(true);
	    }
	    //frame.addKeyListener(chart1);
	    JButton syncButton = new JButton("Synchronize");
	    JButton zoomAllButton = new JButton("Zoom Out");
	    zoomAllButton.addActionListener(new ZoomOutAdapter(charts));
	    syncButton.addActionListener(new SyncAdapter(charts));
	    // Add zoomAll button to the pane
	    panel2.add(syncButton, BorderLayout.NORTH);
	    panel2.add(zoomAllButton, BorderLayout.SOUTH);
	    frame.setVisible(true);
	    
  }

  

}
