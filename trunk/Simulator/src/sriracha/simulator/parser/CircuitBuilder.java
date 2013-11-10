package sriracha.simulator.parser;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.simulator.model.*;
import sriracha.simulator.model.elements.Capacitor;
import sriracha.simulator.model.elements.Diode;
import sriracha.simulator.model.elements.Inductor;
import sriracha.simulator.model.elements.Resistor;
import sriracha.simulator.model.elements.ctlsources.*;
import sriracha.simulator.model.elements.sources.CurrentSource;
import sriracha.simulator.model.elements.sources.Source;
import sriracha.simulator.model.elements.sources.VoltageSource;
import sriracha.simulator.model.models.CircuitElementModel;
import sriracha.simulator.model.models.DiodeModel;
import sriracha.simulator.solver.analysis.Analysis;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.ac.ACAnalysis;
import sriracha.simulator.solver.analysis.ac.ACSubType;
import sriracha.simulator.solver.analysis.dc.DCAnalysis;
import sriracha.simulator.solver.analysis.dc.DCSweep;
import sriracha.simulator.solver.output.filtering.*;

import java.util.*;

public class CircuitBuilder
{

    /**
     *  Map of subcircuits using a String name as key and a SubCircuitTemplate as value
     */
    private HashMap<String, SubCircuitTemplate> subcircuitTemplates = new HashMap<String, SubCircuitTemplate>();

    /**
     * The main circuit object representing the intended netlist circuit
     */
    private Circuit circuit;

    /**
     * List of Analysis
     */
    private ArrayList<Analysis> analysisTypes = new ArrayList<Analysis>();
    /**
     * List of outputFilters
     */
    private ArrayList<OutputFilter> outputFilters = new ArrayList<OutputFilter>();

    /**
     * List of circuit element models, such as models for diodes and BJTs.
     */
    private HashMap<String, CircuitElementModel>  circuitElementModels = new HashMap<String, CircuitElementModel>();

    public Circuit getCircuit()
    {
        return circuit;
    }

    /**
     * Return a read-only Analysis object ArrayList.
     * @return read-only Analysis ArrayList.
     */
    public List<Analysis> getAnalysisTypes()
    {
        return Collections.unmodifiableList(analysisTypes);
    }

    public List<OutputFilter> getOutputFilters()
    {
        return Collections.unmodifiableList(outputFilters);
    }

    /**
     * Constructor which breaks down the netlist into subcircuits, circuit parts,
     * analysis objects and OutputFilter object.
     * @param netlist The target netlist representing the new circuit.
     */
    public CircuitBuilder(String netlist)
    {

        String[] lines = netlist.split("\\r?\\n");

        ArrayList<String> otherLines = new ArrayList<String>();

        //Get circuit name
        circuit = new Circuit(lines[0]);

        //First read an execute all lines starting with "." (except the final ".END")
        //and put aside the rest of the netlist lines in a new Array.
        for (int i = 1; i < lines.length; i++)
        {
            String line = lines[i];
            String upperLine = line.toUpperCase();

            //Create a new subcircuit template.
            if (upperLine.startsWith(".SUBCKT"))
            {
                ArrayList<String> subCircuitLines = new ArrayList<String>();
                while (i < lines.length && !lines[i].toUpperCase().startsWith(".ENDS"))
                {
                    //If String is empty, part of a comment, or an empty space, move to next String
                    if (lines[i].length() == 0 || lines[i].charAt(0) == '*' || Character.isWhitespace(lines[i].charAt(0)))
                    {
                        i++;
                        continue;
                    }

                    subCircuitLines.add(lines[i]);
                    i++;
                }
                parseSubCircuitTemplate(subCircuitLines.toArray(new String[subCircuitLines.size()]));
            }else if(upperLine.startsWith(".MODEL")){
                CircuitElementModel newModel = parseCircuitElementModel(upperLine);
                if(!circuitElementModels.containsKey(newModel.getName())){
                    circuitElementModels.put(newModel.getName(), newModel);
                }else
                    System.out.println("Model name already in use.");
            }else if (upperLine.startsWith(".AC") || upperLine.startsWith(".DC")){
                System.out.println("Parse!!");
                analysisTypes.add(parseAnalysis(upperLine));
            }else if (upperLine.startsWith(".PRINT")){
                outputFilters.add(parsePrint(upperLine));
            }else
                //The normal circuit elements.
                otherLines.add(upperLine);
        }

        //Add all circuit element list into on single list.
        lines = new String[otherLines.size()];
        otherLines.toArray(lines);

        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];

            //End of file: exit
            if (line.equals(".END")) break;

            //If String is empty, part of a comment, or an empty space, move to next String
            if (line.length() == 0 || line.charAt(0) == '*' || Character.isWhitespace(line.charAt(0)))
                continue;

            //If String represent a normal circuit element.
            if (line.charAt(0) != '.')
                parseCircuitElement(circuit, lines, i);
        }
    }

    public OutputFilter parsePrint(String line)
    {
        String[] params = tokenizeLine(line);

        System.out.println("in parsePrint: \n" + line);

        if (params.length < 3)
            throw new ParseException("Not enough parameters for .PRINT: " + line);

        String printTypeStr = params[1];
        AnalysisType printType;

        if (printTypeStr.equalsIgnoreCase("AC"))
            printType = AnalysisType.AC;
        else if (printTypeStr.equalsIgnoreCase("DC"))
            printType = AnalysisType.DC;
        else if (printTypeStr.equalsIgnoreCase("TRAN") || printTypeStr.equalsIgnoreCase("NOISE") || printTypeStr.equalsIgnoreCase("DISTO"))
            throw new UnsupportedOperationException("This format of analysis is currently not supported: " + line);
        else
            throw new ParseException("Invalid Plot analysis format: " + line);

        ArrayList<NodeInfo> resultInfoList = new ArrayList<NodeInfo>();
        for (int i = 2; i < params.length; i++)
        {
            char firstChar = Character.toUpperCase(params[i].charAt(0));
            if (firstChar == 'V' || firstChar == 'I')
            {
                NodeDataFormat dataFormat = StringToOutputType(params[i].substring(1, params[i].indexOf('(')), line);
                String[] nodeList = parseBracketContents(params[i].substring(params[i].indexOf('('), params[i].length()));

                for(int m = 0; m < nodeList.length; m++){
                    System.out.print(nodeList[m] + " ");
                }
                System.out.println("\nnodeList above!");
                HashMap<String, Integer> myTemp = circuit.getNodeMap();
                Iterator it = myTemp.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    System.out.println(pairs.getKey() + " = " + pairs.getValue());
                    it.remove(); // avoids a ConcurrentModificationException
                }


                for (String node : nodeList)
                    if (node.length() == 0)
                        throw new ParseException("Expected a node name: " + line);

                if (firstChar == 'V')
                {
                    if (nodeList.length == 1)
                        resultInfoList.add(new VoltageInfo(dataFormat, circuit.getNodeIndex(nodeList[0])));
                    else if (nodeList.length == 2)
                        resultInfoList.add(new VoltageInfo(dataFormat, circuit.getNodeIndex(nodeList[0]), circuit.getNodeIndex(nodeList[1])));
                    else
                        throw new ParseException("Voltages can only be requested between 1 or 2 nodes: " + line);
                }
                else if (firstChar == 'I')
                {
                    if (nodeList.length != 1 || Character.toUpperCase(nodeList[0].charAt(0)) != 'V')
                        throw new ParseException("Currents can only be requested at a single voltage source: " + line);

                    resultInfoList.add(new CurrentInfo(dataFormat, nodeList[0], circuit));
                }
            }
            else
                throw new UnsupportedOperationException("The expression '" + params[i] + "' is not supported. Line: " + line);
        }

        OutputFilter outputFilter = new OutputFilter(printType);
        for (NodeInfo info : resultInfoList)
            outputFilter.addData(info);

        return outputFilter;
    }

    private NodeDataFormat StringToOutputType(String outputString, String line)
    {
        if (outputString.equals(""))
            return NodeDataFormat.Complex;
        else if (outputString.equalsIgnoreCase("R"))
            return NodeDataFormat.Real;
        else if (outputString.equalsIgnoreCase("I"))
            return NodeDataFormat.Imaginary;
        else if (outputString.equalsIgnoreCase("M"))
            return NodeDataFormat.Magnitude;
        else if (outputString.equalsIgnoreCase("P"))
            return NodeDataFormat.Phase;
        else if (outputString.equalsIgnoreCase("DB"))
            return NodeDataFormat.Decibels;

        throw new ParseException("Invalid output format: " + line);
    }

    private String[] parseBracketContents(String bracketString)
    {
        bracketString = bracketString.trim();

        if (bracketString.charAt(0) != '(' || bracketString.charAt(bracketString.length() - 1) != ')')
            throw new ParseException("Expected an expression enclosed in brackets. Found: " + bracketString);

        // strip brackets
        bracketString = bracketString.substring(1, bracketString.length() - 1);

        return bracketString.split(",\\s*");
    }

    private String[] tokenizeLine(String line)
    {
        ArrayList<String> params = new ArrayList<String>();

        String currentParam = "";
        int bracketLevel = 0;
        for (int i = 0; i < line.length(); i++)
        {
            if (Character.isWhitespace(line.charAt(i)) && bracketLevel == 0)
            {
                if (currentParam.length() > 0)
                    params.add(currentParam);

                currentParam = "";
            }
            else
            {
                if (line.charAt(i) == '(')
                    bracketLevel++;
                else if (line.charAt(i) == ')')
                    bracketLevel--;

                currentParam += line.charAt(i);
            }
        }

        if (currentParam.length() > 0)
            params.add(currentParam);

        if (bracketLevel != 0)
            throw new ParseException("Unmatched brackets: " + line);

        return params.toArray(new String[params.size()]);
    }

    /**
     * Create an Analysis object depending on the content of line (AC, DC, etc.)
     * @param line Instruction on the category of analysis requested.
     * @return The requested Analysis object.
     */
    public Analysis parseAnalysis(String line)
    {
        if (line.startsWith(".AC"))
            return parseSmallSignal(line);
        else if (line.startsWith(".DC"))
            return parseDCAnalysis(line);
        else
            throw new UnsupportedOperationException("This format of analysis is currently not supported: " + line);
    }

    /**
     * Create an DCAnalysis object accordingly to the specs from the input String line
     * @param line netlist description of the target DC analysis
     * @return The DCAnalysis object created
     */
    private DCAnalysis parseDCAnalysis(String line)
    {
        String[] params = line.split("\\s+");

        if (!(params.length == 5 || params.length == 9))
            throw new ParseException("Incorrect number of parameters for DC analysis: " + line);

        Source s1 = (Source) circuit.getElement(params[1]);

        DCSweep sweep1 = new DCSweep(s1, parseDouble(params[2]), parseDouble(params[3]), parseDouble(params[4]));

        if (sweep1.getStep() == 0)
        {
            throw new ParseException("Step size must be larger than 0 for DC analysis");
        }
        if (sweep1.getEndValue() <= sweep1.getStartValue())
        {
            throw new ParseException("End Sweep value must be larger than Start Sweep value");
        }

        DCSweep sweep2 = null;
        if (params.length == 9)
        {
            sweep2 = new DCSweep((Source) circuit.getElement(params[5]), parseDouble(params[6]), parseDouble(params[7]),
                    Integer.parseInt(params[8]));

            if (sweep2.getStep() == 0)
            {
                throw new ParseException("Step size must be larger than 0 for DC analysis");
            }

            if (sweep2.getEndValue() <= sweep2.getStartValue())
            {
                throw new ParseException("End Sweep value must be larger than Start Sweep value");
            }
        }

        return new DCAnalysis(sweep1, sweep2);
    }

    /**
     * Create an ACAnalysis object accordingly to the specs from the input String line
     * @param line netlist description of the target AC analysis
     * @return The ACAnalysis object created
     */
    private ACAnalysis parseSmallSignal(String line)
    {
        String[] params = line.split("\\s+");

        if (params.length != 5)
            throw new ParseException("Incorrect number of parameters for AC analysis: " + line);

        ACSubType subType;

        if (params[1].equals("LIN"))
            subType = ACSubType.Linear;
        else if (params[1].equals("OCT"))
            subType = ACSubType.Octave;
        else if (params[1].equals("DEC"))
            subType = ACSubType.Decade;
        else
            throw new ParseException("Invalid scale format. Scale must be LIN, OCT, or DEC: " + line);

        int numPoints = Integer.parseInt(params[2]);
        double rangeStart = parseDouble(params[3]);
        double rangeStop = parseDouble(params[4]);

        if (numPoints == 0)
        {
            throw new ParseException("Must request more than 0 points for AC analysis");
        }

        return new ACAnalysis(subType, rangeStart, rangeStop, numPoints);
    }

    /**
     * Create a subcircuit using the sub-netlist
     *
     * @param lines sub-netlist from which the subcircuit is created.
     */
    private void parseSubCircuitTemplate(String[] lines)
    {
        //Splitting the first line into subcircuit external connections.
        String[] params = lines[0].split("\\s+");

        //The first line of the subcircuit must contain the name of the subcircuit
        //and at least two nodes
        if (params.length < 3)
            throw new ParseException("Not enough parameters for a subcircuit template: " + lines[0]);

        //Assign the name of the template
        String name = params[1];
        //Assign the external nodes of the template
        SubCircuitTemplate subCircuit = new SubCircuitTemplate(name, params.length - 2);
        for (int i = 2; i < params.length; i++)
            subCircuit.assignNodeMapping(params[i]);

        //Set up the internal mapping of the circuit (iterating through internal circuit elements.)
        for (int i = 1; i < lines.length; i++)
            parseCircuitElement(subCircuit, lines, i);

        //Add new addition to subcircuit template map.
        subcircuitTemplates.put(name, subCircuit);
    }

    public CircuitElementModel parseCircuitElementModel(String line){

        CircuitElementModel newModel;
        newModel = CircuitElementModel.generateModel(line);
        return newModel;
    }

    /**
     * Add the new circuit element to the circuit object.
     * The netlist representation of a standard circuit element does not start with '.'
     * The parameter circuit's node indices will be updated appropriately whenever unused node names
     * are detected within the netlist representation of the new circuit element
     *
     * @param elementCollection The circuit in which the element is to be added.
     * @param lines netlist in the form of array of String lines.
     * @param index The String line where the standard circuit element in String representation
     *              is located.
     */
    private void parseCircuitElement(ICollectElements elementCollection, String[] lines, int index)
    {
        char elementType = Character.toLowerCase(lines[index].charAt(0));

        String[] params = lines[index].split("\\s+");

        if (params.length < 4)
            throw new ParseException("Not enough parameters for a circuit element: " + lines[index]);

        String[] additionalParams = Arrays.copyOfRange(params, 3, params.length);


        switch (elementType)
        {
            case 'r':
                createResistor(elementCollection, params[0], params[1], params[2], params[3]);
                break;

            case 'i':
                createCurrentSource(elementCollection, params[0], params[1], params[2], additionalParams);
                break;

            case 'v':
                createVoltageSource(elementCollection, params[0], params[1], params[2], additionalParams);
                break;

            case 'l':
                createInductor(elementCollection, params[0], params[1], params[2], params[3]);
                break;

            case 'c':
                createCapacitor(elementCollection, params[0], params[1], params[2], params[3]);
                break;

            case 'd':
                createDiode(elementCollection, params[0], params[1], params[2], params[3]);
                break;

            case 'g':
                createVCCS(elementCollection, params[0], params[1], params[2], params[3], params[4], params[5]);
                break;

            case 'e':
                createVCVS(elementCollection, params[0], params[1], params[2], params[3], params[4], params[5]);
                break;

            case 'f':
                createCCCS(elementCollection, params[0], params[1], params[2], params[3], params[4]);
                break;

            case 'h':
                createCCVS(elementCollection, params[0], params[1], params[2], params[3], params[4]);
                break;

            case 'x':
                createSubcircuit(elementCollection, params[0], params[params.length - 1], Arrays.copyOfRange(params, 1, params.length - 1));
                break;

            default:
                throw new ParseException("Unrecognized element format: " + elementType);


        }
    }

    /**
     * Add a new diode circuit element to the circuit using the specified model "modelName"
     *
     * @param elementCollection The collection in which the diode is to be added
     * @param name Name of this diode (Must be unique)
     * @param node1 cathode of the diode, where the current is heading
     * @param node2 anode of the diode, where the current leaves
     * @param modelName Name of the Diode model on which the new Diode is based on (Must exists).
     */
    public void createDiode(ICollectElements elementCollection, String name, String node1, String node2, String modelName){
        Diode d;

        CircuitElementModel myModel = circuitElementModels.get(modelName);


        if(myModel.getKey() == 'D'){
            d = new Diode(name, (DiodeModel)(myModel));
        }else{
            System.out.println("Warning, non diode MODEL specified for a diode element.\n"+
                "Standard diode parameters applied.");
            d = new Diode(name);
        }

        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        d.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(d);
    }

    /**
     * Add a new circuit element using one of the subcircuit template.  It is expected that a
     * subcircuit by the name of subCircuitName has already been created or is available in
     * subcircuitTemplates.
     * @param elementCollection The collection in which the subcircuit is to be added
     * @param name Name of this subcircuit (Must be unique)
     * @param subcircuitName The target subcircuitTemplates' name.
     * @param nodes The netlist list of external node names of the subcircuit. The number of node names is expected
     *              to match the number of external nodes of the intended subcircuit template.
     */
    private void createSubcircuit(ICollectElements elementCollection, String name, String subcircuitName, String[] nodes)
    {
        if (!subcircuitTemplates.containsKey(subcircuitName))
            throw new ParseException("Subcircuit template not found: " + subcircuitName);

        SubCircuit sc = new SubCircuit(name, subcircuitTemplates.get(subcircuitName));

        //Set up the external nodes of the subcircuit
        int[] nodeIndices = new int[nodes.length];
        for (int i = 0; i < nodes.length; i++)
            nodeIndices[i] = elementCollection.assignNodeMapping(nodes[i]);

        //Setting up the node (external) indices, just like any circuit element would do when created.
        sc.setNodeIndices(nodeIndices);
        //Add element as a normal circuit element
        elementCollection.addElement(sc);
    }

    /**
     * Create a new current source and put it into the circuit object
     * @param elementCollection The collection in which the current source is to be added
     * @param name Name of this current source (Must be unique)
     * @param node1 +node, where the current is heading
     * @param node2 -node, where the current leaves
     * @param params netlist instruction specifying the current source's specs.
     */
    private void createCurrentSource(ICollectElements elementCollection, String name, String node1, String node2, String... params)
    {
        SourceValue value = findPhasorOrDC(params);

        CurrentSource source;
        if (value.AC != null)
            source = new CurrentSource(name, value.AC);
        else
            source = new CurrentSource(name, value.DC);

        /*
        * Suggested Code instead of the above.
        * source = new CurrentSource(name, value.DC, value.AC);
        * */

        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        source.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(source);
    }

    /**
     * Create a new voltage source and put it into the circuit object
     * @param elementCollection The collection in which the voltage source is to be added
     * @param name Name of this voltage source (Must be unique)
     * @param node1 +node
     * @param node2 -node
     * @param params netlist instruction specifying the voltage source's specs.
     */
    private void createVoltageSource(ICollectElements elementCollection, String name, String node1, String node2, String... params)
    {
        SourceValue value = findPhasorOrDC(params);

        VoltageSource source = new VoltageSource(name, value.DC, value.AC);

        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        source.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(source);
    }

    /**
     * Create a SourceValue object based on the information provided by params.
     * Possible formats (default value of ac is amplitude=1, phase=0) :
     *      ["DC", amp],
     *      ["AC"],
     *      ["AC", amp],
     *      ["AC", amp, phase],
     *      ["DC", value, "AC"],
     *      ["DC", value, "AC", amp],
     *      ["DC", value, "AC", amp, phase]
     * @param params array of Strings.
     * @return The newly created SourceValue.
     */
    private SourceValue findPhasorOrDC(String... params)
    {
        //dc SourceValue if params has only one String item.
        if (params.length == 1)
            return new SourceValue(parseDouble(params[0]));

        if (params[0].equalsIgnoreCase("DC"))
        {
            //params for dc case cannot have more than 2 items.
            if (params.length > 2)
            {
                //if length > 2, must assign both AC and DC values in new SourceValue
                if (params[2].equalsIgnoreCase("AC"))
                {
                    double amplitude = 1, phase = 0;
                    if (params.length >= 4)
                        amplitude = parseDouble(params[3]);
                    if (params.length >= 5)
                        phase = Math.toRadians(parseDouble(params[4]));

                    double real = amplitude * Math.cos(phase);
                    double imaginary = amplitude * Math.sin(phase);

                    return new SourceValue(parseDouble(params[1]), MathActivator.Activator.complex(real, imaginary));
                }
                //Invalid params syntax.
                else throw new ParseException("Invalid parameters on Voltage Source " + params);
            }
            else
            {
                //params for dc case only.
                return new SourceValue(parseDouble(params[1]));
            }

        }
        //params for ac case only.
        else if (params[0].equalsIgnoreCase("AC"))
        {
            double amplitude = 1, phase = 0;
            if (params.length >= 2)
                amplitude = parseDouble(params[1]);
            if (params.length >= 3)
                phase = Math.toRadians(parseDouble(params[2]));

            double real = amplitude * Math.cos(phase);
            double imaginary = amplitude * Math.sin(phase);

            return new SourceValue(MathActivator.Activator.complex(real, imaginary));
        }
        else
            //Invalid params syntax.
            throw new ParseException("Invalid source format: " + params[0]);
    }

    /**
     * Add a resistor to the circuit object
     * @param elementCollection The collection in which the resistor is to be added
     * @param name Name of this resistor (Must be unique)
     * @param node1 n1
     * @param node2 n2
     * @param value Resistance value in Ohms
     */
    private void createResistor(ICollectElements elementCollection, String name, String node1, String node2, String value)
    {
        Resistor r = new Resistor(name, parseDouble(value));
        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        r.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(r);
    }

    /**
     * Add a new capacitor to the circuit object
     * @param elementCollection The collection in which the capacitor is to be added
     * @param name Name of this capacitor (Must be unique)
     * @param node1 n1
     * @param node2 n2
     * @param value Capacitance value in Farad
     */
    private void createCapacitor(ICollectElements elementCollection, String name, String node1, String node2, String value)
    {
        Capacitor c = new Capacitor(name, parseDouble(value));
        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        c.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(c);
    }

    /**
     * Add a new inductor to the circuit object
     * @param elementCollection The collection in which the inductor is to be added
     * @param name Name of this inductor (Must be unique)
     * @param node1 n1
     * @param node2 n2
     * @param value Inductor value in Henry
     */
    private void createInductor(ICollectElements elementCollection, String name, String node1, String node2, String value)
    {
        Inductor i = new Inductor(name, parseDouble(value));
        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        i.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(i);
    }

    /**
     * Add a new voltage controlled current source (vccs) to the circuit object
     * @param elementCollection The collection in which the vccs is to be added
     * @param name Name of the vccs (Must be unique)
     * @param node1 The dependent side's +node
     * @param node2 The dependent side's -node
     * @param control1 The independent side's +node
     * @param control2 The independent side's -node
     * @param value Control factor by which the generated current is related to the input voltage
     */
    private void createVCCS(ICollectElements elementCollection, String name, String node1, String node2, String control1, String control2, String value)
    {
        VCCS vccs = new VCCS(name, parseDouble(value));
        createVoltageControlledSource(elementCollection, node1, node2, control1, control2, vccs);
    }

    /**
     * Add a new voltage controlled voltage source (vcvs) to the circuit object
     * @param elementCollection The collection in which the vcvs is to be added
     * @param name Name of the vcvs (Must be unique)
     * @param node1 The dependent side's +node
     * @param node2 The dependent side's -node
     * @param control1 The independent side's +node
     * @param control2 The independent side's -node
     * @param value Control factor by which the generated voltage is related to the input voltage
     */
    private void createVCVS(ICollectElements elementCollection, String name, String node1, String node2, String control1, String control2, String value)
    {
        VCVS vcvs = new VCVS(name, parseDouble(value));
        createVoltageControlledSource(elementCollection, node1, node2, control1, control2, vcvs);
    }

    /**
     * Add a new current controlled current source (cccs) to the circuit object
     * @param elementCollection The collection in which the cccs is to be added
     * @param name Name of the cccs (Must be unique)
     * @param node1 The dependent side's +node
     * @param node2 The dependent side's -node
     * @param control The name of the dummy voltage source used to track the control current
     * @param value Control factor by which the generated current is related to the input current
     */
    private void createCCCS(ICollectElements elementCollection, String name, String node1, String node2, String control, String value)
    {
        CircuitElement vSource = circuit.getElement(control);
        CCCS cccs = new CCCS(name, parseDouble(value), (VoltageSource) vSource);

        createCurrentControlledSource(elementCollection, node1, node2, cccs);
    }

    /**
     * Add a new current controlled current source (ccvs) to the circuit object
     * @param elementCollection The collection in which the ccvs is to be added
     * @param name Name of the ccvs (Must be unique)
     * @param node1 The dependent side's +node
     * @param node2 The dependent side's -node
     * @param control The name of the dummy voltage source used to track the control current
     * @param value Control factor by which the generated voltage is related to the input current
     */
    private void createCCVS(ICollectElements elementCollection, String name, String node1, String node2, String control, String value)
    {
        CircuitElement vSource = circuit.getElement(control);
        CCVS ccvs = new CCVS(name, parseDouble(value), (VoltageSource) vSource);
        createCurrentControlledSource(elementCollection, node1, node2, ccvs);
    }

    /**
     * Helper method to add the new voltage control source (VCS) into the collection object.
     * @param elementCollection The collection in which the VCS is to be added
     * @param node1 The dependent side's +node
     * @param node2 The dependent side's -node
     * @param control1 The independent side's +node
     * @param control2 The independent side's -node
     * @param source The target VCS object
     */
    private void createVoltageControlledSource(ICollectElements elementCollection, String node1, String node2, String control1, String control2, VCSource source)
    {
        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        int controlNode1Index = elementCollection.assignNodeMapping(control1);
        int controlNode2Index = elementCollection.assignNodeMapping(control2);
        source.setNodeIndices(node1Index, node2Index, controlNode1Index, controlNode2Index);
        elementCollection.addElement(source);
    }

    /**
     * Helper method to add the new current control source (CCS) into the collection object.
     * @param elementCollection The collection in which the ccs is to be added
     * @param node1 The dependent side's +node
     * @param node2 The dependent side's -node
     * @param source The target CCS object
     */
    private void createCurrentControlledSource(ICollectElements elementCollection, String node1, String node2, CCSource source)
    {
        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        source.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(source);
    }

    /**
     * parses a double and allows for Spice compatible postfixes (case insensitive)
     * T = 10^12
     * G = 10^9
     * Meg = 10^6
     * K = 10^3
     * m = 10^-3
     * u = 10^-6
     * n = 10^-9
     * p = 10^-12
     * f = 10^-15
     * <p/>
     * the string can be followed with any text after these as long as what comes before is parsable
     *
     * @param str - the string to parse
     * @return result as a double
     */
    public static double parseDouble(String str)
    {

        int i = 0;
        char c = str.charAt(0);
        while ((Character.isDigit(c) || c == '.' || c == '-' || Character.toLowerCase(c) == 'e') && ++i < str.length())
        {
            c = str.charAt(i);
        }

        String str1 = str.substring(0, i), str2 = str.substring(i);

        double factor = 1;

        str2 = str2.toLowerCase();

        if (str2.startsWith("t"))
            factor = Math.pow(10, 12);
        else if (str2.startsWith("g"))
            factor = Math.pow(10, 9);
        else if (str2.startsWith("meg"))
            factor = Math.pow(10, 6);
        else if (str2.startsWith("k"))
            factor = Math.pow(10, 3);
        else if (str2.startsWith("m"))
            factor = Math.pow(10, -3);
        else if (str2.startsWith("u"))
            factor = Math.pow(10, -6);
        else if (str2.startsWith("n"))
            factor = Math.pow(10, -9);
        else if (str2.startsWith("p"))
            factor = Math.pow(10, -12);
        else if (str2.startsWith("f"))
            factor = Math.pow(10, -15);


        return factor * Double.parseDouble(str1);

    }

    /**
     * Helper private class to store DC or AC, or both values as one object.
     * NOTE: AC variable is IComplex object, which contains real and imaginary part.
     */
    private class SourceValue
    {
        /**
         * dc value stored in SourceValue object
         */
        public double DC = 0;
        /**
         * ac value stored in SourceValue object
         */
        public IComplex AC = MathActivator.Activator.complex(0, 0);

        /**
         * Construct SourceValue object initializing dc value only.
         * @param dc dc value
         */
        public SourceValue(double dc)
        {
            DC = dc;
        }

        /**
         * Construct SourceValue object initializing ac value only.
         * @param ac ac value
         */
        public SourceValue(IComplex ac)
        {
            AC = ac;
        }

        /**
         * Construct SourceValue object  initializing both ac and dc values.
         * @param DC dc value
         * @param AC ac value
         */
        private SourceValue(double DC, IComplex AC)
        {
            this.DC = DC;
            this.AC = AC;
        }
    }
}