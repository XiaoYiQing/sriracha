package sriracha.simulator.solver.analysis;

import sriracha.simulator.model.Circuit;

public abstract class Analysis
{
    /**
     * The type of analysis (AC, DC, etc.)
     */
    private AnalysisType type;

    public AnalysisType getType()
    {
        return type;
    }

    protected Analysis(AnalysisType type)
    {
        this.type = type;
    }

    /**
     * Apply circuit element stamps from the circuit objects to the
     * "equation" object of this instance of Analysis object.
     */
    public abstract void extractSolvingInfo(Circuit circuit);

    public abstract IAnalysisResults run();
}
