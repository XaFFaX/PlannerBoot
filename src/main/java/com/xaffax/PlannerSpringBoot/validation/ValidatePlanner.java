package com.xaffax.PlannerSpringBoot.validation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xaffax.PlannerSpringBoot.persistence.model.Planner;
import com.xaffax.PlannerSpringBoot.service.PlannerService;

@Component
public class ValidatePlanner
{

	@Autowired
	PlannerService plannerService;

	private static boolean areDatesOverlapped(LocalDateTime startDate1, LocalDateTime endDate1,
			LocalDateTime startDate2, LocalDateTime endDate2)
	{
		// return (startDate1.isBefore(endDate2) &&
		// (startDate2.isBefore(endDate1)));
		return (startDate1.isBefore(endDate2)) && (startDate2.isBefore(endDate1));
	}

	public Map<Boolean, String> validate(Planner planner)
	{
		boolean overlap = false;
		long minutes = Duration.between(planner.getFromDate(), planner.getToDate()).toMinutes();
		Map<Boolean, String> isFoundMessage = new HashMap<>();
		// check to see if appointment is not >120 minutes and <15 minutes
		if (minutes > 120 || minutes < 15)
		{
			isFoundMessage.put(false,
					"Scheduled time cannot be longer than 120 minutes and shorter than 15 minutes! Please update accordingly.");
			return isFoundMessage;
		}
		List<Planner> planners = plannerService.getPlanners();
		planners.removeIf(e -> e.getPlan_id() == planner.getPlan_id());
		// checks for overlapping dates with all plans currently in the database
		for (Planner plannerElem : planners)
		{
			if (planner.getRoom() == plannerElem.getRoom())
			{
				if (areDatesOverlapped(planner.getFromDate(), planner.getToDate(),
						plannerElem.getFromDate(), plannerElem.getToDate()))
				{
					overlap = true;
					break;
				}
			}
		}
		String message = null;
		boolean foundSlot = false;
		boolean doSave = false;
		if (overlap)
		{
			LocalDateTime proposedStartDate = planner.getFromDate();
			LocalDateTime proposedEndDate = planner.getToDate();
			int i = 0;
			// this loop looks for next free slot that will be of the
			// same size as requested.
			// check is done by adding 30 minutes to requested date and checking
			// if there is an overlap,
			// if not, appropriate date is returned as a suggestion,
			// if suitable slot is not found within 20 loop cycles then
			// user is informed that no suitable slot was found

			// TODO: figure out how to do this non-iteratively...
			do
			{
				// LocalDateTime is immutable!!
				proposedStartDate = proposedStartDate.plus(30, ChronoUnit.MINUTES);
				proposedEndDate = proposedEndDate.plus(30, ChronoUnit.MINUTES);
				for (Planner planElem : planners)
				{
					if (planner.getRoom() == planElem.getRoom())
					{
						if (areDatesOverlapped(proposedStartDate, proposedEndDate,
								planElem.getFromDate(), planElem.getToDate()))
						{
							continue;
						} else
						{
							DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
							message = "Sorry, selected time for room number " + planner.getRoom()
									+ " is not available, proposed dates for this room: "
									+ df.format(proposedStartDate) + " to "
									+ df.format(proposedEndDate);
							foundSlot = true;
							i = 100;

						}
					}
				}
			} while (i++ < 20);
			if (!foundSlot)
				message = "Unable to find any suitable timeslot within next few hours.";
		}

		else
		{
			// logger.info("Plan saved successfully, Plan Details=" + plan);
			foundSlot = true;
			doSave = true;
			message = "Added successfully!";
		}
		isFoundMessage.put(doSave, message);
		return isFoundMessage;
	}

}