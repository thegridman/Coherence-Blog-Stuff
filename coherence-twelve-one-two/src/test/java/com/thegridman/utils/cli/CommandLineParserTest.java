package com.thegridman.utils.cli;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight
 */
public class CommandLineParserTest
{
    @CommandLineArg(opt = "a", description = "option 1", required = true)
    private String arg1;

    @CommandLineArg(opt = "b", description = "option 2", required = false)
    private String arg2;

    @Before
    public void setup()
    {
        arg1 = null;
        arg2 = null;
    }

    @Test
    public void shouldSetArgs() throws Exception
    {
        CommandLineParser.parse(this, new String[]{"-a", "test"});
        assertThat(arg1, is("test"));
        assertThat(arg2, is(nullValue()));
    }

}
