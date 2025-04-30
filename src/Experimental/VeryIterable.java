package Experimental;
import java.util.Iterator;

public interface VeryIterable<T> extends Iterable<T> {
    
    // we already have the iterator method
    public Iterator<T> reverseIterator();

}
