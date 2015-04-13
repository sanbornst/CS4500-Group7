package MindChart;

import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxisScalePolicy;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DSorted;
import info.monitorenter.util.Range;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import MindReader.BinaryIO;
import MindReader.ChannelInfo;

/**
 * Class to manage & create from <code>ITrace2D</code>s
 * 
 * @author jordanreedie
 * 
 */
public class ChartManager {

    private final int START = 0;
    private BinaryIO bio;
    private List<Color> colors;
    List<SynchronizedChart> charts;

    private final int NUM_POINTS = 30000;
    
    public ChartManager() {
        bio = new BinaryIO();
        initializeColors();
    }

    public ChartManager(String path) {
        bio = new BinaryIO();
        try {
            bio.open(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        initializeColors();
    }

    public void setRange(int start, int end) {
        for (SynchronizedChart chart : charts) {
            chart.setXRange(start, end);
        }
    }

    public List<SynchronizedChart> generateCharts(int maxPoints) throws IOException {
       
        
        ArrayList<ChannelInfo> channels = bio.getChannels();
        List<ITrace2D> traces = new ArrayList<ITrace2D>();
        // create the trace objects && get some colors going
        for (int i = 0; i < channels.size(); i++) {
            Trace2DSorted trace = new Trace2DSorted();
            trace.setColor(colors.get(i));
            trace.setName(channels.get(i).getName());
            traces.add(trace);
        }

        // generate charts before adding data to traces to prevent deadlock
        List<SynchronizedChart> charts = this.createCharts(traces);
        
        // now populate traces with data!
        for (int i = 0; i < channels.size(); i++) {
            int freq = bio.toFrequency(0, bio.getEndTime(), this.NUM_POINTS);
            bio.read(traces.get(i), channels.get(i).getId(), START,
                    bio.getEndTime(), freq);
        }
       
        for (SynchronizedChart chart : charts) {
            chart.normalizeAxisY();
        }
        // my charts!
        this.charts = charts;
        return charts;
    }

    /**
     * Generate charts from a list of <code>ITrace2D</code>s.
     * 
     * @param traces
     *            The data to generate the charts from
     * @return synchronized charts from the provided traces
     */
    private List<SynchronizedChart> createCharts(List<ITrace2D> traces) {
        List<SynchronizedChart> charts = new ArrayList<SynchronizedChart>();

        // generate charts from traces
        for (ITrace2D trace : traces) {
            charts.add(generateChart(trace));
           
        }

        // this looks weird, i know, but bear with me
        for (SynchronizedChart chart : charts) {
            chart.setFriends(charts);
            chart.setFocusable(true);
        }

        return charts;

    }

    /**
     * Generate a single synchronizable chart from an <code>ITrace2D</code>.
     * 
     * @param data
     *            the trace with which to generate the chart
     * @return the generated, synchronizable chart
     */
    private SynchronizedChart generateChart(ITrace2D data) {
        SynchronizedChart chart = new SynchronizedChart();
        chart.addTrace(data);
        return chart;
    }

    /**
     * Sets the path to the data file
     * @param path
     * @throws IOException
     */
    public void setPath(String path) throws IOException {
        if (bio.isOpen()) {
            bio.close();
        } 
        bio = new BinaryIO();
        bio.open(path);
    }

    private void initializeColors() {
        colors = new ArrayList<Color>();
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.ORANGE);
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.ORANGE);
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.ORANGE);
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.ORANGE);
        colors.add(Color.RED);
    }
   
}
