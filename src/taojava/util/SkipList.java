package taojava.util;

import java.io.PrintWriter;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Vector;

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Leah Greenberg
 * @author Zhi Chen
 * 
 * Consulted code from:
 * William Pugh
 * Nick Knoebeer and Eileen Fordham,
 * Zoe Wolter,
 * http://en.literateprograms.org/Skip_list_%28Java%29
 * 
 * @Time: 10.5 hours
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  //The maximum level for the Skip List is determined to be 20, by default.
  private final int MAX_LEVEL = 20;

  //The probability to create a new level is .5
  private final double PROB = .5;

  //The header or dummy node
  private Node<T> header;

  //Keeps track of the current level in the Skip List
  private int SkipListLevel;

  //A counter to keep track of modifications
  private int mods = 0;

  // +------------------+------------------------------------------------
  // | Internal Classes |
  // +------------------+

  /**
   * Nodes for skip lists.
   */
  public class Node<T>
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
    public Node<T>[] next;
    /**
     * The level the node is on.
     */
    public int nodeLevel;

    /* Constructor */
    public Node(int level, T value)
    {
      this.next = new Node[level + 1];
      this.val = value;
    }//Node(T)

  } // class Node

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+

  public SkipList()
  {
    this.header = new Node<T>(MAX_LEVEL, null);
    this.SkipListLevel = 0;
  }//SkipList(int)

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+

  //A new level is created if a randomly generated number 
  //is less than the probability
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
    return new Iterator<T>()
      {
        Node<T> cursor = SkipList.this.header;

        int mods = SkipList.this.mods;

        void failFast()
        {
          if (this.mods != SkipList.this.mods)
            {
              throw new ConcurrentModificationException();
            }//if()
        }//failFast()

        @Override
        public boolean hasNext()
        {
          failFast();

          return (cursor.next[1] != null);
        }//hasNext()

        @Override
        public T next()
        {
          failFast();

          if (!this.hasNext())
            {
              throw new NoSuchElementException();
            }//if()

          this.cursor = this.cursor.next[1];

          return this.cursor.val;
        }//next()
        
        public void remove()
        {
          failFast();
          
          T temp = cursor.val;
          
          SkipList.this.remove(temp);
          
          this.mods++;
          
        }//remove()

      };//Iterator<T>
  } // iterator()

  // +------------------------+------------------------------------------
  // | Methods from SimpleSet |
  // +------------------------+

  /**
   * Add a value to the set.
   *
   * @param val, the value to be added 
   * @post contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to add, contains(lav) continues to hold.
   */
  public void add(T val)
  {
    Node<T> currentNode = this.header;
    Node<T>[] update = new Node[MAX_LEVEL + 1];

    //Find and record updates
    for (int i = SkipListLevel; i >= 0; i--)
      {
        while ((currentNode.next[i] != null)
               && (((Comparable<T>) currentNode.next[i].val).compareTo(val) < 0))
          {
            currentNode = currentNode.next[i];
          }//while()

        update[i] = currentNode;
      }//for()

    currentNode = currentNode.next[0];

    if ((currentNode == null) || (!currentNode.val.equals(val)))
      {
        int newLevel = randomLevel();

        //Record header updates
        if (newLevel > SkipListLevel)
          {

            for (int i = SkipListLevel + 1; i <= newLevel; i++)
              {
                update[i] = this.header;
              }//for()

            SkipListLevel = newLevel;
          }//if()

        //Insert new node
        currentNode = new Node(newLevel, val);

        //Update the nodes to point to the new node
        for (int i = 0; i <= newLevel; i++)
          {
            currentNode.next[i] = update[i].next[i];

            update[i].next[i] = currentNode;
          }//for()
      }//if()

    //Increase the mods count after adding an element
    mods++;

  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   * 
   * @param val, the value to be checked whether the Skip List cotnains it
   * @post returns TRUE, if val is found in the Skip List
   * @post returns FALSE, if val is not found in the Skip List
   */
  public boolean contains(T val)
  {
    Node<T> currentNode = this.header;

    //Find the node
    for (int i = this.SkipListLevel; i >= 0; i--)
      {
        while ((currentNode.next[i] != null)
               && (currentNode.next[i].val.compareTo(val) < 0))
          {
            currentNode = currentNode.next[i];
          }//while()
      }//for()

    currentNode = currentNode.next[0];

    if ((currentNode != null) && (currentNode.val.equals(val))) //why this order?
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
   * @param val, the value to be removed from the Skip List
   * @pre An element is in the Skip List before the remove method is called
   * @post !contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to remove, contains(lav) continues to hold.
   */
  public void remove(T val)
  {
    Node<T> currentNode = this.header;

    Node<T>[] update = new Node[MAX_LEVEL + 1];

    //Find and record updates
    for (int i = SkipListLevel; i >= 0; i--)
      {
        while ((currentNode.next[i] != null)
               && (currentNode.next[i].val.compareTo(val) < 0))
          {
            currentNode = currentNode.next[i];
          }//while()

        update[i] = currentNode;
      }//for()

    currentNode = currentNode.next[0];

    if(currentNode == null) //why doesn't .equal work?!
      {
        return;
      }//if()
    else if (currentNode.val.equals(val))
      {
        //Remove node from SkipList
        for (int i = 0; i <= SkipListLevel; i++)
          {
            if (update[i].next[i] != currentNode)
              {
                break;
              }//if()
            update[i].next[i] = currentNode.next[i];
          }//for()

        //Decrease list level
        while ((SkipListLevel > 0) && (this.header.next[SkipListLevel] == null))
          {
            SkipListLevel--;
          }//while()

        //Increase the mods count after removing an element
        mods++;

      }//else if()

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
