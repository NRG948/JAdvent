package AoC;

import java.util.HashMap;

/**
 * Example implementation of a day's puzzle.
 * Also illustrates how to use the Reader class.
 */
public class Example extends ADay
{
    public Example() 
    {
        // pass -1 as the day, just to avoid collision with any actual days later
        super(-1, "Example");
    }

    /**
     * Hypothetical first puzzle: 
     * Given a large paragraph of text, return the word that occurs most often.
     * 
     * Example input: "3 french hens 2 turtle doves and a partridge in a pair tree"
     * Expected answer: "a"
     * In fact, every other word only appears once.
     */
    public Object solvePart1(Reader input) 
    {
        HashMap<String, Integer> dictionary = new HashMap<String, Integer>();

        // Loop through all the words, counting them
        while (!input.atEnd())
        {
            // Read one word, or number, or really everything up until the next space
            String word = input.nextNonSpace();
            // Then skip any whitespace between words
            input.scanSpaces();
            
            countWord(dictionary, word);
        }

        // Now loop through the collection of words and count, to find the most used one
        return findHighestValue(dictionary);
    }

    /**
     * A helper for the main solver methods.
     * Given a word, which may or may not already be known to the hashmap, either
     * add the first instance, or increment the count of instances of that word.
     * @param dictionary a list of words, mapped to their instance counts
     * @param word a word to be counted
     */
    void countWord(HashMap<String, Integer> dictionary, String word)
    {
        if (dictionary.containsKey(word))
        {
            // We've already seen this word at least once
            dictionary.put(word, dictionary.get(word) + 1);  // increment the count
        }
        else
        {
            dictionary.put(word, 1);  // first time for this word
        }
    }

    /**
     * A helper for the main solver methods.
     * Find the hash map key which leads to the highest value.
     * @param dictionary a list of words, mapped to their instance counts
     * @return the word with the highest instance count
     */
    String findHighestValue(HashMap<String, Integer> dictionary)
    {
        // Now loop through the list of words, to find the most used one
        int most = 0;
        String answer = null;
        for (String w : dictionary.keySet())
        {
            if (dictionary.get(w) > most)
            {
                answer = w;
                most = dictionary.get(w);
            }
        }
        return answer;
    }

    /**
     * Hypothetical second puzzle:
     * The input text contains a mix of numbers and words.
     * Rather than treating the numbers as just funny words, treat them as 
     * multipliers to the words. A number before a word means that many of
     * that word.
     * 
     * Example input: "3 french hens 2 turtle doves and a partridge in a pair tree"
     * Expected answer: "french"
     * By these rules, "french" appears 3 times, while "turtle" and "a" each appear twice.
     */
    public Object solvePart2(Reader input) 
    {
        HashMap<String, Integer> dictionary = new HashMap<String, Integer>();

        // Loop through all the words, counting them
        while (!input.atEnd())
        {
            // See if the next entry is a number
            // NOTE: the 'next' variant will return null if the next text in the buffer is not a number
            // In that null case, the buffer doesn't move (as if you never asked), but in the
            // non-null case, the integer is returned, and the buffer moves past the number.
            // Also NOTE: the return type is Integer rather than int, because simple ints can't be null.
            // But an Integer can be used like a simple int (unless it is null).
            Integer multiple = input.nextInteger();
            if (multiple != null)
                input.scanSpaces();  // we read a number, so skip past any spaces after it
            else
                multiple = 1;

            // Read one word
            // NOTE: the 'expect' variant means non-words (i.e. numbers or punctuation) throw an exception
            String word = input.expectWord();
            // NOTE: the 'scan' variant of Reader methods don't even return what was read. 
            // They only return a count of characters moved past. So if you wanted
            // to ask IF there were spaces, you could say: if (input.scanSpaces() > 0) ...
            input.scanSpaces();
            
            // Count the word 1 or multiple times
            while (multiple-- > 0)
            {
                countWord(dictionary, word);
            }
        }

        // Now loop through the collection of words and count, to find the most used one
        return findHighestValue(dictionary);
    }

}