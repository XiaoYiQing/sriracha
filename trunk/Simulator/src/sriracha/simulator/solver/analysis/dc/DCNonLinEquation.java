package sriracha.simulator.solver.analysis.dc;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.simulator.Options;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.NonLinCircuitElement;
import sriracha.simulator.model.elements.Diode;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 31/10/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DCNonLinEquation {

    public static final double STD_H = 1e-9;
    public static final double STD_THRESHOLD = 1e-7;

    /**
     * Factory object for the Math module's objects.
     */
    private MathActivator activator = MathActivator.Activator;

    private int circuitNodeCount;


    private IComplexMatrix C;

    private IComplexMatrix G;

    private IComplexVector b;

    private IComplexVector f;

    private ArrayList<NonLinCircuitElement> nonLinearElem;
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
        nonLinearElem = new ArrayList<NonLinCircuitElement>(circuitNodeCount);
    }

    public void applyNonLinearCircuitElem(NonLinCircuitElement input){
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
        //create a new f vector
        int n = f.getDimension();
        f = activator.complexVector(n);

        double h = STD_H;
        double deltaX;

        IComplexMatrix G1;
        IComplexVector b1;

        //Initial guess is a all 0 vector.
        IComplexVector x = activator.complexVector(n);
        NonLinCircuitElement nextElem;

        do{
            //G1 = G + C/h
            G1 = (IComplexMatrix)C.plus(G.times(1/h));
            //b1 = (1/h)*C*x + b
            b1 = (IComplexVector)(((C.times(x)).times(1/h)).plus(b));
            deltaX = 1;

            while(deltaX > STD_THRESHOLD){
                Iterator iter = nonLinearElem.iterator();
                while(iter.hasNext()){
                    nextElem = (NonLinCircuitElement)iter.next();
                }
            }


        }while(f.getMax().getMag()>1e-15);

        return null;
    }

    //The following is a temporary method to build up Newton Iteration solver.

    /**
     * A Newton Raphson iteration method which is taylored to solve
     * the non-linear case.
     * @param G actually: (G+C/h)
     * @param b actually: (C/h*x(n) + b(n+1))
     * @param x0 initial guess of node voltages
     * @return
     */
    public IComplexVector myNewtonRap(IComplexMatrix G, IComplexVector b, IComplexVector x0)
    {

        /*
        * phi(x) = Gx + f(x) - b
        * d(phi(x))/dx = G + df(x)/dx
        *
        * */

        int n = b.getDimension();
        //initial guess

        IComplexVector f0 = activator.complexVector(n);
        IComplexMatrix df0 = activator.complexMatrix(n,n);

        for(int i = 0; i < nonLinearElem.size(); i++){
            nonLinearElem.get(i).getNonLinContribution(f0,x0);
            nonLinearElem.get(i).getHessianContribution(df0,x0);
        }

        //phi(x) = Gx + f(x) - b
        IComplexVector phi = (IComplexVector)((G.times(x0)).plus(f0)).minus(b);
        //d(phi(x))/dx = G + df(x)/dx
        IComplexMatrix J = (IComplexMatrix)G.plus(df0);

        J.inverse();

        //deltaX = -J' * phi(x)
        IComplexVector deltaX = (IComplexVector)J.times(phi).times((-1));

        if(deltaX.getMax().getMag() > STD_THRESHOLD){
            x0 = (IComplexVector)x0.plus(deltaX);
            return myNewtonRap(G, b, x0);
        }else{
            return (IComplexVector)x0.plus(deltaX);
        }
    }

    public static void main(String[]args){

        IComplexMatrix myG = MathActivator.Activator.complexMatrix(2,2);
        myG.setValue(0,0,1,0);
        myG.setValue(0,1,-1,0);
        myG.setValue(1,0,-1,0);
        myG.setValue(1,1,1,0);

        IComplexVector myb = MathActivator.Activator.complexVector(2);
        myb.setValue(0,1,0);

        DCNonLinEquation myEq = new DCNonLinEquation(2);
        myEq.G = myG;
        myEq.b = myb;

        Diode d1 = new Diode("D1",1);
        Diode d2 = new Diode("D2",1);
        d1.setNodeIndices(0,-1);
        d2.setNodeIndices(1,-1);

        IComplexVector myF = MathActivator.Activator.complexVector(2);
        IComplexVector myX = MathActivator.Activator.complexVector(2);
        IComplexMatrix myJ = MathActivator.Activator.complexMatrix(2,2);

        myX.setValue(0,0.1,0);
        myX.setValue(1,0.1,0);

        myEq.applyNonLinearCircuitElem(d1);
        myEq.applyNonLinearCircuitElem(d2);

        System.out.println(myEq.myNewtonRap(myEq.G,myEq.b,myX));

        /*d1.getNonLinContribution(myF, myX);
        d1.getHessianContribution(myJ, myX);
        System.out.println(myF);
        System.out.println(myJ);*/


    }
}