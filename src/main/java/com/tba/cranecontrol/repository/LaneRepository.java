package com.tba.cranecontrol.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tba.cranecontrol.model.Lane;

@Repository
public interface LaneRepository extends MongoRepository<Lane, String> {
}
