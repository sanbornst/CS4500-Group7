package MindReader;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IToolTipType;
import info.monitorenter.gui.chart.ITracePoint2D;
import java.awt.event.MouseEvent;

class EventToolTip implements IToolTipType {
  
  EventToolTip() {
    
  }
  
  @Override
  public String getDescription() {
    return "Values, snap to nearest point";
  }
  
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