/*
 * Copyright (C) 2012 The Guava Authors
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

package com.google.common.hash;

import com.google.caliper.Param;
import com.google.caliper.legacy.Benchmark;
import com.google.caliper.runner.CaliperMain;

import java.security.MessageDigest;

/**
 * Benchmarks for comparing instance creation of {@link MessageDigest}s.
 *
 * @author Kurt Alfred Kluever
 */
public class MessageDigestCreationBenchmark extends Benchmark {

  @Param({"MD5", "SHA-1", "SHA-256", "SHA-512"})
  private String algorithm;

  private MessageDigest md;

  public void setUp() throws Exception {
    md = MessageDigest.getInstance(algorithm);
  }

  public int timeGetInstance(int reps) throws Exception {
    int retValue = 0;
    for (int i = 0; i < reps; i++) {
      retValue ^= MessageDigest.getInstance(algorithm).getDigestLength();
    }
    return retValue;
  }

  public int timeClone(int reps) throws Exception {
    int retValue = 0;
    for (int i = 0; i < reps; i++) {
      retValue ^= ((MessageDigest) md.clone()).getDigestLength();
    }
    return retValue;
  }

  public static void main(String[] args) {
    CaliperMain.main(MessageDigestCreationBenchmark.class, args);
  }
}
