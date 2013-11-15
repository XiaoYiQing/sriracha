package sriracha.math.interfaces;

public interface IRealVector extends IVector
{
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
     * Set all values in the vector to 0.
     */
    public void clear();

    @Override
    public IRealVector clone();
}
