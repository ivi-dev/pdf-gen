package com.pdfgen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StandardStreamsTest {

    private StandardStreams streams;

    private PrintStream mockOut;
    
    private PrintStream mockErr;

    @BeforeEach
    void setUp() {
        streams = new StandardStreams(mockOut, mockErr);
    }

    @Test
    void outReturnsValue() {
        assertEquals(mockOut, streams.out());
    }

    @Test
    void errReturnsValue() {
        assertEquals(mockOut, streams.err());
    }

}
