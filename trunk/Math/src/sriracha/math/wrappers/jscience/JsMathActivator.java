package sriracha.math.wrappers.jscience;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.*;

/**
 * Object of this class are simply factory
 * for objects in the math module: JsComplexMatrix,
 * JsComplex, JsRealMatrix, JsRealVector, JsComplexVector.
 */
public class JsMathActivator extends MathActivator {


    @Override
    public IComplexMatrix complexMatrix(int i, int j) {
        return new JsComplexMatrix(i, j);
    }

    @Override
    public IComplex complex(double real, double imag) {
        return new JsComplex(real, imag);
    }

    @Override
    public IRealMatrix realMatrix(int i, int j) {
        return new JsRealMatrix(i, j);
    }

    @Override
    public IRealVector realVector(int length) {
        return new JsRealVector(length);
    }

    @Override
    public IComplexVector complexVector(int length) {
        return new JsComplexVector(length);
    }
}
