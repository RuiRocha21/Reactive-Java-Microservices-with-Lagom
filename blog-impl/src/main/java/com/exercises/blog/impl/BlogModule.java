package com.exercises.blog.impl;

import com.exercises.blog.api.BlogService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class BlogModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindService(BlogService.class, BlogServiceImpl.class);
    }
}