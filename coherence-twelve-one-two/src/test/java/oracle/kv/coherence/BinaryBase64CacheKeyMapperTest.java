package oracle.kv.coherence;

import com.tangosol.io.Base64OutputStream;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import oracle.kv.Key;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Jonathan Knight
 */
public class BinaryBase64CacheKeyMapperTest
{
    @Test
    public void shouldEncodeKeyWithNoPrefix() throws Exception
    {
        Binary binary = new Binary(new byte[]{0x1F, 0x2E, 0x3D, 0x4C, 0x5B, 0x5A, 0x01, 0x02, 0x11});
        BinaryEntry entry = mock(BinaryEntry.class);

        when(entry.getBinaryKey()).thenReturn(binary);

        BinaryBase64CacheKeyMapper mapper = new BinaryBase64CacheKeyMapper();
        Key key = mapper.mapCacheKey(entry);

        assertThat(key.toString(), is("/" + toBase64(binary.toByteArray())));
    }

    @Test
    public void shouldEncodeKeyWithPrefixEndingInSlash() throws Exception
    {
        Binary binary = new Binary(new byte[]{0x1F, 0x2E, 0x3D, 0x4C, 0x5B, 0x5A, 0x01, 0x02, 0x11});
        BinaryEntry entry = mock(BinaryEntry.class);

        when(entry.getBinaryKey()).thenReturn(binary);

        BinaryBase64CacheKeyMapper mapper = new BinaryBase64CacheKeyMapper("/test/");
        Key key = mapper.mapCacheKey(entry);

        assertThat(key.toString(), is("/test/" + toBase64(binary.toByteArray())));
    }

    @Test
    public void shouldEncodeKeyWithPrefixNotEndingInSlash() throws Exception
    {
        Binary binary = new Binary(new byte[]{0x1F, 0x2E, 0x3D, 0x4C, 0x5B, 0x5A, 0x01, 0x02, 0x11});
        BinaryEntry entry = mock(BinaryEntry.class);

        when(entry.getBinaryKey()).thenReturn(binary);

        BinaryBase64CacheKeyMapper mapper = new BinaryBase64CacheKeyMapper("/test");
        Key key = mapper.mapCacheKey(entry);

        assertThat(key.toString(), is("/test/" + toBase64(binary.toByteArray())));
    }

    private String toBase64(byte[] bytes) throws IOException
    {
        StringWriter writer = new StringWriter(bytes.length);
        Base64OutputStream stream = new Base64OutputStream(writer);
        stream.write(bytes);
        return writer.getBuffer().toString();
    }
}
