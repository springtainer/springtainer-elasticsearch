package com.avides.springboot.springtainer.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

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
    public void testCrud()
    {
        // create
        var indexQuery = new IndexQuery();
        indexQuery.setId("key1");
        indexQuery.setObject(new DummyDocument("key1", "value1"));
        index(indexQuery, elasticsearchRestTemplate.getIndexCoordinatesFor(DummyDocument.class));

        // read
        assertThat(elasticsearchRestTemplate.get("key1", DummyDocument.class).getValue()).isEqualTo("value1");

        // update
        var updateQuery = new IndexQuery();
        updateQuery.setId("key1");
        updateQuery.setObject(new DummyDocument("key1", "value2"));
        index(updateQuery, elasticsearchRestTemplate.getIndexCoordinatesFor(DummyDocument.class));
        assertThat(elasticsearchRestTemplate.get("key1", DummyDocument.class).getValue()).isEqualTo("value2");

        // delete
        elasticsearchRestTemplate.delete("key1", elasticsearchRestTemplate.getIndexCoordinatesFor(DummyDocument.class));
        assertNull(elasticsearchRestTemplate.get("key1", DummyDocument.class));
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfiguration
    {
        // nothing
    }

    @Document(indexName = "test")
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class DummyDocument
    {
        @Id
        private String key;

        private String value;
    }
}
