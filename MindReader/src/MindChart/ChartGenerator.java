package MindChart;

import info.monitorenter.gui.chart.ITrace2D;

import java.util.ArrayList;
import java.util.List;


/**
 * Utility class to generate chart(s) from <code>ITrace2D</code>s
 * @author jordanreedie
 *
 */
public class ChartGenerator {
	
	/**
	 * Generate charts from a list of <code>ITrace2D</code>s.
	 * @param traces The data to generate the charts from
	 * @return synchronized charts from the provided traces
	 */
	public static List<SynchronizedChart> generateCharts(List<ITrace2D> traces) {
		List<SynchronizedChart> charts = new ArrayList<SynchronizedChart>();
		
		//generate charts from traces
		for (ITrace2D trace : traces) {
			charts.add(generateChart(trace));
		}
		
		//this looks weird, i know, but bear with me
		for (SynchronizedChart chart : charts) {
			chart.setFriends(charts);
		}
		
		return charts;
		
	}
	
	/**
	 * Generate a single synchronizable chart from an <code>ITrace2D</code>. 
	 * @param data the trace with which to generate the chart
	 * @return the generated, synchronizable chart
	 */
	public static SynchronizedChart generateChart(ITrace2D data) {
		SynchronizedChart chart = new SynchronizedChart();
		chart.addTrace(data);
		return chart;		
	}
	
	
	

}
