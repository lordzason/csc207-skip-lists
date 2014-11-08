package taojava.util;

import java.util.Iterator;

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Leah Greenberg
 * @author Zhi Chen
 * 
 * Code from:
 * http://www.sanfoundry.com/java-program-implement-skip-list/
 * 
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  private Node header;

  private int infinity;

  private Node bottom = null;

  private Node tail = null;

  // +------------------+------------------------------------------------
  // | Internal Classes |
  // +------------------+

  /**
   * Nodes for skip lists.
   */
  public class Node
  {
    // +--------+--------------------------------------------------------
    // | Fields |
    // +--------+

    /**
     * The value stored in the node.
     */
    T val;
    /**
     * The node to the right.
     */
    Node right;
    /**
     * The node to the bottom.
     */
    Node down;

    /* Constructor */
    public Node(T x)
    {
      this(x, null, null);
    }//Node(T)

    /* Constructor */
    public Node(T x, Node rt, Node dt)
    {
      val = x;
      right = rt;
      down = dt;
    }//Node(T, Node, Node)

  } // class Node

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+
  public SkipList(int inf)
  {

    this.infinity = inf;

    bottom = new Node(0);

    bottom.right = this.bottom.down = this.bottom;

    tail = new Node(infinity);

    tail.right = this.tail;

    this.header = new Node(infinity, tail, bottom);

  }//SkipList(int)

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+

  // +-----------------------+-------------------------------------------
  // | Methods from Iterable |
  // +-----------------------+

  /**
   * Return a read-only iterator (one that does not implement the remove
   * method) that iterates the values of the list from smallest to
   * largest.
   */
  public Iterator<T> iterator()
  {
    // STUB
    return null;
  } // iterator()

  // +------------------------+------------------------------------------
  // | Methods from SimpleSet |
  // +------------------------+

  /**
   * Add a value to the set.
   *
   * @post contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to add, contains(lav) continues to hold.
   */
  public void add(T val)
  {
    // STUB
    Node current = header;

    bottom.val = val;

    while (current != bottom)
      {
        while (current.val.compareTo(val) < 0)
          {
            current = current.right;

            /*  If gap size is 3 or at bottom level and must insert, then promote middle element */
            if ((current.down.right.right.val).compareTo(current.val) < 0)
              {
                current.right =
                    new Node(current.val, current.right,
                             current.down.right.right);
                current.val = current.down.right.val;
              }//if()
            else
              {
                current = current.down;
              }//else()
          }//while()
      }//while()

    /* Raise height of skiplist if necessary */
    if (header.right != tail)
      {
        header = new Node(infinity, tail, header);
      }//if()
    
  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   */
  public boolean contains(T val)
  {
    // STUB
    return false;
  } // contains(T)

  /**
   * Remove an element from the set.
   *
   * @post !contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to remove, contains(lav) continues to hold.
   */
  public void remove(T val)
  {
    // STUB
  } // remove(T)

  // +--------------------------+----------------------------------------
  // | Methods from SemiIndexed |
  // +--------------------------+

  /**
   * Get the element at index i.
   *
   * @throws IndexOutOfBoundsException
   *   if the index is out of range (index < 0 || index >= length)
   */
  public T get(int i)
  {
    // STUB
    return null;
  } // get(int)

  /**
   * Determine the number of elements in the collection.
   */
  public int length()
  {
    // STUB
    return 0;
  } // length()

} // class SkipList<T>
