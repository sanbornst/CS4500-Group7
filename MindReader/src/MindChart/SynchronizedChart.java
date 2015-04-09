package MindChart;

import java.util.ArrayList;
import java.util.List;

import info.monitorenter.gui.chart.IAxis;

/**
 * A Chart class to support synchronization across multiple charts. I'm not
 * super psyched about this implementation, but it's the best I came up with.
 * 
 * @author jordanreedie
 * 
 */
public class SynchronizedChart extends XZoomPanChart {

	private boolean synced;
	private List<SynchronizedChart> friends;

	public SynchronizedChart() {
		super();
		this.synced = true;
	}

	public SynchronizedChart(boolean synced) {
		this.synced = synced;
	}

	public SynchronizedChart(List<SynchronizedChart> friends) {
		super();
		this.friends = friends;
	}

	/**
	 * If <code>synced</code> is <code>true</code>, zoom this and all synced
	 * graphs Otherwise, just zoom this graph
	 * 
	 * @param xmin
	 *            starting x pos
	 * @param xmax
	 *            ending x pos
	 */
	public void zoom(double xmin, double xmax) {
		if (friends != null && synced) {
			zoomAllFriends(xmin, xmax);
		} else {
			super.zoom(xmin, xmax);
		}
	}

	/**
	 * If <code>synced</code> is set to <code>true</code>, zoom this and all
	 * synced graphs Otherwise, just zoom this graph
	 * 
	 * @param axis
	 *            the axis to zoom on
	 * @param start
	 *            starting point
	 * @param end
	 *            ending point
	 * */
	public void zoom(IAxis<?> axis, double start, double end) {
		if (friends != null && synced) {
			zoomAllFriends(axis, start, end);
		} else {
			super.zoom(axis, start, end);
		}
	}

	/**
	 * If <code>synced</code> is set to <code>true</code>, zoom this and all
	 * synced graphs Otherwise, just zoom this graph
	 * 
	 * @param xmin
	 *            starting x pos
	 * @param xmax
	 *            ending x pos
	 * @param ymin
	 *            starting y pos
	 * @param ymax
	 *            ending y pos
	 */
	@Override
	public void zoom(double xmin, double xmax, double ymin, double ymax) {
		if (friends != null && synced) {
			zoomAllFriends(xmin, xmax, ymin, ymax);
		}
	}

	/**
	 * Tells all friends to zoom
	 * 
	 * @param axis
	 *            axis to zoom on
	 * @param start
	 *            starting point to zoom on
	 * @param end
	 *            ending point to zoom on
	 */
	private void zoomAllFriends(IAxis<?> axis, double start, double end) {
		for (SynchronizedChart friend : friends) {
			friend.zoomSelf(axis, start, end);
		}
	}

	/**
	 * Tells all friends to zoom
	 * 
	 * @param xmin
	 *            starting x pos to zoom on
	 * @param xmax
	 *            ending x pos to zoom on
	 * @param ymin
	 *            starting y pos to zoom on
	 * @param ymax
	 *            ending y pos to zoom on
	 */
	private void zoomAllFriends(double xmin, double xmax, double ymin,
			double ymax) {
		for (SynchronizedChart friend : friends) {
			friend.zoomSelf(xmin, xmax, ymin, ymax);
		}
	}

	/**
	 * Tells all friends to zoom
	 * 
	 * @param xmin
	 *            starting x pos to zoom on
	 * @param xmax
	 *            ending x pos to zoom on
	 */
	private void zoomAllFriends(double xmin, double xmax) {
		for (SynchronizedChart friend : friends) {
			friend.zoomSelf(xmin, xmax);
		}
	}

	/**
	 * Actually zooms the chart, instead of telling everyone to zoom
	 * 
	 * @param axis
	 *            the axis to zoom on
	 * @param start
	 *            the starting point of the zoom
	 * @param end
	 *            the ending point of the zoom
	 */
	public void zoomSelf(IAxis<?> axis, double start, double end) {
		super.zoom(fixAxis(axis), start, end);
	}
	
	
	private void zoomSelf(double xmin, double xmax, double ymin, double ymax) {
		super.zoom(xmin, xmax, ymin, ymax);
	}

	public void zoomSelf(double xmin, double xmax) {
		super.zoom(xmin, xmax);
	}

	/**
	 * Ensures the axis is pointing to the right graph
	 * @param axis
	 * @return
	 */
	private IAxis<?> fixAxis(IAxis<?> axis) {
		if (axis.getTitle().equalsIgnoreCase("x")) {
			return this.getAxisX();
		} else {
			return this.getAxisY();
		}
	}

	/**
	 * tell all charts to pan the given number of units
	 * @param deltaX the number of x units to pan
	 */
	public void pan(double deltaX) {
		if (synced) {
			for (SynchronizedChart chart : friends) {
				chart.panSelf(deltaX);
			}
		} else {
			super.pan(deltaX);
		}
	}

	/**
	 * Pan the chart by the given number of units
	 * @param deltaX x units to pan
	 */
	public void panSelf(double deltaX) {
		super.pan(deltaX);
	}

	/**
	 * @return the friends
	 */
	public List<SynchronizedChart> getFriends() {
		return friends;
	}

	/**
	 * @return whether or not graphs are synced
	 */
	public boolean isSynced() {
		return synced;
	}

	/**
	 * @param synced
	 *            the synced to set
	 */
	public void setSynced(boolean synced) {
		this.synced = synced;
	}

	/**
	 * @param friends
	 *            the friends to set
	 */
	public void setFriends(List<SynchronizedChart> friends) {
		this.friends = friends;
	}
}
