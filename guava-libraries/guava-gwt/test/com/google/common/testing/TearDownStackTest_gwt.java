/*
 * Copyright (C) 2008 The Guava Authors
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
package com.google.common.testing;
public class TearDownStackTest_gwt extends com.google.gwt.junit.client.GWTTestCase {
@Override public String getModuleName() {
  return "com.google.common.testing.testModule";
}
public void testMultipleTearDownsHappenInOrder() throws Exception {
  com.google.common.testing.TearDownStackTest testCase = new com.google.common.testing.TearDownStackTest();
  try {
    testCase.testMultipleTearDownsHappenInOrder();
  }
 finally {
    try {
      testCase.tearDown();
    } catch (Throwable t) {
      // ignore exceptions in tearDown().
    }
  }
}

public void testSingleTearDown() throws Exception {
  com.google.common.testing.TearDownStackTest testCase = new com.google.common.testing.TearDownStackTest();
  try {
    testCase.testSingleTearDown();
  }
 finally {
    try {
      testCase.tearDown();
    } catch (Throwable t) {
      // ignore exceptions in tearDown().
    }
  }
}

public void testThrowingTearDown() throws Exception {
  com.google.common.testing.TearDownStackTest testCase = new com.google.common.testing.TearDownStackTest();
  try {
    testCase.testThrowingTearDown();
  }
 finally {
    try {
      testCase.tearDown();
    } catch (Throwable t) {
      // ignore exceptions in tearDown().
    }
  }
}
}
