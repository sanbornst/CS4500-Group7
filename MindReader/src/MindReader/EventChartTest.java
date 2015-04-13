package MindReader;

import javax.swing.JFrame;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.errorbars.*;
import info.monitorenter.gui.chart.IErrorBarPolicy;
import info.monitorenter.gui.chart.IErrorBarPainter;
import info.monitorenter.gui.chart.pointpainters.PointPainterLine;
import info.monitorenter.gui.chart.pointpainters.PointPainterDisc;
import java.io.IOException;
import java.awt.Color;

class EventChartTest{
  public static void main(String[] args) throws IOException {
    JFrame testFrame = new JFrame("Testing Events");
    Chart2D testChart = new Chart2D();
    ITrace2D trace0 = new Trace2DSimple();
    //trace0.setTracePainter(new TracePainterDisc());
    //trace0.setColor(java.awt.Color.RED);
    testChart.setToolTipType(new EventToolTip());
    testChart.addTrace(trace0);
    //IErrorBarPolicy errors = new ErrorBarPolicyAbsoluteSummation(5, 5);
    //errors.setShowNegativeYErrors(true);
    //errors.setShowPositiveYErrors(true);
    //IErrorBarPainter errorPainter = new ErrorBarPainterLine();
    //errors.setErrorBarPainter(errorPainter);
    //errorPainter.setEndPointPainter(new PointPainterDisc());
    //errorPainter.setEndPointColor(Color.WHITE);
    //errorPainter.setConnectionPainter(new PointPainterLine());
    //errorPainter.setConnectionColor(Color.RED);
    //trace0.setErrorBarPolicy(errors);
    
    EventIO testEvent = new EventIO();
    testEvent.open("CFSArmy1B_Pp37_7-14-14_event.txt");
    testEvent.read(trace0, 0, 700000, 900000, 0);
    
    
    testFrame.add(testChart);
    testFrame.pack();
    testFrame.setVisible(true);
  }
}