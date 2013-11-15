package sriracha.math.interfaces;

public interface IComplexVector extends IVector {

    public IComplex getValue(int i);

    public void setValue(int i, IComplex value);

    public void setValue(int i, double real, double complex);

    public void addValue(int i, IComplex value);

    public void addValue(int i, double real, double complex);

    public void copy(IComplexVector target);
    /**
     * Get the vector's maximum complex value according to magnitude of the
     * complex number.
     * @return Complex value with the highest magnitude.
     */
    public IComplex getMax();

    /**
     * Set all values in the vector to 0.
     */
    public void clear();
}
