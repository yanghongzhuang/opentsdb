// This file is part of OpenTSDB.
// Copyright (C) 2016  The OpenTSDB Authors.
//
// This program is free software: you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 2.1 of the License, or (at your
// option) any later version.  This program is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
// General Public License for more details.  You should have received a copy
// of the GNU Lesser General Public License along with this program.  If not,
// see <http://www.gnu.org/licenses/>.
package net.opentsdb.execution;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.RejectedExecutionException;

import com.stumbleupon.async.Deferred;

import net.opentsdb.data.DataShardsGroup;
import net.opentsdb.exceptions.RemoteQueryExecutionException;
import net.opentsdb.query.context.QueryContext;
import net.opentsdb.query.pojo.Query;

/**
 * A base query executor that may spawn a tree of sub executors for processing.
 * @since 3.0
 */
public abstract class QueryExecutor {

  /** The query context. */
  protected final QueryContext context;
  
  /** Set to true when the upstream caller has marked this stream as (or cancelled) */
  protected final AtomicBoolean cancelled;

  /**
   * Default ctor.
   * @param context A non-null stream context for all components of this stream.
   * @throws IllegalArgumentException if the context was null.
   */
  public QueryExecutor(final QueryContext context) {
    if (context == null) {
      throw new IllegalArgumentException("Context cannot be null for "
          + "QueryExecutors.");
    }
    this.context = context;
    cancelled = new AtomicBoolean();
  }
  
  /**
   * Runs the given query.
   * @param query A non-null query to execute.
   * @return A deferred to wait on for results. The result will be a shard group
   * (potentially empty) or an exception.
   * @throws IllegalArgumentException if the query was null.
   * @throws RejectedExecutionException (in the deferred) if the query could not
   * be executed due to an error such as already being cancelled.
   * @throws RemoteQueryExecutionException (in the deferred) if the remote call
   * failed.
   */
  public abstract Deferred<DataShardsGroup> executeQuery(final Query query);
  
  /**
   * Method called to close and release all resources.
   * @return A non-null deferred that may contain a null response or an exception
   * on completion.
   */
  public abstract Deferred<Object> close();
  
}
