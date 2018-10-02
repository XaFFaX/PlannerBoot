package com.xaffax.PlannerSpringBoot.persistence.repository;

import org.springframework.data.repository.CrudRepository;

import com.xaffax.PlannerSpringBoot.persistence.model.Planner;

public interface PlannerRepository extends CrudRepository<Planner, Long> {

}
