import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.*;

/**
 * This class is meant to facilitate solving the Advent of Code puzzles
 * https://adventofcode.com
 * 
 * For each day, create a new class that extends ADay, and write your logic
 * for solvePart1, and then solvePart2. See Example.java for an example.
 */
public class JAdvent 
{
    /** TODO: what year's puzzles are you solving? */
    static int year = 2016;
    static JFrame _frame;
    static JComboBox<ADay> _combo;
    static JButton _execButton;
    static JTextArea _input;
    static JTextField _output1;
    static JTextField _output2;
    static JLabel _time1;
    static JLabel _time2;

    public static void main(String[] args) throws Exception 
    {
        CreateFrame();
        restoreInput();
    }

    /**
     * Create a Swing UI with a selector for the puzzle number
     * and text input for the raw data
     */
    private static void CreateFrame()
    {
        _frame = new JFrame("Advent of Code " + year);
        _frame.setBounds(100, 100, 630, 730);
        
        // Problem selector
        JLabel label = new JLabel("Problems: ");
        label.setBounds(10, 10, 80, 20);
        _frame.add(label);

        _combo = new JComboBox<ADay>(ADay.allDays());
        _combo.setBounds(90, 10, 400, 20);
        _combo.setSelectedIndex(_combo.getItemCount() - 1);  // select the last (newest) problem by default
        _combo.addActionListener(e -> restoreInput());
        _frame.add(_combo);

        // Execute button (oversized)
        _execButton = new JButton("Execute!");
        _execButton.setBounds(500, 10, 100, 40);
        _execButton.addActionListener(e -> execute() );
        _frame.add(_execButton);

        // Raw input
        label = new JLabel("Input data:");
        label.setBounds(10, 40, 200, 20);
        _frame.add(label);

        _input = new JTextArea();
        _input.setLineWrap(true);
        _input.setAutoscrolls(true);
        _input.setFont(new Font("Courier New", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(_input);  // needed for vertical scrollbar on input
        scroll.setBounds(10, 65, 590, 520);
        _frame.add(scroll);

        // Problem output
        label = new JLabel("Part 1 output:");
        label.setBounds(10, 600, 150, 20);
        _frame.add(label);

        _output1 = new JTextField();
        _output1.setBounds(170, 600, 330, 20);
        _frame.add(_output1);

        _time1 = new JLabel();
        _time1.setBounds(510, 600, 90, 20);
        _frame.add(_time1);

        label = new JLabel("Part 2 output:");
        label.setBounds(10, 630, 150, 20);
        _frame.add(label);

        _output2 = new JTextField();
        _output2.setBounds(170, 630, 330, 20);
        _frame.add(_output2);

        _time2 = new JLabel();
        _time2.setBounds(510, 630, 90, 20);
        _frame.add(_time2);

        // Show the frame
        _frame.setLayout(null);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setVisible(true);

        // Place focus on EXECUTE
        _execButton.requestFocus();
    }

    /**
     * Save each day's input in a temp file, where it can be restored each run
     */
    private static File tempFile(int day)
    {
        String prop = "java.io.tmpdir";
        String tempDir = System.getProperty(prop);
        String path = tempDir + "\\AdventOfCode-" + year + "-day" + day + ".txt";
        return new File(path);
    }

    /**
     * At each boot, and again if the combo is changed to another day,
     * restore the input text to its previous value (for that day)
     */
    private static void restoreInput()
    {
        try
        {
            ADay day = (ADay)_combo.getSelectedItem();
            File file = tempFile(day == null ? 0 : day.getDay());
            if (file.exists() && file.canRead())
            {
                FileReader fr = new FileReader(file);
                BufferedReader buf = new BufferedReader(fr);
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = buf.readLine()) != null)
                {
                    if (builder.length() > 0)
                        builder.append("\r\n");
                    builder.append(line);
                }
                fr.close();
                _input.setText(builder.toString());
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Save input for the next run (of the same day)
     */
    private static void saveInput(int day, String input)
    {
        try
        {
            File file = tempFile(day);
            if (file.exists())
                file.delete();
            if (file.createNewFile())
            {
                FileWriter fw = new FileWriter(file);
                fw.append(input);
                fw.close();
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Execute the selected problem, with the provided input.
     * Write the output to the Output box at the bottom of the app.
     */
    private static void execute()
    {
        ADay day = (ADay)_combo.getSelectedItem();
        String data = _input.getText();
        saveInput(day == null ? 0 : day.getDay(), data);

        _time1.setText("");
        _time2.setText("");
        _output1.setBackground(Color.gray);
        _output2.setBackground(Color.gray);

        // Try to solve the first half of this problem
        try
        {
            Reader reader = new Reader(data);
            long start = System.currentTimeMillis();
            String answer = "" + day.solvePart1(reader);
            long millis = System.currentTimeMillis() - start;
            _output1.setText(answer);
            _output1.setBackground(Color.white);
            _output1.setForeground(Color.black);
            _time1.setText(String.format("%d.%03d sec", millis / 1000, millis % 1000));
        }
        catch (Exception ex)
        {
            System.out.println("EXCEPTION: " + ex.getMessage());
            ex.printStackTrace(new java.io.PrintStream(System.out));
            _output1.setText("EXCEPTION: " + ex.getMessage());
            _output1.setBackground(Color.white);
            _output1.setForeground(Color.red);
        }

        // Try to solve the second half of this problem
        try
        {
            Reader reader = new Reader(data);
            long start = System.currentTimeMillis();
            String answer = "" + day.solvePart2(reader);
            long millis = System.currentTimeMillis() - start;
            _output2.setText(answer);
            _output2.setBackground(Color.white);
            _output2.setForeground(Color.black);
            _time2.setText(String.format("%d.%03d sec", millis / 1000, millis % 1000));
        }
        catch (Exception ex)
        {
            _output2.setBackground(Color.white);
            if (ex.getMessage().equals("Not implemented yet"))
            {
                _output2.setText(ex.getMessage());
                _output2.setForeground(Color.gray);
            }
            else
            {
                ex.printStackTrace(new java.io.PrintStream(System.out));
                _output2.setText("EXCEPTION: " + ex.getMessage());
                _output2.setForeground(Color.red);
            }
        }
    }
}