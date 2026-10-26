package com.atoken.update.util;

public interface ConstantUtil {
    String TBALE_BLOCK = "block";
    String TABLE_TX_RECORD = "tx_record";
    int TABLE_BLOCK_SYNCED = 1;
    int TABLE_BLOCK_UNSYNCED = 0;
    int TABLE_BLOCK_IS_NOT_ISOLATED = 0;
    int TABLE_BLOCK_IS_ISOLATED = 1;
    int TABLE_BLOCK_UNCONFIRM = 0;
    int TABLE_BLOCK_CONFIRMED = 1;
    String TBALE_TX = "tx";
    int TBALE_PLAT_TYPE_SEND = 0;
    int TBALE_PLAT_TYPE_NOT_SEND = 1;
    int TBALE_TX_ATOKEN_ALL = 2;
    int TABLE_TX_STATUS_UNBLOCK = 0;
    int TABLE_TX_STATUS_UNCONFIRM = 1;
    int TABLE_TX_STATUS_CONFIRMED = 2;
    String TBALE_ADDRESS = "address";
    String TABLE_VALIDATOR = "validator";
    String TABLE_NOMINATE_EXTRINSIC="nominate_extrinsic";
    String TABLE_DELEGATE_DETAIL="delegate_detail";
    int TABLE_DELETED = 1;
    int TABLE_UNDELETED = 0;
    String TABLE_TX_DETAIL = "tx_detail";
    String TABLE_TOKEN = "token";
    String TABLE_SYNC_ERROR = "sync_error";
    String TABLE_ADDRESS_ASSET = "address_asset";
    String ASSERT_TYPE_MAIN = "MAIN";
    String REDIS_PREFIX_EXTERNAL_PURSE_NAME_ONT = "ExternalPurseNameOnt";
    String REDIS_KEY_BLOCK_DETAIL = "block_detail";

    int ATOM_VALIDATOR_STATUS_BONDED = 2;
    int DOT_VALIDATOR_STATUS_BONDED = 2;

    long XWC_TRANSFER_TIME= 8 * 60 * 60;
}
