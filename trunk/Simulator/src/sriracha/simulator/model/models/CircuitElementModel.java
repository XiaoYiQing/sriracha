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
}
