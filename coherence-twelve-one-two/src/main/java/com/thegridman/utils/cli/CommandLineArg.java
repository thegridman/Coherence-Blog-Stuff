package com.thegridman.utils.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jonathan Knight
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandLineArg
{
    /** the name of the option */
    String opt();

    /** the long representation of the option */
    String longOpt() default "";

    /** the name of the argument for this option */
    String argName() default "arg";

    /** description of the option */
    String description();

    /** specifies whether this option is required to be present */
    boolean required() default false;

    /** specifies whether the argument value of this Option is optional */
    boolean optionalArg() default false;

    /** the number of argument values this option can have */
    int numberOfArgs() default 1;

    /** the character that is the value separator */
    char valueSep() default ',';
}
