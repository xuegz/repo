package com.atoken.update.service.ont.impl;

import com.atoken.update.dao.ont.OntTokenDao;
import com.atoken.update.entity.ont.Token;
import com.atoken.update.service.ont.OntTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OntTokenServiceImpl implements OntTokenService {
    @Autowired
    private OntTokenDao tokenDao;

    @Override
    public List<Token> selectTokes() {
        return tokenDao.selectTokes();
    }

    @Override
    public Token selectTokenByAssetType(String assetType) {
        return tokenDao.selectTokenByAssetType(assetType);
    }

}
