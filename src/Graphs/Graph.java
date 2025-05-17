package Graphs;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.NoSuchElementException;

import Maps.HashtableMap;

/**
 * This is an adjacency list representation of a graph. Although we use a hashtable map with chained collision
 * handling to store the adjacent nodes for efficiency
 */
public class Graph<NodeType, EdgeType extends Number> implements GraphADT<NodeType, EdgeType>{

    /**
     * Node class to represent nodes in this graph
     */
    protected class Node{
        NodeType data;


        /** Stores an edge going out of this node and the node it's going to */
        HashtableMap<Node, Edge> edgesOutgoing;
        /** Stores an edge coming into this node and the node it's coming from */
        HashtableMap<Node, Edge> edgesIncoming;

        /**
         * Creates a new node
         * @param data
         */
        public Node(NodeType data){
            this.data = data;
            edgesOutgoing = new HashtableMap<>(5);
            edgesIncoming = new HashtableMap<>(5);
        }

        /**
         * Added a new edge to the edge list. Making sure that the node we're going to also know's we're an incoming node
         * 
         * @param newAdjacent
         */
        public void addEdge(Node goingTo, EdgeType weight){

            try{ // we see if our edge list already contains this node, and if it does
                 // we just update the weight with the new weight
                Edge edge = edgesOutgoing.get(goingTo);
                edge.weight = weight;
            }catch(NoSuchElementException e){ // if we don't have this edge, we create this edge and add it to our outgoing edge list
            // and our goingTo's node ingoing edge list
                Edge outgoingEdge = new Edge(weight, goingTo);
                Edge incomingEdge = new Edge(weight, this);

                edgesOutgoing.put(goingTo, outgoingEdge);
                goingTo.edgesIncoming.put(this, incomingEdge);
            }
        }

    }

    /**
     * This edge class maintains edges for this graph
     * Whether or not an edge is incoming our outgoing depends on which list it's being stored in
     */
    protected class Edge{
        EdgeType weight;
        Node connection;

        /**
         * Creates a new edge
         * @param weight the weight of the edge
         * @param goingTo the node we're going to
         */
        public Edge(EdgeType weight, Node connection){
            this.weight = weight;
            this.connection = connection;
        }
    }

    HashtableMap<NodeType, Node> nodeMapper = new HashtableMap<>();

    /**
     * Inserts a new node into our graph with the given data data
     * 
     * @param data - the data to be contained in the given node
     * @return false if our graph already contains the node (i.e. a node that contains data that .equals() our given data)
     */
    @Override
    public boolean insertNode(NodeType data) {

        if (nodeMapper.containsKey(data)){
            return false;
        }

        nodeMapper.put(data, new Node(data));
        return true;

    }

    /**
     * Removes a node from the graph and all it's associated connection
     */
    @Override
    public boolean removeNode(NodeType data) {
        
        Node nodeToRemove;

        try{
            nodeToRemove = nodeMapper.get(data);
        }catch(NoSuchElementException e){
            return false;
        }

        // First, we remove all the incoming edges of us in the nodes that we're going to
        
        List<Node> outgoingConnections = nodeToRemove.edgesOutgoing.getKeys();
        for (Node node : outgoingConnections){
            node.edgesIncoming.remove(nodeToRemove);
        }

        // And then we remove the outgoing edges of us from nodes that are incoming to us
        List<Node> incomingConnections = nodeToRemove.edgesIncoming.getKeys();
        for (Node node : incomingConnections){
            node.edgesOutgoing.remove(nodeToRemove);
        }

        nodeMapper.remove(data);
        return true;

    }

    /**
     * Checks if our graph contains a node with the given data i.e. a node that contains data
     * that .equals() returns true when compared with this data
     * 
     * @param data - the data
     */
    @Override
    public boolean containsNode(NodeType data) {
        return nodeMapper.containsKey(data);
    }

    /**
     * Returns a list of the data of all the nodes in the graph
     */
    @Override
    public List<NodeType> getAllNodes() {
        return nodeMapper.getKeys();
    }

    /**
     * This returns the total amount of nodes in our graph
     */
    @Override
    public int getNodeCount() {
        return nodeMapper.getSize();
    }

    /**
     * Inserts a new edge between the given nodes from pred to succ. If the nodes don't already exist in the graph,
     * the are created
     * 
     * @param pred
     * @param succ
     * @param weight
     * @return
     */
    @Override
    public boolean insertEdge(NodeType pred, NodeType succ, EdgeType weight) {
        Node predNode;
        Node succNode;

        try{
            predNode = nodeMapper.get(pred);
            succNode = nodeMapper.get(succ);
        }catch(NoSuchElementException e){ // if we don't already have the nodes in our graph, we add them
            insertNode(pred);
            insertNode(succ);

            predNode = nodeMapper.get(pred);
            succNode = nodeMapper.get(succ);
        }

        predNode.addEdge(succNode, weight);

        return true;
    }

    @Override
    public boolean removeEdge(NodeType pred, NodeType succ) {
        Node predNode;
        Node succNode;
        
        // we get our predecessor and successor nodes
        try{
            predNode = nodeMapper.get(pred);
            succNode = nodeMapper.get(succ);
        }catch(NoSuchElementException e){
            return false;
        }

        try{
            predNode.edgesOutgoing.remove(succNode);
        }catch(NoSuchElementException e){ // if our predecessor doesn't contain the successor as an outgoing edge, we can't remove it
            return false;
        }

        // if we do have the edge, we don't forget to remove the incoming edge from the successor node
        succNode.edgesIncoming.remove(predNode);
        return true;

    }

    /**
     * Checks if the graph contains an edge between the two given nodes
     */
    @Override
    public boolean containsEdge(NodeType pred, NodeType succ) {
        try{
            Node predNode = nodeMapper.get(pred);
            Node succNode = nodeMapper.get(succ);

            for (Node node : predNode.edgesOutgoing.getKeys()){
                if (node.equals(succNode)){
                    return true;
                }
            }

            return false;

        }catch(NoSuchElementException e){
            return false;
        }

    }


    /**
     * Gets an edge between the two nodes pred and succ if it exists
     * If the nodes aren't in our graph or if an edge doesn't exist between them, a NoSuchElementException is thrown
     * 
     * @return the weight of the edge between the given nodes if it exists
     * @throws NoSuchElementException if the nodes aren't in our graph or no edge exists between them
     */
    @Override
    public EdgeType getEdge(NodeType pred, NodeType succ) throws NoSuchElementException{

        Node predNode = nodeMapper.get(pred); // if the nodes aren't in our graph, then this method naturally throws a NoSuchElementException
        Node succNode = nodeMapper.get(succ);

        for (Node node : predNode.edgesOutgoing.getKeys()){
            if (node.equals(succNode)){
                return predNode.edgesOutgoing.get(succNode).weight;
            }
        }

        throw new NoSuchElementException("Edge between nodes doesn't exist in our graph");
    }

    /**
     * This gets the total number of edges in the graph
     * Because of my implementation, this has complexity O(N), where N is the number of nodes
     */
    @Override
    public int getEdgeCount() {
        int count = 0;

        for (NodeType nodeVal : nodeMapper.getKeys()){
            Node node = nodeMapper.get(nodeVal);
            count += node.edgesOutgoing.getSize();
        }

        return count;
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

    /**
     * Creates a dot file in the current working directory of the current state of the graph
     */
    public void toDotFile(String filename) throws IOException{

        String fileString = "digraph " + filename + " {\n";
        for (NodeType nodeVal : nodeMapper.getKeys()){
            
            Node node = nodeMapper.get(nodeVal);
            List<Node> edgeNodes = node.edgesOutgoing.getKeys();

            if (edgeNodes.isEmpty()){
                fileString += String.format("   \"%s\";\n", node.data);
            }else{
                for (Node edgeNode : edgeNodes){
                    Edge edge = node.edgesOutgoing.get(edgeNode);
                    fileString += String.format("   \"%s\" -> \"%s\" [length=%.3f];\n", node.data, edgeNode.data, edge.weight.doubleValue());
                }
            }

        }

        fileString += "}";


        try{
            PrintWriter pw = new PrintWriter(filename+".dot");
            pw.println(fileString);
            pw.close();
        }catch(IOException e){
            throw new IOException("File already exists");
        }

    }

    public static void main(String[] args) {

        Graph<String, Integer> graph = new Graph<>();
        graph.insertEdge("Sussex", "Lannon", 89);
        graph.insertEdge("Sussex", "Milwaukee", 80);
        graph.insertEdge("Superior", "San Antonio", 5000);
        graph.insertEdge("Sussex", "Germantown", 50);
        graph.insertEdge("Sussex", "Appleton", 67);
        graph.insertEdge("Milwaukee", "Chicago", 100);
        graph.insertEdge("San Antonio", "Austin", 200);
        graph.insertEdge("Dallas", "Austin", 90);
        graph.insertEdge("Sussex", "Chicago", 120);
        graph.insertEdge("Sussex", "Menomenee Falls", 90);
        graph.insertEdge("Madison", "Neenah", 85);
        graph.insertEdge("Milwaukee", "Madison", 100);
        graph.insertEdge("Madison", "Superior", 200);
        graph.insertEdge("Germantown", "Menomenee Falls", 90);



        try{
            graph.toDotFile("mygraph");
        }catch(IOException e){
            System.out.println("Couldn't make file");
        }
        

    }

    
    
}
