package com.xaffax.PlannerSpringBoot;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.xaffax.PlannerSpringBoot.persistence.model.Planner;

// this is to to run tests in specific order.
// by that additional operations like removal of records from
// database can be avoided, thus making the code shorter.
// debatable whether it is better than making code more explicit.
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PlannerSpringBootApplicationTests
{
	private final static DateTimeFormatter	df	= DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	@Autowired
	private MockMvc							mockMvc;

	private final Planner					initialPlan;
	private final Planner					conflictPlan;

	{
		initialPlan = new Planner();
		conflictPlan = new Planner();
		// id is ignored by the database (auto increment),
		// but needed for the request
		initialPlan.setPlan_id(100L);
		initialPlan.setRoom(100);
		initialPlan.setUser_name("TestUser");
		initialPlan.setToDate(LocalDateTime.now());
		initialPlan.setFromDate(LocalDateTime.now().minus(60, ChronoUnit.MINUTES));
	}

	// @Before
	// public void setUpPlans() throws Exception
	// {
	//
	// initialPlan = new Planner();
	//
	// }

	@Test
	public void aShouldReturnDefaultMessage() throws Exception
	{
		this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("Planner List")));
	}

	@Test
	public void bShouldAddPlan() throws Exception
	{
		this.performRequest(this.initialPlan, "/", "Added");
	}

	@Test
	public void cShouldRejectPlanLowerOverlap() throws Exception
	{
		conflictPlan.setPlan_id(100L);
		conflictPlan.setRoom(100);
		conflictPlan.setUser_name("TestUser");
		conflictPlan.setToDate(initialPlan.getToDate().minus(10, ChronoUnit.MINUTES));
		conflictPlan.setFromDate(initialPlan.getFromDate().minus(10, ChronoUnit.MINUTES));

		this.performRequest(this.conflictPlan, "/", "Sorry, selected time for room number");
	}

	@Test
	public void dShouldRejectPlanHigherOverlap() throws Exception
	{
		conflictPlan.setPlan_id(100L);
		conflictPlan.setRoom(100);
		conflictPlan.setUser_name("TestUser");
		conflictPlan.setToDate(initialPlan.getToDate().plus(10, ChronoUnit.MINUTES));
		conflictPlan.setFromDate(initialPlan.getFromDate().plus(10, ChronoUnit.MINUTES));

		this.performRequest(this.conflictPlan, "/", "Sorry, selected time for room number");
	}

	@Test
	public void eShouldRejectPlanInternalOverlap() throws Exception
	{
		conflictPlan.setPlan_id(100L);
		conflictPlan.setRoom(100);
		conflictPlan.setUser_name("TestUser");
		conflictPlan.setToDate(initialPlan.getToDate().minus(10, ChronoUnit.MINUTES));
		conflictPlan.setFromDate(initialPlan.getFromDate().plus(10, ChronoUnit.MINUTES));

		this.performRequest(this.conflictPlan, "/", "Sorry, selected time for room number");
	}

	@Test
	public void fShouldRejectPlanSameAsInitial() throws Exception
	{
		this.performRequest(this.initialPlan, "/", "Sorry, selected time for room number");
	}

	@Test
	public void gShouldAddPlanAdjacentHigh() throws Exception
	{
		conflictPlan.setPlan_id(100L);
		conflictPlan.setRoom(100);
		conflictPlan.setUser_name("TestUser");
		conflictPlan.setToDate(initialPlan.getToDate().plus(30, ChronoUnit.MINUTES));
		conflictPlan.setFromDate(initialPlan.getToDate());

		this.performRequest(this.conflictPlan, "/", "Added");
	}

	@Test
	public void hShouldAddPlanAdjacentLow() throws Exception
	{
		conflictPlan.setPlan_id(100L);
		conflictPlan.setRoom(100);
		conflictPlan.setUser_name("TestUser");
		conflictPlan.setToDate(initialPlan.getFromDate());
		conflictPlan.setFromDate(conflictPlan.getToDate().minus(30, ChronoUnit.MINUTES));

		this.performRequest(this.conflictPlan, "/", "Added");
	}

	@Test
	public void iShouldRejectTooShort() throws Exception
	{
		conflictPlan.setPlan_id(100L);
		conflictPlan.setRoom(100);
		conflictPlan.setUser_name("TestUser");
		conflictPlan.setToDate(initialPlan.getToDate().plus(1, ChronoUnit.DAYS));
		conflictPlan.setFromDate(conflictPlan.getToDate().minus(14, ChronoUnit.MINUTES));

		this.performRequest(this.conflictPlan, "/",
				"Scheduled time cannot be longer than 120 minutes");
	}

	@Test
	public void jShouldRejectTooLong() throws Exception
	{
		conflictPlan.setPlan_id(100L);
		conflictPlan.setRoom(100);
		conflictPlan.setUser_name("TestUser");
		conflictPlan.setToDate(initialPlan.getToDate().plus(1, ChronoUnit.DAYS));
		conflictPlan.setFromDate(conflictPlan.getToDate().minus(121, ChronoUnit.MINUTES));

		this.performRequest(this.conflictPlan, "/",
				"Scheduled time cannot be longer than 120 minutes");
	}

	@Test
	public void kRemovalDefaultMessage() throws Exception
	{
		this.mockMvc.perform((get("/remove/1"))).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("Removed succesfully!")));
	}

	@Test
	public void lErrorPages() throws Exception
	{
		this.mockMvc.perform((get("/remove/100"))).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("was not found!")));

		// below does not work as expected.
		// probably mock does not account for
		// spring's redirect to error view for 404 (?).
		// This returns 404 and empty view despite redirect to error view.

		// this.mockMvc.perform((get("/some/page/that/does/not/exist"))).andDo(print())
		// .andExpect(status().isNotFound())
		// .andExpect(content().string(containsString("Our Engineers are on
		// it")));
	}

	private void performRequest(Planner planner, String path, String containingString)
			throws Exception
	{
		this.mockMvc
				.perform((post(path).param("userName", planner.getUser_name())
						.param("room", planner.getRoom().toString())
						.param("fromDate", df.format(planner.getFromDate()))
						.param("toDate", df.format(planner.getToDate()))
						.param("id", planner.getPlan_id().toString())))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString(containingString)));
	}

}