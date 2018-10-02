package com.xaffax.PlannerSpringBoot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xaffax.PlannerSpringBoot.persistence.model.Planner;
import com.xaffax.PlannerSpringBoot.persistence.repository.PlannerRepository;

@Service
public class PlannerService {
	@Autowired
	PlannerRepository plannerRepository;

	public Planner saveOrUpdatePlanner(Planner planner) {
		plannerRepository.save(planner);
		return planner;
	}

	public Iterable<Planner> getPlanners() {
		return plannerRepository.findAll();
	}

	public void removePlanner(long id) {
		plannerRepository.deleteById(id);
	}

	public Planner getPlanner(long id) {
		Optional<Planner> optioPlanner = plannerRepository.findById(id);
		return optioPlanner.isPresent() ? optioPlanner.get() : null;
	}
}
