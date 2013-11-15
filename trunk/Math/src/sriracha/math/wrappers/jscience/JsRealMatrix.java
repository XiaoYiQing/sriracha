package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.vector.Float64Matrix;
import sriracha.math.interfaces.*;

import java.text.DecimalFormat;

class JsRealMatrix extends JsMatrix implements IRealMatrix
{


    public JsRealMatrix(int m, int n)
    {
        matrix = Float64Matrix.valueOf(new double[m][n]);
    }

    JsRealMatrix(Float64Matrix m)
    {
        matrix = m;
    }


    Float64Matrix getMatrix()
    {
        return (Float64Matrix) matrix;
    }


    public IRealVector solve(IRealVector vector)
    {
        if (vector instanceof JsRealVector)
        {
            return new JsRealVector(getMatrix().solve(((JsRealVector) vector).getVector()));
        }

        return null;
    }

    public IComplexVector solve(IComplexVector vector)
    {
        if (vector instanceof JsComplexVector)
        {
            return new JsComplexVector(makeComplex(this).getMatrix().solve(((JsComplexVector) vector).getVector()));
        }
        return null;
    }


    @Override
    public IVector solve(IVector b)
    {
        if (b instanceof IComplexVector)
        {
            return solve((IComplexVector) b);
        } else if (b instanceof IRealVector)
        {
            return solve((IRealVector) b);
        }

        return null;
    }

    @Override
    public double getValue(int i, int j)
    {
        return getMatrix().get(i, j).doubleValue();
    }

    @Override
    public void setValue(int i, int j, double value)
    {
        getMatrix().set(i, j, Float64.valueOf(value));
    }

    @Override
    public void addValue(int i, int j, double value)
    {
        Float64 previousValue = getMatrix().get(i, j);
        getMatrix().set(i, j, previousValue.plus(value));

    }

    @Override
    public void copy(IRealMatrix target){

        if(this.sameSize(target)){
            int m = getNumberOfRows();
            int n = getNumberOfColumns();
            for(int i = 0; i < m; i++)
                for(int j = 0; j < n; j++)
                    this.setValue(i, j, target.getValue(i, j));
        }else{
            System.out.println("Error: unmatched matrix size for copying.  No change applied.");
        }
    }

    @Override
    public double getMax(){
        int m = getNumberOfRows();
        int n = getNumberOfColumns();
        double temp;

        double max = 0;

        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                temp = this.getValue(i, j);
                if(temp > max){
                    max = temp;
                }
            }
        }
        return max;
    }

    @Override
    public double getMin(){
        int m = getNumberOfRows();
        int n = getNumberOfColumns();
        double temp;

        double min = 0;

        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                temp = this.getValue(i, j);
                if(temp < min){
                    min = temp;
                }
            }
        }
        return min;
    }

    @Override
    public double getMaxMag(){
        double max = Math.abs(getMax());
        double min = Math.abs(getMin());

        if(max >= min){
            return max;
        }else{
            return min;
        }
    }

    @Override
    public void clear(){
        int m = getNumberOfRows();
        int n = getNumberOfColumns();

        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                this.setValue(i, j, 0);
            }
        }
    }


    @Override
    public String toString()
    {
        DecimalFormat format = new DecimalFormat("+0.0000;-0.0000");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix.getNumberOfRows(); i++)
        {
            for (int j = 0; j < matrix.getNumberOfColumns(); j++)
            {
                sb.append(format.format(getMatrix().get(i, j)) + "\t");
            }
            sb.append('\n');
        }
        return sb.toString();
    }


    @Override
    public IRealMatrix clone()
    {
        return new JsRealMatrix(getMatrix().copy());
    }

}
