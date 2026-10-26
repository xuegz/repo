package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

public class AtomEvent {
    private String type;
    private List<AtomTag> attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AtomTag> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AtomTag> attributes) {
        this.attributes = attributes;
    }
}
