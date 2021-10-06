package com.exercises.blog.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.concurrent.Immutable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Immutable
@JsonDeserialize
@Value
@Builder
//@With
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public final class PostContent {


    @NonNull
    String title;
    @NonNull
    String body;
    @NonNull
    String author;

    public PostContent withBody(String body){
        return this.body == body ? this : new PostContent(title, body,author);
    }

}

