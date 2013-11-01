package sriracha.simulator.model.elements.sources;

import sriracha.math.interfaces.IComplex;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.solver.analysis.dc.DCEquation;

/**
 * base class for all normal sources, Not Controlled sources
 */
public abstract class Source extends CircuitElement
{

    /**
     * Node indices for source
     * for current source: Current flows from nPlus, through source, out nMinus
     */
    protected int nPlus, nMinus;


    protected double dcValue;

    protected IComplex acPhasorValue;


    /**
     * Constructor initializing name, dc and complex ac values.
     * @param name element name from netlist.
     * @param dcValue the new dc value for the source.
     * @param acPhasorValue the new ac value for the source.
     */
    protected Source(String name, double dcValue, IComplex acPhasorValue)
    {
        super(name);
        this.dcValue = dcValue;
        this.acPhasorValue = acPhasorValue;
    }


    /**
     * When performing DC sweep for this source this method is called
     * to change its stamp in the equation with this new dc value. it is assumed that
     * the passed equation was stamped with the sources default value, and not the previous one
     * in the sweep since the source does not know what that was.
     *
     * @param newDCValue - new DC value for source
     * @param equation   - original equation.
     */
    public abstract void modifyStamp(double newDCValue, DCEquation equation);

    /**
     * Set the indices that correspond to the circuit element's nodes.
     * The nodes are assumed to be in the order they are in the netlist.
     * (-1 is always ground)
     *
     * @param indices the ordered node indices
     */
    @Override
    public void setNodeIndices(int... indices)
    {
        nPlus = indices[0];
        nMinus = indices[1];

    }
}
