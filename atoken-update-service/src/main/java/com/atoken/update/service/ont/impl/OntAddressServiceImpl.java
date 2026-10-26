package com.atoken.update.service.ont.impl;

import com.atoken.update.dao.ont.OntAddressDao;
import com.atoken.update.entity.ont.Address;
import com.atoken.update.service.ont.OntAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OntAddressServiceImpl implements OntAddressService {
    @Autowired
    private OntAddressDao addressDao;

    @Override
    public List<Address> selectAddressByAddress(String fromAddress, String toAddress) {
        return addressDao.selectAddressByAddress(fromAddress, toAddress);
    }
}
