package com.exercises.blog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import com.exercises.blog.api.PostContent;

import javax.annotation.concurrent.Immutable;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;


public interface BlogEvent extends Jsonable, AggregateEvent<BlogEvent> {

    @Override
    default AggregateEventTagger<BlogEvent> aggregateTag() {
        return BlogEventTag.TAG;
    }

    @Immutable
    @JsonDeserialize
    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class PostAdded implements BlogEvent, CompressedJsonable {
        @NonNull
        String id;
        @NonNull
        Instant timestamp;
        @NonNull
        PostContent content;
    }

    @Immutable
    @JsonDeserialize
    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class PostUpdated implements BlogEvent, CompressedJsonable {
        @NonNull
        Instant timestamp;
        @NonNull
        PostContent content;
    }

    @Immutable
    @JsonDeserialize
    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class PostDeleted implements BlogEvent, CompressedJsonable {
        @NonNull
        String author;
        @NonNull
        Instant timestamp;
    }
}