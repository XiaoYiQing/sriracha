package sriracha.simulator.model;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;

/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 03/11/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class NonLinCircuitElement extends CircuitElement{

    protected MathActivator activator = MathActivator.Activator;

    /**
     * Circuit Element Constructor
     *
     * @param name element name from netlist
     */
    protected NonLinCircuitElement(String name) {
        super(name);
    }

    /**
     * Method add the contribution of this instance of non-linear circuit element
     * into the f vector.
     * @param f vector(array) in which the contribution is added.
     * @param inputs the necessary data required to compute the contribution.
     */
    public abstract void getNonLinContribution(IComplexVector f, double ... inputs);


}