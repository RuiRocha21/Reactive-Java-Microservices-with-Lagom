package com.exercises.censured.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class CensuredRepository {

    private final CassandraSession uninitializedSession;

    // Will return the session when the Cassandra tables have been successfully created
    private volatile CompletableFuture<CassandraSession> initializedSession;

    @Inject
    public CensuredRepository(CassandraSession uninitializedSession) {
        this.uninitializedSession = uninitializedSession;
        // Eagerly create the session
        session();
    }

    private CompletionStage<CassandraSession> session() {
        // If there's no initialized session, or if the initialized session future completed
        // with an exception, then reinitialize the session and attempt to create the tables
        if (initializedSession == null || initializedSession.isCompletedExceptionally()) {
            initializedSession = uninitializedSession.executeCreateTable(
                    "CREATE TABLE IF NOT EXISTS postCensured ("
                            + "id text, timestamp bigint, title text, body text, author text, "
                            + "PRIMARY KEY (author, timestamp))"
            ).thenApply(done -> uninitializedSession).toCompletableFuture();
        }
        return initializedSession;
    }

    public CompletionStage<Done> insertPost(String author, String timestamp, String title, String body, String id) {
        return session().thenCompose(session ->
                session.executeWrite("INSERT INTO postCensured (author, timestamp, title, body, id) VALUES (?, ?, ?, ?, ?)",
                        author, timestamp,title,body, id)
        );
    }

    public CompletionStage<Optional<String>> getPost(String name) {
        return session().thenCompose(session ->
                session.selectOne("SELECT author FROM postCensured WHERE name = ?", name)
        ).thenApply(maybeRow -> maybeRow.map(row -> row.getString("author")));
    }

    public CompletionStage<Done> updatePost(String title, String body,  String author,Instant timestamp) {
        return session().thenCompose(session ->
                session.executeWrite("UPDATE postCensured set title = ?, body = ? where author = ? and timestamp = ?",
                        title, body,author, timestamp)
        );
    }

    public CompletionStage<Done> deletePost( String author,Instant timestamp) {
        return session().thenCompose(session ->
                session.executeWrite("DELETE FROM postCensured WHERE author = ? and timestamp = ?",
                        author, timestamp)
        );
    }
}
