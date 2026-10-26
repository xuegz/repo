package com.atoken.blockchain.http.request.account.dot.sidecar;

import com.atoken.blockchain.http.request.account.dot.sidecar.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: erwin
 * @time: 2020/9/9 2:37 下午
 */
@FeignClient(name = "dot-sidecar-remote-service", url = "${rpc.dot.url.sidecar_node:http://172.17.3.66:8080}")
public interface DotSidecarRemoteClient {

    /**
     * 查询地址资产信息
     *
     * @param addr
     * @return
     */
    @GetMapping(value = "/accounts/{addr}/balance-info")
    BalanceInfoResp balanceInfo(@PathVariable("addr") String addr);

    /**
     * 查询当前最新块
     *
     * @return
     */
    @GetMapping(value = "/blocks/head")
    BlockResp blocksHead();

    /**
     * 根据块hash 查询块信息
     *
     * @param blockHash
     * @return
     */
    @GetMapping(value = "/blocks/{blockHash,blockId}")
    BlockResp blocks(@PathVariable("blockHash") String blockHash);

    /**
     * 根据块高查询块信息
     *
     * @param blockId
     * @return
     */
    @GetMapping(value = "/blocks/{blockId}")
    BlockResp blocks(@PathVariable("blockId") Integer blockId);

    /**
     * 查询交易池中的交易数量
     *
     * @return
     */
    @GetMapping(value = "/node/transaction-pool")
    TransactionPoolResp transactionPool();

    /**
     * 交易发送
     *
     * @param transactionReq
     * @return
     */
    @PostMapping(value = "/transaction")
    TransactionResp transaction(@RequestBody TransactionReq transactionReq);

}
