package coherence;

/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2010, 2013 Oracle and/or its affiliates.  All rights reserved.
 *
 */

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

/**
 * This class provides POF serialization of Person.
 */
public class PersonSerializer implements PofSerializer
{
    final private static int VERSION_ID = 1;
    final private static int FIRST_NAME = 1;
    final private static int LAST_NAME = 2;
    final private static int PHONE = 3;
    final private static int AGE = 4;

    /**
     * Default constructor
     */
    public PersonSerializer()
    {
    }

    /**
     * Serialize a Person object.
     *
     * @param out The PofWriter to write to
     * @param o   The Person object to write
     * @throws IOException if the object o is not a Person
     */
    @Override
    public void serialize(PofWriter out, Object o)
            throws IOException
    {
        if (o.getClass() == Person.class)
        {
            out.setVersionId(VERSION_ID);
            out.writeString(FIRST_NAME, ((Person) o).getFirstname());
            out.writeString(LAST_NAME, ((Person) o).getLastname());
            out.writeString(PHONE, ((Person) o).getPhone());
            out.writeInt(AGE, ((Person) o).getAge());
            out.writeRemainder(null);
        }
        else
        {
            throw new IOException(
                    "Invalid type presented to Person Serializer: " + o.getClass());
        }
    }

    /**
     * Deserialize a Person object.
     *
     * @param in The PofReader to read from
     * @return The Person object read from the input stream
     * @throws IOException if the stream does not contain the serialization
     *                     of a Person object in an expected version.
     */
    @Override
    public Object deserialize(PofReader in)
            throws IOException
    {
        if (in.getVersionId() != VERSION_ID)
        {
            throw new IOException(
                    "PersonSerializer encountered unexpected " +
                    "version id: " + in.getVersionId());
        }

        final String firstName = in.readString(FIRST_NAME);
        final String lastName = in.readString(LAST_NAME);
        final String phone = in.readString(PHONE);
        final int age = in.readInt(AGE);
        in.readRemainder();

        final Person person = new Person();
        person.setFirstname(firstName);
        person.setLastname(lastName);
        person.setPhone(phone);
        person.setAge(age);
        return person;
    }
}

