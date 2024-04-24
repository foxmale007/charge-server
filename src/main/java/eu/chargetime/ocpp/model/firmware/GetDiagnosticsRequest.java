package eu.chargetime.ocpp.model.firmware;
/*
  ChargeTime.eu - Java-OCA-OCPP

  MIT License

  Copyright (C) 2016-2018 Thomas Volden <tv@chargetime.eu>
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

import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.utilities.MoreObjects;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"location", "startTime", "stopTime", "retries", "retryInterval"})
public class GetDiagnosticsRequest implements Request {

  private String location;
  private Integer retries;
  private Integer retryInterval;
  private ZonedDateTime startTime;
  private ZonedDateTime stopTime;

  /** @deprecated use {@link #GetDiagnosticsRequest(String)} to be sure to set required fields */
  @Deprecated
  public GetDiagnosticsRequest() {}

  /**
   * Handle required fields.
   *
   * @param location String, the destination folder, see {@link #setLocation(String)}
   */
  public GetDiagnosticsRequest(String location) {
    setLocation(location);
  }

  @Override
  public boolean transactionRelated() {
    return false;
  }

  @Override
  public boolean validate() {
    return location != null;
  }

  /**
   * This contains the location (directory) where the diagnostics file shall be uploaded to.
   *
   * @return String, the destination folder.
   */
  public String getLocation() {
    return location;
  }

  /**
   * Required. This contains the location (directory) where the diagnostics file shall be uploaded
   * to.
   *
   * @param location String, the destination folder.
   */
  @XmlElement
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * This specifies how many times Charge Point must try to upload the diagnostics before giving up.
   * If this field is not present, it is left to Charge Point to decide how many times it wants to
   * retry.
   *
   * @return int, minimum number of tries.
   */
  public Integer getRetries() {
    return retries;
  }

  /**
   * Optional. This specifies how many times Charge Point must try to upload the diagnostics before
   * giving up. If this field is not present, it is left to Charge Point to decide how many times it
   * wants to retry.
   *
   * @param retries int, minimum number of tries.
   */
  @XmlElement
  public void setRetries(Integer retries) {
    this.retries = retries;
  }

  /**
   * The interval in seconds after which a retry may be attempted. If this field is not present, it
   * is left to Charge Point to decide how long to wait between attempts.
   *
   * @return int, seconds
   */
  public Integer getRetryInterval() {
    return retryInterval;
  }

  /**
   * Optional. The interval in seconds after which a retry may be attempted. If this field is not
   * present, it is left to Charge Point to decide how long to wait between attempts.
   *
   * @param retryInterval int, seconds
   */
  @XmlElement
  public void setRetryInterval(Integer retryInterval) {
    this.retryInterval = retryInterval;
  }

  /**
   * This contains the date and time of the oldest logging information to include in the
   * diagnostics.
   *
   * @return ZonedDateTime, oldest log entry
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  /**
   * Optional. This contains the date and time of the oldest logging information to include in the
   * diagnostics.
   *
   * @param startTime ZonedDateTime, oldest log entry
   */
  @XmlElement
  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * This contains the date and time of the latest logging information to include in the
   * diagnostics.
   *
   * @return ZonedDateTime, latest log entry
   */
  public ZonedDateTime getStopTime() {
    return stopTime;
  }

  /**
   * Optional. This contains the date and time of the latest logging information to include in the
   * diagnostics.
   *
   * @param stopTime ZonedDateTime, latest log entry
   */
  @XmlElement
  public void setStopTime(ZonedDateTime stopTime) {
    this.stopTime = stopTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GetDiagnosticsRequest that = (GetDiagnosticsRequest) o;
    return Objects.equals(retries, that.retries)
        && Objects.equals(retryInterval, that.retryInterval)
        && Objects.equals(location, that.location)
        && Objects.equals(startTime, that.startTime)
        && Objects.equals(stopTime, that.stopTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, retries, retryInterval, startTime, stopTime);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("location", location)
        .add("retries", retries)
        .add("retryInterval", retryInterval)
        .add("startTime", startTime)
        .add("stopTime", stopTime)
        .add("isValid", validate())
        .toString();
  }
}
