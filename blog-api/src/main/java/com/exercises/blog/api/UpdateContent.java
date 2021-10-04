package com.exercises.blog.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;


import javax.annotation.concurrent.Immutable;


@Immutable
@JsonDeserialize
@Value
@Builder
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public final class UpdateContent {

    @NonNull
    String title;
    @NonNull
    String body;
}