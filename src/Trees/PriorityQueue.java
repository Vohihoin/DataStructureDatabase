package Trees;

import java.util.Arrays;
import java.util.Random;

/**
 * Heap implementation of priority queue. This heap can either be a min or max heap, but that has to be set
 * when creating the heap. By default, the heap is min.
 */
public class PriorityQueue<T extends Comparable<T>>{

    private Object[] heapArray;
    private int nextIndex;
    private final boolean IS_MIN;

     
    public PriorityQueue(){
        heapArray = new Object[10];
        IS_MIN = true;
    }

    public PriorityQueue(int capacity){
        heapArray = new Object[capacity];
        IS_MIN = true;
    }

    public PriorityQueue(boolean IS_MIN){
        heapArray = new Object[10];
        this.IS_MIN = IS_MIN;
    }

    /**
     * Creates a new queue with the given initial capacity
     * @param capacity
     * @param IS_MIN
     */
    public PriorityQueue(int capacity, boolean IS_MIN){
        heapArray = new Object[capacity];
        this.IS_MIN = IS_MIN;
    }  

    /**
     * Adds the new element to the queue.
     * @param element
     * @throws NullPointerException if element is null
     */
    public void enqueue(T element){
        if (element == null){
            throw new NullPointerException();
        }
        if (isFull()){
            increaseSize();
        }

        heapArray[nextIndex] = element;
        heapifyUp(nextIndex++);

    }

    /**
     * Returns the topmost element in the queue. If in a min heap, the smallest value, and if a max heap, the
     * largest. This doesn't however remove the elements from the queue.
     * @return the topmost element in the queue, or null if the queue is empty
     */
    @SuppressWarnings("unchecked")
    public T peek(){
        if (isEmpty()){
            return null;
        }
        return (T)heapArray[0];
    }

    /**
     * Returns the topmost element in the queue. If in a min heap, the smallest value, and if a max heap, the
     * largest. This does remove the topmost element from the queue.
     * @return the topmost element in the queue, or null if the queue is empty
     */
    @SuppressWarnings("unchecked")
    public T poll(){
        if (isEmpty()){
            return null;
        }

        T toReturn = (T)(heapArray[0]);
        heapArray[0] = heapArray[nextIndex-1];
        heapArray[nextIndex-1] = null;
        nextIndex--;

        heapifyDown(0);

        return toReturn;

    }

    /**
     * Heapify's up the index to maintain the heap property
     * @param index
     */
    @SuppressWarnings("unchecked")
    private void heapifyUp(int index){

        
        while(index > 0){
            if (!switchUp((T)heapArray[index], (T)heapArray[parent(index)])){
                break;
            }
            switchIndices(index, parent(index));
            index = parent(index);
        }
        
    }

    /**
     * 
     * Heapifies down the index to maintain the heap property
     * 
     */
    @SuppressWarnings("unchecked")
    private void heapifyDown(int index){

        while(hasLeftChild(index)){
            
            T representativeChild = (T)heapArray[leftChild(index)];
            int representativeChildIndex = leftChild(index);

            if (hasRightChild(index) && switchUp((T)heapArray[rightChild(index)], (T)heapArray[leftChild(index)])){
                representativeChild = (T)heapArray[rightChild(index)];
                representativeChildIndex = rightChild(index);
            }

            //System.out.println(representativeChildIndex + " " + heapArray[index] + " " + representativeChild);
            if (!switchUp(representativeChild, (T)heapArray[index])){
                break;
            }

            switchIndices(index, representativeChildIndex);
            index = representativeChildIndex;

        }

    }


    /**
     * Checks if the object at this index has a left child
     * @param index
     * @return
     */
    private boolean hasLeftChild(int index){
        return leftChild(index) < nextIndex;
    }

    /**
     * Checks if the object at this index has a right child
     * @param index
     * @return
     */
    private boolean hasRightChild(int index){
        return rightChild(index) < nextIndex;
    }

    /**
     * Switches the objects at indices 1 and 2
     * @param index1
     * @param index2
     */
    private void switchIndices(int index1, int index2){
        Object temp = heapArray[index1];
        heapArray[index1] = heapArray[index2];
        heapArray[index2] = temp;
    }

    /**
     * Returns true if child has greater priority than the parent
     * @return
     */
    public boolean switchUp(T child, T parent){
        return (IS_MIN && child.compareTo(parent) < 0) || (!IS_MIN && child.compareTo(parent) > 0);
    }

    /**
     * Returns the index of the parent of an index
     * @param index
     * @return
     */
    private int parent(int index){
        return (index - 1)/2;
    }

    /**
     * Returns the index of the right child of the current index.
     * @param index
     * @return
     */
    private int rightChild(int index){
        return 2*index + 2;
    }

    /**
     * Returns the index of the left child of this current index
     * @param index
     * @return
     */
    private int leftChild(int index){
        return 2*index + 1;
    }

    /**
     * Returns true if the current array holding the queue is full
     * @return
     */
    private boolean isFull(){
        return nextIndex == heapArray.length;
    }

    /**
     * Returns the current size of the priority queue
     * @return the current size of the priority queue
     */
    public int size(){
        return nextIndex;
    }

    /**
     * Returns true if the heap is empty
     * @return true if the heap is empty
     */
    public boolean isEmpty(){
        return nextIndex == 0;
    }

    /**
     * Increases the size of the array when it gets full
     */
    private void increaseSize(){
        heapArray = Arrays.copyOf(heapArray, nextIndex * 2);
    }

    public static void main(String[] args) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(true);
        Random rand = new Random();
        for (int i = 0; i < 34; i++){
            pq.enqueue(rand.nextInt(8000));
        }

        while(!pq.isEmpty()){
            System.out.println(pq.poll());
        }
    }
    
}
