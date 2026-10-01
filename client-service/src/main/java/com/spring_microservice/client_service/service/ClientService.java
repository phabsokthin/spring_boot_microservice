package com.spring_microservice.client_service.service;

import com.spring_microservice.client_service.entity.Client;
import com.spring_microservice.client_service.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public Client create(Client client) {

        if (repository.existsByClientCode(client.getClientCode())) {
            throw new RuntimeException("Client code already exists");
        }

        client.setStatus("ACTIVE");
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());

        return repository.save(client);
    }

    // READ ALL
    public List<Client> getAll() {
        return repository.findAll();
    }

    // READ ONE
    public Client getById(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Client not found"));
    }

    // UPDATE
    public Client update(String id, Client request) {

        Client client = getById(id);

        client.setClientName(request.getClientName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());
        client.setStatus(request.getStatus());
        client.setUpdatedAt(LocalDateTime.now());

        return repository.save(client);
    }

    // DELETE
    public void delete(String id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("Client not found");
        }

        repository.deleteById(id);
    }
}