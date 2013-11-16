package sriracha.simulator.solver.analysis.dc;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.Options;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.CircuitElement;

public class DCEquation
{


    private IRealMatrix C;

    private IRealVector b;


    private DCEquation(int nodeCount)
    {
        C = MathActivator.Activator.realMatrix(nodeCount, nodeCount);
        b = MathActivator.Activator.realVector(nodeCount);
    }

    private DCEquation(IRealMatrix c, IRealVector b)
    {
        this.C = c;
        this.b = b;
    }

    /**
     * This method acts as the official constructor of DCEquation objects.
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
        DCEquation equation = new DCEquation(circuit.getMatrixSize());

        for (CircuitElement element : circuit.getElements())
        {
            element.applyDC(equation);
        }

        return equation;

    }

    public IRealVector solve()
    {

        if (Options.isPrintMatrix())
        {
            System.out.println(C);
            System.out.println("=\n");
            System.out.println(b);
        }

        return C.solve(b);
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

    public DCEquation clone()
    {
        return new DCEquation(C.clone(), b.clone());
    }




}
