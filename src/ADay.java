import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

/**
 * The base class for each day's two problems.
 * Override this class. In the constructor, identify the day.
 * Then override either solveString1 or solvePart1,
 * depending on whether the answer to the puzzle is a string or an integer.
 * Once you've succeeded at part 1, override either solveString2 or solvePart2.
 * 
 * Example:
 * public class Day26 extends ADay
 * {
 *    public Day26() { super(26, "Boxing Day!"); }
 *
 *    public Object solvePart1(Reader input) 
 *    {
 *        // your work goes here for part 1
 *        // see Reader.java for examples of how to parse input.
 *        return your_answer;
 *    }
 * }
 * 
 * If you need support classes, nest them inside your derived day class.
 */
public abstract class ADay
{
    private int _day;
    private String _title;

    protected ADay(int day, String title)
    {
        _day = day;
        _title = title;
    }

    public int getDay() { return _day; }
    public String getTitle() { return _title; }

    public String toString()
    {
        return "Day " + getDay() + ": " + getTitle();
    }

    /**
     * Override solvePart1 when you create each new Day.
     */
    public abstract Object solvePart1(Reader input);

    /**
     * Override solvePart2 after you solve part 1.
     */
    public Object solvePart2(Reader input)
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Find all classes that extend AbstractDay
     * They MUST contain a simple public constructor with no arguments.
     */
    public static ADay[] allDays()
    {
        ArrayList<ADay> list = new ArrayList<ADay>();
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Class<?>[] emptyArgs = {};
        try
        {
            Enumeration<URL> urls = loader.getResources(".");
            while (urls.hasMoreElements())
            {
                URL url = urls.nextElement();
                File dir = new File(url.getFile());
                File[] files = dir.listFiles();
                System.out.println("Reflecting on classed in " + dir);
                for (File file : files) 
                {
                    if (file.getName().endsWith(".class")) 
                    {
                        String name = file.getName();
                        name = name.substring(0, name.length() - 6);  // strip .class
                        try
                        {
                            Class<?> c = Class.forName(name);
                            Type t = c.getGenericSuperclass();
                            while (t != null && t != Object.class)
                            {
                                if (t == ADay.class)
                                {
                                    Constructor<?> ctor = c.getConstructor(emptyArgs);
                                    ADay day = (ADay)ctor.newInstance((Object[])emptyArgs);
                                    list.add(day);
                                    // System.out.println("Found class " + name);
                                    break;
                                }
                                t = t.getClass().getGenericSuperclass();
                            }
                        }
                        catch (ClassNotFoundException ex)
                        {
                            System.out.println("   Unable to reflect on class " + name);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("   Does not have a plain constructor: public " + name + "()");
                        }
                    }
                }
            }
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }

        list.sort(new DayComparator());

        return list.toArray(new ADay[list.size()]);
    }

    /**
     * Helper class to sort days we discover via reflection.
     */
    static class DayComparator implements Comparator<ADay>
    {
        @Override
        public int compare(ADay d1, ADay d2) {
            return d1.getDay() - d2.getDay();
        }
    }

}