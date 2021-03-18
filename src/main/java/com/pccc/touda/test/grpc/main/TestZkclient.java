package com.pccc.touda.test.grpc.main;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class TestZkclient {
    private static final String CONNECT_ADDR="182.180.115.33:2181";
    private static final String CONNECT_ADDR_LOCAL="127.0.0.1:2181";
    private static final int SESSION_OUTTIME=30000;
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR_LOCAL)
                .connectionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                .namespace("xiefei").build();
        client.start();
        client.create().forPath("/test", "testNodePersist".getBytes());

    }
}
