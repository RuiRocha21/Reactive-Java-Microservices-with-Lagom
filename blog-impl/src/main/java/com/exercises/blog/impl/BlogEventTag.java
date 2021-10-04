package com.exercises.blog.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlogEventTag {

    private static final Logger logger = LoggerFactory.getLogger(BlogEventTag.class);
    /**
     * Tags are used for getting and publishing streams of events. Each event
     * will have this tag, and in this case, we are partitioning the tags into
     * 4 shards, which means we can have 4 concurrent processors/publishers of
     * events.
     */
    public static final AggregateEventShards<BlogEvent> TAG = AggregateEventTag.sharded(BlogEvent.class, 4);
}
