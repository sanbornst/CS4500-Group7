package MindChart;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSorted;

import org.junit.Test;

public class ChartGeneratorTest {

    @Test
    public void generateChartTest() {
        ITrace2D trace1 = new Trace2DSorted();
        
        SynchronizedChart chart = ChartManager.generateChart(trace1);
        
        // Add all points, sin & cos because why not
        for(int i=1; i < 10000; i++) {
            trace1.addPoint(i, Math.sin(i * .01));
        }
        
        
        assertEquals(1, chart.getTraces().size());
        assertEquals(trace1, chart.getTraces().first());
        
    } 
    
    @Test 
    public void generateChartsTest() {
        // some setup 
        List<SynchronizedChart> charts;
        List<ITrace2D> traces = new ArrayList<ITrace2D>();
        
        ITrace2D trace1 = new Trace2DSorted();
        ITrace2D trace2 = new Trace2DSorted(); 
        // Add the trace to the chart. This has to be done before adding points (deadlock prevention): 
        traces.add(trace1);
        traces.add(trace2);
        charts = ChartManager.generateCharts(traces);
        // Add all points, sin & cos because why not
        for(int i=1; i < 10000; i++) {
          trace1.addPoint(i, Math.sin(i * .01));
          trace2.addPoint(i, Math.cos(i * .01));
        }
        
        assertEquals(2, charts.size());
        assertEquals(charts, charts.get(0).getFriends());
        assertEquals(charts, charts.get(1).getFriends());
        
    }

}
