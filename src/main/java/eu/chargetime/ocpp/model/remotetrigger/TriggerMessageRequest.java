package eu.chargetime.ocpp.model.remotetrigger;

/*
ChargeTime.eu - Java-OCA-OCPP
Copyright (C) 2017 Emil Christopher Solli Melar <emil@iconsultable.no>
Copyright (C) 2019 Kevin Raddatz <kevin.raddatz@valtech-mobility.com>

MIT License

Copyright (C) 2017 Emil Christopher Solli Melar
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
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"requestedMessage", "connectorId"})
public class TriggerMessageRequest implements Request {

  private Integer connectorId;
  private TriggerMessageRequestType requestedMessage;

  /**
   * @deprecated use {@link #TriggerMessageRequest(TriggerMessageRequestType)} to be sure to set
   *     required fields
   */
  @Deprecated
  public TriggerMessageRequest() {}

  /**
   * Handle required fields.
   *
   * @param triggerMessageRequestType {@link TriggerMessageRequestType}, see {@link
   *     #setRequestedMessage(TriggerMessageRequestType)}
   */
  public TriggerMessageRequest(TriggerMessageRequestType triggerMessageRequestType) {
    this.requestedMessage = triggerMessageRequestType;
  }

  /**
   * This identifies which connector of the Charge Point is used.
   *
   * @return connector.
   */
  public Integer getConnectorId() {
    return connectorId;
  }

  /**
   * Optional. This identifies which connector of the Charge Point is used.
   *
   * @param connectorId integer. value &gt; 0
   */
  @XmlElement
  public void setConnectorId(Integer connectorId) {
    if (connectorId != null && connectorId <= 0) {
      throw new PropertyConstraintException(connectorId, "connectorId must be > 0");
    }

    this.connectorId = connectorId;
  }

  public TriggerMessageRequestType getRequestedMessage() {
    return requestedMessage;
  }

  /**
   * Required. This identifies which type of message you want to trigger.
   *
   * @param requestedMessage {@link TriggerMessageRequestType}.
   */
  @XmlElement
  public void setRequestedMessage(TriggerMessageRequestType requestedMessage) {
    this.requestedMessage = requestedMessage;
  }

  /**
   * This identifies which type of message you want to trigger.
   *
   * @return connector.
   */
  @Override
  public boolean transactionRelated() {
    return false;
  }

  @Override
  public boolean validate() {
    boolean valid = requestedMessage != null;
    valid &= (connectorId == null || connectorId > 0);

    return valid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TriggerMessageRequest that = (TriggerMessageRequest) o;
    return Objects.equals(connectorId, that.connectorId)
        && requestedMessage == that.requestedMessage;
  }

  @Override
  public int hashCode() {
    return Objects.hash(connectorId, requestedMessage);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("connectorId", connectorId)
        .add("requestedMessage", requestedMessage)
        .add("isValid", validate())
        .toString();
  }
}
