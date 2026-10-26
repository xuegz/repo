package com.atoken.update.service.ont;

import com.atoken.update.entity.ont.AddressAsset;

public interface OntAddressAssetService {
    Integer insertAddressAsset(AddressAsset addressAsset);

    Integer existAddressAsset(String address, String contractAddress);

    Integer updateAddressAsset(AddressAsset addressAsset);

    Integer updateAsset(AddressAsset addressAsset);

    Integer updateMainAsset(AddressAsset addressAsset);
}
