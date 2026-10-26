package com.atoken.request;

import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class EosRequestTest {
    @Test
    public void getBalance() {
        String eosNodeUrl = "https://api.eosbeijing.one";
        String url = eosNodeUrl + "/v1/chain/get_currency_balance";
        Request request = new Request();
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("account", "xiaoliangeos");
        requestBody.put("code", "eosio.token");
        ArrayList<String> resposne = request.method(Method.POST).url(url).data(requestBody).setContentType(ContentType.JSON_UTF8).send(ArrayList.class);
        System.out.println("resposne = " + resposne);
    }
}
