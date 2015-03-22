package MindReader;

import info.monitorenter.gui.chart.TracePoint2D;
import info.monitorenter.gui.chart.ITracePoint2D;

public class TracePointString extends TracePoint2D implements ITracePoint2D {
    private String text;
    
    TracePointString(double xValue, double yValue, String text) {
        super(xValue, yValue);
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(String newText) {
        this.text = newText;
    }
}