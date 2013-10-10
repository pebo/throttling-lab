package com.jayway.throttling.impl.hz;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.jayway.throttling.Exceptions;
import com.jayway.throttling.Interval;
import com.jayway.throttling.ThrottlingService;

public class HzThrottlingServiceAtomic implements ThrottlingService {

	private static final Logger logger = LoggerFactory.getLogger(HzThrottlingServiceAtomic.class);

	private HazelcastInstance hazelcastInstance;

	public HzThrottlingServiceAtomic(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

	@Override
	public boolean allow(String account, long cost, Callable<Interval> newInterval) {

		IAtomicLong expiry = hazelcastInstance.getAtomicLong(account + "interval");
		Interval interval = getInterval(newInterval);

		long now = System.currentTimeMillis();
		long timestamp = now + interval.seconds * 1000;
		
		long expirationTime = expiry.get();
		if (expirationTime == 0 || now > expirationTime) {
			expiry.set(timestamp);
			IAtomicLong credits = hazelcastInstance.getAtomicLong(account + "credits");
			credits.set(interval.credits - 1);
			logger.debug("{} - Creating new value", account);
			return true;
		} else {
			IAtomicLong credits = hazelcastInstance.getAtomicLong(account + "credits");
			if (credits.get() > 0) {
				logger.debug("{} - decr new value: {}", account, credits.decrementAndGet());

				return true;
			} else {
				logger.debug("{} - out of credits!", account);
				return false;
			}
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
