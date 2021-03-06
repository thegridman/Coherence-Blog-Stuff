/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package coherence;

@SuppressWarnings("all")
public class Person extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord
{
    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Person\",\"namespace\":\"coherence\",\"fields\":[{\"name\":\"firstname\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":\"\"},{\"name\":\"lastname\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":\"\"},{\"name\":\"phone\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":\"\"},{\"name\":\"age\",\"type\":\"int\",\"default\":0}]}");
    @Deprecated
    public java.lang.String firstname;
    @Deprecated
    public java.lang.String lastname;
    @Deprecated
    public java.lang.String phone;
    @Deprecated
    public int age;

    public org.apache.avro.Schema getSchema()
    {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    public java.lang.Object get(int field$)
    {
        switch (field$)
        {
            case 0:
                return firstname;
            case 1:
                return lastname;
            case 2:
                return phone;
            case 3:
                return age;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$)
    {
        switch (field$)
        {
            case 0:
                firstname = (java.lang.String) value$;
                break;
            case 1:
                lastname = (java.lang.String) value$;
                break;
            case 2:
                phone = (java.lang.String) value$;
                break;
            case 3:
                age = (java.lang.Integer) value$;
                break;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    /**
     * Gets the value of the 'firstname' field.
     */
    public java.lang.String getFirstname()
    {
        return firstname;
    }

    /**
     * Sets the value of the 'firstname' field.
     *
     * @param value the value to set.
     */
    public void setFirstname(java.lang.String value)
    {
        this.firstname = value;
    }

    /**
     * Gets the value of the 'lastname' field.
     */
    public java.lang.String getLastname()
    {
        return lastname;
    }

    /**
     * Sets the value of the 'lastname' field.
     *
     * @param value the value to set.
     */
    public void setLastname(java.lang.String value)
    {
        this.lastname = value;
    }

    /**
     * Gets the value of the 'phone' field.
     */
    public java.lang.String getPhone()
    {
        return phone;
    }

    /**
     * Sets the value of the 'phone' field.
     *
     * @param value the value to set.
     */
    public void setPhone(java.lang.String value)
    {
        this.phone = value;
    }

    /**
     * Gets the value of the 'age' field.
     */
    public java.lang.Integer getAge()
    {
        return age;
    }

    /**
     * Sets the value of the 'age' field.
     *
     * @param value the value to set.
     */
    public void setAge(java.lang.Integer value)
    {
        this.age = value;
    }

    /**
     * Creates a new Person RecordBuilder
     */
    public static coherence.Person.Builder newBuilder()
    {
        return new coherence.Person.Builder();
    }

    /**
     * Creates a new Person RecordBuilder by copying an existing Builder
     */
    public static coherence.Person.Builder newBuilder(coherence.Person.Builder other)
    {
        return new coherence.Person.Builder(other);
    }

    /**
     * Creates a new Person RecordBuilder by copying an existing Person instance
     */
    public static coherence.Person.Builder newBuilder(coherence.Person other)
    {
        return new coherence.Person.Builder(other);
    }

    /**
     * RecordBuilder for Person instances.
     */
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Person>
            implements org.apache.avro.data.RecordBuilder<Person>
    {

        private java.lang.String firstname;
        private java.lang.String lastname;
        private java.lang.String phone;
        private int age;

        /**
         * Creates a new Builder
         */
        private Builder()
        {
            super(coherence.Person.SCHEMA$);
        }

        /**
         * Creates a Builder by copying an existing Builder
         */
        private Builder(coherence.Person.Builder other)
        {
            super(other);
        }

        /**
         * Creates a Builder by copying an existing Person instance
         */
        private Builder(coherence.Person other)
        {
            super(coherence.Person.SCHEMA$);
            if (isValidValue(fields()[0], other.firstname))
            {
                this.firstname = (java.lang.String) data().deepCopy(fields()[0].schema(), other.firstname);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.lastname))
            {
                this.lastname = (java.lang.String) data().deepCopy(fields()[1].schema(), other.lastname);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.phone))
            {
                this.phone = (java.lang.String) data().deepCopy(fields()[2].schema(), other.phone);
                fieldSetFlags()[2] = true;
            }
            if (isValidValue(fields()[3], other.age))
            {
                this.age = (java.lang.Integer) data().deepCopy(fields()[3].schema(), other.age);
                fieldSetFlags()[3] = true;
            }
        }

        /**
         * Gets the value of the 'firstname' field
         */
        public java.lang.String getFirstname()
        {
            return firstname;
        }

        /**
         * Sets the value of the 'firstname' field
         */
        public coherence.Person.Builder setFirstname(java.lang.String value)
        {
            validate(fields()[0], value);
            this.firstname = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'firstname' field has been set
         */
        public boolean hasFirstname()
        {
            return fieldSetFlags()[0];
        }

        /**
         * Clears the value of the 'firstname' field
         */
        public coherence.Person.Builder clearFirstname()
        {
            firstname = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'lastname' field
         */
        public java.lang.String getLastname()
        {
            return lastname;
        }

        /**
         * Sets the value of the 'lastname' field
         */
        public coherence.Person.Builder setLastname(java.lang.String value)
        {
            validate(fields()[1], value);
            this.lastname = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'lastname' field has been set
         */
        public boolean hasLastname()
        {
            return fieldSetFlags()[1];
        }

        /**
         * Clears the value of the 'lastname' field
         */
        public coherence.Person.Builder clearLastname()
        {
            lastname = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        /**
         * Gets the value of the 'phone' field
         */
        public java.lang.String getPhone()
        {
            return phone;
        }

        /**
         * Sets the value of the 'phone' field
         */
        public coherence.Person.Builder setPhone(java.lang.String value)
        {
            validate(fields()[2], value);
            this.phone = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /**
         * Checks whether the 'phone' field has been set
         */
        public boolean hasPhone()
        {
            return fieldSetFlags()[2];
        }

        /**
         * Clears the value of the 'phone' field
         */
        public coherence.Person.Builder clearPhone()
        {
            phone = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        /**
         * Gets the value of the 'age' field
         */
        public java.lang.Integer getAge()
        {
            return age;
        }

        /**
         * Sets the value of the 'age' field
         */
        public coherence.Person.Builder setAge(int value)
        {
            validate(fields()[3], value);
            this.age = value;
            fieldSetFlags()[3] = true;
            return this;
        }

        /**
         * Checks whether the 'age' field has been set
         */
        public boolean hasAge()
        {
            return fieldSetFlags()[3];
        }

        /**
         * Clears the value of the 'age' field
         */
        public coherence.Person.Builder clearAge()
        {
            fieldSetFlags()[3] = false;
            return this;
        }

        @Override
        public Person build()
        {
            try
            {
                Person record = new Person();
                record.firstname = fieldSetFlags()[0] ? this.firstname : (java.lang.String) defaultValue(fields()[0]);
                record.lastname = fieldSetFlags()[1] ? this.lastname : (java.lang.String) defaultValue(fields()[1]);
                record.phone = fieldSetFlags()[2] ? this.phone : (java.lang.String) defaultValue(fields()[2]);
                record.age = fieldSetFlags()[3] ? this.age : (java.lang.Integer) defaultValue(fields()[3]);
                return record;
            }
            catch (Exception e)
            {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }
}
