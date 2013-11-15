package sriracha.math.interfaces;

public interface IComplexMatrix extends IMatrix{

    public IComplex getValue(int i, int j);

    public void setValue(int i, int j, IComplex value);

    public void setValue(int i, int j, double real, double complex);

    public void addValue(int i, int j, IComplex value);

    public void addValue(int i, int j, double real, double complex);

    /**
     * Copy all complex values within the target Complex matrix, IF both matrices have
     * the same size.
     * @param target target matrix.
     */
    public void copy(IComplexMatrix target);

    @Override
    public IComplexVector solve(IVector vector);

    /**
     * Get the matrix's maximum complex value according to magnitude of the
     * complex number.
     * @return Complex value with the highest magnitude.
     */
    public IComplex getMax();

    /**
     * Get the matrix's minimum complex value according to magnitude of the
     * complex number.
     * @return Complex value with the highest magnitude.
     */
    public IComplex getMin();

    /**
     * Set all values in the matrix to 0.
     */
    public void clear();
}
