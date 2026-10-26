package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

/**
 * @author: erwin
 * @time: 2021/4/1 3:02 下午
 */
@Data
public class PalletStorageItemMetadata {
    private String modifier;
    private String documentation;
    private String name;
    private Object type;
    private String fallback;
}
