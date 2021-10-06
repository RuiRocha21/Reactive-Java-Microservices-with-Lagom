package com.exercises.censured.impl;

import akka.Done;
import akka.stream.javadsl.Flow;

import com.exercises.blog.api.BlogService;
import com.exercises.blog.api.BlogEventApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class CensuredSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(CensuredSubscriber.class);

    @Inject
    public CensuredSubscriber(BlogService blogService, CensuredRepository repository){
        logger.info("Censured Subscriber");
        blogService.postTopic().subscribe()
                .atLeastOnce(
                        Flow.<BlogEventApi>create().mapAsync(3, event -> {
                            if (event instanceof BlogEventApi.PostAdded) {
                                BlogEventApi.PostAdded addPost = (BlogEventApi.PostAdded) event;
                                String bodyCensurate = censurateBody(addPost.getContent().getBody());
                                logger.info(bodyCensurate);
                                return repository.insertPost(addPost.getContent().getAuthor(),
                                        addPost.getTimestamp().toString(),
                                        addPost.getContent().getTitle(),
                                        bodyCensurate,
                                        addPost.getId());
                            }else if(event instanceof BlogEventApi.PostUpdated){
                                BlogEventApi.PostUpdated addUpdate = (BlogEventApi.PostUpdated) event;
                                String bodyCensurate = censurateBody(addUpdate.getContent().getBody());
                                return repository.updatePost(addUpdate.getContent().getTitle(),
                                        bodyCensurate,
                                        addUpdate.getContent().getAuthor(),
                                        addUpdate.getTimestamp().toString());
                            }else if(event instanceof BlogEventApi.PostDeleted){
                                BlogEventApi.PostDeleted addDelete = (BlogEventApi.PostDeleted) event;
                                return repository.deletePost(addDelete.getAuthor(),addDelete.getTimestamp().toString());
                            }else{
                                return CompletableFuture.completedFuture(Done.getInstance());
                            }
                        })
                );
    }

    private String censurateBody(String body) {
        ArrayList<String> lista = new ArrayList<String>();
        lista.add("shit");
        lista.add("fuck off");
        lista.add("fuck");
        lista.add("bitch");
        String newBody = "";
        for (int i = 0; i<lista.size();i++){
                if(body.contains(lista.get(i))){
                    body = body.toLowerCase().replaceAll(lista.get(i), "[CENSURED]");
                }
        }
        newBody = body;
        return newBody;
    }
}

