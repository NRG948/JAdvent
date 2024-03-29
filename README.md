# JAdvent
Java template for the Advent Of Code puzzle challenge

## Welcome!
This code is a template for anyone trying to solve the http://www.AdventOfCode.com puzzles.
Place this code in a fresh java project, code your solutions, compile, and run.
You might want to create a new such project for each puzzle year:
[2015](http://www.AdventOfCode.com/2015)
[2016](http://www.AdventOfCode.com/2016)
[2017](http://www.AdventOfCode.com/2017)
[2018](http://www.AdventOfCode.com/2018)
[2019](http://www.AdventOfCode.com/2019)

## The UI
The App uses Swing to build a simple form, which lists the various days in the
top combo box, and gives a large text box for the puzzle input. At the bottom 
are fields to display the answers for part 1 and part 2.
On the assumption that the puzzles are solved in order, the combo always
defaults to the most recent (i.e. highest #) day. Also, once you've pasted
the input for a given day, it will be saved, so that re-runs of this program
will already have that text.

## Day classes
In the Advent Of Code puzzles, each day presents two new puzzles.
Create a new class for each day's puzzle code. That class **must** extend `ADay`.
It **must** have a simple public constructor (no parameters). By convention,
that constructor should in turn call its super class, passing the number and title of the day.

Example:
> `class Day5 extends ADay {`<br />
> &nbsp; `public Day5() {`<br />
> &nbsp; &nbsp; `super(5, "How About a Nice Game of Chess?");`<br />
> &nbsp; `}`<br />
> `}`

Furthermore, your day classes must at least override `solvePart1`.
The full declaration is 
> `public Object solvePart1(Reader input) {`<br />
> &nbsp; *// TODO: read data from input, and find the answers* <br />
> &nbsp; `return "your answer";`<br />
> `}`

Note that because the return type is `Object`, you can actually
return an integer (or `long` if really large integers are involved),
or a `String`, or whatever.


Next, once you've solved part 1, you can also override `solvePart2`.
Until you do, the UI shows solvePart2 as <i>unimplemented</i>.
> `public Object solvePart2(Reader input) {`<br />
> &nbsp; `return 1234.5678;`<br />
> `}`

## Check out Example.java
It is an example solution for a hypothetical day 0. It also highlights a few ways to use the reader.



# The Reader class
Notice that the parameter to the two solve methods is a **Reader**.
This is a custom parser, to make pulling apart the puzzle input especially easy.
Readers have numerous helper methods, which you can explore. But most fall into 4 main method groups:
<dl>
<dt><b>expect...</b><dt>
<dd>Reads and returns the next ... whatever - int, word, N characters ... whatever.
If the next text in the input is not the requested thing, the reader throws an exception.
In the context of these puzzles, that generally means you have misunderstood the input data format.
<br /><i>Examples:</i> <code>expectWord(), expectInteger()</code>, ...
</dd>

<dt><b>next...</b><dt>
<dd>Tries to read and return the next ... data type. If it can, then great!
If it can't, it returns null.
<br /><i>Examples:</i> <code>nextWord(), nextInteger(), nextChar()</code>, ...
</dd>

<dt><b>scan...</b><dt>
<dd>Like next...(), these try to read the specified data type. But they do not return what was read.
Instead, they only return a count of characters processed. That makes them good for a test to see
IF a thing is present. 
<br /><i>Examples:</i> <code>scan("Go ")</code> or <code>scanSpaces()</code> each return 0 if they fail,
or >0 if the string "Go " or any spaces respectively are the next thing to read.
</dd>

<dt><b>lines</b><dt>
<dd>An iterator for jumping from line to line. Can be used in a for-each loop:<br />
<code>for (Reader line : input.lines()) { process(line.expectWord()); }</code>
<br />
If instead of an iterator, you simply want an array of Readers, or just an array of strings for each line,
call <code>allLines()</code> or <code>allLineStrings()</code> 
</dd>
</dl>

If you don't want to use these custom readers, the original String is `input.String()`

# The MD5 class
MD5 hashing is used frequently by Advent of Code puzzles.
This sounds daunting, but it isn't meant to be. It just happens to be a 
reliable way for the puzzle author to generate pseudo-random data, 
regardless of platform or programming language.

If your puzzle asks for an MD5 hash (which will be a 32-byte array), use our 
built-in MD5 utility. It takes either a string, or your own byte[]:
> `byte[] myHash = MD5.hash(puzzle_input);`


**<i>Good luck!!</i>**
