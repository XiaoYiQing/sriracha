package sriracha.simulator.solver.analysis.dc;


import sriracha.simulator.model.elements.sources.Source;

public class DCSweep
{
    private Source source;

    private double startValue, endValue;
    private double step;


    public DCSweep(Source source, double startValue, double vEnd, double step)
    {
        this.source = source;
        this.startValue = startValue;
        this.endValue = vEnd;
        this.step = step;
    }


    public Source getSource()
    {
        return source;
    }

    public double getStartValue()
    {
        return startValue;
    }

    public double getEndValue()
    {
        return endValue;
    }

    public double getStep()
    {
        return step;
    }

    public String toString(){
        return ("DCSweep -> Source: " + source.name +
                "\b Start value: " + startValue +
                "\b endValue: " + endValue +
                "\b step: " + step);
    }
}
