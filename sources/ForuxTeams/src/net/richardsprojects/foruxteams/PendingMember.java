package net.richardsprojects.foruxteams;

import java.util.UUID;

public class PendingMember {

	private UUID member;
	private int timeLeft;
	
	public PendingMember(UUID member) {
		this.member = member;
		this.timeLeft = 30;
	}
	
	public int getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(int timeLeft) {
		this.timeLeft = timeLeft;
	}

	public UUID getMember() {
		return member;
	}	
}
