package sriracha.simulator.model.models;

import sriracha.simulator.model.elements.Diode;
import sriracha.simulator.parser.CircuitBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 29/10/13
 * Time: 9:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DiodeModel extends CircuitElementModel{

    private double is;
    private double vt;


    public DiodeModel(char key, String line) {
        super(key, line.split("\\s+")[1]);

        //parse the ".model" line's characteristics between parentheses into separate Strings
        //example: .model mName D (IS=0 RS=0 ...)
        String parameterSect = line.substring(line.indexOf("(") + 1, line.indexOf(")"));

        String[] parameters;

        try{
            parameters = parameterSect.split("\\s+");

            for(String str: parameters){
                String characteristicName = str.substring(0,str.indexOf("="));
                characteristicName.toUpperCase();
                if(characteristicName.equals("IS")){
                    is = CircuitBuilder.parseDouble(str.substring(str.indexOf("=")+1, str.length()));
                }else if(characteristicName.equals("VT"))
                    vt = CircuitBuilder.parseDouble(str.substring(str.indexOf("=")+1, str.length()));
            }
        }catch(StringIndexOutOfBoundsException e1){
            e1.printStackTrace();
            System.out.println("Something went wrong with Diode model parameter netlist.");
            System.out.println("Default model will be applied.");

            is = Diode.STD_IS;
            vt = Diode.STD_VT;
        }
    }


    public double getIs() {
        return is;
    }

    public double getVt() {
        return vt;
    }
}
