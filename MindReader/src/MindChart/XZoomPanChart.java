package MindChart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxisScalePolicy;
import info.monitorenter.gui.chart.IRangePolicy;
import info.monitorenter.gui.chart.ZoomableChart;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyUnbounded;
import info.monitorenter.util.Range;

/**
 * A Chart subclass to support x-axis zooming as well as panning Note: Much of
 * the zooming code is copied from the ZoomableChart class from the JChart2D
 * library itself, with some modifications to disable y-axis zooming and enable
 * panning.
 * 
 * @author jordanreedie
 * 
 */
public class XZoomPanChart extends Chart2D implements KeyListener,
        MouseListener, MouseMotionListener {

    private final int SCALING_FACTOR = 100;
    /**
     * Store the last mouse click and test in the mouseDragged-method which
     * mouse-button was clicked.
     */
    private int m_lastPressedButton;

    /** The starting point of the mouse drag operation (click, then move). */
    private Point2D m_startPoint;

    /**
     * Range policy used to zoom out to the minimum bounds that show every data
     * point.
     */
    private IRangePolicy m_zoomAllRangePolicy = new RangePolicyUnbounded();

    /** The area to zoom. */
    private Rectangle2D m_zoomArea;

    public XZoomPanChart() {
        super();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(final MouseEvent e) {
        /*
         * This will be used for zoom out in case ZoomableChart was not wrapped
         * in a ChartPanel. Might be replaced by "Hold down Ctrl and wheel"
         * which seems a famous gesture since e.g. Microsoft Visio.
         */
        if (e.getClickCount() == 2) {
            this.zoomAll();
        }
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(final MouseEvent e) {
        if (this.m_lastPressedButton != MouseEvent.BUTTON1) {
            return;
        }

        if ((e.getX() < 20) || (e.getX() > this.getXChartEnd())) {
            // nop

        } else {
            double startX;
            double endX;
            double dimX;

            // x-coordinate
            if (e.getX() > this.m_startPoint.getX()) {
                startX = this.m_startPoint.getX();
                endX = e.getX();
            } else {
                startX = e.getX();
                endX = this.m_startPoint.getX();
            }

            if (startX < this.getXChartStart()) {
                startX = this.getXChartStart();
            }

            if (endX > (this.getWidth() - 20)) {
                endX = this.getWidth() - 20;
            }

            dimX = endX - startX;

            this.m_zoomArea = new Rectangle2D.Double(startX, 0, dimX,
                    this.getYChartStart());

            this.setRequestedRepaint(true);
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(final MouseEvent e) {
        // nop
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(final MouseEvent e) {
        // nopnopnop
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(final MouseEvent e) {
        // nop
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(final MouseEvent e) {
        this.m_startPoint = new Point2D.Double(e.getX(), e.getY());
        this.m_lastPressedButton = e.getButton();
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(final MouseEvent e) {

        if (this.m_zoomArea != null) {

            // x-coordinate
            double startPx = this.m_zoomArea.getX();
            double endPx = this.m_zoomArea.getX() + this.m_zoomArea.getWidth();

            // y-coordinate
            double startPy = this.m_zoomArea.getY();
            double endPy = this.m_zoomArea.getY() + this.m_zoomArea.getHeight();

            // do not zoom extremely small areas (does not work properly because
            // of
            // calculation)
            if ((endPx - startPx) < 20 || (endPy - startPy) < 20) {
                this.m_zoomArea = null;
                this.setRequestedRepaint(true);
            } else {

                List<IAxis<?>> axisList = this.getAxes();
                for (Iterator<IAxis<?>> i = axisList.iterator(); i.hasNext();) {
                    IAxis<?> iAxis = i.next();
                    if ((Chart2D.CHART_POSITION_BOTTOM == iAxis
                            .getAxisPosition())
                            || (Chart2D.CHART_POSITION_TOP == iAxis
                                    .getAxisPosition())) {// its
                        // x
                        // axis
                        this.zoom(iAxis, startPx, endPx);
                    }
                    if ((Chart2D.CHART_POSITION_LEFT == iAxis.getAxisPosition())
                            || (Chart2D.CHART_POSITION_RIGHT == iAxis
                                    .getAxisPosition())) {// its
                        // x
                        // axis
                        this.zoom(iAxis, startPy, endPy);
                    }
                }
            }
        }
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected synchronized void paintComponent(final Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (this.m_zoomArea != null) {
            g2.draw(this.m_zoomArea);
            g2.setPaint(new Color(255, 255, 0, 100));
            g2.fill(this.m_zoomArea);
        }
    }

    /**
     * Zooms to the selected bounds in x-axis.
     * <p>
     * 
     * @param xmin
     *            the lower x bound.
     * 
     * @param xmax
     *            the upper x bound.
     */
    public void zoom(final double xmin, final double xmax) {

        this.m_zoomArea = null;

        IAxis<?> axis = this.getAxisX();
        IRangePolicy zoomPolicy = new RangePolicyFixedViewport(new Range(xmin,
                xmax));
        axis.setRangePolicy(zoomPolicy);
    }

    /**
     * Zooms the axis to the pixel value of start and end points.
     * <p>
     * Does not check for the position of axis so the caller must take care to
     * provide start x and end x for horizontal and start y and end y for
     * vertical axes.
     * 
     * @param axis
     *            the axis to zoom.
     * 
     * @param startP
     *            the start coordinate in the dimension of the given axis in
     *            pixel coords.
     * 
     * @param endP
     *            the end coordinate in the dimension of the given axis in pixel
     *            coords.
     */
    public void zoom(IAxis<?> axis, final double startP, final double endP) {

        this.m_zoomArea = null;

        double axisMin = axis.translatePxToValue((int) startP);
        double axisMax = axis.translatePxToValue((int) endP);

        IRangePolicy zoomPolicy = new RangePolicyFixedViewport(new Range(
                axisMin, axisMax));
        axis.setRangePolicy(zoomPolicy);

        // yuck
        this.normalizeAxisY();

    }

    /**
     * Sets the y axis range to something reasonable (default axis management is
     * kinda bad)
     */
    public void normalizeAxisY() {
        //TODO needs a bit o' tweaking
        // it seems the problem is getTraces.getMax<x/y> return the max for ALL
        // points in the trace, not just the currently visible ones...
        // not sure a way around that
        double max = this.getTraces().first().getMaxY();
        
        double min = this.getTraces().first().getMinY();
        double diff = Math.abs(max - min);
        System.out.println("original name: " + this.getName() + " min: " + min + " max: " + max);

        max += diff * 0.2;
        min -= diff * 0.2;

        if (max == 0) {
            max += 0.5;
        }
        if (min == 0) {
            min -= 0.5;
        }
        System.out.println("name: " + this.getName() + " min: " + min + " max: " + max);
        this.setYRange(min, max);
    }

    /**
     * Zooms to the selected bounds in both directions.
     * <p>
     * 
     * @param xmin
     *            the lower x bound (value of chart (vs. pixel of screen)).
     * 
     * @param xmax
     *            the upper x bound (value of chart (vs. pixel of screen)).
     * 
     * @param ymin
     *            the lower y bound (value of chart (vs. pixel of screen)).
     * 
     * @param ymax
     *            the upper y bound (value of chart (vs. pixel of screen)).
     */
    public void zoom(final double xmin, final double xmax, final double ymin,
            final double ymax) {

        this.m_zoomArea = null;

        IAxis<?> axisX = this.getAxisX();
        IRangePolicy zoomPolicyX = new RangePolicyFixedViewport(new Range(xmin,
                xmax));
        axisX.setRangePolicy(zoomPolicyX);

        IAxis<?> axisY = this.getAxisY();
        IRangePolicy zoomPolicyY = new RangePolicyFixedViewport(new Range(ymin,
                ymax));
        axisY.setRangePolicy(zoomPolicyY);
    }

    /**
     * Resets the zooming area to a range that displays all data.
     */
    public void zoomAll() {
        List<IAxis<?>> axisList = this.getAxes();
        for (Iterator<IAxis<?>> i = axisList.iterator(); i.hasNext();) {
            IAxis<?> iAxis = i.next();
            iAxis.setRangePolicy(this.m_zoomAllRangePolicy);
        }
    }

    /**
     * Pan the chart by <code>deltaX</code> in the x-axis 
     * 
     * @param deltaX
     *            the amount to pan the x-axis
     */
    public void pan(double deltaX) {
        
        IAxis<?> axisX = this.getAxisX();

        double xMin = axisX.getRange().getMin() + deltaX;
        double xMax = axisX.getRange().getMax() + deltaX;

        this.setXRange(xMin, xMax);

    }

    /**
     * Manually set the X-Axis range
     * 
     * @param xMin
     *            the lower bound for the axis
     * @param xMax
     *            the upper bound for the axis
     */
    public void setXRange(double xMin, double xMax) {
        IAxis<?> axisX = this.getAxisX();
        IRangePolicy zoomPolicyX = new RangePolicyFixedViewport(new Range(xMin,
                xMax));
        axisX.setRangePolicy(zoomPolicyX);
        this.normalizeAxisY();
    }

    /**
     * Manually set the Y-Axis range
     * 
     * @param yMin
     *            the lower bound for the axis
     * @param yMax
     *            the upper bound for the axis
     */
    public void setYRange(double yMin, double yMax) {
        IAxis<?> axisY = this.getAxisY();
        Range yRange = new Range(yMin, yMax);
        axisY.setRange(yRange);
    }

    /**
     * Determines the amount to pan based on the level of zoom
     * 
     * @return
     */
    private int scaledPanAmount() {
        IAxis<?> axisX = this.getAxisX();
        Range xRange = axisX.getRange();
        double dataRange = xRange.getExtent();
        return (int) (dataRange / SCALING_FACTOR);
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            pan(-1 * scaledPanAmount());
            break;
        case KeyEvent.VK_RIGHT:
            pan(scaledPanAmount());
            break;
        default:
            // nop nop nop
            break;

        }
    }
   
    @Override
    public void keyTyped(KeyEvent e) {
        // nop
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // nope nop
    }
}
