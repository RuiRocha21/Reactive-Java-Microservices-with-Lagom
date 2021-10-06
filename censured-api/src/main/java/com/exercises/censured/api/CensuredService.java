package com.exercises.censured.api;

import com.exercises.blog.api.PostContent;
import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.exercises.blog.api.UpdateContent;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface CensuredService extends Service{

    ServiceCall<Source<PostContent, NotUsed>, Source<String, NotUsed>> directStream();

    ServiceCall<Source<PostContent, NotUsed>, Source<?, NotUsed>> update();

    ServiceCall<Source<PostContent, NotUsed>, Source<?, NotUsed>> delete();
    @Override
    default Descriptor descriptor() {
        return named("censured")
                .withCalls(
                        namedCall("direct-stream", this::directStream)
                       ,namedCall("update", this::update),
                        namedCall("delete", this::delete)
                )
                .withAutoAcl(true);
    }
}
