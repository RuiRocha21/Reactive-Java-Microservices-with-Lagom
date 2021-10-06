package com.exercises.blog.impl;

import akka.japi.Pair;
import com.exercises.blog.api.*;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;

import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.transport.BadRequest;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.lightbend.lagom.javadsl.persistence.ReadSide;

import com.datastax.driver.core.Row;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service implementation for the blog microservice. This service is essentially a wrapper for the
 * persistence entity API.
 */
public class BlogServiceImpl implements BlogService {

    private static final Logger logger = LoggerFactory.getLogger(BlogServiceImpl.class);

    private final PersistentEntityRegistry registry;
    private final CassandraSession db;

    @Inject
    public BlogServiceImpl(final PersistentEntityRegistry registry, ReadSide readSide,
                           CassandraSession db) {
        this.registry = registry;
        this.db = db;

        registry.register(BlogEntity.class);
        readSide.register(BlogEventProcessor.class);
    }

    @Override
    public ServiceCall<NotUsed, Optional<PostContent>> getPost(final String id) {
        return request -> registry.refFor(BlogEntity.class, id)
                .ask(BlogCommand.GetPost.INSTANCE);
    }

    @Override
    public ServiceCall<PostContent, String> addPost() {
        return content -> registry.refFor(BlogEntity.class, UUID.randomUUID().toString())
                .ask(new BlogCommand.AddPost(content));
    }

    @Override
    public ServiceCall<UpdateContent, Done> updatePost(final String id) {
        return content -> registry.refFor(BlogEntity.class, id)
                .ask(new BlogCommand.UpdatePost(content));
    }

    @Override
    public ServiceCall<NotUsed, Done> deletePost(final String id) {
        return request -> registry.refFor(BlogEntity.class, id)
                .ask(BlogCommand.DeletePost.INSTANCE);
    }

    @Override
    public ServiceCall<NotUsed, PSequence<PostSummary>> getAllPosts(Integer pageNo, Integer pageSize) {
        return req -> {
            CompletionStage<PSequence<PostSummary>> result = db.selectAll("SELECT * FROM postcontent")
                    .thenApply(rows -> {
                        List<PostSummary> posts = rows.stream()
                                .skip(pageNo*pageSize)
                                .limit(pageSize)
                                .map(this::mapPostSummary).collect(Collectors.toList());
                        return TreePVector.from(posts);
                    });
            return result;
        };
    }

    @Override
    public ServiceCall<NotUsed, Source<PostSummary, ?>> getLivePosts() {
        return req -> {

            Source<PostSummary, ?> result = db.select("SELECT * FROM postcontent")
                    .map(this::mapPostSummary);
            return CompletableFuture.completedFuture(result);
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<PostSummary>> getPostsByAuthor(final String author, Integer pageNo, Integer pageSize) {
        return req -> {
            CompletionStage<PSequence<PostSummary>> result = db.selectAll("SELECT * FROM postcontent where author = ? ORDER BY timestamp DESC", author)
                    .thenApply(rows -> {
                        List<PostSummary> posts = rows.stream()
                                .skip(pageNo*pageSize)
                                .limit(pageSize)
                                .map(this::mapPostSummary).collect(Collectors.toList());
                        return TreePVector.from(posts);
                    });
            return result;
        };
    }

    @Override
    public ServiceCall<String, Source<PostSummary, ?>> getLivePostsByAuthor() {

        return author -> {

            Source<PostSummary, ?> result = db.select("SELECT * FROM postcontent where author = ? ORDER BY timestamp DESC", author)
                    .map(this::mapPostSummary);
            return CompletableFuture.completedFuture(result);
        };
    }

    @Override
    public Topic<BlogEventApi> postTopic() {
        logger.info("Post topic");
        //return TopicProducer.taggedStreamWithOffset(BlogEvent.TAG.allTags(), (tag, offset) ->
        return TopicProducer.taggedStreamWithOffset(BlogEventTag.TAG.allTags(), (tag, offset) ->
                registry.eventStream(tag, offset)
                        .map(eventAndOffset -> {
                            BlogEventApi eventToPublish;
                            BlogEvent event = eventAndOffset.first();
                            if(event instanceof BlogEvent.PostAdded){
                                BlogEvent.PostAdded postAdd = (BlogEvent.PostAdded) event;
                                eventToPublish = new BlogEventApi.PostAdded(postAdd.getId(), postAdd.getTimestamp(), postAdd.getContent());

                            }else if(event instanceof BlogEvent.PostUpdated){
                                BlogEvent.PostUpdated postUpdate = (BlogEvent.PostUpdated) event;
                                eventToPublish = new BlogEventApi.PostUpdated(postUpdate.getTimestamp(),postUpdate.getContent());
                            }else if(event instanceof BlogEvent.PostDeleted){
                                BlogEvent.PostDeleted postDelete = (BlogEvent.PostDeleted) event;
                                eventToPublish = new BlogEventApi.PostDeleted(postDelete.getAuthor(), postDelete.getTimestamp());
                            }else{
                                throw new IllegalArgumentException("Unknown event: " + event);
                            }
                            return Pair.create(eventToPublish, eventAndOffset.second());
                        })
        );
    }

    private PostSummary mapPostSummary(Row row) {
        return new PostSummary(
                row.getString("id"),
                row.getString("title")
        );
    }

}
