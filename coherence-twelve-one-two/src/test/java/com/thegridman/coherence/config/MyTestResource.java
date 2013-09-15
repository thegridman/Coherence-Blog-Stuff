package com.thegridman.coherence.config;

/**
 * @author Jonathan Knight
 */
public class MyTestResource
{
    private String paramValue;

    public MyTestResource()
    {
    }

    public MyTestResource(String paramValue)
    {
        this.paramValue = paramValue;
    }

    public String getParamValue()
    {
        return paramValue;
    }

    @Override
    public String toString()
    {
        return "MyTestResource{" +
               "paramValue='" + paramValue + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        MyTestResource that = (MyTestResource) o;

        if (paramValue != null ? !paramValue.equals(that.paramValue) : that.paramValue != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return paramValue != null ? paramValue.hashCode() : 0;
    }
}
