package com.motomapia;

import java.util.List;

import lombok.Data;

import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueConstants;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Convenience methods for adding to the appengine queues.
 */
public class Queues
{
	/** Just a slightly more convenient interface for our purposes */
	@Data
	public static class QueueHelper {
		private final Queue queue;

		public void add(DeferredTask payload) {
			this.add(null, payload);
		}

		public void add(Transaction txn, DeferredTask payload) {
			queue.add(txn, TaskOptions.Builder.withPayload(payload));
		}

		/** Allows any number of tasks; automatically partitions as necessary */
		public void add(Iterable<? extends DeferredTask> payloads) {
			Iterable<TaskOptions> opts = Iterables.transform(payloads, new Function<DeferredTask, TaskOptions>() {
				@Override
				public TaskOptions apply(DeferredTask task) {
					return TaskOptions.Builder.withPayload(task);
				}
			});

			Iterable<List<TaskOptions>> partitioned = Iterables.partition(opts, QueueConstants.maxTasksPerAdd());

			for (List<TaskOptions> piece: partitioned)
				queue.add(null, piece);
		}
	}

	/** The default queue ('default' is a java keyword, oops) */
	public static QueueHelper deflt() {
		return new QueueHelper(QueueFactory.getDefaultQueue());
	}

	/** Queue for place synchronization */
	public static QueueHelper sync() {
		return new QueueHelper(QueueFactory.getQueue("sync"));
	}
}