import sriracha.math.MathActivator;
import sriracha.math.interfaces.*;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.elements.Diode;
import sriracha.simulator.model.elements.Resistor;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.dc.DCNonLinEquation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 30/10/13
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tests {

    public static void main(String[]args){
        /*
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

        System.out.println("C:\n" + C);

        //IMatrix D = A.plus(C);
        //System.out.println(D);

        IComplexVector b = activator.complexVector(3);
        b.setValue(0,activator.complex(4,9));
        b.setValue(1,activator.complex(9,0));
        b.setValue(2,activator.complex(1,4));
        System.out.println("b:\n" + b);


        System.out.println(D.solve(b));

        System.out.println("Max value in C is: " + C.getMax());
        System.out.println("Max value in b is: " + b.getMax());


        IComplexVector k = (IComplexVector)C.times(b);
        System.out.println("C*b:\n" + k);
        */

        DCEquation myEq1 = new DCEquation(5);
        //DCEquation myEq2 = new DCEquation(5);
        DCNonLinEquation myEq2 = new DCNonLinEquation(5);

        ArrayList<DCEquation> myEqList = new ArrayList<DCEquation>();
        myEqList.add(myEq1);
        myEqList.add(myEq2);

        Diode D1 = new Diode("D1");
        for(int i = 0; i < myEqList.size(); i++){
            DCEquation tempEq = myEqList.get(i).clone();
            if(tempEq instanceof DCNonLinEquation)
                System.out.println("Found");
        }

        /*
        Circuit myCirc = new Circuit("MyCircuit");
        Resistor R1 = new Resistor("R1", 1);
        Diode D1 = new Diode("D1");
        myCirc.addElement(R1);
        System.out.println(myCirc.isLinear());

        myCirc.addElement(D1);
        System.out.println(myCirc.isLinear());
        */
    }
}
