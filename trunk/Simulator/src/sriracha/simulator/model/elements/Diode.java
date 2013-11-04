package sriracha.simulator.model.elements;

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

    protected int nodeA, nodeB;
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


    public Diode(DiodeModel model){
        super(model.getName());
        this.is = model.getIs();
        this.vt = model.getVt();
    }


    public double getNonLinContribution(double... v){

        return is*(Math.exp(v[0]/vt)-1);
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
