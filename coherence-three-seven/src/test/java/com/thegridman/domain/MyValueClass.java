package com.thegridman.domain;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * @author Jonathan Knight
 */
@Portable
public class MyValueClass
{
    @PortableProperty(value = 0)
    public String fieldOne;

    @PortableProperty(value = 1)
    public String fieldTwo;

    @PortableProperty(value = 2)
    public String fieldThree;

    public MyValueClass()
    {
    }

    public MyValueClass(String fieldOne, String fieldTwo, String fieldThree)
    {
        this.fieldOne = fieldOne;
        this.fieldTwo = fieldTwo;
        this.fieldThree = fieldThree;
    }

    public String getFieldOne()
    {
        return fieldOne;
    }

    public String getFieldTwo()
    {
        return fieldTwo;
    }

    public String getFieldThree()
    {
        return fieldThree;
    }
}
