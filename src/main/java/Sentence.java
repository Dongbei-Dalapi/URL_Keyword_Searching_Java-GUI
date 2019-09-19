/**
 * Author: Jinghua Xu
 * Last Modified Date: 16 July 2018
 * Description: Sentence class represents a sentence consists of words as Strings which are storeed in ArrayList<String>
 */



import java.util.*;

public class Sentence {
    //index indicates the positions(indexes) of the target word in sentence(array)
    private ArrayList<Integer> index;
    //words stored in this array
    private String[] sentence;

    //constructors, mutators, accessors

    /**
     * constructores
     */
    public Sentence()
    {
        index = new ArrayList<Integer>();;
        sentence = null;
    }

    public Sentence(ArrayList<Integer> newIndex, String[] newSentence)
    {
        //the way to copy one ArrayList to another
        setBoth(newIndex,newSentence);
    }
    public Sentence(String[] newSentence)
    {
        index = new ArrayList<Integer>();
        sentence = newSentence;
    }
    /**
     * mutators
     */
    public void setIndex(ArrayList<Integer> newindex)
    {
        index = newindex;
    }
    public void setSentence(String[] newsentence)
    {
        sentence = newsentence;
    }
    public void setBoth(ArrayList<Integer> newIndex, String[] newSentence)
    {
        index = newIndex;
        sentence = newSentence;
    }
    public void addIndex(int aindex)
    {
        Integer i = new Integer(aindex);
        index.add(i);
    }

    /**
     * accesors
     */
    public String[] getSentence()
    {
        return sentence;
    }
    public ArrayList<Integer> getIndex()
    {
        return index;
    }
}