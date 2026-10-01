package com.spring_microservice.client_service.controller;



import com.spring_microservice.client_service.entity.Client;
import com.spring_microservice.client_service.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Client> create(
            @RequestBody Client client) {

        return ResponseEntity.ok(
                service.create(client)
        );
    }

    // TEST API
    @GetMapping("/test")
    public String test() {
        return "Client is working";
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Client>> getAll() {

        return ResponseEntity.ok(
                service.getAll()
        );
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Client> getById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                service.getById(id)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Client> update(
            @PathVariable String id,
            @RequestBody Client client) {

        return ResponseEntity.ok(
                service.update(id, client)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}