package com.spring_microservice.client_service.repository;

import com.spring_microservice.client_service.entity.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClientRepository
        extends MongoRepository<Client, String> {

    Optional<Client> findByClientCode(String clientCode);

    boolean existsByClientCode(String clientCode);
}