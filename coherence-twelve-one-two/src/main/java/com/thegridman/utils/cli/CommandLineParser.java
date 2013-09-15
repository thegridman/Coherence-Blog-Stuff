package com.thegridman.utils.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jonathan Knight
 */
public class CommandLineParser
{
    public static <T> T parse(T mainArgs, String[] args) throws Exception
    {
        Class commandLineClass = mainArgs.getClass();
        Options options = new Options();

        Map<Option,Field> optionMap = new HashMap<>();
        for (Field field : commandLineClass.getDeclaredFields())
        {
            CommandLineArg commandLineArg = field.getAnnotation(CommandLineArg.class);
            if (commandLineArg != null)
            {
                Option option = new Option(commandLineArg.opt(), commandLineArg.description());
                String longOpt = commandLineArg.longOpt();
                if (!longOpt.isEmpty())
                {
                    option.setLongOpt(longOpt);
                }
                option.setRequired(commandLineArg.required());
                option.setOptionalArg(commandLineArg.optionalArg());
                option.setArgs(commandLineArg.numberOfArgs());
                option.setValueSeparator(commandLineArg.valueSep());
                option.setArgName(commandLineArg.argName());
                options.addOption(option);
                optionMap.put(option, field);
            }
        }

        org.apache.commons.cli.CommandLineParser commandLineParser = new GnuParser();
        CommandLine commandLine;
        try
        {
            commandLine = commandLineParser.parse(options, args);
        }
        catch (ParseException e)
        {
            String syntax = "java " + commandLineClass.getName() + " [Options]";
            StringWriter stringWriter = new StringWriter();
            new HelpFormatter().printHelp(new PrintWriter(stringWriter), 120, syntax, null, options, 1, 3, null);
            throw new RuntimeException(e.getMessage() + "\n" + stringWriter.getBuffer().toString(), e);
        }

        for (Map.Entry<Option,Field> entry : optionMap.entrySet())
        {
            Option option = entry.getKey();
            Object value;
            if (option.hasArgs())
            {
                value = commandLine.getOptionValues(option.getOpt());
            }
            else
            {
                value = commandLine.getOptionValue(option.getOpt());
            }

            entry.getValue().setAccessible(true);
            entry.getValue().set(mainArgs, value);
        }

        return mainArgs;
    }
}
