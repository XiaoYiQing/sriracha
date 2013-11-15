package sriracha.simulator.solver.analysis.dc;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
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
    public static final double STD_THRESHOLD = 1e-4;

    /**
     * Factory object for the Math module's objects.
     */
    private MathActivator activator = MathActivator.Activator;

    private int circuitNodeCount;


    private IRealMatrix C;

    private IRealMatrix G;

    private IRealVector b;

    private IRealVector f;

    private ArrayList<NonLinCircuitElement> nonLinearElem;
    /**
     * private constructor creating a new DCNonLinEquation object with matrix equation
     * size indicated by circuitNodeCount.
     * @param circuitNodeCount
     */
    private DCNonLinEquation(int circuitNodeCount)
    {
        this.circuitNodeCount = circuitNodeCount;
        C = activator.realMatrix(circuitNodeCount, circuitNodeCount);
        G = activator.realMatrix(circuitNodeCount, circuitNodeCount);
        b = activator.realVector(circuitNodeCount);
        f = activator.realVector(circuitNodeCount);

        //Note: the array list initiate with a guessed size of amount of
        //non-linear circuit element. (guessing it as number of nodes)
        nonLinearElem = new ArrayList<NonLinCircuitElement>(circuitNodeCount);
    }

    public void applyNonLinearCircuitElem(NonLinCircuitElement input){
        nonLinearElem.add(input);
    }

    /**
     * Apply stamp value to the matrix equation.
     * @param i x matrix coordinate
     * @param j y matrix coordinate
     * @param value
     */
    public void applyMatrixStamp(int i, int j, double value)
    {

        //no stamps to ground
        if (i == -1 || j == -1) return;

        if (value != 0)
            C.addValue(i, j, value);


    }

    public void applySourceVectorStamp(int i, double d)
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
    IRealVector solve()
    {
        //create a new f vector
        int n = f.getDimension();
        f = activator.realVector(n);

        double h = STD_H;
        double deltaX;

        IRealMatrix G1;
        IRealVector b1;

        //Initial guess is a all 0 vector.
        IRealVector x = activator.realVector(n);
        NonLinCircuitElement nextElem;

        do{
            //G1 = G + C/h
            G1 = (IRealMatrix)C.plus(G.times(1/h));
            //b1 = (1/h)*C*x + b
            b1 = (IRealVector)(((C.times(x)).times(1/h)).plus(b));
            deltaX = 1;

            while(deltaX > STD_THRESHOLD){
                Iterator iter = nonLinearElem.iterator();
                while(iter.hasNext()){
                    nextElem = (NonLinCircuitElement)iter.next();
                }
            }


        }while(f.getMax()>1e-15);

        return null;
    }

    //The following is a temporary method to build up Newton Iteration solver.
    public IRealVector myNewtonRapComp(IRealMatrix G, IRealVector b,
        ArrayList<NonLinCircuitElement> nonLinearElem){

        int size = b.getDimension();

        //The scale factor for the b vector.
        double alpha = 0;
        //
        int steps = 10;


        IRealVector x0 = activator.realVector(size);
        IRealVector answer = activator.realVector(size);

        //x0.setValue(0,0.1);
        //x0.setValue(1,0.1);

        //skip 0th step, where the answer is going to be a zero vector.
        for(int i = 0; i < steps; i++){

            alpha += 1.0/steps;

            myNewtonRap(G, (b.times(alpha)), nonLinearElem, x0, answer);

            System.out.println("At iteration " + i + ", with alpha = " + alpha +", answer:");
            System.out.println(answer.getValue(0) + "\n" + answer.getValue(1)+"\n");

            x0.copy(answer);
        }

        return answer;
    }

    /**
     * A Newton Raphson iteration method which is taylored to solve
     * the non-linear case.
     * @param G
     * @param b
     * @param xGuess initial guess of node voltages
     * @param answer the vector in which the final result is stored
     * @return signal variable indicating whether the N-R iter converged or not.
     *      (-1 = divergent, 0 = convergent)
     */
    public int myNewtonRap(IRealMatrix G, IRealVector b,
        ArrayList<NonLinCircuitElement> nonLinearElem, IRealVector xGuess, IRealVector answer)
    {
        /*
        * phi(x) = Gx + f(x) - b
        * d(phi(x))/dx = G + df(x)/dx
        * */

        int n = b.getDimension();

        double prevChangeMag = Integer.MAX_VALUE;
        double presentChangeMag;

        //initial guess
        IRealVector x0 = activator.realVector(n);
        x0.copy(xGuess);
        //The non-linear element contribution vector
        IRealVector f0 = activator.realVector(n);
        //The Hessian matrix
        IRealMatrix df0 = activator.realMatrix(n,n);

        IRealVector deltaX = activator.realVector(n);
        IRealVector phi = activator.realVector(n);
        //The Jacobian matrix
        IRealMatrix J = activator.realMatrix(n,n);

        do{

            f0.clear();
            df0.clear();

            for(int i = 0; i < nonLinearElem.size(); i++){
                nonLinearElem.get(i).getNonLinContribution(f0,x0);
                nonLinearElem.get(i).getHessianContribution(df0,x0);
            }

            //phi(x) = Gx + f(x) - b
            phi.copy((IRealVector)((G.times(x0)).plus(f0)).minus(b));
            //d(phi(x))/dx = G + df(x)/dx
            J.copy((IRealMatrix)G.plus(df0));


            J.inverse();

            //deltaX = -J' * phi(x)
            deltaX.copy((IRealVector)J.times(phi).times((-1)));
            x0 = (IRealVector)x0.plus(deltaX);

            presentChangeMag = Math.abs(deltaX.getMaxMag());
            if(presentChangeMag < prevChangeMag){
                prevChangeMag = presentChangeMag;

                System.out.println("presentChangeMag: " + presentChangeMag);
            }else{
                //return -1;
            }

        }while(deltaX.getMaxMag() > STD_THRESHOLD);

        answer.copy(x0);
        return 0;
    }

    public ArrayList<NonLinCircuitElement> getNonLinearElem() {
        return nonLinearElem;
    }

    public static void main(String[]args){

        IRealMatrix myG = MathActivator.Activator.realMatrix(2,2);
        myG.setValue(0,0,1);
        myG.setValue(0,1,-1);
        myG.setValue(1,0,-1);
        myG.setValue(1,1,1);

        IRealVector myb = MathActivator.Activator.realVector(2);
        myb.setValue(0,1);

        DCNonLinEquation myEq = new DCNonLinEquation(2);
        myEq.G = myG;
        myEq.b = myb;

        Diode d1 = new Diode("D1",1);
        Diode d2 = new Diode("D2",1);
        d1.setNodeIndices(0,-1);
        d2.setNodeIndices(1,-1);

        IRealVector myF = MathActivator.Activator.realVector(2);
        IRealVector myX = MathActivator.Activator.realVector(2);
        IRealMatrix myJ = MathActivator.Activator.realMatrix(2, 2);
        IRealVector answer = MathActivator.Activator.realVector(2);

        myX.setValue(0,0.1);
        myX.setValue(1,0.1);

        myEq.applyNonLinearCircuitElem(d1);
        myEq.applyNonLinearCircuitElem(d2);

        //myEq.myNewtonRap(myEq.G, myEq.b, myEq.getNonLinearElem(), myX,answer);

        answer = myEq.myNewtonRapComp(myEq.G, myEq.b, myEq.getNonLinearElem());
        System.out.println(answer.getValue(0) + " \n" + answer.getValue(1));

        /*d1.getNonLinContribution(myF, myX);
        d1.getHessianContribution(myJ, myX);
        System.out.println(myF);
        System.out.println(myJ);*/


    }
}