package eu.chargetime.ocpp.model.core;

/*
ChargeTime.eu - Java-OCA-OCPP
Copyright (C) 2015-2016 Thomas Volden <tv@chargetime.eu>
Copyright (C) 2019 Kevin Raddatz <kevin.raddatz@valtech-mobility.com>

MIT License

Copyright (C) 2016-2018 Thomas Volden
Copyright (C) 2019 Kevin Raddatz <kevin.raddatz@valtech-mobility.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.utilities.MoreObjects;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/** Sent by the Central System to the Charge Point. */
@XmlRootElement
public class ChangeAvailabilityRequest implements Request {

  private Integer connectorId = -1;
  private AvailabilityType type;

  /**
   * @deprecated use {@link #ChangeAvailabilityRequest(Integer, AvailabilityType)} to be sure to set
   *     required fields
   */
  @Deprecated
  public ChangeAvailabilityRequest() {}

  /**
   * Handle required fields.
   *
   * @param connectorId integer, must be a non-negative number.
   * @param type the {@link AvailabilityType} of the connector, see {@link
   *     #setType(AvailabilityType)}
   */
  public ChangeAvailabilityRequest(Integer connectorId, AvailabilityType type) {
    setConnectorId(connectorId);
    setType(type);
  }

  /**
   * The id of the connector for which availability needs to change. Id '0' (zero) is used if the
   * availability of the Charge Point and all its connectors needs to change.
   *
   * @return identification of the connector. 0 = all.
   */
  public int getConnectorId() {
    return connectorId;
  }

  /**
   * Required. The id of the connector for which availability needs to change. Id '0' (zero) is used
   * if the availability of the Charge Point and all its connectors needs to change.
   *
   * @param connectorId integer, must be a non-negative number.
   */
  @XmlElement
  public void setConnectorId(int connectorId) {
    if (connectorId < 0) {
      throw new PropertyConstraintException(connectorId, "connectorId must be >= 0");
    }

    this.connectorId = connectorId;
  }

  /**
   * This contains the type of availability change that the Charge Point should perform.
   *
   * @return {@link AvailabilityType} of the connector.
   */
  public AvailabilityType getType() {
    return type;
  }

  /**
   * Required. This contains the type of availability change that the Charge Point should perform.
   *
   * @param type {@link AvailabilityType} of the connector
   */
  @XmlElement
  public void setType(AvailabilityType type) {
    this.type = type;
  }

  /**
   * This contains the type of availability change that the Charge Point should perform.
   *
   * @return {@link AvailabilityType} of the connector.
   */
  @Deprecated
  public AvailabilityType objType() {
    return type;
  }

  @Override
  public boolean validate() {
    return type != null && connectorId != null && connectorId >= 0;
  }

  @Override
  public boolean transactionRelated() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeAvailabilityRequest that = (ChangeAvailabilityRequest) o;
    return Objects.equals(connectorId, that.connectorId) && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(connectorId, type);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("connectorId", connectorId)
        .add("type", type)
        .add("isValid", validate())
        .toString();
  }
}
