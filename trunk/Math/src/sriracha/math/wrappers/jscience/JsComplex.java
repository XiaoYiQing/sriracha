package sriracha.math.wrappers.jscience;

import org.jscience.mathematics.number.Complex;
import sriracha.math.interfaces.IComplex;

/**
 * Wrapper class for the Complex class from jscience
 * library.
 */
class JsComplex implements IComplex {

    //Note: Complex is from external library.
    Complex value;

    public JsComplex(double real, double imag) {
        value = Complex.valueOf(real, imag);
    }

    public JsComplex(Complex c) {
        value = c;
    }


    @Override
    public double getImag() {
        return value.getImaginary();
    }

    @Override
    public void setImag(double complex) {
        value = Complex.valueOf(value.getReal(), complex);
    }

    @Override
    public double getReal() {
        return value.getReal();
    }

    @Override
    public void setReal(double real) {
        value = Complex.valueOf(real, value.getImaginary());
    }

    @Override
    public IComplex plus(IComplex d) {
        return new JsComplex(d.getReal() + getReal(), d.getImag() + getImag());
    }

    @Override
    public IComplex minus(IComplex d) {
        return new JsComplex(getReal() - d.getReal(), getImag() - d.getImag());
    }

    @Override
    public IComplex opposite() {
        return new JsComplex(value.opposite());
    }

    @Override
    public String toString() {
        return value.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }

    static Complex make(IComplex complex) {
        return Complex.valueOf(complex.getReal(), complex.getImag());
    }
}
