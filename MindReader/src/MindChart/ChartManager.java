package MindChart;

import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSorted;

import java.awt.Color;
import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;

import MindReader.BinaryIO;
import MindReader.ChannelInfo;
import MindReader.EventIO;
import MindReader.IbiIO;
import MindReader.Utils;

/**
 * Class to manage & create charts from <code>ITrace2D</code>s
 * 
 * @author jordanreedie
 * 
 */
public class ChartManager {

    /**
     * Minimum chart height, in pixels
     */
    public static int MINIMUM_CHART_HEIGHT = 200;

    private final int START = 0;
    private BinaryIO bio;
    private EventIO eio;
    private IbiIO ibio;
    private List<Color> colors;
    private List<SynchronizedChart> mwCharts;
    private SynchronizedChart eChart;
    private SynchronizedChart iChart;
    private List<SynchronizedChart> allCharts;
    private boolean reloaded = false;

    private int NUM_POINTS = 30000;

    public ChartManager() {
        bio = new BinaryIO();
        eio = new EventIO();
        ibio = new IbiIO();
        initializeColors();
    }

    public ChartManager(String mwPath, String ePath, String iPath) {
        bio = new BinaryIO();
        eio = new EventIO();
        ibio = new IbiIO();
        try {
            bio.open(mwPath);
            eio.open(ePath);
            ibio.open(iPath);
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
        }

        for (SynchronizedChart chart : allCharts) {
            chart.zoomAll();
            chart.normalizeAxisY();
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
            ITrace2D trace = mwCharts.get(i).getTraces().first();
            trace.removeAllPoints();
            bio.read(trace, channels.get(i).getId(), start_ms, end_ms,
                    bio.toFrequency(start_ms, end_ms, NUM_POINTS));
        }

        // make sure the chart views are set appropriately
        for (SynchronizedChart chart : allCharts) {
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

        List<SynchronizedChart> charts = new ArrayList<SynchronizedChart>();
        if (this.bio.isOpen()) {
            this.mwCharts = generateMwCharts();
            charts.addAll(this.mwCharts);

        }

        if (this.ibio.isOpen()) {
            this.iChart = generateIbiChart();
            insertAfterECG(this.iChart, charts);
        }
        // event chart comes last
        if (this.eio.isOpen()) {
            this.eChart = generateEventChart();
            charts.add(eChart);
        }

        // this looks weird, i know, but bear with me
        for (SynchronizedChart chart : charts) {
            chart.setFriends(charts);
        }

        // sync each chart with the previous chart in the list
        for (int i = 1; i < charts.size(); i++) {
            charts.get(i).setSynchronizedXStartChart(charts.get(i - 1));
        }

        // my charts!
        this.allCharts = charts;
        normalizeCharts();
        return this.allCharts;
    }

    /**
     * Insert <code>toInsert</code> after the ECG chart in <code>charts</code>
     * 
     * @param toInsert
     *            the chart to insert
     * @param charts
     *            to list of charts to insert into
     */
    private void insertAfterECG(SynchronizedChart toInsert,
            List<SynchronizedChart> charts) {

        int initialSize = charts.size();
        for (int i = 0; i < initialSize; i++) {
            if (charts.get(i).getName().contains("ECG")) {
                charts.add(i + 1, toInsert);
            }
        }

        // if the ibi chart never got inserted, do it now
        if (charts.size() == initialSize) {
            charts.add(toInsert);
        }
    }

    /**
     * Generate charts from the currently open MindWare (.mw) file
     * 
     * @return
     * @throws IOException
     */
    private List<SynchronizedChart> generateMwCharts() throws IOException {
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

        return charts;
    }

    /**
     * call the normalizeAxisY method on all charts
     */
    private void normalizeCharts() {
        for (SynchronizedChart chart : allCharts) {
            chart.normalizeAxisY();
        }
    }

    /**
     * Generate the event chart
     * 
     * @return
     * @throws IOException
     */
    private SynchronizedChart generateEventChart() throws IOException {
        // event file only has one channel
        ChannelInfo channel = eio.getChannels().get(0);
        ITrace2D trace = new Trace2DSorted();
        trace.setColor(colors.get(0));
        trace.setName(channel.getName());
        SynchronizedChart chart = this.generateChart(trace);

        eio.read(trace, channel.getId(), START, eio.getEndTime(), 1);

        return chart;
    }

    /**
     * Generate the IBI chart
     * 
     * @return
     * @throws IOException
     */
    private SynchronizedChart generateIbiChart() throws IOException {
        // ibi file only has one channel
        ChannelInfo channel = ibio.getChannels().get(0);
        ITrace2D trace = new Trace2DSorted();
        trace.setColor(colors.get(0));
        trace.setName(channel.getName());
        SynchronizedChart chart = this.generateChart(trace);
        ibio.read(trace, channel.getId(), START, ibio.getEndTime(), 1);

        return chart;
    }

    /**
     * Generate the overlay chart
     * 
     * @return
     * @throws NoSuchObjectException
     */
    public SynchronizedChart generateOverlay() throws NoSuchObjectException {

        if (this.mwCharts == null || this.mwCharts.size() == 0) {
            throw new NoSuchObjectException(
                    "Mindware File has not been read yet!");
        }
        SynchronizedChart overlay = new SynchronizedChart();

        // only use mw charts for the overlay, as the ibi & event files throw
        // off the scale
        for (SynchronizedChart chart : mwCharts) {
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
        chart.setName(data.getName());
        return chart;
    }

    /**
     * Sets the path to the data file    
     * 
     * @param path
     * @throws IOException
     */
    public void openMwFile(String path) throws IOException {
        if (bio.isOpen()) {
            bio.close();
        }
        bio = new BinaryIO();
        bio.open(path);
    }

    public void openIbiFile(String path) throws IOException {
        try {
            ibio.close();
        } catch (IOException e) {
            // eat it.
        }

        ibio = new IbiIO();
        ibio.open(path);

    }

    /**
     * Opens the given path to the event file
     * 
     * @param path
     * @throws IOException
     */
    public void openEventFile(String path) throws IOException {
        try {
            eio.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        eio = new EventIO();
        eio.open(path);
    }

    /**
     * for the hell of it
     */
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
