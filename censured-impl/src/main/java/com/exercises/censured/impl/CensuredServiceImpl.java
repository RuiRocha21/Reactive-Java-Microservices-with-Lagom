package com.exercises.censured.impl;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.exercises.blog.api.UpdateContent;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import com.exercises.blog.api.BlogService;
import com.exercises.censured.api.CensuredService;
import com.exercises.blog.api.PostContent;
import javafx.geometry.Pos;

import javax.inject.Inject;

import java.time.Instant;
import java.util.UUID;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class CensuredServiceImpl implements CensuredService{

    private final BlogService blogService;
    private final CensuredRepository repository;

    @Inject
    public CensuredServiceImpl(BlogService blogService, CensuredRepository repository) {
        this.blogService = blogService;
        this.repository = repository;
    }

    public ServiceCall<Source<PostContent, NotUsed>, Source<String, NotUsed>> directStream(){
        return addPost -> completedFuture(
                addPost.mapAsync(8, name -> blogService.addPost().invoke()));
    }

    public ServiceCall<Source<PostContent, NotUsed>, Source<?, NotUsed>> update(){
        return upDatePost -> completedFuture(
                upDatePost.mapAsync(8, up ->blogService.updatePost(UUID.randomUUID().toString()).invoke()));
    }

    public ServiceCall<Source<PostContent, NotUsed>, Source<?, NotUsed>> delete(){
        return deletePost -> completedFuture(
                deletePost.mapAsync(8, del ->blogService.deletePost(UUID.randomUUID().toString()).invoke()));
    }
}
