package oracle.kv.coherence;

import com.oracle.tools.deferred.Deferred;
import com.oracle.tools.deferred.InstanceUnavailableException;
import com.oracle.tools.deferred.UnresolvableInstanceException;
import com.oracle.tools.runtime.console.SystemApplicationConsole;
import com.oracle.tools.runtime.java.ContainerBasedJavaApplicationBuilder;
import com.oracle.tools.runtime.java.JavaApplication;
import com.oracle.tools.runtime.java.JavaApplicationBuilder;
import com.oracle.tools.runtime.java.JavaApplicationSchema;
import com.oracle.tools.runtime.java.SimpleJavaApplication;
import com.oracle.tools.runtime.java.SimpleJavaApplicationSchema;
import com.oracle.tools.runtime.java.container.Container;
import com.tangosol.net.InetAddressHelper;
import com.tangosol.util.Resources;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.impl.util.KVStoreMain;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static com.oracle.tools.deferred.DeferredHelper.ensured;

/**
 * @author Jonathan Knight
 */
public class KVLiteLauncher
{
    public static final String PROP_KV_STORENAME = "oracle.kv.storename";
    public static final String PROP_KV_HELPERHOSTS = "oracle.kv.helperhosts";
    public static final String PROP_KV_HOSTNAME = "oracle.kv.hostname";
    public static final String PROP_KV_PORT = "oracle.kv.port";

    private final String kvRoot;
    private JavaApplication kvLite;

    public KVLiteLauncher(String kvRoot)
    {
        this.kvRoot = kvRoot;
    }

    public void startKVLite(JavaApplicationBuilder builder, Iterable<Integer> ports) throws Exception
    {
        JavaApplicationSchema kvLiteSchema = createKvLiteScheme(ports);
        kvLite = (JavaApplication) builder.realize(kvLiteSchema, "KVLite", new SystemApplicationConsole());
    }

    public void stopKVLite() throws Exception
    {
        if (kvLite != null)
        {
            kvLite.destroy();
            kvLite = null;
            deleteRecursive(new File(kvRoot));
        }
    }

    public void loadSchema(String schemaName) throws Exception
    {
        URL url = Resources.findFileOrResource(schemaName, null);
        File file = new File(url.toURI());
        String[] args = {"runadmin", "-host", getHostName(), "-port", getPort(), "ddl", "add-schema", "-file", file.getCanonicalPath()};
        KVStoreMain.main(args);
    }

    public KVStore getKVStore()
    {
        return ensured(new DeferredKVStoreConnector()).get();
    }

    public String getStoreName()
    {
        return kvLite.getSystemProperty(PROP_KV_STORENAME);
    }

    public String getHelperHosts()
    {
        return kvLite.getSystemProperty(PROP_KV_HELPERHOSTS);
    }

    public String getHostName()
    {
        return kvLite.getSystemProperty(PROP_KV_HOSTNAME);
    }

    public String getPort()
    {
        return kvLite.getSystemProperty(PROP_KV_PORT);
    }

    public Properties getSystemProperties()
    {
        return kvLite.getSystemProperties();
    }

    private JavaApplicationSchema<SimpleJavaApplication, SimpleJavaApplicationSchema> createKvLiteScheme(Iterable<Integer> ports) throws Exception
    {
        File kvHome = new File("/var/kv/kv-2.1.8");
        File libDir = new File(kvHome, "lib");
        StringBuilder path = new StringBuilder();
        for (File f : libDir.listFiles())
        {
            if (f.getName().endsWith(".jar"))
            {
                path.append(f.getCanonicalPath())
                        .append(File.pathSeparator);
            }
        }

        String storeName = "kvstore";
        String hostName = InetAddressHelper.getLocalHost().getHostName();
        Iterator<Integer> it = ports.iterator();
        int port = it.next();
        int adminPort = it.next();

        File kvRootFile = new File(kvRoot);
        if (kvRootFile.exists())
        {
            deleteRecursive(kvRootFile);
        }
        if (!kvRootFile.exists())
        {
            kvRootFile.mkdirs();
        }

        return new SimpleJavaApplicationSchema(KVStoreMain.class.getCanonicalName(), path.toString())
                .setArgument("kvlite")
                .setArgument("-root").setArgument(kvRootFile.getAbsolutePath())
                .setArgument("-store").setArgument(storeName)
                .setArgument("-host").setArgument(hostName)
                .setArgument("-port").setArgument(String.valueOf(port))
                .setArgument("-admin").setArgument(String.valueOf(adminPort))
                .setSystemProperty(PROP_KV_STORENAME, storeName)
                .setSystemProperty(PROP_KV_HELPERHOSTS, hostName + ":" + port)
                .setSystemProperty(PROP_KV_HOSTNAME, hostName)
                .setSystemProperty(PROP_KV_PORT, port)
                .setWorkingDirectory(kvHome);
    }

    private static boolean deleteRecursive(File path) throws FileNotFoundException
    {
        if (path == null)
        {
            return false;
        }

        if (!path.exists())
        {
            throw new FileNotFoundException(path.getAbsolutePath());
        }

        boolean ret = true;
        if (path.isDirectory())
        {
            for (File f : path.listFiles())
            {
                ret = ret && deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length < 1)
        {
            throw new IllegalArgumentException("Missing kvRoot parameter");
        }
        Container.start();
        KVLiteLauncher launcher = new KVLiteLauncher(args[0]);
        List<Integer> kvPorts = Arrays.asList(50000, 50001);
        launcher.startKVLite(new ContainerBasedJavaApplicationBuilder(), kvPorts);
        final Object waiter = new Object();
        synchronized (waiter)
        {
            waiter.wait();
        }
    }

    private class DeferredKVStoreConnector implements Deferred<KVStore>
    {
        @Override
        public KVStore get() throws UnresolvableInstanceException, InstanceUnavailableException
        {
            try
            {
                return KVStoreFactory.getStore(new KVStoreConfig(getStoreName(), getHelperHosts()));
            }
            catch (Exception e)
            {
                // we assume any exception means we should retry
                throw new InstanceUnavailableException(this, e);
            }
        }

        @Override
        public Class<KVStore> getDeferredClass()
        {
            return KVStore.class;
        }
    }
}
