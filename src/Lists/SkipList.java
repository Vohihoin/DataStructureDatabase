package Lists;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * An ordered list of elements
 */
public class SkipList<T extends Comparable<T>> implements List<T>{

    /**
     * Skip list iterator that iterates over all the elements in the list in order
     */
    protected class SkipListIterator implements Iterator<T>{

        SkipList<T> list;
        ListNode curr;

        /**
         * Creates a new skip list iterator over a given list
         * @param list
         */
        public SkipListIterator(SkipList<T> list){
            curr = list.root;
        }

        @Override
        public boolean hasNext() {
            return (curr.nextAt(0) != null); // we use lane level 0 because every element is there, so this is just a linked listS
        }

        @Override
        public T next() {
            if (!hasNext()){
                throw new NoSuchElementException("Don't have a next value");
            }
            T toReturn = curr.nextAt(0).data;
            curr = curr.nextAt(0);
            return toReturn;
        }


    }

    /**
     * This protected helper class is what makes up the nodes that form the skip list 
     */
    protected class ListNode{
        
        /**
         * The skip list node data
         */
        T data;
        /**
         * The arraylist of all the links at different levels in this skip list
         */
        ArrayList<ListNode> nextsInLanes = new ArrayList<>();

        /**
         * This creates a new list node with null data
         */
        public ListNode(){
            this.data = null;
        }

        /**
         * This creates a new node with the given data
         * @param data the given data
         */
        public ListNode(T data){
            this.data = data;
        }

        /**
         * This essentialy adds a lane at a particular level. The lanes start from 0, and go on.
         * This method will however not add a lane if the laneLevel > currentHighestLevel + 1. 
         * So, it can change the next value a node points to in a particular lane and add one more lane above
         * but it can't add more than one lane above
         * 
         * @param laneLevel the laneLevel we're setting our next node at
         * @param next the next node we're now pointing to at the given laneLevel
         * @return true if we can add the given next node at the next level, and we always can except if
         * the laneLevel > currentHighestLevel + 1
         */
        public boolean addNextAt(int laneLevel, ListNode next){
            if (laneLevel > nextsInLanes.size()){
                return false; // we should add connections in the lanes below
            }
            if (laneLevel == nextsInLanes.size()){
                nextsInLanes.add(next);
            }else{
                nextsInLanes.set(laneLevel, next);
            }
            
            return true;
        }

        /**
         * This finds the next node after us at a given level
         * @param laneLevel
         * @return the next node after us at a given level
         * @throws IllegalArgumentException if laneLevel > currentHighestLevel
         */
        public ListNode nextAt(int laneLevel) throws IllegalArgumentException{
            try{
                return nextsInLanes.get(laneLevel);
            }catch(IndexOutOfBoundsException e){
                throw new IllegalArgumentException("We don't have a lane that high yet");
            }
        }

        /**
         * Returns the highest lane level of a given node
         * @return the highest lane level of a given node
         */
        public int highestLevel(){
            return nextsInLanes.size()-1;
        }

    } 
   
    
    // NOW WE HAVE OUR ACTUAL SKIP LIST STUFF
    private int size;
    private ListNode root;

    /**
     * This makes a new empty skiplist
     */
    public SkipList(){
        this.size = 0;
        root = new ListNode();
    }


    /**
     * This adds a new value to the skiplist. The skiplist is an ordered collection, so it places the value in order in the list
     */
    @Override
    public boolean add(T newElement) throws NullPointerException{

        if (newElement == null){
            throw new NullPointerException("Can't have null objects because this is an ordered list");
        }

        ListNode newNode = new ListNode(newElement);
        Random rand = new Random();

        int highestLevel = 0;
        // our node is automatically in level 0
        
        while (true){ // we flip a coin to decide how many levels our node appears on
            if (!rand.nextBoolean()){
                break;
            }
            highestLevel++;
        }

        // System.out.println(newElement + ": " + highestLevel);

        for (int i = 0; i <= highestLevel; i++){
            newNode.addNextAt(i, null); 
            // this initialization is very useful because we building our connections from the 
            // top downwards and they also help make sure that if a node is added last, it points to null indicating the end
        }

        // WE'RE ADDING OUR FIRST NODE, very simple case
        if (isEmpty()){ 
            for (int i = 0; i <= highestLevel; i++){
                root.addNextAt(i, newNode);
            }
            size++;
            return true;
        }

        // OTHER NON FIRST NODE CASE
        // We have to traverse our skip list

        // BUT FIRST IF OUR NODES HIGHEST LEVEL IS GREATER THAN OUR SKIP LISTS HIGHEST LEVEL
        // WE FIRST CONNECT IT THERE

        int currLevel = root.highestLevel(); // we set this here above because we add more levels to root
        // but if we're adding, we'd already have added our nodes to that level
        // System.out.println("Root's highest level: " + currLevel);

        if (highestLevel > root.highestLevel()){
            int startIndex = root.highestLevel()+1;
            for (int i = startIndex; i <= highestLevel; i++){
                root.addNextAt(i, newNode);
            }
        }

        ListNode curr = root;
        while (currLevel >= 0){ // loop for adding values

           
            ListNode lookedAhead = curr.nextAt(currLevel); 
            // System.out.println("Current level at: " + currLevel + " Current node: " + curr.data + " Looked ahead: " + ((lookedAhead == null) ? lookedAhead : lookedAhead.data));

            if (lookedAhead == null){
                // then we're the last element to be added in this lane
                if (currLevel <= highestLevel){
                    curr.addNextAt(currLevel, newNode);
                }
                currLevel--;
                continue;
            }

            if (lookedAhead.data.compareTo(newNode.data) <= 0){ // lookedAhead < newData
                curr = lookedAhead; // <= makes sure that nodes that get added after are later in the list because we traverse
                continue;
            }

            if (lookedAhead.data.compareTo(newNode.data) > 0){
                if (currLevel <= highestLevel){
                    // so we insert our new node between the looked ahead and current
                    newNode.addNextAt(currLevel, lookedAhead);
                    curr.addNextAt(currLevel, newNode);
                }
                currLevel--; // we lower our level because we found a value greater than our new value
            }

        }

        size++;
        return true;
    }

    /**
     * This contains a string of all the data in all the lanes
     * from the highest to lowest lane
     * @return
     */
    public String laneStrings(){
        String returnString = "";
        for (int i = root.highestLevel(); i > -1; i--){
            returnString += getLaneData(i) + "\n";
        }

        return returnString;
    }

    /**
     * Gets the size of a given lane
     * @param laneNumber
     * @return
     * @throws
     */
    public int getLaneSize(int laneNumber){

        if (root.highestLevel() < laneNumber){
            throw new IllegalArgumentException("We don't have these many lanes");
        }

        ListNode curr = root;
        int count = 0;
        while (curr != null){
            count++;
            curr = curr.nextAt(laneNumber);
        }

        return count - 1; // because we also counted the root

    }

    /**
     * Gets the data for all the nodes stored in a given lane
     * @param laneNumber
     * @return a list of the data in a given of the skip list
     * @throws IllegalArgumentException if the skip list doesn't have a lane this high
     */
    public List<T> getLaneData(int laneNumber){

        if (root.highestLevel() < laneNumber){
            throw new IllegalArgumentException("We don't have these many lanes");
        }

        LinkedList<T> linkedList = new LinkedList<>();
        ListNode curr = root;
        while (curr != null){
            linkedList.add(curr.data);
            curr = curr.nextAt(laneNumber);
        }

        linkedList.remove(0);
        return linkedList;
    }

    /**
     * Returns the size of the skiplist
     * @return the size of the skiplist
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns true is the skip list is empty
     * @return true if the skip list is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Checks if the skiplist contains a given item
     * @return true if the skiplist contains a given item, false otherwise
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object item) {
        try{
            T toFind = (T)item;
        }catch(ClassCastException | NullPointerException e){
            return false;
        }

        T toFind = (T)item;

        int currLevel = root.highestLevel();
        ListNode curr = root;

        while(currLevel >= 0){

            ListNode lookAhead = curr.nextAt(currLevel);

            if (lookAhead == null){ // then our value is greater than everything at this level
                currLevel--;
                continue;
            }
            if (lookAhead.data.compareTo(toFind) > 0){ //  lookAhead > toFind, so we know our value doesn't exist on this level
                currLevel--;
                continue;
            }
            if (lookAhead.data.equals(toFind)){ // we found at least one instance of our value
                return true;
            }
            
            // else our lookead is less than us, so we can traverse it
            curr = lookAhead;

        }

        // if we've gone through all the lanes at each level and our value isn't there, we can return false;
        return false;
    }

    /**
     * Returns an iterator over the skiplists elements. This iterator goes over the skip lists elements in order
     * @return an iterator over the skiplists elements.
     */
    @Override
    public Iterator<T> iterator() {
        return (new SkipListIterator(this));
    }

    /**
     * Returns an array of the skip lists current elements
     */
    @Override
    public Object[] toArray() {
        return getLaneData(0).toArray(); // our 0 lane contains all our datas
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toArray'");
    }



    
    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {

        boolean removedAtLeastOne = false; // boolean indicator if we removed at least one value

        try{
            T val = (T)o;
        }catch(ClassCastException | NullPointerException e){
            return false;
        }
        T val = (T)o;

        int currLevel = root.highestLevel();
        ListNode curr = root;

        while(currLevel >= 0){

            ListNode lookAhead = curr.nextAt(currLevel);
            if (lookAhead == null){ // we've reached the end of this layer, but everything here is still less than us,
                                    // so we go down a layer that may have higher values
                currLevel--;
                
            }
            if (lookAhead.data.compareTo(val) < 0){ // the looked ahead < current, we traverse
                curr = lookAhead;
                continue;
            }
            if (lookAhead.data.equals(val)){ // we found our value to remove
                curr.addNextAt(currLevel, lookAhead.nextAt(currLevel)); // we bypass our looked ahead value, removing it
                // but we may still have duplicates of our value at this level, so we stay here with the same level and curr node
                // until we find a value greater than us
                removedAtLeastOne = true;
                continue;
            }
            
            // WHEN WE FIND A VALUE GREATER THAN US, WE CAN NOW GO DOWN A LEVEL
            currLevel--;

        }

        return removedAtLeastOne;

    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'containsAll'");
    }

    /**
     * Adds all the items in the collection. Only returns false if the collection contains a null item because we can't add null items
     */
    @Override
    public boolean addAll(Collection<? extends T> collection) {
        for (T item : collection){
            if (!this.add(item)){
                return false;
            }
        }
        return true;

    }

    /**
     * Adding at index isn't supported for skipList because it is an ordered list
     * 
     * @throws UnsupportedOperationException because this operation isn't supported
     * by skip lists
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Adding at index isn't supported for skipList because it is an ordered list");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeAll'");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'retainAll'");
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }

    @Override
    public T get(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public T set(int index, T element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public void add(int index, T element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public T remove(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }

    @Override
    public int indexOf(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'indexOf'");
    }

    @Override
    public int lastIndexOf(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'lastIndexOf'");
    }

    @Override
    public ListIterator<T> listIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'subList'");
    }

    public static void main(String[] args) {
        SkipList<Integer> skipList = new SkipList<>();
        
        skipList.add(5);
        skipList.add(1);
        skipList.add(12);
        skipList.add(90);

        System.out.println(skipList.contains(0));
        System.out.println(skipList.contains(5));
        System.out.println(skipList.contains(1));
        System.out.println(skipList.contains(90));
        System.out.println(skipList.contains(12));
        System.out.println(skipList.contains(1000));
        
    }
    
}
