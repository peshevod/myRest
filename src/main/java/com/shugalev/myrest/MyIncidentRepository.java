package com.shugalev.myrest;

import java.util.*;

//import org.springframework.data.repository.CrudRepository;
    public class MyIncidentRepository/* implements CrudRepository<Incident, String>*/{
/*        private ArrayList<Incident> incidents=new ArrayList<>();

        public MyIncidentRepository() {
            this.incidents=new ArrayList<>();
        }

        public ArrayList<Incident> getIncidents() {
            return incidents;
        }

        public void setIncidents(ArrayList<Incident> incidents) {
            this.incidents = incidents;
        }

//        @Override
        public <S extends Incident> S save(S entity) {
            incidents.add(entity);
            return entity;
        }

//        @Override
        public <S extends Incident> Iterable<S> saveAll(Iterable<S> entities) {
            for(S entity:entities) incidents.add(entity);
            return entities;
        }

//        @Override
        public Optional<Incident> findById(String s) {
            for(Incident inc:incidents) if(inc.getId().equals(Long.parseLong(s))) return Optional.of(inc);
            return Optional.empty();
        }

//        @Override
        public boolean existsById(String s) {
            for(Incident inc:incidents) if(inc.getId().equals(Long.parseLong(s))) return true;
            return false;
        }

//        @Override
        public Iterable<Incident> findAll() {
            return incidents;
        }

//        @Override
        public Iterable<Incident> findAllById(Iterable<String> strings) {
            ArrayList<Incident> l=new ArrayList<>();
            for(Incident inc:incidents) {
                for(String s : strings)
                {
                    if (inc.getId().equals(Long.parseLong(s))) l.add(inc);
                }
            }
            return l;
        }

//        @Override
        public long count() {
            return incidents.size();
        }

//        @Override
        public void deleteById(String s) {
            for(Incident inc:incidents) if(inc.getId().equals(Long.parseLong(s))) incidents.remove(inc);
        }

//        @Override
        public void delete(Incident entity) {
            incidents.remove(entity);
        }

//        @Override
        public void deleteAllById(Iterable<? extends String> strings) {
            for(Incident inc:incidents) {
                for (String s : strings) if (inc.getId().equals(Long.parseLong(s))) incidents.remove(inc);
            }
        }

//        @Override
        public void deleteAll(Iterable<? extends Incident> entities) {
            for(Incident inc:entities) incidents.remove(inc);
        }

//        @Override
        public void deleteAll() {
            incidents.clear();
        }*/

    }
