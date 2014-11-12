package taojava.util;

/* The authors of this work have released all rights to it and placed it
in the public domain under the Creative Commons CC0 1.0 waiver
(http://creativecommons.org/publicdomain/zero/1.0/).

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Retrieved from: http://en.literateprograms.org/Skip_list_(Java)?oldid=15959
*/

public class SkipSet<E extends Comparable<? super E>>
{
  public static final double P = 0.5;
  public static final int MAX_LEVEL = 6;

  public static int randomLevel()
  {
    int lvl = (int) (Math.log(1. - Math.random()) / Math.log(1. - P));
    return Math.min(lvl, MAX_LEVEL);
  }//randomLevel()

  public final SkipNode<E> header = new SkipNode<E>(MAX_LEVEL, null);
  public int level = 0;

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    SkipNode<E> x = header.forward[0];
    while (x != null)
      {
        sb.append(x.value);
        x = x.forward[0];
        if (x != null)
          sb.append(",");
      }//while()
    sb.append("}");
    return sb.toString();
  }//toString()

  public boolean contains(E searchValue)
  {
    SkipNode<E> x = header;
    for (int i = level; i >= 0; i--)
      {
        while (x.forward[i] != null
               && x.forward[i].value.compareTo(searchValue) < 0)
          {
            x = x.forward[i];
          }//while()
      }//for()
    x = x.forward[0];
    return x != null && x.value.equals(searchValue);
  }//contains(E)

  @SuppressWarnings("unchecked")
  public void insert(E value)
  {
    SkipNode<E> x = header;
    SkipNode<E>[] update = new SkipNode[MAX_LEVEL + 1];

    for (int i = level; i >= 0; i--)
      {
        while (x.forward[i] != null && x.forward[i].value.compareTo(value) < 0)
          {
            x = x.forward[i];
          }//while()
        update[i] = x;
      }//for()
    x = x.forward[0];

    if (x == null || !x.value.equals(value))
      {
        int lvl = randomLevel();

        if (lvl > level)
          {
            for (int i = level + 1; i <= lvl; i++)
              {
                update[i] = header;
              }//for()
            level = lvl;
          }//if()
        x = new SkipNode<E>(lvl, value);
        for (int i = 0; i <= lvl; i++)
          {
            x.forward[i] = update[i].forward[i];
            update[i].forward[i] = x;
          }//for()
      }//if()
  }//insert(E)

  @SuppressWarnings("unchecked")
  public void delete(E value)
  {
    SkipNode<E> x = header;
    SkipNode<E>[] update = new SkipNode[MAX_LEVEL + 1];

    for (int i = level; i >= 0; i--)
      {
        while (x.forward[i] != null && x.forward[i].value.compareTo(value) < 0)
          {
            x = x.forward[i];
          }//while()
        update[i] = x;
      }//for()
    x = x.forward[0];

    if (x.value.equals(value))
      {
        for (int i = 0; i <= level; i++)
          {
            if (update[i].forward[i] != x)
              break;
            update[i].forward[i] = x.forward[i];
          }//for()
        while (level > 0 && header.forward[level] == null)
          {
            level--;
          }//while()
      }//if()
  }//delete

  public static void main(String[] args)
  {

    SkipSet<Integer> ss = new SkipSet<Integer>();
    
    System.out.println(ss.toString());
    
    ss.insert(5);
    ss.insert(5);
    ss.insert(10);
    ss.insert(10);
    ss.insert(7);
    ss.insert(7);
    ss.insert(6);
    ss.insert(6);

    System.out.println(ss.toString());

    if (ss.contains(7))
      {
        System.out.println("7 is in the list");
      }

    System.out.println(ss.toString());

    ss.delete(7);
    
    System.out.println("Deleted 7");

    System.out.println(ss.toString());

    if (!ss.contains(7))
      {
        System.out.println("7 has been deleted");
      }
  }
}