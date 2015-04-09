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
    private ChartManager cm;

    /**
     * Creates an instance that will reset zooming on the given zoomable charts
     * upon the triggered action.
     * <p>
     * 
     * @param charts
     *            the charts to reset zooming on.
     */
    public ZoomOutAdapter(final ChartManager cm) {
        this.cm = cm;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent event) {
        cm.zoomOut();
    }

}