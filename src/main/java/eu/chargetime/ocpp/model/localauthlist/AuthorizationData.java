package eu.chargetime.ocpp.model.localauthlist;

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

import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.model.Validatable;
import eu.chargetime.ocpp.model.core.IdTagInfo;
import eu.chargetime.ocpp.utilities.ModelUtil;
import eu.chargetime.ocpp.utilities.MoreObjects;
import java.util.Objects;

public class AuthorizationData implements Validatable {

  private String idTag;
  private IdTagInfo idTagInfo;

  /** @deprecated use {@link #AuthorizationData(String)} be sure to set required fields */
  @Deprecated
  public AuthorizationData() {}

  /**
   * Handle required fields.
   *
   * @param idTag String, the idTag, see {@link #setIdTag(String)}
   */
  public AuthorizationData(String idTag) {
    setIdTag(idTag);
  }

  /**
   * The identifier to which this authorization applies
   *
   * @return String the idTag
   */
  public String getIdTag() {
    return idTag;
  }

  /**
   * Required. The identifier to which this authorization applies
   *
   * @param idTag String, the idTag
   */
  public void setIdTag(String idTag) {
    if (!ModelUtil.validate(idTag, 20)) {
      throw new PropertyConstraintException(idTag, "Exceeds limit of 20 chars");
    }

    this.idTag = idTag;
  }

  public IdTagInfo getIdTagInfo() {
    return idTagInfo;
  }

  public void setIdTagInfo(IdTagInfo idTagInfo) {
    if (!idTagInfo.validate()) {
      throw new PropertyConstraintException(idTagInfo, "Failed Validation");
    }

    this.idTagInfo = idTagInfo;
  }

  @Override
  public boolean validate() {
    return ModelUtil.validate(idTag, 20) && idTagInfo.validate();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthorizationData that = (AuthorizationData) o;
    return Objects.equals(idTag, that.idTag) && Objects.equals(idTagInfo, that.idTagInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(idTag, idTagInfo);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("idTag", idTag)
        .add("idTagInfo", idTagInfo)
        .toString();
  }
}
