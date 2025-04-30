package Experimental;

/**
 * A class is Indexable over T if it can return an Indexor over T
 * An Indexor is an object built over an iterator that takes the elements that an iterator returns
 * and labels them with the natural numbers (N) including 0 
 * 
 * Typically a class that is indexable stores some kind of data, but it doesn't have to
 * Also, since Indexable extends Iterable, a class that is Indexable also has to Iterable
 * @see Indexor
 * @param T the type of elements we're indexing
 */
public interface Indexable<T> extends Iterable<T>{
    

    /**
     * 
     * @return
     */
    public Indexor<T> indexor();


}
