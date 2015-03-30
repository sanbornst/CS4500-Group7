package MindReader;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IToolTipType;
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
    TracePointString p = (TracePointString) cd.getNearestPointManhattan(me);
    return p.getText();
  }
}