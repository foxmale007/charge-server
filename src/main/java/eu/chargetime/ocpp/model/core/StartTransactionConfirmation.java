package eu.chargetime.ocpp.model.core;

/*
 * ChargeTime.eu - Java-OCA-OCPP
 *
 * MIT License
 *
 * Copyright (C) 2016-2018 Thomas Volden <tv@chargetime.eu>
 * Copyright (C) 2019 Kevin Raddatz <kevin.raddatz@valtech-mobility.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.utilities.MoreObjects;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent by the Central System to the Charge Point in response to a {@link StartTransactionRequest}.
 */
@XmlRootElement(name = "startTransactionResponse")
@XmlType(propOrder = {"transactionId", "idTagInfo"})
public class StartTransactionConfirmation implements Confirmation {

  private IdTagInfo idTagInfo;
  private Integer transactionId;

  /**
   * @deprecated use {@link #StartTransactionConfirmation(IdTagInfo, Integer)} to be sure to set
   *     required fields
   */
  @Deprecated
  public StartTransactionConfirmation() {}

  /**
   * Handle required fields.
   *
   * @param idTagInfo the {@link IdTagInfo}, see {@link #setIdTagInfo(IdTagInfo)}
   * @param transactionId integer, transaction, see {@link #setTransactionId(Integer)}
   */
  public StartTransactionConfirmation(IdTagInfo idTagInfo, Integer transactionId) {
    setIdTagInfo(idTagInfo);
    setTransactionId(transactionId);
  }

  @Override
  public boolean validate() {
    boolean valid = true;
    if (valid &= idTagInfo != null) valid &= idTagInfo.validate();
    valid &= transactionId != null;
    return valid;
  }

  /**
   * This contains information about authorization status, expiry and parent id.
   *
   * @return the {@link IdTagInfo}.
   */
  public IdTagInfo getIdTagInfo() {
    return idTagInfo;
  }

  /**
   * Required. This contains information about authorization status, expiry and parent id.
   *
   * @param idTagInfo the {@link IdTagInfo}.
   */
  @XmlElement
  public void setIdTagInfo(IdTagInfo idTagInfo) {
    this.idTagInfo = idTagInfo;
  }

  /**
   * This contains the transaction id supplied by the Central System.
   *
   * @return transaction id.
   */
  public Integer getTransactionId() {
    return transactionId;
  }

  /**
   * Required. This contains the transaction id supplied by the Central System.
   *
   * @param transactionId integer, transaction.
   */
  @XmlElement
  public void setTransactionId(Integer transactionId) {
    this.transactionId = transactionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StartTransactionConfirmation that = (StartTransactionConfirmation) o;
    return Objects.equals(idTagInfo, that.idTagInfo)
        && Objects.equals(transactionId, that.transactionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(idTagInfo, transactionId);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("idTagInfo", idTagInfo)
        .add("transactionId", transactionId)
        .add("isValid", validate())
        .toString();
  }
}
