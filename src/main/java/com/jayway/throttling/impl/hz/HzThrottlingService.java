package com.jayway.throttling.impl.hz;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.jayway.throttling.Exceptions;
import com.jayway.throttling.Interval;
import com.jayway.throttling.ThrottlingService;

public class HzThrottlingService implements ThrottlingService {

	private static final Logger logger = LoggerFactory
			.getLogger(HzThrottlingService.class);

	private HazelcastInstance hazelcastInstance;

	public HzThrottlingService(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

	@Override
	public boolean allow(String account, long cost,
			Callable<Interval> newInterval) {

		ILock lock = hazelcastInstance.getLock(account);
		lock.lock();
		try {
			IMap<Object, Object> map = hazelcastInstance.getMap(account);
			Long counter = (Long) map.get("counter");
			if (counter == null) {
				logger.debug("{} - Creating new value", account);
				Interval interval = getInterval(newInterval);
				map.put("counter", interval.credits - 1, interval.seconds,
						TimeUnit.SECONDS);
				return true;
			}

			if (counter > 0) {
				logger.debug("{} - decr new value: {}", account, counter--);
				map.put("counter", counter);
				return true;
			}
			logger.debug("{} - out of credits!", account);
			return false;
		} finally {
			lock.unlock();
		}

	}

	private Interval getInterval(Callable<Interval> newInterval) {
		try {
			return newInterval.call();
		} catch (Exception e) {
			throw Exceptions.asUnchecked(e);
		}
	}

}
