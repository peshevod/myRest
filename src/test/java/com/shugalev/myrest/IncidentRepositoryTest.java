package com.shugalev.myrest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import static org.junit.jupiter.api.Assertions.*;

class IncidentRepositoryTest {
    @Autowired
    LocalContainerEntityManagerFactoryBean entityManagerFactory;
    @Autowired
    private MyLogger myLogger;

    @Autowired
    private IncidentRepository incidentRepository;
    @Test
    void getIncidents() {
    }

    @Test
    void findAll() {
        Iterable<Incident> incidents=incidentRepository.findAll();
        for(Incident inc:incidents) myLogger.getLogger().info("Record {}",inc.getId());
    }

    @Test
    void findAllById() {
    }

    @Test
    void count() {
    }
}