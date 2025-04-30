package Trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Trie class for storing general ascii strings
 */
public class Trie{
    
    private final int numberOfChars = 128;

    /**
     * 
     */
    protected class TrieNode{

        TrieNode[] edges;
        private boolean finalBit;
        

        public TrieNode(){
            edges = new TrieNode[128];
            finalBit = false;
        }

        public boolean finalBit(){
            return finalBit;
        }

        private int map(char c){
            return c;
        }

        public boolean hasEdgeAt(char c){
            return edges[map(c)] != null;
        }

        public TrieNode edgeAt(char c){
            return edges[map(c)];
        }

        public boolean noLeavingEdges(){
            boolean noLeavingEdges = true;
            for (TrieNode edge : edges){
                noLeavingEdges = noLeavingEdges && (edge == null); // so if the edge is ever not null, we know it's false
            }

            return noLeavingEdges;
        }

        public TrieNode getLeavingNodeOf(char c){
            return edges[c];
        }

        public void makeFinalBitFalse(){
            finalBit = false;
        }

        public void makeFinalBitTrue(){
            finalBit = true;
        }
            


        /**
         * Tries to add an edge for a particular character if it doesn't exist. If it does exist,
         * then no new edge is added and false is returned
         * @return true if a new edge for a letter is able to be added.
         */
        public boolean addEdge(char c){
            if (hasEdgeAt(c)){
                return false;
            }
            edges[map(c)] = new TrieNode();
            return true;
        }

        public void setEdge(char c, TrieNode node){
            edges[map(c)] = node;
        }

        @Override
        public String toString(){
            String returnString = "";
            char[] allAsciiChars = new char[numberOfChars];
            for (int i = 0; i < allAsciiChars.length; i++){
                allAsciiChars[i] = (char)i;
            }
            returnString += Arrays.toString(allAsciiChars) + "\n";

            char[] notNullOrNot = new char[numberOfChars];
            for (int i = 0; i < notNullOrNot.length; i++){
                notNullOrNot[i] = (edges[i] != null) ? 'T' : 'F';
            }
            returnString += Arrays.toString(notNullOrNot);

            return returnString;
        }

    }


    private TrieNode root;
    private int numItems;

    public Trie(){
        numItems = 0;
    }

    /**
     * Adds a new string to the Trie
     * @param s
     */
    public boolean add(String s){

        boolean alreadyContainsSequence = true;
        if (isEmpty()){
            root = new TrieNode();
        }

        char[] chars = s.toCharArray();

        TrieNode curr = root;
        for (char c : chars){
            if (!curr.hasEdgeAt(c)){
                alreadyContainsSequence = false; // if we have to add an edge it doesn't already contains the sequence
                curr.addEdge(c);
            }
            curr = curr.getLeavingNodeOf(c);
        }

        if (alreadyContainsSequence && curr.finalBit()){ // this is a check for whether the word is already in our trie
            // the curr node at this point is the node added when making or going through the edge for our last character
            // so if its final bit is 1, then we already contain that value
            return false;
        }


        numItems++;
        curr.makeFinalBitTrue();
        return true;

    }

    public boolean delete(String s){
        ArrayList<Boolean> indic = new ArrayList<>();
        deleteRecursive(s, root, indic);
        return indic.get(0);
    }

    /**
     * Recursive helper method for deleting strings
     * @param sequence the current sequence we're looking to delete
     * @param curr the current node we're deleting at
     * @param indic this arraylist is meant to be a shared variable across all method calls to check if the string sequence was
     * found or not. The whole point is that since we're returning a TrieNode, we can't return a boolean, so I create a "global" arraylist
     * that we can check the boolean condition for
     * @return
     */
    private TrieNode deleteRecursive(String sequence, TrieNode curr, ArrayList<Boolean> indic){
        if (sequence.isEmpty()){
            if (curr.finalBit()){
                indic.add(true);
            }else{ // string isn't in our trie
                indic.add(false);
                return curr;
            }
            
            if (curr.noLeavingEdges()){ // if we have no leaving edges
                return null;
            }else{
                return curr;
            }
        }

        if (!curr.hasEdgeAt(sequence.charAt(0))){ // if our next character doesn't exist in the sequence
            indic.add(false);
            return curr;
        }else{ // if we do have the character in our sequence
            curr.setEdge(sequence.charAt(0), deleteRecursive(sequence.substring(1), curr.edgeAt(sequence.charAt(0)), indic));
        }

        if (indic.get(0)){ // if our string was in our trie
            if (curr.noLeavingEdges() && !curr.finalBit()){ // our current node was only needed for the deleted string
                return null;
            }
        }
        
        return curr;
    }

    public List<String> allStrings(){
        return addingStringsRecursive("", root);
    }

    private List<String> addingStringsRecursive(String currString, TrieNode leavingNode){
        if (leavingNode.noLeavingEdges()){ // we should expect the final bit to be true, because at this point,
            // an existing node with no leaving edges must be an end node

            List<String> list = new ArrayList<>();
            list.add(currString);
            return list;
        }

        List<String> returnList = new ArrayList<>();
        if (leavingNode.finalBit()){ // this node does have leaving edges, but a string may also end here, so we check and add that
            returnList.add(currString);
        }

        for (int i = 0; i < numberOfChars; i++){
            if (leavingNode.edges[i] != null){ // so what we do ehn, is that if the index of a character has a non-null value
                                                // we know that character sequence exists and we add that and then keep trying to extend
                                                // in that character's leaving node
                returnList.addAll(addingStringsRecursive(currString+""+(char)(i), leavingNode.edges[i]));
            }
        }

        return returnList;

    }

    /**
     * Returns the number of elements in the trie
     * @return
     */
    public int size(){
        return numItems;
    }


    /**
     * Returns true if the trie is empty
     * @return
     */
    public boolean isEmpty(){
        return (root == null);
    }

    public static void main(String[] args) {
        Trie t = new Trie();
        t.add("hello");
        t.add("hello world");
        t.add("hi");
        t.add("cool");
        t.add("sup");
        t.add("niceeee");
        t.add("high");
        System.out.println(t.allStrings());
        System.out.println(t.delete("cool"));
        System.out.println(t.allStrings());
    }
    
    
}
