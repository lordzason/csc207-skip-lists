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
 * Nick Knoebeer and Eileen Fordham,
 * Zoe Wolter,
 * http://en.literateprograms.org/Skip_list_%28Java%29
 * 
 * @Time: 7.5 hours
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  private final int MAX_LEVEL = 20;

  private final double PROB = .5;

  private Node header;

  private Node last;

  private int SkipListLevel = 0;

  private int mods = 0;
  
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
    public Vector<Node> next;
    /**
     * The level the node is on.
     */
    public int nodeLevel;

    /* Constructor */
    public Node(int level, T value)
    {
      this.next = new Vector<Node>(level + 1);
      this.next.setSize(MAX_LEVEL);
      this.val = value;

      for (int i = 0; i < SkipListLevel; i++)
        {
          this.next.set(i, null);
        }//for()

    }//Node(T)

  } // class Node

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+

  public SkipList()
  {
    this.header = new Node(MAX_LEVEL, null);
    this.last = new Node(MAX_LEVEL, null);

    for (int i = 0; i < MAX_LEVEL; i++)
      {
        this.last.next.set(i, null);

        this.header.next.set(i, this.last);
      }//for()
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
    return new Iterator<T>()
      {
        Node cursor = SkipList.this.header;

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

          return (cursor.next.get(1) != null);
        }//hasNext()

        @Override
        public T next()
        {
          failFast();

          if (!this.hasNext())
            {
              throw new NoSuchElementException();
            }//if()
          
          this.cursor = this.cursor.next.get(1);

          return this.cursor.val;
        }//next()

      };//Iterator<T>
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
    Vector<Node> update = new Vector<Node>(MAX_LEVEL + 1);

    Node currentNode = this.header;

    //find and record updates
    for (int i = SkipListLevel; i >= 0; i--)
      {
        
        while ((currentNode.next.get(i).val != null) //"currentNode.next.get(i).val" is addition
               && (currentNode.next.get(i).val.compareTo(val) < 0))
          {
            currentNode = currentNode.next.get(i);
          }//while()

        update.add(i, currentNode);
      }//for()

    currentNode = currentNode.next.get(0);

    //Record header updates
    if ((currentNode.val == null) || (currentNode.val.equals(val))) //"currentNode.val == null" is addition
      {
        int newLevel = randomLevel();

        if (newLevel > SkipListLevel)
          {
            update.setSize(newLevel); //addition
            
            for (int i = SkipListLevel + 1; i < newLevel; i++)
              {
                update.set(i, this.header);
              }//for()

            SkipListLevel = newLevel;

          }//if()

        //Insert new node
        currentNode = new Node(newLevel, val); //create the new node

        //Update the nodes to point to the new node
        for (int i = 0; i <= newLevel; i++)
          {
            currentNode.next.set(i, update.elementAt(i).next.get(i));

            update.elementAt(i).next.set(i, currentNode);
          }//for()
      }//if()

  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   */
  public boolean contains(T val)
  {
    Node currentNode = this.header;

    for (int i = this.SkipListLevel; i >= 0; i--)
      {
        while ((currentNode.next.get(i) != null)
               && (currentNode.next.get(i).val.compareTo(val) < 0))
          {
            currentNode = currentNode.next.get(i);
          }//while()
      }//for()

    currentNode = currentNode.next.get(0);

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
    Node currentNode = this.header;

    Vector<Node> update = new Vector<Node>(MAX_LEVEL + 1);

    for (int i = SkipListLevel; i >= 0; i--)
      {
        while ((currentNode.next.get(i) != null)
               && (currentNode.next.get(i).val.compareTo(val) < 0))
          {
            currentNode = currentNode.next.get(i);
          }//while()

        update.set(i, currentNode);
      }//for()

    currentNode = currentNode.next.get(0);

    if (currentNode.val.equals(val))
      {

        //Remove node from SkipList
        for (int i = 0; i <= SkipListLevel; i++)
          {
            if (update.get(i).next.get(i) != currentNode)
              {
                break;
              }//if()
            update.get(i).next.set(i, currentNode.next.get(i));
          }//for()

        //Decrease list level
        while ((SkipListLevel > 0)
               && (this.header.next.get(SkipListLevel) == null))
          {
            SkipListLevel--;
          }//while()
      }//if()
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

  public static void main(String args[])
  {
    PrintWriter pen = new PrintWriter(System.out, true);

    SkipList<Integer> skiplist = new SkipList<Integer>();

    pen.println(skiplist);

    skiplist.add(5);
    skiplist.add(6);
    skiplist.add(7);
    skiplist.add(8);
    skiplist.add(9);
    skiplist.add(10);
    skiplist.add(4);

    if (skiplist.contains(7))
      {
        pen.println("7 is in the list! :)");

      }//if()

    pen.println(skiplist);

    skiplist.remove(7);

    pen.println(skiplist);

    if (!skiplist.contains(7))
      {
        pen.println("7 has been deleted! :(");
      }//if()
  }//main()
} // class SkipList<T>
