// Copyright (c) 2012 Cloudera, Inc. All rights reserved.

package com.cloudera.impala.service;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class InsertQueryTest extends BaseQueryTest {
  @Test
  public void TestInsert() {
    runQueryInAllBatchAndClusterPerms("insert", false, 1000, INSERT_FORMATS,
        SMALL_BATCH_SIZES, SMALL_CLUSTER_SIZES);
  }

  @Test
  public void TestInsertOverwrite() {
    runQueryInAllBatchAndClusterPerms("insert_overwrite", false, 1000, TEXT_FORMAT_ONLY,
        SMALL_BATCH_SIZES, SMALL_CLUSTER_SIZES);
  }

  @Test
  public void TestInsertNull() {
    runQueryInAllBatchAndClusterPerms("insert_null", false, 1000, TEXT_FORMAT_ONLY,
        SMALL_BATCH_SIZES, SMALL_CLUSTER_SIZES);
  }

  // Because we disagree with hive on what to do on overflow, only
  // test overflow on those types that we can insert.
  // Hive makes them NULL.
  @Test
  public void TestOverflow() {
    runQueryInAllBatchAndClusterPerms("overflow", false, 1000, INSERT_FORMATS,
        ImmutableList.of(0), ImmutableList.of(1));
  }

  //TODO - see hdfs-text-scanner.cc for what needs to be done to support NULL partition
  //keys.
  //@Test
  //public void TestInsertNulls() {
  //  runQueryTestFile("insert-nulls", false, 1000);
  //}
}
