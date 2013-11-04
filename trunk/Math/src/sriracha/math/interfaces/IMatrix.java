package sriracha.math.interfaces;


public interface IMatrix {




    public IMatrix plus(IMatrix m);
    public IMatrix minus(IMatrix m);
    public IMatrix times(IMatrix m);

    public IMatrix times(double n);
    public IVector times(IVector v);

    public IVector solve(IVector b);

    public IMatrix clone();

}
