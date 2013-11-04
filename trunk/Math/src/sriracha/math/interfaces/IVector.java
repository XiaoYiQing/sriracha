package sriracha.math.interfaces;

import org.jscience.mathematics.vector.Vector;

public interface IVector {

    public int getDimension();

    public IVector plus(IVector vector);

    public IVector minus(IVector vector);

    public IVector times(double d);

    public IVector clone();

    public Vector getVector();

    public IVector opposite();
}
