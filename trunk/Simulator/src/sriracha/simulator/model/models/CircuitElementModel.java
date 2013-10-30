package sriracha.simulator.model.models;

/**
 * Circuit element model class used for circuit elements which require
 * a model specified to fully cover its characteristics.
 */
public abstract class CircuitElementModel {
    private char key;
    private String name;

    public CircuitElementModel(char key, String name){
        this.key = key;
        this.name = name;
    }

    public char getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    /**
     * Construct a new model using the netlist "line".
     * @param line format: .MODEL MODName MODType (param1=a param2=b ...)
     * @return
     */
    public static CircuitElementModel generateModel(String line){

        //The third String (seperated by space) of the .MODEL statement indicates
        //the type of the model.
        String key = line.split("\\s+")[2];
        key.toUpperCase();


        if(key.equals("D")){
            return new DiodeModel('D', line);
        }else{
            System.out.println("Unknown circuit model type.");
            return null;
        }
    }
}
