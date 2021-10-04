package com.exercises.blog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.exercises.blog.api.PostContent;

import java.time.Instant;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
@JsonDeserialize
@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class BlogState implements CompressedJsonable {

    private Logger logger = LoggerFactory.getLogger(BlogState.class);

    public static final BlogState EMPTY = new BlogState(Optional.empty(), Optional.empty());

    Optional<PostContent> content;

    Optional<Instant> timestamp;
}