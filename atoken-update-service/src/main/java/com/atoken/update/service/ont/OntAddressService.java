package com.atoken.update.service.ont;

import com.atoken.update.entity.ont.Address;

import java.util.List;

public interface OntAddressService {
    List<Address> selectAddressByAddress(String fromAddress, String toAddress);

}
