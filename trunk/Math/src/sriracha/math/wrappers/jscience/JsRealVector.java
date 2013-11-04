package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.Float64Vector;
import org.jscience.mathematics.vector.Vector;
import sriracha.math.interfaces.IRealVector;
import sriracha.math.interfaces.IVector;

import java.text.DecimalFormat;

class JsRealVector extends JsVector implements IRealVector
{


    public JsRealVector(int dimension)
    {
        vector = Float64Vector.valueOf(new double[dimension]);
    }

    JsRealVector(Float64Vector vector)
    {
        this.vector = vector;
    }

    JsRealVector(Vector<Float64> vector)
    {
        this.vector = Float64Vector.valueOf(vector);
    }

    @Override
    public IVector opposite()
    {
        return new JsRealVector(getVector().opposite());
    }

    @Override
    public IVector minus(IVector vector)
    {
        return plus(vector.opposite());
    }

    @Override
    public String toString()
    {
        DecimalFormat format = new DecimalFormat("+0.0000;-0.0000");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vector.getDimension(); i++)
        {
            sb.append(format.format(getVector().get(i)) + "\t");
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public IVector plus(IVector v)
    {
        if (v instanceof JsRealVector)
        {
            return new JsRealVector(((JsRealVector) v).vector.plus(vector));
        } else if (v instanceof JsComplexVector)
        {
            return new JsComplexVector(makeComplex(this).getVector().plus(((JsComplexVector) v).getVector()));
        }

        return null;


    }

    @Override
    public IRealVector times(double d)
    {
        return new JsRealVector(getVector().times(d));
    }

    @Override
    public double getValue(int i)
    {
        return getVector().getValue(i);
    }

    @Override
    public void setValue(int i, double value)
    {
        getVector().set(i, Float64.valueOf(value));
    }

    @Override
    public void addValue(int i, double value)
    {
        setValue(i, getValue(i) + value);
    }

    @Override
    public IRealVector clone()
    {
        return new JsRealVector(Float64Vector.valueOf(vector.copy()));
    }

    public Float64Vector getVector()
    {
        return (Float64Vector) vector;
    }
}
