package MindChart;

import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxisScalePolicy;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.util.Range;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Listen for a zoom out event. Zooms out all charts.
 * @author jordanreedie
 *
 */
public class ZoomOutAdapter implements ActionListener {
    /** The zoomable charts to act upon. */
    private List<SynchronizedChart> m_zoomableCharts;

    /**
     * Creates an instance that will reset zooming on the given zoomable charts
     * upon the triggered action.
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
            double max = chart.getTraces().first().getMaxY() + 1;
            double min = chart.getTraces().first().getMinY() - 1;
            
            IAxis<IAxisScalePolicy> yAxis = (IAxis<IAxisScalePolicy>) chart.getAxisY();
            yAxis.setRangePolicy(new RangePolicyFixedViewport(new Range(min, max)));
        }
    }

}