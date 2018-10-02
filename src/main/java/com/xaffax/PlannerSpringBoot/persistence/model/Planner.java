package com.xaffax.PlannerSpringBoot.persistence.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Planner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long plan_id;

	@Column(nullable = false)
	private String user_name;

	@Column(nullable = false)
	private Integer room;

	@Column(nullable = false)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private LocalDateTime fromDate;

//	public Planner(String user_name, Integer room, LocalDateTime fromDate, LocalDateTime toDate) {
//		super();
//		this.user_name = user_name;
//		this.room = room;
//		this.fromDate = fromDate;
//		this.toDate = toDate;
//	}
//
//	public Planner() {
//		super();
//	}

	@Column(nullable = false)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private LocalDateTime toDate;

	public Long getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(Long plan_id) {
		this.plan_id = plan_id;
	}

	public Integer getRoom() {
		return room;
	}

	public void setRoom(Integer room) {
		this.room = room;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	@Transient
	public LocalDateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDateTime fromDate) {
		this.fromDate = fromDate;
	}

	@Transient
	public LocalDateTime getToDate() {
		return toDate;
	}

	public void setToDate(LocalDateTime toDate) {
		this.toDate = toDate;
	}

	@Override
	public String toString() {
		return "Planner [plan_id=" + plan_id + ", user_name=" + user_name + ", room=" + room + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + "]";
	}
}