package com.exercises.censured.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import com.exercises.blog.api.BlogService;
import com.exercises.censured.api.CensuredService;
import com.exercises.blog.api.PostContent;
import javax.inject.Inject;

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
}
