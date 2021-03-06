/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.io;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.CharStreams.copy;
import static com.google.common.io.CharStreams.newReaderSupplier;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.TestLogHandler;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Unit test for {@link CharStreams}.
 *
 * @author Chris Nokleberg
 */
public class CharStreamsTest extends IoTestCase {

  private static final String TEXT
      = "The quick brown fox jumped over the lazy dog.";

  static final InputSupplier<? extends Reader> BROKEN_READ
      = CharStreams.newReaderSupplier(ByteStreamsTest.BROKEN_READ, UTF_8);

  static final OutputSupplier<? extends Writer> BROKEN_WRITE
      = CharStreams.newWriterSupplier(ByteStreamsTest.BROKEN_WRITE, UTF_8);

  static final InputSupplier<? extends Reader> BROKEN_CLOSE_INPUT
      = CharStreams.newReaderSupplier(ByteStreamsTest.BROKEN_CLOSE_INPUT, UTF_8);

  static final OutputSupplier<? extends Writer> BROKEN_CLOSE_OUTPUT
      = CharStreams.newWriterSupplier(ByteStreamsTest.BROKEN_CLOSE_OUTPUT, UTF_8);

  static final InputSupplier<? extends Reader> BROKEN_GET_INPUT
      = CharStreams.newReaderSupplier(ByteStreamsTest.BROKEN_GET_INPUT, UTF_8);

  static final OutputSupplier<? extends Writer> BROKEN_GET_OUTPUT
      = CharStreams.newWriterSupplier(ByteStreamsTest.BROKEN_GET_OUTPUT, UTF_8);

  private static final ImmutableSet<InputSupplier<? extends Reader>> BROKEN_INPUTS =
      ImmutableSet.of(BROKEN_CLOSE_INPUT, BROKEN_GET_INPUT, BROKEN_READ);
  private static final ImmutableSet<OutputSupplier<? extends Writer>> BROKEN_OUTPUTS
      = ImmutableSet.of(BROKEN_CLOSE_OUTPUT, BROKEN_GET_OUTPUT, BROKEN_WRITE);

  public void testToString() throws IOException {
    assertEquals(TEXT, CharStreams.toString(new StringReader(TEXT)));
    assertEquals(TEXT,
        CharStreams.toString(CharStreams.newReaderSupplier(TEXT)));
  }

  public void testSkipFully_blockingRead() throws IOException {
    Reader reader = new NonSkippingReader("abcdef");
    CharStreams.skipFully(reader, 6);
    assertEquals(-1, reader.read());
  }
  
  private static class NonSkippingReader extends StringReader {
    NonSkippingReader(String s) {
      super(s);
    }
    
    @Override
    public long skip(long n) {
      return 0;
    }
  }
  
  public void testReadLines_fromReadable() throws IOException {
    byte[] bytes = "a\nb\nc".getBytes(Charsets.UTF_8.name());
    List<String> lines = CharStreams.readLines(
        new InputStreamReader(new ByteArrayInputStream(bytes), Charsets.UTF_8));
    assertEquals(ImmutableList.of("a", "b", "c"), lines);
  }

  public void testReadLines_withLineProcessor() throws IOException {
    InputSupplier<StringReader> r = CharStreams.newReaderSupplier("a\nb\nc");

    // Test a LineProcessor that always returns false.
    LineProcessor<Integer> alwaysFalse = new LineProcessor<Integer>() {
      int seen;
      @Override
      public boolean processLine(String line) {
        seen++;
        return false;
      }
      @Override
      public Integer getResult() {
        return seen;
      }
    };
    assertEquals("processLine was called more than once", 1,
        CharStreams.readLines(r, alwaysFalse).intValue());

    // Test a LineProcessor that always returns true.
    LineProcessor<Integer> alwaysTrue = new LineProcessor<Integer>() {
      int seen;
      @Override
      public boolean processLine(String line) {
        seen++;
        return true;
      }
      @Override
      public Integer getResult() {
        return seen;
      }
    };
    assertEquals("processLine was not called for all the lines", 3,
        CharStreams.readLines(r, alwaysTrue).intValue());

    // Test a LineProcessor that is conditional.
    final StringBuilder sb = new StringBuilder();
    LineProcessor<Integer> conditional = new LineProcessor<Integer>() {
      int seen;
      @Override
      public boolean processLine(String line) {
        seen++;
        sb.append(line);
        return seen < 2;
      }
      @Override
      public Integer getResult() {
        return seen;
      }
    };
    assertEquals(2, CharStreams.readLines(r, conditional).intValue());
    assertEquals("ab", sb.toString());
  }

  public void testAlwaysCloses() throws IOException {
    CheckCloseSupplier.Input<Reader> okRead
        = newCheckReader(CharStreams.newReaderSupplier(TEXT));
    CheckCloseSupplier.Output<Writer> okWrite
        = newCheckWriter(new OutputSupplier<Writer>() {
          @Override
          public Writer getOutput() {
            return new StringWriter();
          }
        });
    CheckCloseSupplier.Input<Reader> brokenRead = newCheckReader(BROKEN_READ);
    CheckCloseSupplier.Output<Writer> brokenWrite
        = newCheckWriter(BROKEN_WRITE);

    CharStreams.copy(okRead, okWrite);
    assertTrue(okRead.areClosed());
    assertTrue(okWrite.areClosed());

    try {
      CharStreams.copy(okRead, brokenWrite);
      fail("expected exception");
    } catch (Exception e) {
      assertEquals("broken write", e.getMessage());
    }
    assertTrue(okRead.areClosed());
    assertTrue(brokenWrite.areClosed());

    try {
      CharStreams.copy(brokenRead, okWrite);
      fail("expected exception");
    } catch (Exception e) {
      assertEquals("broken read", e.getMessage());
    }
    assertTrue(brokenRead.areClosed());
    assertTrue(okWrite.areClosed());

    try {
      CharStreams.copy(brokenRead, brokenWrite);
      fail("expected exception");
    } catch (Exception e) {
      assertEquals("broken read", e.getMessage());
    }
    assertTrue(brokenRead.areClosed());
    assertTrue(brokenWrite.areClosed());

    assertEquals(TEXT, CharStreams.toString(okRead));
    assertTrue(okRead.areClosed());

    try {
      CharStreams.toString(brokenRead);
      fail("expected exception");
    } catch (Exception e) {
      assertEquals("broken read", e.getMessage());
    }
    assertTrue(brokenRead.areClosed());

    try {
      CharStreams.write("hello world", brokenWrite);
      fail("expected exception");
    } catch (Exception e) {
      assertEquals("broken write", e.getMessage());
    }
    assertTrue(brokenWrite.areClosed());
  }

  public void testCopySuppliersExceptions() {
    if (!Closer.SuppressingSuppressor.isAvailable()) {
      // test that exceptions are logged

      TestLogHandler logHandler = new TestLogHandler();
      Closeables.logger.addHandler(logHandler);
      try {
        for (InputSupplier<? extends Reader> in : BROKEN_INPUTS) {
          runFailureTest(in, newStringWriterSupplier());
          assertTrue(logHandler.getStoredLogRecords().isEmpty());

          runFailureTest(in, BROKEN_CLOSE_OUTPUT);
          assertEquals((in == BROKEN_GET_INPUT) ? 0 : 1, getAndResetRecords(logHandler));
        }

        for (OutputSupplier<? extends Writer> out : BROKEN_OUTPUTS) {
          runFailureTest(newReaderSupplier("ABC"), out);
          assertTrue(logHandler.getStoredLogRecords().isEmpty());

          runFailureTest(BROKEN_CLOSE_INPUT, out);
          assertEquals(1, getAndResetRecords(logHandler));
        }

        for (InputSupplier<? extends Reader> in : BROKEN_INPUTS) {
          for (OutputSupplier<? extends Writer> out : BROKEN_OUTPUTS) {
            runFailureTest(in, out);
            assertTrue(getAndResetRecords(logHandler) <= 1);
          }
        }
      } finally {
        Closeables.logger.removeHandler(logHandler);
      }
    } else {
      // test that exceptions are suppressed

      for (InputSupplier<? extends Reader> in : BROKEN_INPUTS) {
        int suppressed = runSuppressionFailureTest(in, newStringWriterSupplier());
        assertEquals(0, suppressed);

        suppressed = runSuppressionFailureTest(in, BROKEN_CLOSE_OUTPUT);
        assertEquals((in == BROKEN_GET_INPUT) ? 0 : 1, suppressed);
      }

      for (OutputSupplier<? extends Writer> out : BROKEN_OUTPUTS) {
        int suppressed = runSuppressionFailureTest(newReaderSupplier("ABC"), out);
        assertEquals(0, suppressed);

        suppressed = runSuppressionFailureTest(BROKEN_CLOSE_INPUT, out);
        assertEquals(1, suppressed);
      }

      for (InputSupplier<? extends Reader> in : BROKEN_INPUTS) {
        for (OutputSupplier<? extends Writer> out : BROKEN_OUTPUTS) {
          int suppressed = runSuppressionFailureTest(in, out);
          assertTrue(suppressed <= 1);
        }
      }
    }
  }

  private static int getAndResetRecords(TestLogHandler logHandler) {
    int records = logHandler.getStoredLogRecords().size();
    logHandler.clear();
    return records;
  }

  private static void runFailureTest(
      InputSupplier<? extends Reader> in, OutputSupplier<? extends Writer> out) {
    try {
      copy(in, out);
      fail();
    } catch (IOException expected) {
    }
  }

  /**
   * @return the number of exceptions that were suppressed on the expected thrown exception
   */
  private static int runSuppressionFailureTest(
      InputSupplier<? extends Reader> in, OutputSupplier<? extends Writer> out) {
    try {
      copy(in, out);
      fail();
    } catch (IOException expected) {
      return CloserTest.getSuppressed(expected).length;
    }
    throw new AssertionError(); // can't happen
  }

  private static OutputSupplier<Writer> newStringWriterSupplier() {
    return new OutputSupplier<Writer>() {
      @Override public Writer getOutput() {
        return new StringWriter();
      }
    };
  }

  public void testSkipFully_EOF() throws IOException {
    Reader reader = new StringReader("abcde");
    try {
      CharStreams.skipFully(reader, 6);
      fail("expected EOFException");
    } catch (EOFException e) {
      // expected
    }
  }

  public void testSkipFully() throws IOException {
    String testString = "abcdef";
    Reader reader = new StringReader(testString);

    assertEquals(testString.charAt(0), reader.read());
    CharStreams.skipFully(reader, 1);
    assertEquals(testString.charAt(2), reader.read());
    CharStreams.skipFully(reader, 2);
    assertEquals(testString.charAt(5), reader.read());

    assertEquals(-1, reader.read());
  }

  public void testAsWriter() {
    // Should wrap Appendable in a new object
    Appendable plainAppendable = new StringBuilder();
    Writer result = CharStreams.asWriter(plainAppendable);
    assertNotSame(plainAppendable, result);
    assertNotNull(result);

    // A Writer should not be wrapped
    Appendable secretlyAWriter = new StringWriter();
    result = CharStreams.asWriter(secretlyAWriter);
    assertSame(secretlyAWriter, result);
  }

  public void testWriteString() throws IOException {
    final StringWriter sw = new StringWriter();
    String expected = "foo";
    CharStreams.write(expected, new OutputSupplier<Writer>() {
      @Override public Writer getOutput() {
        return sw;
      }
    });
    assertEquals(expected, sw.toString());
  }

  public void testCopy() throws IOException {
    StringBuilder builder = new StringBuilder();
    long copied = CharStreams.copy(new StringReader(ASCII), builder);
    assertEquals(ASCII, builder.toString());
    assertEquals(ASCII.length(), copied);

    StringBuilder builder2 = new StringBuilder();
    copied = CharStreams.copy(new StringReader(I18N), builder2);
    assertEquals(I18N, builder2.toString());
    assertEquals(I18N.length(), copied);
  }

  /**
   * Test for Guava issue 1061: http://code.google.com/p/guava-libraries/issues/detail?id=1061
   *
   * <p>CharStreams.copy was failing to clear its CharBuffer after each read call, which effectively
   * reduced the available size of the buffer each time a call to read didn't fill up the available
   * space in the buffer completely. In general this is a performance problem since the buffer size
   * is permanently reduced, but with certain Reader implementations it could also cause the buffer
   * size to reach 0, causing an infinite loop.
   */
  public void testCopyWithReaderThatDoesNotFillBuffer() throws IOException {
    // need a long enough string for the buffer to hit 0 remaining before the copy completes
    String string = Strings.repeat("0123456789", 100);
    StringBuilder b = new StringBuilder();
    // the main assertion of this test is here... the copy will fail if the buffer size goes down
    // each time it is not filled completely
    long copied = CharStreams.copy(newNonBufferFillingReader(new StringReader(string)), b);
    assertEquals(string, b.toString());
    assertEquals(string.length(), copied);
  }

  public void testNullWriter() throws Exception {
    // create a null writer
    Writer nullWriter = CharStreams.nullWriter();
    // write to the writer
    nullWriter.write('n');
    String test = "Test string for NullWriter";
    nullWriter.write(test);
    nullWriter.write(test, 2, 10);
    // nothing really to assert?
    assertSame(CharStreams.nullWriter(), CharStreams.nullWriter());
  }

  private static CheckCloseSupplier.Input<Reader> newCheckReader(
      InputSupplier<? extends Reader> delegate) {
    return new CheckCloseSupplier.Input<Reader>(delegate) {
      @Override protected Reader wrap(Reader object, final Callback callback) {
        return new FilterReader(object) {
          @Override public void close() throws IOException {
            callback.delegateClosed();
            super.close();
          }
        };
      }
    };
  }

  private static CheckCloseSupplier.Output<Writer> newCheckWriter(
      OutputSupplier<? extends Writer> delegate) {
    return new CheckCloseSupplier.Output<Writer>(delegate) {
      @Override protected Writer wrap(Writer object, final Callback callback) {
        return new FilterWriter(object) {
          @Override public void close() throws IOException {
            callback.delegateClosed();
            super.close();
          }
        };
      }
    };
  }

  /**
   * Returns a reader wrapping the given reader that only reads half of the maximum number of
   * characters that it could read in read(char[], int, int).
   */
  private static Reader newNonBufferFillingReader(Reader reader) {
    return new FilterReader(reader) {
      @Override
      public int read(char[] cbuf, int off, int len) throws IOException {
        // if a buffer isn't being cleared correctly, this method will eventually start being called
        // with a len of 0 forever
        if (len <= 0) {
          fail("read called with a len of " + len);
        }
        // read fewer than the max number of chars to read
        // shouldn't be a problem unless the buffer is shrinking each call
        return in.read(cbuf, off, Math.max(len - 1024, 0));
      }
    };
  }
}
