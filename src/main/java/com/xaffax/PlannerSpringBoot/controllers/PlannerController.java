package com.xaffax.PlannerSpringBoot.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.xaffax.PlannerSpringBoot.persistence.model.Planner;
import com.xaffax.PlannerSpringBoot.service.PlannerService;
import com.xaffax.PlannerSpringBoot.validation.ValidatePlanner;

@Controller
@RequestMapping("/")
class PlannerController
{
	private final static DateTimeFormatter	df	= DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	@Autowired
	PlannerService							plannerService;

	@Autowired
	ValidatePlanner							validatePlanner;

	@RequestMapping(method = RequestMethod.GET)
	ModelAndView home()
	{
		List<Planner> planners = plannerService.getPlanners();
		Map<String, Object> params = new HashMap<>();
		params.put("planners", planners);

		return new ModelAndView("planners", params);
	}

	@RequestMapping(value = "remove/{id}", method = RequestMethod.GET)
	ModelAndView removePlanner(@PathVariable long id)
	{
		// return "redirect:/";
		ModelAndView modelAndView = new ModelAndView("planners");
		modelAndView.addObject("message", plannerService.removePlanner(id));
		modelAndView.addObject("planners", plannerService.getPlanners());
		return modelAndView;
	}

	@RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
	ModelAndView editPlanner(@PathVariable long id)
	{
		return new ModelAndView("planners", "plannerRet", plannerService.getPlanner(id));
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	ModelAndView addStudent(@RequestParam Long id, @RequestParam String userName,
			@RequestParam Integer room, @RequestParam String fromDate, @RequestParam String toDate)
			throws Exception
	{

		ModelAndView modelAndView = new ModelAndView("planners");
		try
		{
			Planner planner = new Planner();
			planner.setPlan_id(id);
			planner.setUser_name(userName);
			planner.setRoom(room);
			planner.setFromDate(LocalDateTime.parse(fromDate, df));
			planner.setToDate(LocalDateTime.parse(toDate, df));
			Map.Entry<Boolean, String> entry = validatePlanner.validate(planner).entrySet()
					.iterator().next();
			if (entry.getKey() == Boolean.TRUE)
				planner = plannerService.saveOrUpdatePlanner(planner);
			modelAndView.addObject("message", entry.getValue());
		} catch (Exception ex)
		{
			modelAndView.addObject("message", "Failed to add planner: " + ex.getMessage());
		}
		modelAndView.addObject("planners", plannerService.getPlanners());
		return modelAndView;
	}
}