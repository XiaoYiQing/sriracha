import sriracha.math.MathActivator;
import sriracha.math.interfaces.*;

/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 30/10/13
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tests {

    public static void main(String[]args){

        MathActivator activator = MathActivator.Activator;

        IRealMatrix A = activator.realMatrix(3,3);
        A.setValue(0,0,1);
        A.setValue(1,1,1);
        A.setValue(2,2,1);
        System.out.println(A);

        IComplexMatrix C = activator.complexMatrix(3,3);
        C.setValue(0,1,activator.complex(2,1));
        C.setValue(0,0,activator.complex(2,8));
        C.setValue(2,2,activator.complex(5,2));
        C.setValue(1,0,activator.complex(1,9));

        System.out.println(C);

        IMatrix D = A.plus(C);
        System.out.println(D);

        IComplexVector b = activator.complexVector(3);
        b.setValue(0,activator.complex(4,9));
        b.setValue(1,activator.complex(9,0));
        b.setValue(2,activator.complex(1,4));

        System.out.println(D.solve(b));

        System.out.println("Max value in C is: " + C.getMax());
        System.out.println("Max value in b is: " + b.getMax());
    }
}
