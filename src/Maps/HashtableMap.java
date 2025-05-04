package Maps;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.List;
import java.lang.Math;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HashtableMap<KeyType, ValueType>{

    /**
     * This is the protected class used to represent the key value pairs that are to be stored in the hashmap 
     */
    protected class Pair {

        public KeyType key;
        public ValueType value;

        /**
         * This creates a new pair with the given key and value
         */
        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }

    }

    private LinkedList<Pair>[] table = null; // our capacity is our table length
    private final double loadFactor = 0.8; // this is our load factor that represents how full our hashtable can be before we resize
    int size; // the current size of the hashtable

    /**
     * This creates a new hashtable with a given capacity
     */
    @SuppressWarnings("unchecked") 
     public HashtableMap(int capacity){ 
        table = (LinkedList<Pair>[])(new LinkedList[capacity]); // our capacity is our table length
        size = 0;
    }

    /**
     * This creates a new hashtable with the default capacity (64)
     */
    public HashtableMap(){ // with default capacity = 64
        this(64);
    } 


    /**
     * This is the hashmap function that maps keys to array indices
     */
    private int hashMap(KeyType key){
        return (Math.abs(key.hashCode()) % table.length);
    }


    /**
     * Adds a new key,value pair to the hashmap. It is ok that the value is null but not the
     * @param key the key of the key,value pair
     * @param value the value that key maps to
     * @throws IllegalArgumentException if key already maps to a value
     * @throws NullPointerException if key is null
     */
    public void put(KeyType key, ValueType value) throws IllegalArgumentException{

        // PARAMETER CHECKS
        if (key == null){
            throw new NullPointerException("We can't have a null key");
        }
        if (containsKey(key)){
            throw new IllegalArgumentException("Key already exists in map");
        }

        // RESIZE AND REHASH IF THE TABLE WILL BE AT CAPACITY WITH THE NEWLY ADDED VALUE
        if (willBeAtCapacity()){
            resizeAndRehash();
        }

        // TIME TO PLACE THE KEY VALUE PAIR 
        int index = hashMap(key);
        if (table[index] == null){
            LinkedList<Pair> chain = new LinkedList<>();
            table[index] = chain;
        }

        table[index].add(new Pair(key, value));
        size++;

    }

    /**
     * This helper method resizes and rehashes when our table will be at capacity if we add another value
     */
    @SuppressWarnings("unchecked")
    private void resizeAndRehash(){
        LinkedList<Pair>[] formerTable = table;
        table = (LinkedList<Pair>[])(new LinkedList[table.length*2]); // we make a hashtable with double the capacity
        size = 0;
        
        for (int i = 0; i < formerTable.length; i++){
            LinkedList<Pair> chain = formerTable[i];
            if (chain != null){
                for (Pair p : chain){
                    this.put(p.key, p.value);
                }
            }
        }
    }

    /**
     * Checks if the hashTable will be at capacity if we add a value
     */
    private boolean willBeAtCapacity(){
        return ((1.0)*(size+1) / table.length) >= loadFactor;
    }

    /**
     * Checks whether a key maps to a value in this collection.
     * @param key the key to check
     * @return true if the key maps to a value, and false is the
     *         key doesn't map to a value
     */
    public boolean containsKey(KeyType key){
        int index = hashMap(key);
        if (table[index] == null){
            return false;
        }
        

        // at this point, we know this index does have a linked list
        LinkedList<Pair> chain = table[index];
        for (Pair p : chain){ // and we check that chain to see if our key is in the table 
            if (p.key.equals(key)){ 
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves the specific value associated with a particular key
     * @param key the key to look up
     * @return the value that key maps to
     * @throws NoSuchElementException when key is not stored in this
     *         collection
     */
    public ValueType get(KeyType key) throws NoSuchElementException{
        int index = hashMap(key);
        if (table[index] == null){
            throw new NoSuchElementException("Key doesn't exist in hashtable");
        }
        

        // at this point, we know this index does have a linked list
        LinkedList<Pair> chain = table[index];
        for (Pair p : chain){
            if (p.key.equals(key)){ // if we find a pair with that key, we return the value
                return p.value;
            }
        }

        throw new NoSuchElementException("Key doesn't exist in hashtable");

    }

    /**
     * Remove the mapping for a key from this collection.
     * @param key the key whose mapping to remove
     * @return the value that the removed key mapped to
     * @throws NoSuchElementException when key is not stored in this
     *         collection
     */
    public ValueType remove(KeyType key) throws NoSuchElementException{

        LinkedList<Pair> chain = table[hashMap(key)];
        if (chain == null){ // if our chain is null, we know our key can't be in our table
            throw new NoSuchElementException("Key doesn't exist in map");
        }
        
        for (Pair p : chain){ // then we check our chain for the key
            if (p.key.equals(key)){
                chain.remove(p);
                return p.value;
            }
        }

        throw new NoSuchElementException("Key doesn't exist in map"); // and if we can't find it, then it doesn't exist in the map
    }

    /**
     * Removes all key,value pairs from this collection.
     */
    @SuppressWarnings("unchecked")
    public void clear(){
        size = 0;
        table = (LinkedList<Pair>[])(new LinkedList[table.length]);
    }

    /**
     * Returns the number of key value pairs stored in our hashmap
     */
    public int getSize(){
        return size;
    }

    /**
     * Returns the current capacity of our hash table
     */
    public int getCapacity(){
        return table.length;
    }

    /**
     * Retrieves this collection's keys.
     * @return a list of keys in the underlying array for this collection
     */
    public List<KeyType> getKeys(){

        LinkedList<KeyType> keys = new LinkedList<>();

        for (int i = 0; i < table.length; i++){
            LinkedList<Pair> chain = table[i];
            if (chain != null){
                for (Pair p : chain){
                    keys.add(p.key);
                }
            }
        }

        return keys;

    }

    

    /**
     * TEST 1: Basic tests that testes whether the Hashtable can properly store, check for and get the right value for a key
     */
    @Test
    public void test1(){

        HashtableMap<Integer, String> hashtable = new HashtableMap<>();

        // we add some key_value pairs
        hashtable.put(1, "Hello World");
        hashtable.put(2, "Cool beans");
        hashtable.put(3, "What's up y'all");
        hashtable.put(4, null);
        hashtable.put(7, "Niceties");
        hashtable.put(8, "1 + 1 is 2; -1 that's 3 quick maths");

        // check whether it's able to detect what it does and doesn't contain
        Assertions.assertTrue(!hashtable.containsKey(0));
        Assertions.assertTrue(!hashtable.containsKey(10));
        Assertions.assertTrue(!hashtable.containsKey((new Random()).nextInt(10) + 20)); // generates a number between 20 and 29

        Assertions.assertTrue(hashtable.containsKey(1) && hashtable.get(1).equals("Hello World"));
        Assertions.assertTrue(hashtable.containsKey(2) && hashtable.get(2).equals("Cool beans"));
        Assertions.assertTrue(hashtable.containsKey(3) && hashtable.get(3).equals("What's up y'all"));
        Assertions.assertTrue(hashtable.containsKey(4) && hashtable.get(4) == null);
        Assertions.assertTrue(hashtable.containsKey(7) && hashtable.get(7).equals("Niceties"));
        Assertions.assertTrue(hashtable.containsKey(8) && hashtable.get(8).equals("1 + 1 is 2; -1 that's 3 quick maths"));

    }

    /**
     * This tests that the right exceptions are thrown when trying to put and retrieve values
     */
    @Test
    public void test2(){

        HashtableMap<Integer, String> hashtable = new HashtableMap<>();
        Random rand = new Random();

        // TEST 1: NullPointer thrown with null key
        try{
            hashtable.put(null, "Hello");  
            Assertions.fail(); 
        }catch(NullPointerException e){
        }catch(Exception e){
            Assertions.fail(e.getClass() + " " + e.getMessage());
        }
        
        try{
            hashtable.put(null, "IS this null?? Oooh ");
            Assertions.fail();
        }catch(NullPointerException e){
        }catch(Exception e){
            Assertions.fail(e.getClass() + " " + e.getMessage());
        }

        // TEST 2: IllegalArgument thrown with duplicate key

        hashtable.put(9, "Ba bam!");

        try{
            hashtable.put(9, "Ba bam blam!");
            Assertions.fail();
        }catch(IllegalArgumentException e){
        }catch(Exception e){
            Assertions.fail(e.getClass() + " " + e.getMessage());
        }
        
        // TEST 3: NoSuchElement thrown when getting a non-existent key

        try{
            hashtable.get(rand.nextInt(40) + 10); // gets random keys betwen 10 - 49
            Assertions.fail();
        }catch(NoSuchElementException e){
        }catch(Exception e){
            Assertions.fail();
        }

    }


    /**
     * This tests the remove method
     */
    @Test
    public void test3(){


        HashtableMap<Integer, String> hashtable = new HashtableMap<>();
        Random rand = new Random();

        // we add some key_value pairs
        hashtable.put(1, "Hello World");
        hashtable.put(2, "Cool beans");
        hashtable.put(3, "What's up y'all");
        hashtable.put(4, null);
        hashtable.put(7, "Niceties");
        hashtable.put(8, "1 + 1 is 2; -1 that's 3 quick maths");

        List<Integer> keys = hashtable.getKeys();
        Assertions.assertTrue(keys.contains(1));
        Assertions.assertTrue(keys.contains(2));
        Assertions.assertTrue(keys.contains(3));
        Assertions.assertTrue(keys.contains(4));
        Assertions.assertTrue(keys.contains(7));
        Assertions.assertTrue(keys.contains(8));
        

        // TEST 1: Removing key value pairs in our hashmap works
        try{
            Assertions.assertTrue(hashtable.remove(1).equals("Hello World"));
            Assertions.assertTrue(hashtable.remove(2).equals("Cool beans"));
            Assertions.assertTrue(hashtable.remove(3).equals("What's up y'all"));
            Assertions.assertTrue(hashtable.remove(4) == null);
            Assertions.assertTrue(hashtable.remove(7).equals("Niceties"));
            Assertions.assertTrue(hashtable.remove(8).equals("1 + 1 is 2; -1 that's 3 quick maths"));
        }catch(NoSuchElementException e){
            Assertions.fail("Shouldn't throw a NoSuchElementException, keys are contained");
        }catch(Exception e){
            Assertions.fail("Exception shouldn't be thrown and bruh, this is even the Wrong Exception");
        }

        // TEST 2: Removing non-existent key value pairs throws NoSuchElement

        try{
            hashtable.remove(1000 + rand.nextInt(90)); // indices between 1000 and 1089
            Assertions.fail();
        }catch(NoSuchElementException e){
        }catch(Exception e){
            Assertions.fail("Bruh, wrong exception thrown!");
        }


    }

    /**
     * This tests the size and capactiy methods
     */
    @Test
    public void test4(){

        Random rand = new Random();
        int capacity = rand.nextInt(100) + 100; // 100 and 199 
        HashtableMap<Integer, Integer> hashtable = new HashtableMap<>(capacity);

        // TEST 1: Empty hashtable has size 0
        Assertions.assertEquals(0, hashtable.getSize());

        // TEST 2: Checking that the hashtable has the right capacity set
        Assertions.assertEquals(capacity, hashtable.getCapacity());
        
        // TEST 3: hashtable with a random number of items
        int numItems = rand.nextInt(78) + 10;
        for (int i = 0; i < numItems; i++){ // adding a random number of integers between 10 and 87
            hashtable.put(i, i);
        }

        Assertions.assertEquals(numItems, hashtable.getSize());

    }

    /**
     * This tests the clear method
     */
    @Test
    public void test5(){

        Random rand = new Random();
        HashtableMap<Integer, String> hashtable = new HashtableMap<>();

        int numItems = rand.nextInt(45) + 12; // random num between 12 and 56 
        for (int i = 0; i <numItems; i++){
            hashtable.put(i, "" + i + " is in the house!");
        }

        // TEST: 
        // We first test that our numbers are in the table
        Assertions.assertEquals(numItems, hashtable.getSize());
        // Then we clear
        hashtable.clear();
        // Then we check that our size is 0 and we don't contain any of the keys

        Assertions.assertEquals(0, hashtable.getSize());
        
        for (int i = 0; i < numItems; i++){
            Assertions.assertTrue(!hashtable.containsKey(i), "Shouldn't contain key: " + i);
        }

    }

    /**
     * This tests whether the hashMap is able to properly rehash
     */
    @Test
    public void test6(){

        HashtableMap<Character, String> hashMap = new HashtableMap<>(10);

        hashMap.put('A', " for apple");
        hashMap.put('B', " for ball");
        hashMap.put('C', " for cat");
        hashMap.put('D', " for dog");
        hashMap.put('E', " for ear");
        hashMap.put('F', " for fine");
        hashMap.put('G', " for gnat");

        // AT THIS POINT, WE'VE PUT 7 ITEMS, AND OUR LOAD FACTOR IS 0.8, 
        // so as we try and put our next element, our hashtable will rehash and resize
        hashMap.put('H', " for hot");

        // TEST 1: capacity should be doubled
        Assertions.assertEquals(20, hashMap.getCapacity());

        // Now we add some more elements
        hashMap.put('I', " for ice-cream");
        hashMap.put('J', " for joking");

        // TEST 2: we're still able to find all our elements and our size is the right value

        Assertions.assertTrue(hashMap.containsKey('A') && hashMap.get('A').equals(" for apple"));
        Assertions.assertTrue(hashMap.containsKey('B') && hashMap.get('B').equals(" for ball"));
        Assertions.assertTrue(hashMap.containsKey('C') && hashMap.get('C').equals(" for cat"));
        Assertions.assertTrue(hashMap.containsKey('D') && hashMap.get('D').equals(" for dog"));
        Assertions.assertTrue(hashMap.containsKey('E') && hashMap.get('E').equals(" for ear"));
        Assertions.assertTrue(hashMap.containsKey('F') && hashMap.get('F').equals(" for fine"));
        Assertions.assertTrue(hashMap.containsKey('G') && hashMap.get('G').equals(" for gnat"));
        Assertions.assertTrue(hashMap.containsKey('H') && hashMap.get('H').equals(" for hot"));
        Assertions.assertTrue(hashMap.containsKey('I') && hashMap.get('I').equals(" for ice-cream"));
        Assertions.assertTrue(hashMap.containsKey('J') && hashMap.get('J').equals(" for joking"));

        Assertions.assertEquals(10, hashMap.getSize());

    }


}