package sriracha.math.interfaces;

public interface IComplexMatrix extends IMatrix{

    public IComplex getValue(int i, int j);

    public void setValue(int i, int j, IComplex value);

    public void addValue(int i, int j, IComplex value);

    @Override
    public IComplexVector solve(IVector vector);

    public IComplex getMax();
}
