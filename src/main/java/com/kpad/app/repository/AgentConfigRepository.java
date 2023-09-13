package com.kpad.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kpad.app.dto.AgentDTO;

@Repository
public interface AgentConfigRepository extends MongoRepository<AgentDTO, Integer> {

	List<AgentDTO> findAll();
	
	Optional<AgentDTO> findByAgentConfigId(Integer configId);
}
