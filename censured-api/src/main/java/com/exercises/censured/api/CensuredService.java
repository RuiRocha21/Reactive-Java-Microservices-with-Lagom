package com.exercises.censured.api;

import com.exercises.blog.api.PostContent;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.namedCall;

public interface CensuredService extends Service{

    ServiceCall<Source<PostContent, NotUsed>, Source<String, NotUsed>> directStream();

    @Override
    default Descriptor descriptor() {
        return named("censured")
                .withCalls(
                        namedCall("direct-stream", this::directStream)
                )
                .withAutoAcl(true);
    }
}
