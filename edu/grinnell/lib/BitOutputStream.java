package edu.grinnell.lib;

import java.io.*;

/**
 * A BitOutputStream allows bit-by-bit writing to a file.
 */
public class BitOutputStream {
	private PrintStream output;
	private int digits; // a buffer used to build up next set of digits
	private int numDigits; // how many digits are currently in the buffer
	private boolean debug; // set to true to write ASCII 0s and 1s rather than
							// bits

	private static final int BYTE_SIZE = 8; // digits per byte

	/**
	 * Constructs a new BitOutputStream attached to the given file.
	 * @param file the file to write to
	 * @param debug true iff you want to output the bits as ASCII 0s and 1s
	 * @throws FileNotFoundException if the file is not found
	 */
	public BitOutputStream(String file, boolean debug) throws FileNotFoundException {
		this.output = new PrintStream(file);
		this.debug = debug;
	}
	
	/**
     * Constructs a new BitOutputStream attached to the given file.
     * @param file the file to write to
     * @throws FileNotFoundException if the file is not found
     */
	public BitOutputStream(String file) throws FileNotFoundException { this(file, false); }

	/**
	 * Writes the given bit to the stream.
	 * @param bit the bit to write (0 or 1)
	 */
	public void writeBit(int bit) {
		if (debug) {
			output.print(bit);
		} else {
			if (bit < 0 || bit > 1)
				throw new IllegalArgumentException("Illegal bit: " + bit);
			digits += bit << numDigits;
			numDigits++;
			if (numDigits == BYTE_SIZE)
				flush();
		}
	}

    /**
     * Writes the lower n bits to the stream in big-endian style.
     * @param bits the bits to write as an integer
     * @param n the number of bits to write from the integer
     */
    public void writeBits(int bits, int n) {
        for (int i = n-1; i >= 0; i--) {
            writeBit((bits >>> i) % 2);
        }
    }

    /**
     * Flushes the buffer. If numDigits < BYTE_SIZE, this will
     * effectively pad the output with extra 0's, so this should
     * be called only when numDigits == BYTE_SIZE or when we are
     * closing the output.
     */
	private void flush() {
		output.write(digits);
		digits = 0;
		numDigits = 0;
	}

	/** Closes the stream, flushing any remaining bits to the file */
	public void close() {
		if (numDigits > 0)
			flush();
		output.close();
	}

	protected void finalize() {
		close();
	}
}
