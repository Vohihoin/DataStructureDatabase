package Lists;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * An ordered list of elements
 */
public class SkipList<T extends Comparable<T>> implements Collection<T>{

    /**
     * Skip list iterator that iterates over all the elements in the list in order
     */
    protected class SkipListIterator implements Iterator<T>{

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
    public int size() {
        return size;
    }

    /**
     * Returns true is the skip list is empty
     * @return true if the skip list is empty, false otherwise
     */
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Checks if the skiplist contains a given item
     * @return true if the skiplist contains a given item, false otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean contains(Object item) {

        T toFind;

        try{
            toFind = (T)item;
        }catch(ClassCastException | NullPointerException e){
            return false;
        }

        toFind = (T)item;

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
    public Iterator<T> iterator() {
        return (new SkipListIterator(this));
    }

    /**
     * Returns an array of the skip lists current elements
     */
    public Object[] toArray() {
        return getLaneData(0).toArray(); // our 0 lane contains all our datas
    }



    /**
     * 
     * @throws ClassCastException if there's a type mismatch between the array type given and our generic type i.e if the elements in our
     * skiplist can't be casted into the given array type
     */
    @SuppressWarnings("unchecked")
    public <P> P[] toArray(P[] a) throws ClassCastException{

        try{

            if (a.length < size){ // if our array can't fit all our elements, we make a new array with the given type and place our elements there
                return (P[])Arrays.copyOf(toArray(), size, a.getClass());
            }

            System.arraycopy(toArray(), 0, a, 0, size);
            if (a.length > size){
                a[size] = null;
            }
            return a;

        }catch(ClassCastException | ArrayStoreException e){
            throw new ClassCastException("Type mismatch beteen our elements and the array type given, we can't cast our array to the given"+
                                            " type.");
        }

    }



    
    /**
     * Removes the first instance it finds of a given object from the skiplist if it exists. This first instance will
     * typically be the least recently added instance of this object
     * 
     * @return true if the object exists and thus
     */
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {

        T val;
        try{
            val = (T)o;
        }catch(ClassCastException | NullPointerException e){
            return false;
        }
        val = (T)o;

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
                return true;
            }
            
            // WHEN WE FIND A VALUE GREATER THAN US, WE CAN NOW GO DOWN A LEVEL
            currLevel--;

        }

        return false;

    }

    /**
     * Checks if the list contains all of the elements in the given collection
     * 
     * Has O(klogn) complexity where n is the skip list size and k is the collection's size
     * @param collection - the collection of items to look for in our skiplist
     * @throws NullPointerException if the collection given is null
     * @throws ClassCastException
     * 
     * @return true if the skiplist contains all the desired elements
     */
    @SuppressWarnings("unchecked")
    public boolean containsAll(Collection<?> collection) {

        if (collection == null){
            throw new NullPointerException("Collection given can't be null!");
        }

        try{

            Collection<T> toUse = (Collection<T>)(collection);

            // This is just a quick check to improve efficiency. We know that we don't allow null elements
            // so, if a collection contains null elements then we can't possibly contain every element in it
            try{
                if (collection.contains(null)){
                    return false;
                }
            }catch(NullPointerException e){ // if we get a null pointer exception, this collection doesn't allow nulls, neither do we, so we're good with it
            }
            
            boolean containsAll = true;
            for (T element : toUse){
                containsAll &= contains(element);
            }
            return containsAll;

        }catch(ClassCastException e){
            throw new ClassCastException("Can't work with elements not of our given generic type");
        }

    }

    /**
     * Adds all the items in the collection. Only returns false if the collection contains a null item because we can't add null items
     */
    public boolean addAll(Collection<? extends T> collection) {
        for (T item : collection){
            if (!this.add(item)){
                return false;
            }
        }
        return true;

    }


    /**
     * Removes every element from the list that is contained in that collection
     * 
     * @throws ClassCastException if the collection's type is not equals to our skip list's type
     * @throws NullPointerException if the collection given is null
     */
    @SuppressWarnings("unchecked")
    public boolean removeAll(Collection<?> collection) throws ClassCastException, NullPointerException{

        if (collection == null){
            throw new NullPointerException("Can't work with null collections");
        }

        try{

            Collection<T> toUse = (Collection<T>)(collection);
            boolean removedAtLeastOne = false;
            for (T element : toUse){
                removedAtLeastOne |= this.remove(element);
            }
            return removedAtLeastOne;

        }catch(ClassCastException e){
            throw new ClassCastException( "We don't allow collections that are not of the generic type of our skiplist");
        }

    }

    /**
     * Removes everything from the list that is not in the collection
     */
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'retainAll'");
    }

    /**
     * Clears the entire skip list
     */
    public void clear() {
        root.nextsInLanes.clear();
        size = 0;
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
