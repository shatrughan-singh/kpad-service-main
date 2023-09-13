package com.kpad.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kpad.app.dto.AgentDTO;
import com.kpad.app.dto.ServiceUrlDTO;

@Repository
public interface ServiceUrlRepository extends MongoRepository<ServiceUrlDTO, Integer> {

	List<ServiceUrlDTO> findAll();
	
	Optional<AgentDTO> findByAgentConfigId(Integer configId);
	
	List<ServiceUrlDTO> findByDeptName();
}
