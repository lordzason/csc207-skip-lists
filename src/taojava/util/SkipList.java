package taojava.util;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Leah Greenberg
 * @author Zhi Chen
 * 
 * Consulted code from Nick Knoebeer and Eileen Fordham
 * http://en.literateprograms.org/Skip_list_%28Java%29
 * 
 * @Time: 5 hours
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  private final int MAX_LEVEL = 20;

  private final double PROB = .5;

  private T NIL = null;

  private Node header;

  private int SkipListLevel = 0;

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
    public T val;
    /**
     * An array list of nodes.
     */
    public Vector<Node> levels;
    /**
     * The level the node is on.
     */
    public int nodeLevel;

    /* Constructor */
    public Node(int level, T value)
    {
      this.levels = new Vector<Node>(SkipListLevel + 1);
      this.val = value;
    }//Node(T)

  } // class Node

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+

  public SkipList()
  {
    this.header = new Node(MAX_LEVEL, null);
  }//SkipList(int)

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+

  public int randomLevel()
  {
    int newLevel = 1;

    Random rand = new Random();

    while (rand.nextDouble() < this.PROB)
      {
        newLevel++;
      }//while()

    return Integer.min(newLevel, this.MAX_LEVEL);
  }//randomLevel()

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
    Vector<Node> update = new Vector<Node>(MAX_LEVEL);
    
    Node currentNode = this.header;
    
    for(int i = SkipListLevel; i >= 0; i--)
      {
        Node temp = currentNode.levels.get(i);
        
        while(temp.key < searchKey)
          {
            currentNode = temp;
            update.add(i, currentNode);
          }//while()
        
        currentNode = currentNode.levels.get(0);
        
        if(currentNode.key == searchKey)
          {
            currentNode.val = val;
          }//if()
        else
          {
            int newLevel = randomLevel();
            
            if(newLevel > SkipListLevel)
              {
                for(int i = SkipListLevel + 1; i <= newLevel; i++)
                  {
                    update[i] = this.header; //FIX
                  }//for()
                SkipListLevel = newLevel;
              }//if()
            currentNode = new Node(newLevel, val);
            for(int j = 0; j <= newLevel; j++)
              {
                currentNode.levels.set(j, update.get(j).levels.elementAt(j));
                update.get(j).levels.set(j, currentNode);
              }//for()
          }//else()
            
      }//for()
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*
    Node current = header;

    bottom.val = val;

    while (current != bottom)
      {
        while (current.val.compareTo(val) < 0)
          {
            current = current.right;

            //  If gap size is 3 or at bottom level and must insert, then promote middle element
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
    
    //Raise height of skiplist if necessary
    if (header.right != tail)
      {
        header = new Node(infinity, tail, header);
      }//if()
    */

  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   */
  public boolean contains(T val)
  {
    Node currentNode = this.header;

    for (int i = this.SkipListLevel; i >= 0; i--)
      {
        Node temp = currentNode.levels.get(i);

        while ((temp != null) && (temp.val.compareTo(val) < 0))
          {
            currentNode = temp;
          }//while()
      }//for()

    currentNode = currentNode.levels.get(0);

    if ((currentNode.val == val) && (currentNode != null))
      {
        return true;
      }//if()
    else
      {
        return false;
      }//else()

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
