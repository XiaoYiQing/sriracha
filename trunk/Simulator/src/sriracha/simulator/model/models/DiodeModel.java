package sriracha.simulator.model.models;

/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 29/10/13
 * Time: 9:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DiodeModel extends CircuitElementModel{

    private double is;

    public DiodeModel(char key, String name, String line) {
        super(key, name);

        //parse the ".model" line's characteristics between parentheses into separate Strings
        //example: .model mName D (IS=0 RS=0 ...)
        String[] parameters = line.substring(line.indexOf("(")+1, line.indexOf(")")).split("\\s+");

        for(String s: parameters){
            String characteristicName = s.substring(0,s.indexOf("="));
            characteristicName.toLowerCase();
            if(characteristicName.equals("is")){
                is = Double.parseDouble(s.substring(s.indexOf("=")+1, s.length()-1));
            }
        }
    }
}
