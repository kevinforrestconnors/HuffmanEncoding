package edu.grinnell.lib;

import java.io.*;

/**
 * A BitInputStream allows bit-by-bit reading from a file.
 */
public class BitInputStream {
    private FileInputStream input;
    private int digits;     // next set of digits (buffer)
    private int numDigits;  // how many digits from buffer have been used

    private static final int BYTE_SIZE = 8;  // digits per byte

    /**
     * Constructs a new BitInputStream attached to the given file
     * @param file where the bits will go
     */
    public BitInputStream(String file) {
        try {
            input = new FileInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        nextByte();
    }

    
    /** @return the next bit from input (0 or 1) or -1 if the stream is out of data */
    public int readBit() {
        // if at eof, return -1
        if (digits == -1)
            return -1;
        int result = digits % 2;
        digits /= 2;
        numDigits++;
        if (numDigits == BYTE_SIZE)
            nextByte();
        return result;
    }

    /**
     * @param n the number of bits to read (0--32)
     * @return the next n bits of the stream packed in a single integer
     */
    public int readBits(int n) {
        int ret = 0;
        for (int i = 0; i < n; i++) {
            int bit = readBit();
            if (bit == -1) { return -1; }
            ret = (ret << 1) | bit;
        }
        return ret;
    }

    /**
     * Refreshes the internal buffer with the next BYTE_SIZE bits.
     */
    private void nextByte() {
        try {
            digits = input.read();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        numDigits = 0;
    }

    /** Closes the stream, flushing any remaining bits to the file. */
    public void close() {
        try {
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    protected void finalize() {
        close();
    }
}
