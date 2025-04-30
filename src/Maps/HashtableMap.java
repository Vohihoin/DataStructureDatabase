package Maps;
import java.util.LinkedList;

public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType>{

    protected class Pair {

        public KeyType key;
        public ValueType value;

        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }

    }

    private int capacity;
    private LinkedList<Pair>[] table = null;

    /**
     * This creates a new hashtable with a given capacity
     */
    @SuppressWarnings("unchecked") 
     public HashtableMap(int capacity){ 
        this.capacity = capacity;
        table = (LinkedList<Pair>[])(new LinkedList[capacity]);
    }

    /**
     * This creates a new hashtable with the default capacity
     */
    public HashtableMap(){ // with default capacity = 64
        new HashtableMap<>(64);
    }




}