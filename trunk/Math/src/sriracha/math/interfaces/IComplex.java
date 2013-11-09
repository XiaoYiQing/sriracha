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
    public void setReal(double real);

    /**
     * Get the magnitude of this IComplex number.
     * @return
     */
    public double getMag();


    public boolean isComplex();

    public IComplex plus(IComplex d);
    public IComplex plus(double real, double img);

    public IComplex minus(IComplex d);
    public IComplex minus(double real, double img);


    public IComplex opposite();
}

