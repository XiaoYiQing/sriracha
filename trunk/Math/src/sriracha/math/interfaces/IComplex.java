package sriracha.math.interfaces;

/**
 * Objects implementing IComplex interface are
 * wrapper classes for the Complex class from the
 * jscience library.
 */
public interface IComplex {

    public double getImag();

    public void setImag(double imag);

    public double getReal();

    /**
     * Get the magnitude of this IComplex number.
     * @return
     */
    public double getMag();

    public void setReal(double real);

    IComplex plus(IComplex d);
    IComplex minus(IComplex d);

    IComplex opposite();
}

