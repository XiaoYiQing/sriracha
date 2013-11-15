package sriracha.math.interfaces;


public interface IMatrix {




    public IMatrix plus(IMatrix m);
    public IMatrix minus(IMatrix m);
    public IMatrix times(IMatrix m);

    public IMatrix times(double n);
    public IVector times(IVector v);

    public IVector solve(IVector b);

    /**
     * Return the matrix's number of rows.
     * @return
     */
    public int getNumberOfRows();

    /**
     * Return the matrix's number of columns.
     * @return
     */
    public int getNumberOfColumns();

    /**
     * Invert this matrix;
     */
    public void inverse();

    /**
     * transpose this matrix;
     */
    public void transpose();

    /**
     * Return true if target is a matrix with the same size, false otherwise.
     * @param target target matrix which size is to be compared with.
     * @return
     */
    public boolean sameSize(IMatrix target);

    public IMatrix clone();

}
