package sriracha.math.interfaces;

public interface IRealMatrix extends IMatrix
{
    public double getValue(int i, int j);

    public void setValue(int i, int j, double value);

    public void addValue(int i, int j, double value);

    public void copy(IRealMatrix target);

    public int getNumberOfRows();

    public int getNumberOfColumns();

    /**
     * Get the matrix's maximum value.
     * @return highest value in the matrix.
     */
    public IComplex getMax();

    public IRealVector solve(IRealVector vector);

    public IComplexVector solve(IComplexVector vector);

    @Override
    public IRealMatrix clone();
}
