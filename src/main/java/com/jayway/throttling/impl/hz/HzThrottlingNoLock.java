package com.jayway.throttling.impl.hz;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jayway.throttling.ThrottlingContext;
import com.jayway.throttling.ThrottlingService;

public class HzThrottlingNoLock implements ThrottlingContext{

	private HazelcastInstance instance;

	public HzThrottlingNoLock() {
		Config cfg = new Config();
		instance = Hazelcast.newHazelcastInstance(cfg);	}

	@Override
	public ThrottlingService getThrottlingService() {
		return new HzThrottlingServiceNoLock(instance);
	}

	@Override
	public void close() {
		instance.getLifecycleService().shutdown();
	}
}
