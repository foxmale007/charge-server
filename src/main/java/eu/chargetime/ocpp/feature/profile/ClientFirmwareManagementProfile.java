package eu.chargetime.ocpp.feature.profile;
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

import eu.chargetime.ocpp.feature.*;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.firmware.*;
import java.util.HashSet;
import java.util.UUID;

public class ClientFirmwareManagementProfile implements Profile {

  private HashSet<Feature> features;
  private ClientFirmwareManagementEventHandler eventHandler;

  public ClientFirmwareManagementProfile(ClientFirmwareManagementEventHandler eventHandler) {
    this.eventHandler = eventHandler;
    features = new HashSet<>();
    features.add(new DiagnosticsStatusNotificationFeature(null));
    features.add(new FirmwareStatusNotificationFeature(null));
    features.add(new GetDiagnosticsFeature(this));
    features.add(new UpdateFirmwareFeature(this));
  }

  /**
   * Create a {@link DiagnosticsStatusNotificationRequest} with required values.
   *
   * @param status required. Identification of the {@link DiagnosticsStatus}.
   * @return an instance of {@link DiagnosticsStatusNotificationRequest}.
   * @see DiagnosticsStatusNotificationRequest
   * @see DiagnosticsStatusNotificationFeature
   */
  public DiagnosticsStatusNotificationRequest createDiagnosticsStatusNotificationRequest(
      DiagnosticsStatus status) {
    return new DiagnosticsStatusNotificationRequest(status);
  }

  /**
   * Create a {@link FirmwareStatusNotificationRequest} with required values.
   *
   * @param status required. Identification of the {@link FirmwareStatus}.
   * @return an instance of {@link FirmwareStatusNotificationRequest}.
   * @see FirmwareStatusNotificationRequest
   * @see FirmwareStatusNotificationFeature
   */
  public FirmwareStatusNotificationRequest createFirmwareStatusNotificationRequest(
      FirmwareStatus status) {
    return new FirmwareStatusNotificationRequest(status);
  }

  @Override
  public ProfileFeature[] getFeatureList() {
    return features.toArray(new ProfileFeature[0]);
  }

  @Override
  public Confirmation handleRequest(UUID sessionIndex, Request request) {
    Confirmation result = null;

    if (request instanceof GetDiagnosticsRequest) {
      result = eventHandler.handleGetDiagnosticsRequest((GetDiagnosticsRequest) request);
    } else if (request instanceof UpdateFirmwareRequest) {
      result = eventHandler.handleUpdateFirmwareRequest((UpdateFirmwareRequest) request);
    }

    return result;
  }
}
