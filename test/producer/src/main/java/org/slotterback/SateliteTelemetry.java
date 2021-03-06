/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.slotterback;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class SateliteTelemetry extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -5568510541296869409L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SateliteTelemetry\",\"namespace\":\"org.slotterback\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"time\",\"type\":\"long\"},{\"name\":\"latitude\",\"type\":\"double\"},{\"name\":\"longitude\",\"type\":\"double\"},{\"name\":\"telemetry\",\"type\":{\"type\":\"record\",\"name\":\"telemetry\",\"fields\":[{\"name\":\"battery\",\"type\":\"int\"},{\"name\":\"computer\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"double\"},{\"name\":\"error\",\"type\":\"int\"}]}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<SateliteTelemetry> ENCODER =
      new BinaryMessageEncoder<SateliteTelemetry>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<SateliteTelemetry> DECODER =
      new BinaryMessageDecoder<SateliteTelemetry>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<SateliteTelemetry> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<SateliteTelemetry> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<SateliteTelemetry> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<SateliteTelemetry>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this SateliteTelemetry to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a SateliteTelemetry from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a SateliteTelemetry instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static SateliteTelemetry fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.CharSequence id;
   private long time;
   private double latitude;
   private double longitude;
   private Telemetry telemetry;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public SateliteTelemetry() {}

  /**
   * All-args constructor.
   * @param id The new value for id
   * @param time The new value for time
   * @param latitude The new value for latitude
   * @param longitude The new value for longitude
   * @param telemetry The new value for telemetry
   */
  public SateliteTelemetry(java.lang.CharSequence id, java.lang.Long time, java.lang.Double latitude, java.lang.Double longitude, Telemetry telemetry) {
    this.id = id;
    this.time = time;
    this.latitude = latitude;
    this.longitude = longitude;
    this.telemetry = telemetry;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return time;
    case 2: return latitude;
    case 3: return longitude;
    case 4: return telemetry;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.CharSequence)value$; break;
    case 1: time = (java.lang.Long)value$; break;
    case 2: latitude = (java.lang.Double)value$; break;
    case 3: longitude = (java.lang.Double)value$; break;
    case 4: telemetry = (Telemetry)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   * @return The value of the 'id' field.
   */
  public java.lang.CharSequence getId() {
    return id;
  }


  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.lang.CharSequence value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'time' field.
   * @return The value of the 'time' field.
   */
  public long getTime() {
    return time;
  }


  /**
   * Sets the value of the 'time' field.
   * @param value the value to set.
   */
  public void setTime(long value) {
    this.time = value;
  }

  /**
   * Gets the value of the 'latitude' field.
   * @return The value of the 'latitude' field.
   */
  public double getLatitude() {
    return latitude;
  }


  /**
   * Sets the value of the 'latitude' field.
   * @param value the value to set.
   */
  public void setLatitude(double value) {
    this.latitude = value;
  }

  /**
   * Gets the value of the 'longitude' field.
   * @return The value of the 'longitude' field.
   */
  public double getLongitude() {
    return longitude;
  }


  /**
   * Sets the value of the 'longitude' field.
   * @param value the value to set.
   */
  public void setLongitude(double value) {
    this.longitude = value;
  }

  /**
   * Gets the value of the 'telemetry' field.
   * @return The value of the 'telemetry' field.
   */
  public Telemetry getTelemetry() {
    return telemetry;
  }


  /**
   * Sets the value of the 'telemetry' field.
   * @param value the value to set.
   */
  public void setTelemetry(Telemetry value) {
    this.telemetry = value;
  }

  /**
   * Creates a new SateliteTelemetry RecordBuilder.
   * @return A new SateliteTelemetry RecordBuilder
   */
  public static org.slotterback.SateliteTelemetry.Builder newBuilder() {
    return new org.slotterback.SateliteTelemetry.Builder();
  }

  /**
   * Creates a new SateliteTelemetry RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new SateliteTelemetry RecordBuilder
   */
  public static org.slotterback.SateliteTelemetry.Builder newBuilder(org.slotterback.SateliteTelemetry.Builder other) {
    if (other == null) {
      return new org.slotterback.SateliteTelemetry.Builder();
    } else {
      return new org.slotterback.SateliteTelemetry.Builder(other);
    }
  }

  /**
   * Creates a new SateliteTelemetry RecordBuilder by copying an existing SateliteTelemetry instance.
   * @param other The existing instance to copy.
   * @return A new SateliteTelemetry RecordBuilder
   */
  public static org.slotterback.SateliteTelemetry.Builder newBuilder(org.slotterback.SateliteTelemetry other) {
    if (other == null) {
      return new org.slotterback.SateliteTelemetry.Builder();
    } else {
      return new org.slotterback.SateliteTelemetry.Builder(other);
    }
  }

  /**
   * RecordBuilder for SateliteTelemetry instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<SateliteTelemetry>
    implements org.apache.avro.data.RecordBuilder<SateliteTelemetry> {

    private java.lang.CharSequence id;
    private long time;
    private double latitude;
    private double longitude;
    private Telemetry telemetry;
    private Telemetry.Builder telemetryBuilder;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(org.slotterback.SateliteTelemetry.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.time)) {
        this.time = data().deepCopy(fields()[1].schema(), other.time);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.latitude)) {
        this.latitude = data().deepCopy(fields()[2].schema(), other.latitude);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.longitude)) {
        this.longitude = data().deepCopy(fields()[3].schema(), other.longitude);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.telemetry)) {
        this.telemetry = data().deepCopy(fields()[4].schema(), other.telemetry);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (other.hasTelemetryBuilder()) {
        this.telemetryBuilder = Telemetry.newBuilder(other.getTelemetryBuilder());
      }
    }

    /**
     * Creates a Builder by copying an existing SateliteTelemetry instance
     * @param other The existing instance to copy.
     */
    private Builder(org.slotterback.SateliteTelemetry other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.time)) {
        this.time = data().deepCopy(fields()[1].schema(), other.time);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.latitude)) {
        this.latitude = data().deepCopy(fields()[2].schema(), other.latitude);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.longitude)) {
        this.longitude = data().deepCopy(fields()[3].schema(), other.longitude);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.telemetry)) {
        this.telemetry = data().deepCopy(fields()[4].schema(), other.telemetry);
        fieldSetFlags()[4] = true;
      }
      this.telemetryBuilder = null;
    }

    /**
      * Gets the value of the 'id' field.
      * @return The value.
      */
    public java.lang.CharSequence getId() {
      return id;
    }


    /**
      * Sets the value of the 'id' field.
      * @param value The value of 'id'.
      * @return This builder.
      */
    public org.slotterback.SateliteTelemetry.Builder setId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'id' field has been set.
      * @return True if the 'id' field has been set, false otherwise.
      */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'id' field.
      * @return This builder.
      */
    public org.slotterback.SateliteTelemetry.Builder clearId() {
      id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'time' field.
      * @return The value.
      */
    public long getTime() {
      return time;
    }


    /**
      * Sets the value of the 'time' field.
      * @param value The value of 'time'.
      * @return This builder.
      */
    public org.slotterback.SateliteTelemetry.Builder setTime(long value) {
      validate(fields()[1], value);
      this.time = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'time' field has been set.
      * @return True if the 'time' field has been set, false otherwise.
      */
    public boolean hasTime() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'time' field.
      * @return This builder.
      */
    public org.slotterback.SateliteTelemetry.Builder clearTime() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'latitude' field.
      * @return The value.
      */
    public double getLatitude() {
      return latitude;
    }


    /**
      * Sets the value of the 'latitude' field.
      * @param value The value of 'latitude'.
      * @return This builder.
      */
    public org.slotterback.SateliteTelemetry.Builder setLatitude(double value) {
      validate(fields()[2], value);
      this.latitude = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'latitude' field has been set.
      * @return True if the 'latitude' field has been set, false otherwise.
      */
    public boolean hasLatitude() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'latitude' field.
      * @return This builder.
      */
    public org.slotterback.SateliteTelemetry.Builder clearLatitude() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'longitude' field.
      * @return The value.
      */
    public double getLongitude() {
      return longitude;
    }


    /**
      * Sets the value of the 'longitude' field.
      * @param value The value of 'longitude'.
      * @return This builder.
      */
    public org.slotterback.SateliteTelemetry.Builder setLongitude(double value) {
      validate(fields()[3], value);
      this.longitude = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'longitude' field has been set.
      * @return True if the 'longitude' field has been set, false otherwise.
      */
    public boolean hasLongitude() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'longitude' field.
      * @return This builder.
      */
    public org.slotterback.SateliteTelemetry.Builder clearLongitude() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'telemetry' field.
      * @return The value.
      */
    public Telemetry getTelemetry() {
      return telemetry;
    }


    /**
      * Sets the value of the 'telemetry' field.
      * @param value The value of 'telemetry'.
      * @return This builder.
      */
    public org.slotterback.SateliteTelemetry.Builder setTelemetry(Telemetry value) {
      validate(fields()[4], value);
      this.telemetryBuilder = null;
      this.telemetry = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'telemetry' field has been set.
      * @return True if the 'telemetry' field has been set, false otherwise.
      */
    public boolean hasTelemetry() {
      return fieldSetFlags()[4];
    }

    /**
     * Gets the Builder instance for the 'telemetry' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public Telemetry.Builder getTelemetryBuilder() {
      if (telemetryBuilder == null) {
        if (hasTelemetry()) {
          setTelemetryBuilder(Telemetry.newBuilder(telemetry));
        } else {
          setTelemetryBuilder(Telemetry.newBuilder());
        }
      }
      return telemetryBuilder;
    }

    /**
     * Sets the Builder instance for the 'telemetry' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public org.slotterback.SateliteTelemetry.Builder setTelemetryBuilder(Telemetry.Builder value) {
      clearTelemetry();
      telemetryBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'telemetry' field has an active Builder instance
     * @return True if the 'telemetry' field has an active Builder instance
     */
    public boolean hasTelemetryBuilder() {
      return telemetryBuilder != null;
    }

    /**
      * Clears the value of the 'telemetry' field.
      * @return This builder.
      */
    public org.slotterback.SateliteTelemetry.Builder clearTelemetry() {
      telemetry = null;
      telemetryBuilder = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SateliteTelemetry build() {
      try {
        SateliteTelemetry record = new SateliteTelemetry();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.time = fieldSetFlags()[1] ? this.time : (java.lang.Long) defaultValue(fields()[1]);
        record.latitude = fieldSetFlags()[2] ? this.latitude : (java.lang.Double) defaultValue(fields()[2]);
        record.longitude = fieldSetFlags()[3] ? this.longitude : (java.lang.Double) defaultValue(fields()[3]);
        if (telemetryBuilder != null) {
          try {
            record.telemetry = this.telemetryBuilder.build();
          } catch (org.apache.avro.AvroMissingFieldException e) {
            e.addParentField(record.getSchema().getField("telemetry"));
            throw e;
          }
        } else {
          record.telemetry = fieldSetFlags()[4] ? this.telemetry : (Telemetry) defaultValue(fields()[4]);
        }
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<SateliteTelemetry>
    WRITER$ = (org.apache.avro.io.DatumWriter<SateliteTelemetry>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<SateliteTelemetry>
    READER$ = (org.apache.avro.io.DatumReader<SateliteTelemetry>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.id);

    out.writeLong(this.time);

    out.writeDouble(this.latitude);

    out.writeDouble(this.longitude);

    this.telemetry.customEncode(out);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.id = in.readString(this.id instanceof Utf8 ? (Utf8)this.id : null);

      this.time = in.readLong();

      this.latitude = in.readDouble();

      this.longitude = in.readDouble();

      if (this.telemetry == null) {
        this.telemetry = new Telemetry();
      }
      this.telemetry.customDecode(in);

    } else {
      for (int i = 0; i < 5; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.id = in.readString(this.id instanceof Utf8 ? (Utf8)this.id : null);
          break;

        case 1:
          this.time = in.readLong();
          break;

        case 2:
          this.latitude = in.readDouble();
          break;

        case 3:
          this.longitude = in.readDouble();
          break;

        case 4:
          if (this.telemetry == null) {
            this.telemetry = new Telemetry();
          }
          this.telemetry.customDecode(in);
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










