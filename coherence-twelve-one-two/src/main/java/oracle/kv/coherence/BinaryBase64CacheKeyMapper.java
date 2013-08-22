package oracle.kv.coherence;

import com.tangosol.io.Base64OutputStream;
import com.tangosol.util.Base;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import oracle.kv.Key;

import java.io.IOException;
import java.io.StringWriter;

/**
 * An implementation of a {@link CacheKeyMapper} that maps a Binary
 * cache key to its Base64 encoded String value optionally
 * prefixed with a key prefix String.
 *
 * @author Jonathan Knight
 */
public class BinaryBase64CacheKeyMapper implements BinaryEntryKeyMapper
{
    /**
     * A useful default instance of the default no-prefix mapper to save creating a lot of them
     */
    public static final BinaryBase64CacheKeyMapper INSTANCE = new BinaryBase64CacheKeyMapper();

    /**
     * The Key prefix to prepend to any keys created by this mapper
     */
    private String keyPrefix;

    /**
     * Default constructor to create a mapper with no prefix
     */
    public BinaryBase64CacheKeyMapper()
    {
        this(null);
    }

    /**
     * Create a mapper with the specified Key prefix.
     *
     * @param keyPrefix the String to prepend to the front
     *                  of any Keys created by this mapper.
     */
    public BinaryBase64CacheKeyMapper(String keyPrefix)
    {
        this.keyPrefix = normalizeKeyPrefix(keyPrefix);
    }

    /**
     * Return the specified prefix String making sure
     * it ends with a "/". If the specified prefix
     * is null this method returns just a "/".
     *
     * @param prefix the prefix to normalize, or null.
     * @return the prefix with "/" appended or just "/" if the prefix is null.
     */
    private String normalizeKeyPrefix(String prefix)
    {
        if (prefix == null)
        {
            return "/";
        }

        if (prefix.endsWith("/"))
        {
            return prefix;
        }

        return prefix + "/";
    }

    @Override
    public Key mapCacheKey(BinaryEntry entry)
    {
        Binary binaryKey = entry.getBinaryKey();
        return mapCacheKey(binaryKey);
    }

    @Override
    public Key mapCacheKey(Object o)
    {
        try
        {
            Binary binaryKey = (Binary) o;
            StringWriter writer = new StringWriter(keyPrefix.length() + binaryKey.length());
            writer.write(keyPrefix);
            Base64OutputStream stream = new Base64OutputStream(writer, false);
            binaryKey.writeTo(stream);
            return Key.fromString(writer.getBuffer().toString());
        }
        catch (IOException e)
        {
            throw Base.ensureRuntimeException(e, "Error Base64 encoding binary key");
        }
    }

    public static String encodeBinaryKey(Binary binaryKey)
    {
        return ModBase64.encodeBinary(binaryKey);
    }

    public static Binary decodeBinaryKey(String strKey)
    {
        return ModBase64.decodeBinary(strKey);
    }

    public static class ModBase64
    {
        static final char[] encodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_:".toCharArray();

        static final int[] decodeVals = invertEncodeMap(encodeChars);

        private static int[] invertEncodeMap(char[] charMap)
        {
            int[] revMap = new int[256];
            for (int i = 0; i < 256; i++)
            {
                revMap[i] = -1;
            }

            for (int i = 0; i < charMap.length; i++)
            {
                revMap[charMap[i]] = i;
            }

            return revMap;
        }

        static String encodeBinary(Binary binaryKey)
        {
            int length = binaryKey.length();
            int fullCount = binaryKey.length() / 3;
            int fullLen = fullCount * 3;
            int remain = length - fullLen;

            StringBuilder buffer = new StringBuilder(length * 4 / 3 + 2);

            for (int i = 0; i < fullLen; i += 3)
            {
                int x = (binaryKey.byteAt(i) & 0xFF) << 16 | (binaryKey.byteAt(i + 1) & 0xFF) << 8 | binaryKey.byteAt(i + 2) & 0xFF;

                buffer.append(encodeChars[(x >>> 18 & 0x3F)]);
                buffer.append(encodeChars[(x >>> 12 & 0x3F)]);
                buffer.append(encodeChars[(x >>> 6 & 0x3F)]);
                buffer.append(encodeChars[(x & 0x3F)]);
            }

            if (remain > 0)
            {
                byte byte2 = remain > 1 ? binaryKey.byteAt(fullLen + 1) : 0;

                int x = (binaryKey.byteAt(fullLen) & 0xFF) << 16 | (byte2 & 0xFF) << 8;

                buffer.append(encodeChars[(x >>> 18 & 0x3F)]);
                buffer.append(encodeChars[(x >>> 12 & 0x3F)]);
                if (remain > 1)
                {
                    buffer.append(encodeChars[(x >>> 6 & 0x3F)]);
                }
            }
            return buffer.toString();
        }

        public static Binary decodeBinary(String strKey)
        {
            if (strKey.length() % 4 == 1)
            {
                throw new IllegalArgumentException("input key value is not a valid encoded binary key");
            }

            int totalBytes = 3 * (strKey.length() / 4) + (strKey.length() % 4 == 0 ? 0 : strKey.length() % 4 - 1);

            byte[] result = new byte[totalBytes];

            for (int off = 0; off < strKey.length(); off += 4)
            {
                int nBytes = off + 4 <= strKey.length() ? 3 : strKey.length() % 4 - 1;

                int val1 = decodeVals[(strKey.charAt(off) & 0xFF)];

                int val2 = decodeVals[(strKey.charAt(off + 1) & 0xFF)];

                int val3 = nBytes > 1 ? decodeVals[(strKey.charAt(off + 2) & 0xFF)] : 0;

                int val4 = nBytes > 2 ? decodeVals[(strKey.charAt(off + 3) & 0xFF)] : 0;

                if ((val1 < 0) || (val2 < 0) || (val3 < 0) || (val4 < 0))
                {
                    throw new IllegalArgumentException("input key value is not a valid encoded binary key");
                }

                int value = val1 << 18 | val2 << 12 | val3 << 6 | val4;

                int baseIdx = off * 3 / 4;

                result[baseIdx] = (byte) (value >> 16);
                if (nBytes > 1)
                {
                    result[(baseIdx + 1)] = (byte) (value >> 8 & 0xFF);
                    if (nBytes > 2)
                    {
                        result[(baseIdx + 2)] = (byte) (value & 0xFF);
                    }
                }
            }

            return new Binary(result);
        }
    }

}
