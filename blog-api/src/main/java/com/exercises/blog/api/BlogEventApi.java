package com.exercises.blog.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.concurrent.Immutable;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
        @JsonSubTypes.Type(BlogEventApi.PostAdded.class),
        @JsonSubTypes.Type(BlogEventApi.PostUpdated.class),
        @JsonSubTypes.Type(BlogEventApi.PostDeleted.class)
})
public interface BlogEventApi {

    @Immutable
    @JsonDeserialize
    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    @JsonTypeName(value = "post-added")
    final class PostAdded implements BlogEventApi {
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
    @JsonTypeName(value = "post-updated")
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class PostUpdated implements BlogEventApi {
        @NonNull
        Instant timestamp;
        @NonNull
        PostContent content;
    }

    @Immutable
    @JsonDeserialize
    @Value
    @JsonTypeName(value = "post-deleted")
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class PostDeleted implements BlogEventApi {
        @NonNull
        String author;
        @NonNull
        Instant timestamp;
    }
}