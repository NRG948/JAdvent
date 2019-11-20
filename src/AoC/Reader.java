package AoC;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Utility class for parsing structured input.
 * 
 * A quick recap of the method naming convention: scan*() methods skip optional
 * content, and return the number of characters skipped. By checking if the
 * return value is >0, a caller can still confirm that the content was present.
 * next*() methods read an optional value, and return either the value or null.
 * expect() methods read and return required content, and throw if it is not
 * found.
 * 
 * Following is a typical usage example, when expecting a direction followed by
 * a number. In this example, the direction can vary, but the number is
 * required.
 * 
 * Reader reader = new Reader("left 3"); if (reader.scan("left ")) x -=
 * reader.expectInt(); else if (reader.scan("right ")) x += reader.expectInt();
 */
public class Reader {
    /**
     * The raw data to be read (generally, the guts of a String)
     */
    private char[] buffer;
    /**
     * The index of the next character to read from the buffer
     */
    private int pos;
    /**
     * The index of the end of the accessible buffer. For nested readers, this may not be buffer.length
     */
    private int posStart;
    /**
     * The index of the start of the accessible buffer. For nested readers, this may not be 0
     */
    private int posEnd;

    /**
     * Initialize this reader with a String
     * 
     * @param s
     */
    public Reader(String s) {
        buffer = s.toCharArray();
        posStart = pos = 0;
        posEnd = buffer.length;
    }

    /**
     * Initialize this reader with a sub-range from another reader
     * 
     * @param parent another Reader, whose buffer we will reuse
     * @param start  the first position within the parent buffer
     * @param end    the last position within the parent buffer
     */
    private Reader(Reader parent, int start, int end) {
        buffer = parent.buffer;
        posStart = pos = start;
        posEnd = end;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Position ");
        if (posStart == 0) {
            builder.append(pos);
            builder.append(" of ");
            builder.append(posEnd);
        } else {
            builder.append(pos - posStart);
            builder.append("(" + pos + ")");
            builder.append(" of ");
            builder.append(posEnd - posStart);
            builder.append("(" + posEnd + ")");
        }
        if (pos >= posEnd)
            builder.append("; at end");
        else
            builder.append("; next character == " + buffer[pos]);
        return builder.toString();
    }

    /**
     * The entire original string, which we are reading and parsing,
     * regardless of the current read position.
     */
    public String String()
    {
        return subString(posStart, posEnd);
    }

    /**
     * Extract a substring from the full buffer.
     * 
     * @param start the start position of the substring
     * @param end   the position after the substring
     */
    public String subString(int start, int end) {
        return new String(buffer, start, end - start);
    }

    /**
     * Extract the substring prior to the current position
     */
    public String left() {
        return new String(buffer, posStart, pos);
    }

    /**
     * Extract the substring prior to the current position
     */
    public String right() {
        return new String(buffer, pos, posEnd - pos);
    }

    /**
     * Has the reader reached the end of the buffer?
     * 
     * @return true once all characters have been scanned
     */
    public boolean atEnd() {
        return pos >= posEnd;
    }

    /**
     * Get the current reader position within the buffer.
     */
    public int position() {
        return pos;
    }

    /**
     * Get the total length of the parse buffer.
     * 
     * @return
     */
    public int length() {
        return posEnd;
    }

    /**
     * Get the start position of the reader's range.
     * Zero for simple readers, but can be other values for nested readers.
     */
    public int startPosition() {
        return posStart;
    }

    /**
     * Get the end position of the reader's range.
     * buffer.length for simple readers, but can be other values for nested readers.
     */
    public int endPosition() {
        return posEnd;
    }

    /**
     * Reset reader to the beginning of the buffer
     */
    public void restart() {
        pos = posStart;
    }

    /**
     * Move the parse position to elsewhere in the buffer.
     * 
     * @param pos any position from posStart to posEnd.
     */
    public void setPosition(int pos) {
        if (pos < posStart || pos > posEnd)
            throw new IndexOutOfBoundsException(
                    pos + " is not a valid position. Must be in the range [0, " + posEnd + "]");
        this.pos = pos;
    }

    /**
     * Look at the next character, without incrementing the reader
     */
    public char peekChar() {
        if (pos >= posEnd)
            return '\0';
        return buffer[pos];
    }

    /**
     * Look ahead at the word starting at the current position. Read it, without
     * scanning past it.
     * 
     * @return the word at the current position, if any. or null if no text at pos.
     */
    public String peekWord() {
        int prev = pos;
        String s = nextWord();
        pos = prev;
        return s;
    }

    /**
     * Skip any whitespace
     * 
     * @return the number of whitespace characters
     */
    public int scanSpaces() {
        int prev = pos;
        while (pos < posEnd && Character.isWhitespace(buffer[pos]))
            pos++;
        return pos - prev;
    }

    /**
     * Skip an exact string
     * 
     * @param expected the string (case sensitive) that you expect
     * @return the number of characters skipped
     */
    public int scan(String expected) {
        if (pos + expected.length() > posEnd)
            return 0; // not enough room
        for (int i = 0; i < expected.length(); i++) {
            if (expected.charAt(i) != buffer[pos + i])
                return 0;
        }
        pos += expected.length();
        return expected.length();
    }

    /**
     * Skip a string, ignoring case
     * 
     * @param expected the string (case insensitive) that you expect
     * @return the number of characters skipped
     */
    public int scanIgnoreCase(String expected) {
        if (pos + expected.length() >= posEnd)
            return 0; // not enough room
        for (int i = 0; i < expected.length(); i++) {
            if (Character.toUpperCase(expected.charAt(i)) != Character.toUpperCase(buffer[pos + i]))
                return 0;
        }
        pos += expected.length();
        return expected.length();
    }

    /**
     * Skip an exact string
     * 
     * @param expected the string (case sensitive) that you expect
     * @return the number of characters skipped
     */
    public int scan(char expected) {
        if (pos >= posEnd)
            return 0; // not enough room
        if (peekChar() != expected)
            return 0;

        pos++;
        return 1;
    }

    /**
     * Extract one line (minus the line break characters) into its own reader. Can
     * include blank lines.
     * 
     * @return null if we're at the end of the file
     */
    public Reader nextLine() {
        if (atEnd())
            return null;

        final char[] eol = { '\r', '\n' };
        int prev = pos;
        scanUntil(eol);
        int end = pos;

        if (!atEnd()) {
            // read the \r or \n or \r\n at the end of this line
            char c = expectChar();
            if (c == '\r' && peekChar() == '\n')
                expectChar();
        }

        return new Reader(subString(prev, end));
    }

    /**
     * Read a long (base 10) from the buffer.
     * 
     * @return a Long, if one was next, or null if not
     */
    public Long nextLong() {
        return nextLong(10);
    }

    /**
     * Read a long of any radix from the buffer.
     * 
     * @return a Long, if one was next, or null if not
     */
    public Long nextLong(int radix) {
        int prev = pos;
        long n = 0;
        boolean negative = false;
        if (pos < posEnd && buffer[pos] == '-') {
            // negative sign
            negative = true;
            pos++;
        } else if (pos < posEnd && buffer[pos] == '+') {
            // allow explicit positive sign
            pos++;
        }
        while (pos < posEnd) {
            int val = Character.digit(buffer[pos], radix);
            if (val < 0)
                break;
            pos++;
            n = n * 10 + val;
        }
        if (pos == prev)
            return null;
        if (negative)
            n = -n;
        return new Long(n);
    }

    /**
     * Read an integer (base 10) from the buffer.
     * 
     * @return an Integer, if one was next, or null if not
     */
    public Integer nextInteger() {
        return nextInteger(10);
    }

    /**
     * Read an integer of any radix from the buffer.
     * 
     * @return an Integer, if one was next, or null if not
     */
    public Integer nextInteger(int radix) {
        int prev = pos;
        int n = 0;
        boolean negative = false;
        if (pos < posEnd && buffer[pos] == '-') {
            // negative sign
            negative = true;
            pos++;
        } else if (pos < posEnd && buffer[pos] == '+') {
            // allow explicit positive sign
            pos++;
        }
        while (pos < posEnd) {
            int val = Character.digit(buffer[pos], radix);
            if (val < 0)
                break;
            pos++;
            n = n * 10 + val;
        }
        if (pos == prev)
            return null;
        if (negative)
            n = -n;
        return new Integer(n);
    }

    /**
     * Read the next single character
     * 
     * @return
     */
    public char nextChar() {
        if (pos >= posEnd)
            return '\0';
        return buffer[pos++];
    }

    /**
     * Read an exact number of characters
     * 
     * @param count how many characters
     * @return A string, unless not enough characters are present
     */
    public String nextChars(int count) {
        if (pos + count > posEnd)
            return null;
        pos += count;
        return subString(pos - count, pos);
    }

    /**
     * Read a single word, comprised only of letters.
     * 
     * @return One word, or null if not at a word
     */
    public String nextWord() {
        int prev = pos;
        while (pos < posEnd && Character.isLetter(buffer[pos]))
            pos++;
        if (prev == pos)
            return null;
        return subString(prev, pos);
    }

    /**
     * Read a single word/number/punctuation. Any sequence that doesn't include
     * whitespace.
     * 
     * @return
     */
    public String nextNonSpace() {
        int prev = pos;
        while (pos < posEnd && !Character.isWhitespace(buffer[pos]))
            pos++;
        if (prev == pos)
            return null;
        return subString(prev, pos);
    }

    /**
     * Read all of the text until the first occurrence of a specific character, or
     * the end of the buffer
     * 
     * @param ch the character to stop reading at
     * @return the String of intervening characters
     */
    public String nextUntil(char ch) {
        int prev = pos;
        scanUntil(ch);
        return subString(prev, pos);
    }

    /**
     * Skip any characters until the desired one is reached. Leave the reader at
     * that character, or at the end of the buffer.
     * 
     * @param ch the first character NOT to skip
     * @return the number of characters skipped
     */
    public int scanUntil(char ch) {
        int prev = pos;
        while (pos < posEnd) {
            if (buffer[pos] == ch)
                break;
            pos++;
        }
        return pos - prev;
    }

    /**
     * Skip any characters until one of the desired ones is reached. Leave the
     * reader at that character, or at the end of the buffer.
     * 
     * @param chs an array of characters, any one of which ends the scan
     * @return the number of characters skipped
     */
    public int scanUntil(char[] chs) {
        int prev = pos;
        while (pos < posEnd) {
            for (char c : chs) {
                if (buffer[pos] == c)
                    return pos - prev;
            }
            pos++;
        }
        return pos - prev;
    }

    /**
     * Scan up to and including the next line break (\r, \n, or \r\n together)
     * 
     * @return the number of character scanned
     */
    public int scanUntilNextLine() {
        int prev = pos;
        pos = findNextLineStart(buffer, pos, posEnd);
        return pos - prev;
    }

    /**
     * Find the end of the current line, which is either a line break character, or
     * the end of the buffer.
     * 
     * @param buffer    the buffer we're reading from
     * @param current   a position in the buffer, on any line
     * @param endBuffer the last valid position in the buffer
     * @return the position of the first end-of-line character (\r or \n), or else
     *         the end-of-buffer position
     */
    protected static int findEndOfLine(char[] buffer, int current, int endBuffer) {
        while (current < endBuffer && buffer[current] != '\r' && buffer[current] != '\n')
            current++;
        return current;
    }

    /**
     * Find the start of the next line, if there is one.
     * 
     * @param buffer    the buffer we're reading from
     * @param current   a position in the buffer, on any line
     * @param endBuffer the last valid position in the buffer
     * @return the position of the first character of the next line; or else the
     *         end-of-buffer position, if current is on the last line
     */
    protected static int findNextLineStart(char[] buffer, int current, int endBuffer) {
        int nextLine = endBuffer; // if all else fails
        current = findEndOfLine(buffer, current, endBuffer);
        while (current < endBuffer && nextLine == endBuffer) {
            // Any of \r, \n, or \r\n will cause nextLine to change, which will end the loop
            if (buffer[current] == '\r')
                nextLine = ++current;
            if (buffer[current] == '\n')
                nextLine = ++current;
        }
        return nextLine;
    }

    /**
     * Skip any non-digit (base 10) characters. The '-' signs counts as a digit ONLY
     * if the following character is a real digit Leave the reader at the first
     * digit character, or the end of the buffer.
     * 
     * @return the number of characters skipped
     */
    public int scanUntilDigit() {
        return scanUntilDigit(10);
    }

    /**
     * Skip any non-digit of the desired radix characters. The '-' signs counts as a
     * digit ONLY if the following character is a real digit Leave the reader at the
     * first digit character, or the end of the buffer.
     * 
     * @return the number of characters skipped
     */
    public int scanUntilDigit(int radix) {
        int prev = pos;
        while (pos < posEnd) {
            if (Character.digit(buffer[pos], radix) >= 0)
                break;
            pos++;
        }

        if (pos > prev && buffer[pos - 1] == '-')
            pos--; // Special case a minus sign immediately before the digit we found
        return pos - prev;
    }

    /**
     * Skip past an expected string (case-sensitive).
     * 
     * @param expected the expected string.
     * @return the actual string that was read
     * @throws a runtime exception if the expected string is missing.
     */
    public String expect(String expected) {
        if (scan(expected) == 0)
            throw new ReaderException("exactly '" + expected + "'");
        return subString(pos - expected.length(), pos);
    }

    /**
     * Skip past an expected string (case-insensitive).
     * 
     * @param expected the expected string.
     * @return the actual string that was read
     * @throws a runtime exception if the expected string is missing.
     */
    public String expectIgnoreCase(String expected) {
        if (scanIgnoreCase(expected) == 0)
            throw new ReaderException("'" + expected + "' (case-insensitive)");
        return subString(pos - expected.length(), pos);
    }

    /**
     * Read an expected integer
     * 
     * @throws a runtime exception if an integer is not next
     */
    public int expectInteger() {
        return expectInteger(10);
    }

    /**
     * Read an expected integer (with radix)
     * 
     * @param radix the numeric base of the integer
     * @throws a runtime exception if an integer is not next
     */
    public int expectInteger(int radix) {
        Integer n = nextInteger(radix);
        if (n == null)
            throw new ReaderException("an integer");
        return n;
    }

    /**
     * Read an expected integer
     * 
     * @throws a runtime exception if an integer is not next
     */
    public long expectLong() {
        return expectLong(10);
    }

    /**
     * Read an expected integer (with radix)
     * 
     * @param radix the numeric base of the integer
     * @throws a runtime exception if an integer is not next
     */
    public long expectLong(int radix) {
        Long n = nextLong(radix);
        if (n == null)
            throw new ReaderException("an integer");
        return n;
    }

    /**
     * Read a simple word (letters only).
     * 
     * @throws a runtime exception if a word is not next
     */
    public String expectWord() {
        String s = nextWord();
        if (s == null)
            throw new ReaderException("a word");
        return s;
    }

    /**
     * Read a string up until the next space. Letters, numbers, and punctuation.
     * 
     * @throws a runtime exception if we're at the end, or a space is next
     */
    public String expectNonSpace() {
        String s = nextNonSpace();
        if (s == null)
            throw new ReaderException("something other than a space");
        return s;
    }

    /**
     * Expects that we are not at the end. Then reads one character.
     * 
     * @throws a runtime exception if we are at the end.
     */
    public char expectChar() {
        if (atEnd())
            throw new ReaderException("a character");
        return nextChar();
    }

    /**
     * Confirm that we have reached the end of the buffer.
     * 
     * @throws a runtime exception if we are not at the end.
     */
    public void expectEnd() {
        if (!atEnd())
            throw new ReaderException("the end of buffer");
    }

    /**
     * Create a child reader for a sub-range of this reader
     * @param start
     * @param end
     * @return
     */
    public Reader recurse(int start, int end)
    {
        return new Reader(this, start, end);
    }

    /**
     * An enumeration of lines within this reader, starting at the current position.
     */
    public LineIterator lines() 
    {
        return new LineIterator();
    }

    /**
     * Extract an array of lines, with a reader for each one.
     */
    public Reader[] allLines()
    {
        ArrayList<Reader> lines = new ArrayList<Reader>();
        for (Reader r : lines())
        {
            lines.add(r);
        }
        return lines.toArray(new Reader[lines.size()]);
    }

    /**
     * Extract a string array of the set of all lines.
     */
    public String[] allLineStrings()
    {
        ArrayList<String> lines = new ArrayList<String>();
        for (Reader r : lines())
        {
            lines.add(r.String());
        }
        return lines.toArray(new String[lines.size()]);
    }

    /**
     * How many distinct lines are remain?
     */
    public int countLines()
    {
        int count = 0;
        int prev = pos;
        while (scanUntilNextLine() > 0)
            count++;
        pos = prev;
        return count;
    }

    /**
     * When throwing an exception, annotate what we found instead
     */
    private String foundInstead() {
        StringBuilder builder = new StringBuilder();
        builder.append(" at position ");
        builder.append(pos);
        builder.append("; found: ");
        int prev = pos;
        for (int i = 0; i < 10; i++) {
            char ch = nextChar();
            switch (ch) {
            case ' ':
                builder.append("·"); // space: middle dot
                break;
            case '\t':
                builder.append("→"); // tab: right arrow
                break;
            case '\r':
                builder.append("¶"); // CR: para mark
                break;
            case '\n':
                builder.append("↓"); // LF: down arrow
                break;
            case '\0':
                builder.append("§"); // \0: section mark
                i = 10; // exit for loop
                break;
            default:
                builder.append(ch);
                break;
            }
        }
        pos = prev; // go back
        return builder.toString();
    }

    class ReaderException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ReaderException(String expected) {
            super("Reader expected " + expected + foundInstead());
        }
    }

    /**
     * Enumerate an outer Reader, creating nested readers for each line. Line-break
     * characters are skipped over, and not included with any line.
     */
    class LineIterator implements Iterator<Reader>, Iterable<Reader> 
    {
        /**
         * The reading position of the current line
         */
        int posLine;
        /**
         * The reading position of the next line (and end of the current one)
         */
        int posNextLine;

        public LineIterator() 
        {
            posNextLine = Reader.this.pos;
            posLine = Reader.this.pos - 1;
        }

        @Override
        public boolean hasNext() 
        {
            return posNextLine < Reader.this.posEnd;
        }

        @Override
        public Reader next() 
        {
            // We've already cached the start of the next line
            posLine = posNextLine;
            // Find the end of this next line (before line break characters)
            int posEOL = findEndOfLine(buffer, posLine, Reader.this.posEnd);
            // Then cache the start of the next line, past the line break
            posNextLine = findNextLineStart(buffer, posEOL, Reader.this.posEnd);
            // Return a reader to parse this next line, without the line break
            return new Reader(Reader.this, posLine, posEOL);
        }

        @Override
        public Iterator<Reader> iterator() 
        {
            return this;
        }

    }
}