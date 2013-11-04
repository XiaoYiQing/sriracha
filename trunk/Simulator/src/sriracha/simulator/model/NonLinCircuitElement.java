package sriracha.simulator.model;

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


    /**
     * Circuit Element Constructor
     *
     * @param name element name from netlist
     */
    protected NonLinCircuitElement(String name) {
        super(name);
    }

    public abstract double getNonLinContribution(double ... inputs);


}
