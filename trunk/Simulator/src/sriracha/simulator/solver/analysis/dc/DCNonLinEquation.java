package sriracha.simulator.solver.analysis.dc;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.simulator.Options;
import sriracha.simulator.model.CircuitElement;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 31/10/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DCNonLinEquation {

    public static final double STD_H = 1e-9;

    /**
     * Factory object for the Math module's objects.
     */
    private MathActivator activator = MathActivator.Activator;

    private int circuitNodeCount;


    private IComplexMatrix C;

    private IComplexMatrix G;

    private IComplexVector b;

    private IComplexVector f;

    private ArrayList<CircuitElement> nonLinearElem;
    /**
     * private constructor creating a new DCNonLinEquation object with matrix equation
     * size indicated by circuitNodeCount.
     * @param circuitNodeCount
     */
    private DCNonLinEquation(int circuitNodeCount)
    {
        this.circuitNodeCount = circuitNodeCount;
        C = activator.complexMatrix(circuitNodeCount, circuitNodeCount);
        G = activator.complexMatrix(circuitNodeCount, circuitNodeCount);
        b = activator.complexVector(circuitNodeCount);
        f = activator.complexVector(circuitNodeCount);

        //Note: the array list initiate with a guessed size of amount of
        //non-linear circuit element. (guessing it as number of nodes)
        nonLinearElem = new ArrayList<CircuitElement>(circuitNodeCount);
    }

    public void applyNonLinearCircuitElem(CircuitElement input){
        nonLinearElem.add(input);
    }

    /**
     * Apply complex matrix stamp value to the complex matrix equation.
     * Used by circuit elements.
     * @param i x matrix coordinate
     * @param j y matrix coordinate
     * @param value
     */
    public void applyComplexMatrixStamp(int i, int j, double value)
    {
        //no stamps to ground
        if (i == -1 || j == -1) return;

        if (value != 0)
        {
            G.addValue(i, j, activator.complex(0, value));
        }
    }

    /**
     * Apply real matrix stamp value to the real matrix equation.
     * @param i x matrix coordinate
     * @param j y matrix coordinate
     * @param value
     */
    public void applyRealMatrixStamp(int i, int j, double value)
    {
        //no stamps to ground
        if (i == -1 || j == -1) return;

        if (value != 0)
        {
            C.addValue(i, j, activator.complex(value, 0));
        }
    }

    public void applySourceVectorStamp(int i, IComplex d)
    {
        //no stamps to ground
        if (i == -1) return;

        b.addValue(i, d);
    }

    /**
     * Solve for non-linear transient analysis.  Assumes 0 initial
     * conditions.
     * @return
     */
    IComplexVector solve()
    {




        return null;
    }



}
