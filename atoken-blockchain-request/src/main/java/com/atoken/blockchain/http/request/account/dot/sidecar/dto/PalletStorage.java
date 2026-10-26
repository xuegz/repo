package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * @author: erwin
 * @time: 2021/4/1 3:02 下午
 */
@Data
public class PalletStorage {
	private AtDTO at;
	private String pallet;
	private String palletIndex;
	private List<PalletStorageItemMetadata> items;
}