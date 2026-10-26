package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

/**
 * @author: erwin
 * @time: 2021/4/1 3:19 下午
 */
@Data
public class PalletStorageItem {
    private AtDTO at;
    private String pallet;
    private String storageItem;
    private String palletIndex;
    private Object value;
}
