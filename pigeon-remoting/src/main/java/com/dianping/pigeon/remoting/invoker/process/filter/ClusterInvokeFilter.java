/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.remoting.invoker.process.filter;

import org.apache.logging.log4j.Logger;

import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.monitor.Monitor;
import com.dianping.pigeon.monitor.MonitorLoader;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.process.ServiceInvocationHandler;
import com.dianping.pigeon.remoting.invoker.cluster.Cluster;
import com.dianping.pigeon.remoting.invoker.cluster.ClusterFactory;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import com.dianping.pigeon.remoting.invoker.domain.InvokerContext;

public class ClusterInvokeFilter extends InvocationInvokeFilter {

	private static final Logger logger = LoggerLoader.getLogger(ClusterInvokeFilter.class);
	private Monitor monitor = MonitorLoader.getMonitor();

	public InvocationResponse invoke(ServiceInvocationHandler handler, InvokerContext invocationContext)
			throws Throwable {
		if (logger.isDebugEnabled()) {
			logger.debug("invoke the ClusterInvokeFilter, invocationContext:" + invocationContext);
		}
		InvokerConfig<?> invokerConfig = invocationContext.getInvokerConfig();
		Cluster cluster = ClusterFactory.selectCluster(invokerConfig.getCluster());
		if (cluster == null) {
			throw new IllegalArgumentException("Unsupported cluster type:" + cluster);
		}
		try {
			return cluster.invoke(handler, invocationContext);
		} catch (Throwable e) {
			if (monitor != null) {
				monitor.logError("invoke remote call failed", e);
			}
			logger.error("invoke remote call failed", e);
			throw e;
		}
	}

}
