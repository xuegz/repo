package com.atoken.update.service.ont.impl;

import com.atoken.update.dao.ont.OntAddressAssetDao;
import com.atoken.update.entity.ont.AddressAsset;
import com.atoken.update.service.ont.OntAddressAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OntAddressAssetServiceImpl implements OntAddressAssetService {
    @Autowired
    private OntAddressAssetDao addressAssetDao;

    @Override
    public Integer insertAddressAsset(AddressAsset addressAsset) {
        return addressAssetDao.insertAddressAsset(addressAsset);
    }

    @Override
    public Integer existAddressAsset(String address, String contractAddress) {
        return addressAssetDao.existAddressAsset(address, contractAddress);
    }

    @Override
    public Integer updateAddressAsset(AddressAsset addressAsset) {
        return addressAssetDao.updateAddressAsset(addressAsset);
    }

    @Override
    public Integer updateAsset(AddressAsset addressAsset) {
        return addressAssetDao.updateAsset(addressAsset);
    }

    @Override
    public Integer updateMainAsset(AddressAsset addressAsset) {
        return addressAssetDao.updateMainAsset(addressAsset);
    }
}
