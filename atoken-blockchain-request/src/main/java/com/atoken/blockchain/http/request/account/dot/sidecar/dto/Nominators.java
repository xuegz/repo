package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * @author: erwin
 * @time: 2021/6/8 5:56 下午
 */
@Data
public class Nominators {
	private String submittedIn;
	private boolean suppressed;
	private List<String> targets;

}