package com.atoken.blockchain.http.request.account.ont;

import java.util.List;

public class OntBlock {
    private OntBlockHeader header;
    private List<OntTransaction> transactions;
    private Integer size;
    private String hash;

    public OntBlockHeader getHeader() {
        return header;
    }

    public void setHeader(OntBlockHeader header) {
        this.header = header;
    }

    public List<OntTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<OntTransaction> transactions) {
        this.transactions = transactions;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
