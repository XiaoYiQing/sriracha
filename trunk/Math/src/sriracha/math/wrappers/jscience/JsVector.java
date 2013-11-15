package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.vector.ComplexVector;
import org.jscience.mathematics.vector.Float64Vector;
import org.jscience.mathematics.vector.Vector;
import sriracha.math.interfaces.IVector;

abstract class JsVector implements IVector
{

    protected Vector vector;

    JsVector(Vector vector)
    {
        this.vector = vector;
    }

    JsVector()
    {
    }

    public Vector getVector()
    {
        return vector;
    }


    protected static JsComplexVector makeComplex(JsRealVector vector)
    {
        Float64Vector realVector = vector.getVector();
        int dim = realVector.getDimension();

        Complex[] values = new Complex[dim];

        for (int i = 0; i < dim; i++)
        {
            values[i] = Complex.valueOf(realVector.get(i).doubleValue(), 0);
        }

        return new JsComplexVector(ComplexVector.valueOf(values));
    }


    @Override
    public int getDimension()
    {
        return vector.getDimension();
    }


    @Override
    public IVector minus(IVector vector)
    {
        return plus(vector.opposite());
    }

    public boolean sameSize(IVector target){
        return target.getDimension() == this.getDimension();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vector.getDimension(); i++)
        {
            sb.append(getVector().get(i) + "\t");
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public abstract IVector clone();
}
