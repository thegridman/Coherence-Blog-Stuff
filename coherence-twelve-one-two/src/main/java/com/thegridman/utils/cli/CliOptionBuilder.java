package com.thegridman.utils.cli;

import org.apache.commons.cli.Option;

/**
 * @author Jonathan Knight
 */
public class CLIOptionBuilder
{
    /** long option */
    private String longopt;

    /** option description */
    private String description;

    /** argument name */
    private String argName;

    /** is required? */
    private boolean required;

    /** the number of arguments */
    private int numberOfArgs = Option.UNINITIALIZED;

    /** option type */
    private Object type;

    /** option can have an optional argument value */
    private boolean optionalArg;

    /** value separator for argument value */
    private char valuesep;

    /**
     * private constructor to prevent instances being created
     */
    public CLIOptionBuilder()
    {
    }

    /**
     * The next Option created will have the following long option value.
     *
     * @param newLongopt the long option value
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder withLongOpt(String newLongopt)
    {
        this.longopt = newLongopt;

        return this;
    }

    /**
     * The next Option created will require an argument value.
     *
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder hasArg()
    {
        this.numberOfArgs = 1;

        return this;
    }

    /**
     * The next Option created will require an argument value if
     * <code>hasArg</code> is true.
     *
     * @param hasArg if true then the Option has an argument value
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder hasArg(boolean hasArg)
    {
        this.numberOfArgs = hasArg ? 1 : Option.UNINITIALIZED;

        return this;
    }

    /**
     * The next Option created will have the specified argument value name.
     *
     * @param name the name for the argument value
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder withArgName(String name)
    {
        this.argName = name;

        return this;
    }

    /**
     * The next Option created will be required.
     *
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder isRequired()
    {
        this.required = true;

        return this;
    }

    /**
     * The next Option created uses <code>sep</code> as a means to
     * separate argument values.
     *
     * <b>Example:</b>
     * <pre>
     * Option opt = this.withValueSeparator(':')
     *                           .create('D');
     *
     * CommandLine line = parser.parse(args);
     * String propertyName = opt.getValue(0);
     * String propertyValue = opt.getValue(1);
     * </pre>
     *
     * @param sep The value separator to be used for the argument values.
     *
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder withValueSeparator(char sep)
    {
        this.valuesep = sep;

        return this;
    }

    /**
     * The next Option created uses '<code>=</code>' as a means to
     * separate argument values.
     *
     * <b>Example:</b>
     * <pre>
     * Option opt = this.withValueSeparator()
     *                           .create('D');
     *
     * CommandLine line = parser.parse(args);
     * String propertyName = opt.getValue(0);
     * String propertyValue = opt.getValue(1);
     * </pre>
     *
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder withValueSeparator()
    {
        this.valuesep = '=';

        return this;
    }

    /**
     * The next Option created will be required if <code>required</code>
     * is true.
     *
     * @param newRequired if true then the Option is required
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder isRequired(boolean newRequired)
    {
        this.required = newRequired;

        return this;
    }

    /**
     * The next Option created can have unlimited argument values.
     *
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder hasArgs()
    {
        this.numberOfArgs = Option.UNLIMITED_VALUES;

        return this;
    }

    /**
     * The next Option created can have <code>num</code> argument values.
     *
     * @param num the number of args that the option can have
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder hasArgs(int num)
    {
        this.numberOfArgs = num;

        return this;
    }

    /**
     * The next Option can have an optional argument.
     *
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder hasOptionalArg()
    {
        this.numberOfArgs = 1;
        this.optionalArg = true;

        return this;
    }

    /**
     * The next Option can have an unlimited number of optional arguments.
     *
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder hasOptionalArgs()
    {
        this.numberOfArgs = Option.UNLIMITED_VALUES;
        this.optionalArg = true;

        return this;
    }

    /**
     * The next Option can have the specified number of optional arguments.
     *
     * @param numArgs - the maximum number of optional arguments
     * the next Option created can have.
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder hasOptionalArgs(int numArgs)
    {
        this.numberOfArgs = numArgs;
        this.optionalArg = true;

        return this;
    }

    /**
     * The next Option created will have a value that will be an instance
     * of <code>type</code>.
     *
     * @param newType the type of the Options argument value
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder withType(Object newType)
    {
        this.type = newType;

        return this;
    }

    /**
     * The next Option created will have the specified description
     *
     * @param newDescription a description of the Option's purpose
     * @return the CLIOptionBuilder instance
     */
    public CLIOptionBuilder withDescription(String newDescription)
    {
        this.description = newDescription;

        return this;
    }

    /**
     * Create an Option using the current settings and with
     * the specified Option <code>char</code>.
     *
     * @param opt the character representation of the Option
     * @return the Option instance
     * @throws IllegalArgumentException if <code>opt</code> is not
     * a valid character.  See Option.
     */
    public Option create(char opt) throws IllegalArgumentException
    {
        return create(String.valueOf(opt));
    }

    /**
     * Create an Option using the current settings
     *
     * @return the Option instance
     * @throws IllegalArgumentException if <code>longOpt</code> has not been set.
     */
    public Option create() throws IllegalArgumentException
    {
        if (longopt == null)
        {
            throw new IllegalArgumentException("must specify longopt");
        }

        return create(null);
    }

    /**
     * Create an Option using the current settings and with
     * the specified Option <code>char</code>.
     *
     * @param opt the <code>java.lang.String</code> representation
     * of the Option
     * @return the Option instance
     * @throws IllegalArgumentException if <code>opt</code> is not
     * a valid character.  See Option.
     */
    public Option create(String opt) throws IllegalArgumentException
    {
        // create the option
        Option option = new Option(opt, description);

        // set the option properties
        option.setLongOpt(longopt);
        option.setRequired(required);
        option.setOptionalArg(optionalArg);
        option.setArgs(numberOfArgs);
        option.setType(type);
        option.setValueSeparator(valuesep);
        option.setArgName(argName);

        // return the Option instance
        return option;
    }    
}
