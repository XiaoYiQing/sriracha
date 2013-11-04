package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.vector.ComplexVector;
import org.jscience.mathematics.vector.Vector;
import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IVector;

import java.text.DecimalFormat;

class JsComplexVector extends JsVector implements IComplexVector
{

    public JsComplexVector(int dimension)
    {
        vector = buildZeroVector(dimension);
    }

    JsComplexVector(ComplexVector vector)
    {
        this.vector = vector;
    }

    JsComplexVector(Vector<Complex> vector)
    {
        this.vector = ComplexVector.valueOf(vector);
    }


    private ComplexVector buildZeroVector(int dim)
    {
        Complex arr[] = new Complex[dim];
        for (int i = 0; i < dim; i++)
        {
            arr[i] = Complex.ZERO;
        }

        return ComplexVector.valueOf(arr);
    }


    @Override
    public IVector times(double d)
    {
        return new JsComplexVector(getVector().times(Complex.valueOf(d, 0)));
    }

    @Override
    public IVector opposite()
    {
        return new JsComplexVector(getVector().opposite());
    }

    @Override
    public IVector plus(IVector v)
    {
        if (v instanceof JsComplexVector)
        {
            return new JsComplexVector(((JsComplexVector) v).getVector().plus(getVector()));
        } else if (v instanceof JsRealVector)
        {
            return plus(makeComplex((JsRealVector) v));
        }
        return null;
    }

    @Override
    ComplexVector getVector()
    {
        return (ComplexVector) vector;
    }

    @Override
    public IComplex getValue(int i)
    {
        return new JsComplex(((ComplexVector) vector).get(i));
    }

    @Override
    public IComplex getMax(){
        ComplexVector myVec = (ComplexVector)vector;
        int d = myVec.getDimension();

        double temp = 0;
        double max = 0;
        int maxIndex = 0;

        for(int i = 0; i < d; i++){
            temp = myVec.get(i).magnitude();
            if(temp > max){
                max = temp;
            }
        }

        return new JsComplex(myVec.get(maxIndex).getReal(),
                myVec.get(maxIndex).getImaginary()) {
        };
    }

    @Override
    public void setValue(int i, IComplex value)
    {
        getVector().set(i, JsComplex.make(value));
    }

    @Override
    public void addValue(int i, IComplex value)
    {

        getVector().set(i, getVector().get(i).plus(JsComplex.make(value)));
    }

    @Override
    public String toString()
    {
        DecimalFormat format = new DecimalFormat("0.000E00");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vector.getDimension(); i++)
        {
            Complex val = getVector().get(i);
            sb.append(format.format(val.getReal()));
            sb.append(" + ");
            sb.append(format.format(val.getImaginary()));
            sb.append("j\n");
        }
        return sb.toString();
    }

    @Override
    public IVector clone()
    {
        return new JsComplexVector(ComplexVector.valueOf(vector.copy()));
    }
}
