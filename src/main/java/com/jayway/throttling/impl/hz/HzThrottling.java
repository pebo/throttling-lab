package com.jayway.throttling.impl.hz;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jayway.throttling.ThrottlingContext;
import com.jayway.throttling.ThrottlingService;

public class HzThrottling implements ThrottlingContext{

	private HazelcastInstance instance;

	public HzThrottling() {
//		Config cfg = new ClasspathXmlConfig("hazelcast");
//		cfg.getGroupConfig().setName("hamsterapp");
	
//		TcpIpConfig tcpIpConfig = cfg.getNetworkConfig().getJoin().getTcpIpConfig();
//        tcpIpConfig.setEnabled(true);
//        
//        System.getProperty("hz.hosts");
//        tcpIpConfig.addMember(member)
//        int port = 12301;
//        cfg.getNetworkConfig().setPortAutoIncrement(false);
//        cfg.getNetworkConfig().setPort(port);
//        cfg.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        
		instance = Hazelcast.newHazelcastInstance(null);	}

	@Override
	public ThrottlingService getThrottlingService() {
		return new HzThrottlingService(instance);
	}

	@Override
	public void close() {
		instance.getLifecycleService().shutdown();
	}
}
