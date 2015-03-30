package MindReader;

import javax.swing.JFrame;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import java.io.IOException;

public class IbiIOTest {
  
  public static void main(String[] args) throws IOException {
    JFrame testFrame = new JFrame("Testing IBI");
    Chart2D testChart = new Chart2D();
    ITrace2D trace0 = new Trace2DSimple();
    ITrace2D trace1 = new Trace2DSimple();
    testChart.addTrace(trace0);
    testChart.addTrace(trace1);
    IbiIO testIbi = new IbiIO();
    testIbi.open("PP01_ECG_Actiwave_PA_HRV_IBI_3_13 PM.txt");
    testIbi.read(trace1, 0, 0, 12000, 0);
    BinaryIO testBin = new BinaryIO();
    testBin.open("PA_1.mw");
    testBin.read(trace0, 0, 0, 12000, 0);
    testFrame.add(testChart);
    testFrame.setVisible(true);
  }
}