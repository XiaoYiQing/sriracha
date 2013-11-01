package sriracha.simulator.solver.analysis.dc;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.simulator.Options;

/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 31/10/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DCNonLinEquation {
    /**
     * Factory object for the Math module's objects.
     */
    private MathActivator activator = MathActivator.Activator;

    private int circuitNodeCount;


    private IRealMatrix C;

    private IComplexMatrix G;

    private IComplexVector b;

    private IComplexVector f;

    /**
     * private constructor creating a new DCNonLinEquation object with matrix equation
     * size indicated by circuitNodeCount.
     * @param circuitNodeCount
     */
    private DCNonLinEquation(int circuitNodeCount)
    {
        this.circuitNodeCount = circuitNodeCount;
        C = activator.realMatrix(circuitNodeCount, circuitNodeCount);
        G = activator.complexMatrix(circuitNodeCount, circuitNodeCount);
        b = activator.complexVector(circuitNodeCount);
        f = activator.complexVector(circuitNodeCount);
    }

    /**
     *
     * @param h
     * @return
     */
    IComplexVector solve(double h)
    {

        return null;
    }

}
