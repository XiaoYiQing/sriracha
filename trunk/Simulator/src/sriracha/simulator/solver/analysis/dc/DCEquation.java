package sriracha.simulator.solver.analysis.dc;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.Options;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.CircuitElement;

public class DCEquation
{

    protected IRealMatrix G;

    protected IRealMatrix C;

    protected IRealVector b;

    protected MathActivator activator = MathActivator.Activator;

    protected int circuitNodeCount;

    protected DCEquation(int nodeCount)
    {
        this.circuitNodeCount = nodeCount;
        G = activator.realMatrix(nodeCount, nodeCount);
        C = activator.realMatrix(nodeCount, nodeCount);
        b = activator.realVector(nodeCount);
    }

    protected DCEquation(IRealMatrix G, IRealMatrix C, IRealVector b)
    {
        this.G.copy(G);
        this.C.copy(C);
        this.b.copy(b);
    }

    /**
     * This method acts as the official constructor of DCEquation objects.
     * The method apply the stamps of the circuit elements to the matrix equations.
     * The "applyDC" method of circuit elements will call the "applyRealMatrixStamp" or
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
            System.out.println(G);
            System.out.println("=\n");
            System.out.println(b);
        }

        return G.solve(b);
    }

    /**
     * Apply stamp value to the matrix equation.
     * @param i x matrix coordinate
     * @param j y matrix coordinate
     * @param value
     */
    public void applyRealMatrixStamp(int i, int j, double value)
    {

        //no stamps to ground
        if (i == -1 || j == -1) return;

        if (value != 0)
            G.addValue(i, j, value);


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

        else if(value != 0)
            C.addValue(i, j, value);
    }

    public void applySourceVectorStamp(int i, double d)
    {
        //no stamps to ground
        if (i == -1) return;

        else if(d != 0)
            b.addValue(i, d);
    }

    public DCEquation clone()
    {
        return new DCEquation(G.clone(), C.clone(), b.clone());
    }




}
