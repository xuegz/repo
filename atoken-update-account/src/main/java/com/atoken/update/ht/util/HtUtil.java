package com.atoken.update.ht.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HtUtil {

    public static String decodeAddress(String rawInput) {
        return String.valueOf(FunctionReturnDecoder
                .decodeIndexedValue(rawInput, TypeReference.create(Address.class)).getValue());
    }

    public static String getValueAsString(Type<?> type) {
        if (type instanceof Utf8String) {
            return Utf8String.class.cast(type).getValue();
        }
        if (type instanceof Bytes32) {
            byte[] bs = Bytes32.class.cast(type).getValue();
            int[] is = new int[bs.length];
            for (int i = 0; i < bs.length; i++) {
                is[i] = bs[i] & 0xFF;
            }
            return new String(is, 0, is.length);
        }
        return null;
    }

    public static <T extends Type<?>> Optional<Type<?>> decodeAsType(String rawInput,
            Class<T> clazz) {
        return Optional
                .ofNullable(FunctionReturnDecoder.decode(rawInput,
                        Utils.convert(Arrays.asList(TypeReference.create(clazz)))))
                .filter(e -> !e.isEmpty()).map(e -> e.get(0));
    }

    public static Optional<String> decodeAsUtf8String(String rawInput) {
        return decodeAsType(rawInput, Utf8String.class).map(HtUtil::getValueAsString);
    }

    public static Optional<String> decodeAsBytes32(String rawInput) {
        return decodeAsType(rawInput, Bytes32.class).map(HtUtil::getValueAsString);
    }

    public static String decodeString(String rawInput) {
        return decodeAsUtf8String(rawInput).orElse(decodeAsBytes32(rawInput).orElse(null));
    }

    public static BigInteger decodeUint256(String rawInput) {
        return BigInteger.class.cast(FunctionReturnDecoder
                .decodeIndexedValue(rawInput, TypeReference.create(Uint256.class)).getValue());
    }

    public static BigInteger decodeQuantity(String value) {
        return Numeric.decodeQuantity(value);
    }

    public static BigInteger toBigInt(String hexValue) {
        return Numeric.toBigInt(hexValue);
    }

    public static BigDecimal fromWei(String number, Unit unit) {
        return Convert.fromWei(number, unit);
    }

    public static BigDecimal fromWei(BigInteger number, Unit unit) {
        return fromWei(new BigDecimal(number), unit);
    }

    public static BigDecimal fromWei(BigDecimal number, Unit unit) {
        return Convert.fromWei(number, unit);
    }

    public static BigDecimal toWei(String number, Unit unit) {
        return Convert.toWei(number, unit);
    }

    public static BigDecimal toWei(BigDecimal number, Unit unit) {
        return Convert.toWei(number, unit);
    }

    public static BigDecimal fromWei(String number, int decimals) {
        return fromWei(new BigDecimal(number), decimals);
    }

    public static BigDecimal fromWei(BigInteger number, int decimals) {
        return fromWei(new BigDecimal(number), decimals);
    }

    public static BigDecimal fromWei(BigDecimal number, int decimals) {
        return number.divide(BigDecimal.TEN.pow(decimals));
    }

    public static BigDecimal toWei(String number, int decimals) {
        return toWei(new BigDecimal(number), decimals);
    }

    public static BigDecimal toWei(BigDecimal number, int decimals) {
        return number.multiply(BigDecimal.TEN.pow(decimals));
    }
}
