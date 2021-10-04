package com.exercises.censured.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import com.exercises.blog.api.BlogService;
import com.exercises.censured.api.CensuredService;

public class CensuredModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        // Bind the Censured service
        bindService(CensuredService.class, CensuredServiceImpl.class);
        // Bind the BlogService client
        bindClient(BlogService.class);
        // Bind the subscriber eagerly to ensure it starts up
        bind(CensuredSubscriber.class).asEagerSingleton();
    }
}