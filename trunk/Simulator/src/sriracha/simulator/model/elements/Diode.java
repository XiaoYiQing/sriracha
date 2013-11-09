package sriracha.simulator.model.elements;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.NonLinCircuitElement;
import sriracha.simulator.model.models.DiodeModel;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;

/**
 * Diode circuit element using the equation: I = Is*(exp(V/Vt)-1)
 */
public class Diode extends NonLinCircuitElement{

    /**
     * Standard: 25mV
     */
    public static final double STD_VT = 0.025;
    /**
     * Standard saturation current: 1e-14A
     */
    public static final double STD_IS = 0.00000000000001;

    /**
     * Diode's anode
     */
    protected int nodeA;
    /**
     * Diode's cathode
     */
    protected int nodeB;
    private double is;
    private double vt;

    public Diode(String name){
        super(name);
        is = STD_IS;
        vt = STD_VT;
    }


    public Diode(String name, double Is){
        super(name);
        this.is = Is;
        vt = STD_VT;
    }

    public Diode(String name, double is, double vt){
        super(name);
        this.is = is;
        this.vt = vt;
    }


    public Diode(String name, DiodeModel model){
        super(name);
        this.is = model.getIs();
        this.vt = model.getVt();
    }

    @Override
    public void getNonLinContribution(IComplexVector f, IComplexVector x){

        double value = is*(Math.exp((x.getValue(nodeA).minus(x.getValue(nodeB))).getReal()/vt)-1);
        f.addValue(nodeA, activator.complex(value,0));
        f.addValue(nodeB, activator.complex(-value,0));
    }

    @Override
    public void getJacobian(IComplexVector x, IComplexMatrix J){
        double value = is/vt*Math.exp((x.getValue(nodeA).minus(x.getValue(nodeB))).getReal()/vt);

        J.addValue(nodeA, nodeA, value, 0);
        J.addValue(nodeA, nodeB, -value, 0);
        J.addValue(nodeB, nodeA, -value, 0);
        J.addValue(nodeB, nodeB, value, 0);

    }

    @Override
    public void setNodeIndices(int... indices){
        nodeA = indices[0];
        nodeB = indices[1];
    }

    @Override
    protected int[] getNodeIndices() {
        return new int[]{nodeA, nodeB};
    }

    @Override
    public int getNodeCount() {
        return 2;
    }

    @Override
    public int getExtraVariableCount() {
        return 0;
    }

    @Override
    protected CircuitElement buildCopy(String name, CircuitElement referencedElement) {
        return new Diode(this.name, this.is, this.vt);
    }

    @Override
    public void applyDC(DCEquation equation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void applyAC(ACEquation equation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + is + " " + vt;
    }


    public double getVt() {
        return vt;
    }

    public double getIs() {
        return is;
    }
}
