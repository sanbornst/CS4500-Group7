package MindReader;

import javax.swing.JFrame;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import java.io.IOException;

class EventChartTest{
  public static void main(String[] args) throws IOException {
    JFrame testFrame = new JFrame("Testing IBI");
    Chart2D testChart = new Chart2D();
    ITrace2D trace0 = new Trace2DSimple();
    trace0.setTracePainter(new TracePainterDisc());
    testChart.setToolTipType(new EventToolTip());
    testChart.addTrace(trace0);
    
    EventIO testEvent = new EventIO();
    testEvent.open("PA_1_event.txt");
    testEvent.read(trace0, 0, 0, 12000, 0);
    
    testFrame.add(testChart);
    testFrame.setVisible(true);
  }
}