package Graphs;

import java.lang.reflect.Array;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an adjacency matrix implemention of a weighted directed graph
 */
@SuppressWarnings("unchecked")
public class GraphAM<NodeType, EdgeType extends Number> implements GraphADT<NodeType, EdgeType>{

    private Map<Integer, NodeType> valueMap;
    private int numNodes;
    private Object[][] adjMatrix;

    /**
     * Creates a new adjacency matrix graph
     */
    public GraphAM(){
        numNodes = 0;
        valueMap = new HashMap<>();
        adjMatrix = new Object[10][10];
    }

    @Override
    public boolean insertNode(NodeType data) {
        if (isFull()){
            increaseAMSize();
        }
        if (valueMap.containsValue(data)){
            return false;
        }

        valueMap.put(numNodes++, data);
        return true;
    }

    /**
     * This helps us know if the adjacency matrix is full, so we can update its size
     * @return
     */
    private boolean isFull(){
        return (numNodes == adjMatrix.length); 
    }

    /**
     * This method increases the adjacency matrix size, copying over its elements
     */
    private void increaseAMSize(){

        int newSize = adjMatrix.length*2;
        Object[][] newMatrix = new Object[newSize][newSize]; // creating the new matrix of double the size

        for (int i = 0; i < adjMatrix.length; i++){ // copying over the values from the previous matrixs
            newMatrix[i] = Arrays.copyOf(adjMatrix[i], newSize);
        }

        adjMatrix = newMatrix;
    }

    @Override
    public boolean removeNode(NodeType data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeNode'");
    }

    @Override
    public boolean containsNode(NodeType data) {
        return valueMap.containsValue(data);
    }

    @Override
    public List<NodeType> getAllNodes() {
        return collectionToList(valueMap.values());
    }

    /**
     * This turns a collection over elements of type S into a list
     * @param <S>
     * @param collection
     * @return a list of the elements in a collection
     */
    private <S> List<S> collectionToList(Collection<S> collection){
        ArrayList<S> coolArrayList = new ArrayList<>();
        for (S element : collection){
            coolArrayList.add(element);
        }
        return coolArrayList;
    }

    @Override
    public int getNodeCount() {
        return numNodes;
    }

    @Override
    public boolean insertEdge(NodeType pred, NodeType succ, EdgeType weight) {
        if (weight.doubleValue() < 0.0){
            return false;
        }
        int predIndex = indexOfValue(succ);
        return true;
    }

    private int indexOfValue(NodeType value){
        Collection<Integer> keySet = valueMap.keySet();
        for (Integer i : keySet){
            if (valueMap.get(i).equals(value)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean removeEdge(NodeType pred, NodeType succ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeEdge'");
    }

    @Override
    public boolean containsEdge(NodeType pred, NodeType succ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'containsEdge'");
    }

    @Override
    public EdgeType getEdge(NodeType pred, NodeType succ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEdge'");
    }

    @Override
    public int getEdgeCount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEdgeCount'");
    }

    @Override
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shortestPathData'");
    }

    @Override
    public double shortestPathCost(NodeType start, NodeType end) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shortestPathCost'");
    }

    public static String[] coolMethod() {
        return (String[])(new Object[]{"cool","nice"});
    }
    public static void main(String[] args) {
        new GraphAM<String, Integer>();
    }
    
}
