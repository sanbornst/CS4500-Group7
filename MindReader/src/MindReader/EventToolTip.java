package MindReader;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IToolTipType;
import info.monitorenter.gui.chart.ITracePoint2D;
import java.awt.event.MouseEvent;

/**
 * Implements JChart2D's IToolTipType interface to display event
 * information from its custom trace point
 * 
 * @author Samantha Sanborn
 */
class EventToolTip implements IToolTipType {
  
  // empty constructor
  EventToolTip() {
    
  }
  
  /**
   * Returns a description of the behavior of the tooltip type
   */
  @Override
  public String getDescription() {
    return "Values, snap to nearest point";
  }
  
  /**
   * Returns the appropriate tooltip text for any point where the mouse is on the chart
   * 
   * @param cd the chart being moused over
   * @param me mouse event to determine location of mouse
   */
  @Override
  public String getToolTipText(Chart2D cd, MouseEvent me) {
    ITracePoint2D p = cd.getNearestPointManhattan(me);
    if (p instanceof TracePointString) {
      TracePointString extraDataPoint = (TracePointString) p;
      return extraDataPoint.getText();
    } else {
      return "";
    }
  }
}