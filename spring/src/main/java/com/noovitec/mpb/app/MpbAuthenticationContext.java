package com.noovitec.mpb.app;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class MpbAuthenticationContext {

	//Global storage across all HTTP sessions. Server reboot will reset it.
	private Set<String> sids = new HashSet<String>();

	public void addSid(String sid) {
		this.sids.add(sid);
	}

	public boolean hasSid(String sid) {
		return this.sids.contains(sid);
	}
}