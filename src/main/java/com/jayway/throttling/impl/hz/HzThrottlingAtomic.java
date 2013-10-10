package com.jayway.throttling.impl.hz;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jayway.throttling.ThrottlingContext;
import com.jayway.throttling.ThrottlingService;

public class HzThrottlingAtomic implements ThrottlingContext{

	private HazelcastInstance instance;

	public HzThrottlingAtomic() {
		Config cfg = new Config();
		instance = Hazelcast.newHazelcastInstance(cfg);	}

	@Override
	public ThrottlingService getThrottlingService() {
		return new HzThrottlingServiceAtomic(instance);
	}

	@Override
	public void close() {
		instance.getLifecycleService().shutdown();
	}
}
