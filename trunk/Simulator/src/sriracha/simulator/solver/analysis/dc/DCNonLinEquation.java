package sriracha.simulator.solver.analysis.dc;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.Options;
import sriracha.simulator.model.Circuit;
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
public class DCNonLinEquation extends DCEquation{

    public static final double STD_H = 1e-9;
    public static final double STD_THRESHOLD = 9e-15;
    public static final int STD_DIVERGENCE_TOLERANCE = 5;
    public static final int STD_CONT_METHOD_ATTEMPTS = 3;

    /**
     * Factory object for the Math module's objects.
     */
    private MathActivator activator = MathActivator.Activator;

    private IRealVector f;

    private ArrayList<NonLinCircuitElement> nonLinearElem;
    /**
     * private constructor creating a new DCNonLinEquation object with matrix equation
     * size indicated by circuitNodeCount.
     * @param circuitNodeCount
     */
    public DCNonLinEquation(int circuitNodeCount)
    {
        super(circuitNodeCount);
        f = activator.realVector(circuitNodeCount);

        //Note: the array list initiate with a guessed size of amount of
        //non-linear circuit element. (guessing it as number of nodes)
        nonLinearElem = new ArrayList<NonLinCircuitElement>(circuitNodeCount);
    }

    public void applyNonLinearCircuitElem(NonLinCircuitElement input){
        nonLinearElem.add(input);
    }

    public DCNonLinEquation clone()
    {
        DCNonLinEquation clone = new DCNonLinEquation(this.circuitNodeCount);
        clone.G.copy(this.G);
        clone.b.copy(this.b);
        clone.nonLinearElem = (ArrayList<NonLinCircuitElement>)nonLinearElem.clone();
        return clone;
    }

    /**
     * This method acts as the official constructor of DCNonLinEquation objects.
     * The method apply the stamps of the circuit elements to the matrix equations.
     * The "applyDC" method of circuit elements will call the "applyMatrixStamp" or
     * "applySourceVectorStamp" method of DCEquation class through the elements of
     * the circuit.
     *
     * @param circuit Target circuit object from which circuit elements are obtained.
     *                It is expected to have already been set up with all the
     *                extra variables present.
     * @return
     */
    public static DCEquation generate(Circuit circuit)
    {
        DCNonLinEquation equation = new DCNonLinEquation(circuit.getMatrixSize());

        for (CircuitElement element : circuit.getElements())
        {
            element.applyDC(equation);
        }
        return equation;
    }

    /**
     * Solve for non-linear transient analysis.  Assumes 0 initial
     * conditions.
     * @return
     */
    public IRealVector solve()
    {

        //Note sure about this...
        if (Options.isPrintMatrix())
        {
            System.out.println(G);
            System.out.println("+\n");
            System.out.println(f);
            System.out.println("=\n");
            System.out.println(b);
        }

        //create a new f vector
        int n = b.getDimension();

        return myNewtonRapComp(G, b, nonLinearElem);
    }

    //The following is a temporary method to build up Newton Iteration solver.
    public IRealVector myNewtonRapComp(IRealMatrix G, IRealVector b,
        ArrayList<NonLinCircuitElement> nonLinearElem){

        int size = b.getDimension();

        //The scale factor for the b vector.
        double alpha = 0;
        //Amount of steps toward the final alpha = 1
        int steps = 100;
        //integer indicating whether the continuation method attempt was successful.
        //  (0 = success, -1 = failure)
        int success = -1;
        //integer indicating the amount of failed attempts at continuation method
        int failedAttempts = 0;

        //The node voltage vector (guess)
        IRealVector x0 = activator.realVector(size);
        //The final computed node voltage vector
        IRealVector answer = activator.realVector(size);

        while(success == -1 && failedAttempts < STD_CONT_METHOD_ATTEMPTS){
            //skip 0th step, where the answer is going to be a zero vector.
            for(int i = 0; i < steps; i++){

                alpha += 1.0/steps;

                success = myNewtonRap(G, (b.times(alpha)), nonLinearElem, x0, answer);
                //When divergence occurs, reset all values and chose a larger amounts of
                //steps to aim for a better change of convergence.  Restart continuation method.
                if(success == -1){
                    alpha = 0;
                    steps *= 100;
                    x0.clear();
                    failedAttempts++;
                    break;
                }

                x0.copy(answer);
            }
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

        //dimension of the square matrix
        int n = b.getDimension();

        //flag indicating how many times iteration changes are divergent
        int flag = 0;

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

            //Get the non-linear contribution of the present node voltage guess vector
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

            presentChangeMag = deltaX.getMaxMag();
            if(presentChangeMag < prevChangeMag){
                flag = 0;
                prevChangeMag = presentChangeMag;
            }else{
                flag++;
                //Divergence detected when the largest magnitude of the correcting terms in
                //deltaX increased for some consecutive iterations.
                if(flag > STD_DIVERGENCE_TOLERANCE)
                    return -1;
                prevChangeMag = presentChangeMag;
            }

        }while(presentChangeMag > STD_THRESHOLD);

        answer.copy(x0);
        return 0;
    }

    public ArrayList<NonLinCircuitElement> getNonLinearElem() {
        return nonLinearElem;
    }

    public static void main(String[]args){

        IRealMatrix myG = MathActivator.Activator.realMatrix(3,3);
        myG.setValue(0,0,1);
        myG.setValue(0,1,-1);
        myG.setValue(1,0,-1);
        myG.setValue(1,1,2);
        myG.setValue(2,2,1);

        IRealVector myb = MathActivator.Activator.realVector(3);
        myb.setValue(0,2);

        DCNonLinEquation myEq = new DCNonLinEquation(3);
        myEq.G = myG;
        myEq.b = myb;

        Diode d1 = new Diode("D1",1e-5);
        Diode d2 = new Diode("D2",1e-4);
        Diode d3 = new Diode("D3",1e-6);

        d1.setNodeIndices(1,-1);
        d2.setNodeIndices(0,2);
        d3.setNodeIndices(2,-1);

        //IRealVector myF = MathActivator.Activator.realVector(3);
        //IRealVector myX = MathActivator.Activator.realVector(3);
        //IRealMatrix myJ = MathActivator.Activator.realMatrix(3, 3);
        IRealVector answer = MathActivator.Activator.realVector(3);

        //myX.setValue(0,0.1);
        //myX.setValue(1,0.1);

        myEq.applyNonLinearCircuitElem(d1);
        myEq.applyNonLinearCircuitElem(d2);
        myEq.applyNonLinearCircuitElem(d3);


        //myEq.myNewtonRap(myEq.G, myEq.b, myEq.getNonLinearElem(), myX,answer);

        //answer = myEq.myNewtonRapComp(myEq.G, myEq.b, myEq.getNonLinearElem());
        answer = myEq.solve();
        System.out.println(answer.getValue(0) + " \n" + answer.getValue(1) +
            "\n" + answer.getValue(2));

        /*d1.getNonLinContribution(myF, myX);
        d1.getHessianContribution(myJ, myX);
        System.out.println(myF);
        System.out.println(myJ);*/


    }
}