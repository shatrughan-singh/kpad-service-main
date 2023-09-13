package com.kpad.app.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kpad.app.dto.DepartmentDTO;

@Repository
public interface DepartmentRepository extends MongoRepository<DepartmentDTO, Integer> {

	List<DepartmentDTO> findAll();
}
