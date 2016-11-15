package com.ccservice.b2b2c.atom.service;

import com.danga.MemCached.MemCachedClient;

public class MemcacheCacheManager {

    MemCachedClient memCachedClient;

    public MemCachedClient getMemCachedClient() {
        return memCachedClient;
    }

    public void setMemCachedClient(MemCachedClient memCachedClient) {
        this.memCachedClient = memCachedClient;
    }

}
