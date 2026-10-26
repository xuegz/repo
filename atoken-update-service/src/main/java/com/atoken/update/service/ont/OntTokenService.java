package com.atoken.update.service.ont;

import com.atoken.update.entity.ont.Token;

import java.util.List;

public interface OntTokenService {
    List<Token> selectTokes();

    Token selectTokenByAssetType(String assetType);
}
