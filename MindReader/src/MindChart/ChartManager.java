package MindChart;

import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSorted;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import MindReader.BinaryIO;
import MindReader.ChannelInfo;
import MindReader.Utils;

/**
 * Class to manage & create charts from <code>ITrace2D</code>s
 * 
 * @author jordanreedie
 * 
 */
public class ChartManager {

    private final int START = 0;
    private BinaryIO bio;
    private List<Color> colors;
    private List<SynchronizedChart> charts;
    private boolean reloaded = false;

    private int NUM_POINTS = 30000;

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

    /**
     * Zooms out all charts managed by this <code>ChartManager</code>
     */
    public void zoomOut() {
        if (reloaded) {
            // we've made changes to the resolution, so back out to the default
            try {
                this.setRange(0, Utils.msToSeconds(bio.getEndTime()));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            // we haven't screwed around with resolution, so just normally zoom
            // out
            for (SynchronizedChart chart : charts) {
                chart.zoomAll();
                chart.normalizeAxisY();
            }
        }

        this.reloaded = false;
    }

    /**
     * Set range of charts
     * 
     * @param start
     *            start time, in seconds
     * @param end
     *            end time, in seconds
     * @throws IOException
     */
    public void setRange(double start, double end) throws IOException {
        this.reloaded = true;
        ArrayList<ChannelInfo> channels = bio.getChannels();
        long start_ms = Utils.secondsToMs(start);
        long end_ms = Utils.secondsToMs(end);
        for (int i = 0; i < channels.size(); i++) {
            ITrace2D trace = charts.get(i).getTraces().first();
            trace.removeAllPoints();
            bio.read(trace, channels.get(i).getId(), start_ms, end_ms,
                    bio.toFrequency(start_ms, end_ms, NUM_POINTS));
        }
        for (SynchronizedChart chart : charts) {
            chart.setXRange(start, end);
            chart.normalizeAxisY();
        }

    }

    /**
     * Generates a list of charts
     * 
     * @return the generated list of charts
     * @throws IOException
     */
    public List<SynchronizedChart> generateCharts() throws IOException {

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

    public SynchronizedChart generateOverlay() {
        SynchronizedChart overlay = new SynchronizedChart();

        for (SynchronizedChart chart : charts) {
            overlay.addTrace(chart.getTraces().first());
        }

        return overlay;
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
     * Sets the path to the data file    
     * 
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
    }

}
