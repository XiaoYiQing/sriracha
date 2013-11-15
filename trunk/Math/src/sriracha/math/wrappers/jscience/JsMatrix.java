package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.ComplexMatrix;
import org.jscience.mathematics.vector.ComplexVector;
import org.jscience.mathematics.vector.Float64Matrix;
import org.jscience.mathematics.vector.Matrix;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IMatrix;
import sriracha.math.interfaces.IVector;

import java.util.List;

/**
 * Classes inheriting JsMatrix class are wrapper class
 * of the jscience library's Matrix class.
 */
abstract class JsMatrix implements IMatrix
{

    protected Matrix matrix;

    protected static JsComplexMatrix makeComplex(JsRealMatrix matrix)
    {
        Float64Matrix realMatrix = (Float64Matrix) matrix.matrix;
        int m = realMatrix.getRow(0).getDimension();
        int n = realMatrix.getColumn(0).getDimension();

        Complex[][] values = new Complex[m][n];

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                values[i][j] = Complex.valueOf(realMatrix.get(i, j).doubleValue(), 0);
            }
        }

        return new JsComplexMatrix(ComplexMatrix.valueOf(values));
    }

    @Override
    public IMatrix plus(IMatrix m)
    {
        if (m instanceof JsMatrix)
        {

            if (m instanceof JsComplexMatrix || this instanceof JsComplexMatrix)
            {

                JsComplexMatrix thiscomp = (this instanceof JsComplexMatrix) ? (JsComplexMatrix) this : makeComplex((JsRealMatrix) this);
                JsComplexMatrix thatcomp = (m instanceof JsComplexMatrix) ? (JsComplexMatrix) m : makeComplex((JsRealMatrix) m);

                ComplexMatrix r = thiscomp.getMatrix().plus(thatcomp.getMatrix());

                return new JsComplexMatrix(r);

            } else
            {
                return new JsRealMatrix(((JsRealMatrix) this).getMatrix().plus(((JsRealMatrix) m).getMatrix()));
            }
        }

        return null;
    }

    @Override
    public IMatrix minus(IMatrix m)
    {
        if (m instanceof JsMatrix)
        {

            if (m instanceof JsComplexMatrix || this instanceof JsComplexMatrix)
            {
                return new JsComplexMatrix(ComplexMatrix.valueOf(matrix.minus(((JsMatrix) m).matrix)));
            } else
            {
                return new JsRealMatrix(Float64Matrix.valueOf(matrix.minus(((JsMatrix) m).matrix)));
            }
        }

        return null;
    }

    @Override
    public IMatrix times(IMatrix m)
    {
        if (m instanceof JsMatrix)
        {

            if (m instanceof JsComplexMatrix || this instanceof JsComplexMatrix)
            {
                return new JsComplexMatrix(ComplexMatrix.valueOf(matrix.times(((JsMatrix) m).matrix)));
            } else
            {
                return new JsRealMatrix(Float64Matrix.valueOf(matrix.times(((JsMatrix) m).matrix)));
            }
        }

        return null;
    }

    public IVector times(IVector v)
    {
        if (this instanceof JsMatrix)
        {

            if (this instanceof JsComplexMatrix || this instanceof JsComplexMatrix)
            {
                return new JsComplexVector(this.matrix.times(v.getVector()));
            } else
            {
                return new JsRealVector(this.matrix.times(v.getVector()));
            }
        }

        return null;
    }

    @Override
    public IMatrix times(double n)
    {
        if (this instanceof JsComplexMatrix)
        {
            return new JsComplexMatrix(((ComplexMatrix) matrix).times(Complex.valueOf(n, 0)));
        } else
        {
            return new JsRealMatrix(((Float64Matrix) matrix).times(Float64.valueOf(n)));
        }
    }

    @Override
    public int getNumberOfRows(){
        return matrix.getNumberOfRows();
    }
    @Override
    public int getNumberOfColumns(){
        return matrix.getNumberOfColumns();
    }

    public void inverse(){
        matrix = matrix.inverse();
    }

    public void transpose(){
        matrix = matrix.transpose();
    }

    public boolean sameSize(IMatrix target){
        return (matrix.getNumberOfRows()==target.getNumberOfRows())&&
                (matrix.getNumberOfColumns()==target.getNumberOfColumns());
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix.getNumberOfRows(); i++)
        {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++)
            {

                sb.append(matrix.get(i, j) + "\t");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public abstract IMatrix clone();
}
