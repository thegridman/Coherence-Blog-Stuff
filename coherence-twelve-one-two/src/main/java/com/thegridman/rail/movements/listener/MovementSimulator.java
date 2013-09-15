package com.thegridman.rail.movements.listener;

import com.tangosol.util.Base;
import com.tangosol.util.Resources;
import net.ser1.stomp.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jonathan Knight
 */
public class MovementSimulator implements MovementProvider
{
    private final String fileName;
    private final Map<Listener,Runner> subscriptions;

    public MovementSimulator(String fileName)
    {
        this.fileName = fileName;
        this.subscriptions = new ConcurrentHashMap<>();
    }

    public synchronized void subscribe(Listener listener)
    {
        if (subscriptions.containsKey(listener))
        {
            return;
        }

        Runner t = new Runner(fileName, listener);
        t.start();
        subscriptions.put(listener, t);
    }

    public synchronized void unsubscribe(Listener listener)
    {
        Runner runner = subscriptions.remove(listener);
        if (runner != null)
        {
            runner.unsubscribe();
        }
    }

    public synchronized void close()
    {
        for (Listener listener : subscriptions.keySet())
        {
            unsubscribe(listener);
        }
    }

    private static class Runner extends Thread
    {
        private final Listener listener;
        private final BufferedReader reader;
        private final AtomicBoolean running = new AtomicBoolean(true);
        private static final AtomicInteger counter = new AtomicInteger(0);

        private Runner(String fileName, Listener listener)
        {
            super("MovementSimulator:" + counter.incrementAndGet());
            this.listener = listener;
            try
            {
                URL movementURL = Resources.findFileOrResource(fileName, getClass().getClassLoader());
                reader = new BufferedReader(new InputStreamReader(movementURL.openStream()));
            }
            catch (IOException e)
            {
                throw Base.ensureRuntimeException(e);
            }
        }

        public void unsubscribe()
        {
            running.set(false);
            synchronized (this) {
                this.notifyAll();
            }
        }

        @Override
        public void run()
        {
            try
            {
                String line = reader.readLine();
                while(running.get())
                {
                    if (line != null)
                    {
                        listener.message(Collections.emptyMap(), line);
                    }
                    else
                    {
                        reader.close();
                        running.set(false);
                    }

                    if (!running.get())
                    {
                        break;
                    }

                    synchronized (this)
                    {
                        this.wait(1000);
                    }

                    if (!running.get())
                    {
                        break;
                    }
                    line = reader.readLine();
                }
            }
            catch (IOException e)
            {
                throw Base.ensureRuntimeException(e);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

}
