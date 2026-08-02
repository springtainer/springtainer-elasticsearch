package com.avides.springboot.springtainer.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class EmbeddedElasticsearchContainerAutoConfigurationIT extends AbstractIT
{
    @Test
    public void testGeneratedProperties()
    {
        assertThat(environment.getProperty("embedded.container.elasticsearch.host")).isNotEmpty();
        assertThat(environment.getProperty("embedded.container.elasticsearch.http-port")).isNotEmpty();
        assertThat(environment.getProperty("embedded.container.elasticsearch.transport-port")).isNotEmpty();

        System.out.println();
        System.out.println("Resolved properties:");
        System.out.println("Host:           " + environment.getProperty("embedded.container.elasticsearch.host"));
        System.out.println("HTTP-Port:      " + environment.getProperty("embedded.container.elasticsearch.http-port"));
        System.out.println("Transport-Port: " + environment.getProperty("embedded.container.elasticsearch.transport-port"));
        System.out.println();
    }

    @Test
    public void testCrud() throws IOException
    {
        // create
        index("key1", new DummyDocument("key1", "value1"));
        assertThat(get("key1").getValue()).isEqualTo("value1");

        // update
        index("key1", new DummyDocument("key1", "value2"));
        assertThat(get("key1").getValue()).isEqualTo("value2");

        // delete
        delete("key1");
        assertThat(elasticsearchClient.get(request -> request.index(INDEX).id("key1"), DummyDocument.class).found()).isFalse();
    }

    /**
     * Searching exercises query serialization and hit deserialization, which is the part of the client/server
     * contract most sensitive to a version mismatch between the two. Get-by-id keeps working across such a
     * mismatch, so it does not cover this on its own.
     */
    @Test
    public void testSearch() throws IOException
    {
        index("key1", new DummyDocument("key1", "matching"));
        index("key2", new DummyDocument("key2", "matching"));
        index("key3", new DummyDocument("key3", "different"));

        var response = elasticsearchClient.search(
                request -> request.index(INDEX).query(query -> query.match(match -> match.field("value").query("matching"))),
                DummyDocument.class);

        assertThat(response.hits().total().value()).isEqualTo(2);
        assertThat(response.hits().hits())
                .extracting(hit -> hit.source().getKey())
                .containsExactlyInAnyOrder("key1", "key2");

        delete("key1");
        delete("key2");
        delete("key3");
    }

    private DummyDocument get(String id) throws IOException
    {
        return elasticsearchClient.get(request -> request.index(INDEX).id(id), DummyDocument.class).source();
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfiguration
    {
        // nothing
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class DummyDocument
    {
        private String key;

        private String value;
    }
}
