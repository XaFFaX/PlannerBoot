package com.xaffax.PlannerSpringBoot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.xaffax.PlannerSpringBoot.persistence.model.Planner;
import com.xaffax.PlannerSpringBoot.persistence.repository.PlannerRepository;

@Service
public class PlannerService
{
	@Autowired
	PlannerRepository plannerRepository;

	public Planner saveOrUpdatePlanner(Planner planner)
	{
		plannerRepository.save(planner);
		return planner;
	}

	public List<Planner> getPlanners()
	{
		List<Planner> plannerColl = new ArrayList<>();
		plannerRepository.findAll().forEach(plannerColl::add);
		return plannerColl;
	}

	public String removePlanner(long id)
	{
		String message = "Removed succesfully!";
		try
		{
			plannerRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e)
		{
			message = "Id of " + id + " is was not found!";
		}
		return message;
	}

	public Planner getPlanner(long id)
	{
		Optional<Planner> optioPlanner = plannerRepository.findById(id);
		return optioPlanner.isPresent() ? optioPlanner.get() : null;
	}
}
