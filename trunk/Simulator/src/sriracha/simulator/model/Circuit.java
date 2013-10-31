package sriracha.simulator.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Circuit object, contains all of the elements and takes care of managing node to matrix index mappings.
 * It also informs the simulator of the size of matrix it will need.
 */
public class Circuit implements ICollectElements {

    private String name;

    private HashMap<String, CircuitElement> elements;

    /**
     * mapping from node names in received netlist to index in final matrix.
     * Ground node is always the tuple ("0", -1)
     * Internal SubMatrix nodes are not mapped here
     */
    private HashMap<String, Integer> nodeMap;

    /**
     * Circuit constructor.
     * @param name - first line of Netlist
     */
    public Circuit(String name) {
        this.name = name;
        elements = new HashMap<String, CircuitElement>();
        nodeMap = new HashMap<String, Integer>();
        nodeMap.put("0", -1);       //The ground should always be the first item
    }

    @Override
    public void addElement(CircuitElement e) {
        elements.put(e.name, e);
    }


    @Override
    public int assignNodeMapping(String nodeName) {
        if (!nodeMap.containsKey(nodeName)) {
            nodeMap.put(nodeName, getNodeCount());
        }
        return nodeMap.get(nodeName);
    }

    /**
     * returns the node index corresponding to the specified node name
     * @param nodeName node name
     * @return matrix index
     */
    public int getNodeIndex(String nodeName) {
        return nodeMap.get(nodeName);
    }

    /**
     * @param name CircuitElement name
     * @return the CircuitElement corresponding to this name, or null if not found
     */
    public CircuitElement getElement(String name) {
        if (elements.containsKey(name)) {
            return elements.get(name);
        }
        return null;
    }

    /**
     * The elements Might not be in the order they were found in the netlist.
     * @return all of the elements in this Circuit
     */
    public Collection<CircuitElement> getElements() {
        return elements.values();
    }

    /**
     * number of nodes in the circuit excluding ground and internal subcircuit nodes.
     *
     * @return number of nodes
     */
    int getNodeCount() {
        return nodeMap.size() - 1;
    }


    /**
     * Assigns indices to the additional variables required by some elements
     * and also to internal nodes in subcircuits.
     * (Called by setCircuit() of Simulator class)
     */
    public void assignAdditionalVarIndices() {
        int index = getNodeCount();
        for (CircuitElement e : elements.values()) {
            if (e.getExtraVariableCount() > 0) {
                e.setFirstVarIndex(index);
                index += e.getExtraVariableCount();
            }
        }
    }

    /**
     * @return size of the matrix needed to hold equations for this circuit
     */
    public int getMatrixSize() {
        int evCount = 0;
        for (CircuitElement e : elements.values()) {
            evCount += e.getExtraVariableCount();
        }
        return evCount + getNodeCount();
    }

    @Override
    public String toString() {
        String s = "";
        for (CircuitElement e : elements.values()) {
            s += e + "\n";
        }
        return s;
    }


}
