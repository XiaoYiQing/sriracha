package sriracha.simulator.solver.analysis.ac;

import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.Options;
import sriracha.simulator.Simulator;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.solver.analysis.Analysis;
import sriracha.simulator.solver.analysis.AnalysisType;

public final class ACAnalysis extends Analysis
{
    /**
     * Type of scale for output octave and decade are log scales
     */
    private ACSubType subType;

    private ACEquation equation;

    /**
     * Start frequency
     */
    private double fStart;
    /**
     * End Frequency
     */
    private double fEnd;

    /**
     * For Linear, total number of frequency points or number per decade/octave
     */
    private int points;

    /**
     * @param subType Type of scale for output octave and decade are log scales
     * @param fStart  start frequency
     * @param fEnd    stop frequency
     * @param points  for Linear total number of frequency points or number per decade/octave
     */
    public ACAnalysis(ACSubType subType, double fStart, double fEnd, int points)
    {
        super(AnalysisType.AC);
        this.subType = subType;
        this.fStart = fStart;
        this.fEnd = fEnd;
        this.points = points;
    }


    /**
     * Helper method for analysis
     *
     * @param base
     * @return analysis results
     */
    private ACResults logScaleRun(int base)
    {
        ACResults results = new ACResults();
        double currentFrequency = fStart;
        int magnitudeScale = 1;
        while (currentFrequency <= fEnd)
        {
            currentFrequency = fStart * magnitudeScale;
            if (points == 1)
            {
                results.addVector(currentFrequency, equation.solve(currentFrequency));
            } else
            {
                double range = currentFrequency * (base - 1);
                double interval = range / (points - 1);
                for (int i = 0; i < points && currentFrequency <= fEnd; i++)
                {
                    if (Simulator.Instance.isCancelRequested()) return null;

                    if (Options.isPrintProgress()) System.out.println("AC solving point");
                    IComplexVector soln = equation.solve(currentFrequency);
                    results.addVector(currentFrequency, soln);
                    currentFrequency += interval;

                }
            }


            magnitudeScale *= base;
        }
        return results;
    }

    @Override
    public void extractSolvingInfo(Circuit circuit)
    {
        equation = ACEquation.generate(circuit);
    }

    @Override
    public ACResults run()
    {
        ACResults results = new ACResults();
        switch (subType)
        {
            case Linear:
                if (points == 1)
                { //to avoid divide by 0 issues
                    results.addVector(fStart, equation.solve(fStart));
                } else {
                    double interval = (fEnd - fStart) / (points - 1);
                    for (int i = 0; i < points; i++)
                    {
                        if (Simulator.Instance.isCancelRequested()) return null;

                        double omega = fStart + interval * i;
                        if (Options.isPrintProgress()) System.out.println("AC solving point");
                        IComplexVector soln = equation.solve(omega);
                        results.addVector(omega, soln);

                    }
                }

                break;
            case Decade:
                return logScaleRun(10);
            case Octave:
                return logScaleRun(8);
        }
        return results;
    }

    @Override
    public String toString()
    {
        return ".AC " + subType + " " + points + " " + fStart + " " + fEnd;
    }
}
