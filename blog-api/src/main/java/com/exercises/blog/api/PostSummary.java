package com.exercises.blog.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.concurrent.Immutable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;


@Immutable
@JsonDeserialize
@Value
@Builder
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public final class PostSummary {

    @NonNull
    String id;
    @NonNull
    String title;
}