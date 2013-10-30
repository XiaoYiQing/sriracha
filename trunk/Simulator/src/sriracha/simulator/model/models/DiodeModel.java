package sriracha.simulator.model.models;

import sriracha.simulator.model.elements.Diode;

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

    public DiodeModel(char key, String name, String line) {
        super(key, name);

        //parse the ".model" line's characteristics between parentheses into separate Strings
        //example: .model mName D (IS=0 RS=0 ...)
        String parameterSect = line.substring(line.indexOf("(") + 1, line.indexOf(")"));

        String[] parameters;

        try{
            parameters = parameterSect.split("\\s+");

            for(String str: parameters){
                String characteristicName = str.substring(0,str.indexOf("="));
                characteristicName.toLowerCase();
                if(characteristicName.equals("is")){
                    is = Double.parseDouble(str.substring(str.indexOf("=")+1, str.length()-1));
                }else if(characteristicName.equals("vt"))
                    vt = Double.parseDouble(str.substring(str.indexOf("=")+1, str.length()-1));
            }
        }catch(StringIndexOutOfBoundsException e1){
            e1.printStackTrace();
            System.out.println("Something went wrong with Diode model parameter netlist.");
            System.out.println("Default model will be applied.");

            is = Diode.STD_IS;
            vt = Diode.STD_VT;
        }
    }
}
