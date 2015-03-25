package MindReader;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxisScalePolicy;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.util.Range;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class BinaryIOTest {
    // Path to pull data from 
    private static String path = "/Users/fox/Desktop/data/smaller/PA_1.mw";
    // start time (in ms)
    private static long start = 0;
    // end time (in ms)
    private static long end = 297000;
    // number of points to display per channel
    private static int pointsPerChannel = 30000;
    // math to set frequency to the correct value (assuming 1000 points / s)
    private static int freq = (int) (end - start) / pointsPerChannel;
    
    
    // low end of the scale
    private static int lowScale = -5;
    // high end of the scale
    private static int highScale = 5;

    // Image Settings
    private static boolean toImage = false;
    private static String output = "/path/to/output/test.bmp";
    private static int width = 29700;
    private static int height = 1080;
    
    
    public static void main(String[] args) {
        BinaryIO file = new BinaryIO();
        
        // Create a chart:  
        Chart2D chart = new Chart2D();
        // Create ITraces 
        ITrace2D trace0 = new Trace2DSimple();
        ITrace2D trace1 = new Trace2DSimple();
        ITrace2D trace2 = new Trace2DSimple();
        ITrace2D trace3 = new Trace2DSimple();
        
        // Set line colors
        trace0.setColor(Color.black);
        trace1.setColor(Color.red);
        trace2.setColor(Color.blue);
        trace3.setColor(Color.green);

  
        @SuppressWarnings("unchecked")
        IAxis<IAxisScalePolicy> yAxis = (IAxis<IAxisScalePolicy>) chart.getAxisY();
        yAxis.setRangePolicy(new RangePolicyFixedViewport(new Range(lowScale, highScale)));

        // Add the trace to the chart. This has to be done before adding points (deadlock prevention): 
        chart.addTrace(trace0);
        chart.addTrace(trace1);
        chart.addTrace(trace2);
        chart.addTrace(trace3);
        
        try {
            System.out.println("Opening file...");
            file.open(BinaryIOTest.path);
            
            ArrayList<ChannelInfo> channels = file.getChannels();
            
            // populate the trace names
            trace0.setName(channels.get(0).getName());
            trace1.setName(channels.get(1).getName());
            trace2.setName(channels.get(2).getName());
            trace3.setName(channels.get(3).getName());
            
            // read the data from the file into the traces
            // Channel 0
            file.read(trace0, 0, BinaryIOTest.start, BinaryIOTest.end, BinaryIOTest.freq);
            // Channel 1
            file.read(trace1, 1, BinaryIOTest.start, BinaryIOTest.end, BinaryIOTest.freq);
            // Channel 2
            file.read(trace2, 2, BinaryIOTest.start, BinaryIOTest.end, BinaryIOTest.freq);
            // Channel 3
            //file.read(trace3, 3, BinaryIOTest.start, BinaryIOTest.end, BinaryIOTest.freq);
            
        } catch (IOException e) {
            System.out.println("Unable to open file: " + e);
        }
        
        try {
            System.out.println("Closing file...");
            file.close();
        } catch (IOException e) {
            System.out.println("Unable to close file: " + e);
        }
        
        // display results as either a saved image, or JFrame
        if (BinaryIOTest.toImage){
            try{
                System.out.println("Generating image...");
                BufferedImage s = chart.snapShot(BinaryIOTest.width, BinaryIOTest.height);
                File output = new File(BinaryIOTest.output);
                ImageIO.write(s, "bmp", output);
                System.out.println("Image written to: " + BinaryIOTest.output);
            } catch (Exception e){
                System.out.println("Unable to create image: " + e);
            }
            System.exit(0);
        } else {
            // Make it visible:
            // Create a frame.
            JFrame frame = new JFrame("MinimalStaticChart");
            // add the chart to the frame: 
            frame.getContentPane().add(chart);
            frame.setSize(1024,300);
            // Enable the termination button [cross on the upper right edge]: 
            frame.addWindowListener(
                new WindowAdapter(){
                  public void windowClosing(WindowEvent e){
                      System.exit(0);
                  }
                }
              );
            frame.setVisible(true);
        }
    }
}