package sriracha.math.interfaces;

public interface IRealMatrix extends IMatrix
{
    public double getValue(int i, int j);

    public void setValue(int i, int j, double value);

    public void addValue(int i, int j, double value);

    /**
     * Copy all values in the target matrix, if the target matrix is of the same
     * size.
     * @param target target matrix.
     */
    public void copy(IRealMatrix target);

    /**
     * Get the matrix's maximum value.
     * @return highest value in the matrix.
     */
    public double getMax();

    /**
     * Get the matrix's min value.
     * @return smallest value in the matrix.
     */
    public double getMin();

    /**
     * Get the matrix's highest magnitude. (Returns the absolute value of that value)
     * @return highest magnitude in the matrix.
     */
    public double getMaxMag();

    public IRealVector solve(IRealVector vector);

    public IComplexVector solve(IComplexVector vector);

    /**
     * Set all values in the matrix to 0.
     */
    public void clear();

    @Override
    public IRealMatrix clone();
}
