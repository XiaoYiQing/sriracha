package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.vector.ComplexMatrix;
import sriracha.math.interfaces.*;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;


class JsComplexMatrix extends JsMatrix implements IComplexMatrix
{


    /**
     * Creates an mxn Complex matrix
     */
    public JsComplexMatrix(int m, int n)
    {
        matrix = buildZeroMatrix(m, n);
    }

    JsComplexMatrix(ComplexMatrix m)
    {
        matrix = m;
    }

    ComplexMatrix getMatrix()
    {
        return (ComplexMatrix) matrix;
    }

    private ComplexMatrix buildZeroMatrix(int m, int n)
    {
        Complex arr[][] = new Complex[m][n];
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                arr[i][j] = Complex.ZERO;
            }
        }

        return ComplexMatrix.valueOf(arr);
    }


    @Override
    public IComplex getValue(int i, int j)
    {
        return new JsComplex(getMatrix().get(i, j));
    }

    @Override
    public void setValue(int i, int j, IComplex value)
    {
        matrix.set(i, j, JsComplex.make(value));
    }

    @Override
    public void setValue(int i, int j, double real, double complex){
        matrix.set(i,j, Complex.valueOf(real, complex));
    }

    @Override
    public void addValue(int i, int j, IComplex value)
    {
        Complex previousValue = (Complex) matrix.get(i, j);
        matrix.set(i, j, previousValue.plus(JsComplex.make(value)));
    }

    @Override
    public void addValue(int i, int j, double real, double complex)
    {
        matrix.set(i, j, ((Complex)matrix.get(i,j)).plus(Complex.valueOf(real, complex)));
    }

    @Override
    public IComplexVector solve(IVector b)
    {
        if (b instanceof JsRealVector)
        {
            JsComplexVector v = JsVector.makeComplex((JsRealVector) b);
            return new JsComplexVector(getMatrix().solve(v.getVector()));
        } else
        {
            return new JsComplexVector(getMatrix().solve(((JsComplexVector) b).getVector()));
        }
    }

    @Override
    public IComplex getMax() {
        ComplexMatrix myMat = (ComplexMatrix) matrix;
        int m = myMat.getNumberOfRows();
        int n = myMat.getNumberOfColumns();
        double temp = 0;

        double max = 0;
        int maxRow = 0;
        int maxCol = 0;

        for(int i = 0; i < m; i++){
         for(int j = 0; j < n; j++){
             temp = myMat.get(i,j).magnitude();
             if(temp > max){
                 max = temp;
                 maxRow = i;
                 maxCol = j;
             }
         }
        }

        return new JsComplex(myMat.get(maxRow, maxCol).getReal(),
                myMat.get(maxRow, maxCol).getImaginary()) {
        };
    }

    @Override
    public void clear()
    {
        for(int i = 0; i < matrix.getNumberOfRows(); i++){
            for(int j = 0; j < matrix.getNumberOfRows(); j++){
                matrix.set(i,j,Complex.valueOf(0,0));
            }
        }
    }

    @Override
    public void copy(IComplexMatrix target){
        if((target.getNumberOfRows()==this.getNumberOfRows()&&
                (target.getNumberOfColumns()==this.getNumberOfColumns())))

            for(int i = 0; i < target.getNumberOfRows(); i++){
                for(int j = 0; j < target.getNumberOfColumns(); j++){
                    this.setValue(i,j,target.getValue(i,j));
                }
            }
        else
            System.out.println("Error: unmatched matrix size for copying.  No change applied.");
    }

    @Override
    public int getNumberOfRows(){
        return matrix.getNumberOfRows();
    }

    @Override
    public int getNumberOfColumns(){
        return matrix.getNumberOfColumns();
    }

    @Override
    public IMatrix clone()
    {
        return new JsComplexMatrix(getMatrix().copy());
    }


    @Override
    public String toString()
    {
        DecimalFormat format = new DecimalFormat("0.000E00");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getMatrix().getNumberOfRows(); i++)
        {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++)
            {
                Complex val = getMatrix().get(i, j);

                sb.append(format.format(val.getReal()));
                sb.append(" + ");
                sb.append(format.format(val.getImaginary()));
                sb.append("j\t");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public void inverse(){
        matrix = matrix.inverse();
    }

}
