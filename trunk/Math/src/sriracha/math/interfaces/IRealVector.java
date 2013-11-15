package sriracha.math.interfaces;

public interface IRealVector extends IVector
{
    public IRealVector times(double d);

    public double getValue(int i);

    public void setValue(int i, double value);

    public void addValue(int i, double value);

    public void copy(IRealVector target);

    /**
     * Get the vector's maximum value.
     * @return highest value in the vector.
     */
    public double getMax();

    /**
     * Get the vector's min value.
     * @return smallest value in the vector.
     */
    public double getMin();

    /**
     * Get the vector's highest magnitude. (Returns the absolute value of that value)
     * @return highest magnitude in the vector.
     */
    public double getMaxMag();

    /**
     * Set all values in the vector to 0.
     */
    public void clear();

    @Override
    public IRealVector clone();
}
