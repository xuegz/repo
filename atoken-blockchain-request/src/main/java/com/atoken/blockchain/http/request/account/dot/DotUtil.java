package com.atoken.blockchain.http.request.account.dot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.polkadot.rpc.json.types.JsonRpcMethod;
import org.polkadot.types.Codec;
import org.polkadot.types.codec.CreateType;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v0.Modules;
import org.polkadot.types.primitive.StorageKey;
import org.polkadot.types.rpc.StorageChangeSet;
import org.polkadot.types.type.KeyValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DotUtil {
    public static List<Codec> formatInputs(JsonRpcMethod jsonRpcMethod, List<Object> inputs) {
        final long reqArgCount = jsonRpcMethod.getParams().stream().filter(p -> !p.isOptional()).count();
        String optText = reqArgCount == jsonRpcMethod.getParams().size()
                ? ""
                : "(" + (jsonRpcMethod.getParams().size() - reqArgCount) + " optional)";

        assert inputs.size() >= reqArgCount && inputs.size() <= jsonRpcMethod.getParams().size()
                : "Expected " + jsonRpcMethod.getParams().size() + " parameters" + optText + ", " + inputs + " found instead";

        List<Codec> ret = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            Codec type = CreateType.createType(jsonRpcMethod.getParams().get(i).getType(), inputs.get(i));
            ret.add(type);
        }
        return ret;
    }

    public static Object formatOutput(JsonRpcMethod jsonRpcMethod, List<Codec> params, Object result) {
        if (result instanceof String) {
            String json = ((String) result).trim();

            if (json.startsWith("{")) {
                try {
                    JSONObject jsonObject = JSON.parseObject((String) result);
                    result = jsonObject;
                } catch (Exception e) {
                }
            } else if (json.startsWith("[")) {
                try {
                    JSONArray jsonArray = JSON.parseArray((String) result);
                    result = jsonArray;
                } catch (Exception e) {
                }
            }
        }

        Codec base = CreateType.createType(jsonRpcMethod.getType(), result);

        if (jsonRpcMethod.getType().equals("StorageData")) {
            // single return value (via state.getStorage), decode the value based on the
            // outputType that we have specified. Fallback to Data on nothing
            StorageKey key = ((StorageKey) params.get(0));
            String type = key.getOutputType();
            if (StringUtils.isEmpty(type)) {
                type = "Data";
            }

            org.polkadot.types.Types.ConstructorCodec clazz = CreateType.createClass(type);
            //      const meta = key.meta || { default: undefined, modifier: { isOptional: true } };
            Modules.StorageFunctionMetadata meta = key.getMeta();

            if (key.getMeta() != null
                    && key.getMeta().getType().isMap()
                    && key.getMeta().getType().asMap().isLinked()) {

                // linked map
                return clazz.newInstance(base);
            } else {
                if (meta == null || meta.getModifier().isOptional()) {
                    return new Option<>(clazz, result == null ? null : clazz.newInstance(base));
                } else {
                    return clazz.newInstance(base);
                }
            }

        } else if (jsonRpcMethod.getType().equals("StorageChangeSet")) {
            // multiple return values (via state.storage subscription), decode the values
            // one at a time, all based on the query types. Three values can be returned -
            //   - Base - There is a valid value, non-empty
            //   - null - The storage key is empty (but in the resultset)
            //   - undefined - The storage value is not in the resultset
            List<Codec> ret = Lists.newArrayList();
            Vector<StorageKey> keys = (Vector<StorageKey>) params.get(0);
            for (StorageKey key : keys) {
                // Fallback to Data (i.e. just the encoding) if we don't have a specific type

                String type = key.getOutputType();
                if (StringUtils.isEmpty(type)) {
                    type = "Data";
                }
                org.polkadot.types.Types.ConstructorCodec clazz = CreateType.createClass(type);

                // see if we have a result value for this specific key
                String hexKey = key.toHex();
                KeyValue.KeyValueOption option = ((StorageChangeSet) base).getChanges()
                        .stream()
                        .filter(item -> item.getKey().toHex().equals(hexKey))
                        .findFirst().orElse(null);

                Modules.StorageFunctionMetadata meta = key.getMeta();
                //const meta = meta || { default: undefined, modifier: { isOptional: true } };

                if (option == null) {
                    // if we don't have a value, do not fill in the entry, it will be up to the
                    // caller to sort this out, either ignoring or having a cache for older values
                    ret.add(null);
                } else {
                    if (key.getMeta() != null
                            && key.getMeta().getType().isMap()
                            && key.getMeta().getType().asMap().isLinked()) {

                        // linked map
                        ret.add(clazz.newInstance(option.getValue().unwrapOr(null)));
                    } else {
                        if (meta == null || meta.getModifier().isOptional()) {

                            // create option either with the existing value, or empty when
                            // there is no value returned
                            ret.add(new Option(clazz, option.getValue().isNone() ? null : clazz.newInstance(option.getValue().unwrap())));
                        } else {
                            // for `null` we fallback to the default value, or create an empty type,
                            // otherwise we return the actual value as retrieved
                            ret.add(clazz.newInstance(option.getValue().unwrapOr(meta.getDefault())));
                        }
                    }

                }
            }

            return ret;
        } else if (jsonRpcMethod.getType().equals("Vec<StorageChangeSet>")) {
            // multiple return values (via state.storage subscription), decode the values
            // one at a time, all based on the query types. Three values can be returned -
            //   - Base - There is a valid value, non-empty
            //   - null - The storage key is empty (but in the resultset)
            //   - undefined - The storage value is not in the resultset
            List<Codec> ret = Lists.newArrayList();
            List<StorageKey> keys = Arrays.asList((StorageKey) params.get(0));
            for (StorageKey key : keys) {
                // Fallback to Data (i.e. just the encoding) if we don't have a specific type

                String type = key.getOutputType();
                if (StringUtils.isEmpty(type)) {
                    type = "Data";
                }
                org.polkadot.types.Types.ConstructorCodec clazz = CreateType.createClass(type);

                // see if we have a result value for this specific key
                String hexKey = key.toHex();
                KeyValue.KeyValueOption option = ((StorageChangeSet) (((List)base)).get(0)).getChanges()
                        .stream()
                        .filter(item -> item.getKey().toHex().equals(hexKey))
                        .findFirst().orElse(null);

                Modules.StorageFunctionMetadata meta = key.getMeta();
                //const meta = meta || { default: undefined, modifier: { isOptional: true } };

                if (option == null) {
                    // if we don't have a value, do not fill in the entry, it will be up to the
                    // caller to sort this out, either ignoring or having a cache for older values
                    ret.add(null);
                } else {
                    if (key.getMeta() != null
                            && key.getMeta().getType().isMap()
                            && key.getMeta().getType().asMap().isLinked()) {

                        // linked map
                        ret.add(clazz.newInstance(option.getValue().unwrapOr(null)));
                    } else {
                        if (meta == null || meta.getModifier().isOptional()) {

                            // create option either with the existing value, or empty when
                            // there is no value returned
                            ret.add(new Option(clazz, option.getValue().isNone() ? null : clazz.newInstance(option.getValue().unwrap())));
                        } else {
                            // for `null` we fallback to the default value, or create an empty type,
                            // otherwise we return the actual value as retrieved
                            ret.add(clazz.newInstance(option.getValue().unwrapOr(meta.getDefault())));
                        }
                    }

                }
            }

            return ret;
        }
        return base;
    }
}


