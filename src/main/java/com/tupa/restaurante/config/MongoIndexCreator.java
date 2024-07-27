package com.tupa.restaurante.config;


import com.tupa.restaurante.entidades.Mesa;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;


@Configuration
public class MongoIndexCreator {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        mongoTemplate.indexOps(Mesa.class).ensureIndex(new Index().on("numero", org.springframework.data.domain.Sort.Direction.ASC).unique());
    }
}
