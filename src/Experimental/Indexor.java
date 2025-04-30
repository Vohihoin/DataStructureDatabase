package Experimental;
import java.util.Iterator;

/**
 * This interface builds on the iterator interface. It takes an iterator over some values and then indexes the iterators
 * current values to return. You can then call get on some index i and get the i+1 th value that would have been returned by
 * the iterator i.e. we start indexing from 0
 * 
 * @param T the type of values we return
 */
public interface Indexor<T> {
    
    /**
     * 
     */
    public void setIterator(Iterator<T> iterator);

    /**
     * 
     * @param i
     * @return
     * @throws IndexOutOfBoundsException
     */
    public T get(int i);

}
