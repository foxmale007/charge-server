package eu.chargetime.ocpp.feature.profile;
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

import eu.chargetime.ocpp.feature.*;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * The core feature profile contains the features from OCPP v. 1.5
 *
 * <p>Contains methods to create outgoing client requests.
 */
public class ClientCoreProfile implements Profile {

  ArrayList<Feature> features;
  private ClientCoreEventHandler eventHandler;

  /**
   * Set up handler for client core feature requests.
   *
   * @param handler call back methods for client events
   */
  public ClientCoreProfile(ClientCoreEventHandler handler) {
    features = new ArrayList<>();
    eventHandler = handler;

    features.add(new AuthorizeFeature(null));
    features.add(new BootNotificationFeature(null));
    features.add(new ChangeAvailabilityFeature(this));
    features.add(new ChangeConfigurationFeature(this));
    features.add(new ClearCacheFeature(this));
    features.add(new DataTransferFeature(null));
    features.add(new GetConfigurationFeature(this));
    features.add(new HeartbeatFeature(null));
    features.add(new MeterValuesFeature(null));
    features.add(new RemoteStartTransactionFeature(this));
    features.add(new RemoteStopTransactionFeature(this));
    features.add(new ResetFeature(this));
    features.add(new StartTransactionFeature(null));
    features.add(new StatusNotificationFeature(null));
    features.add(new StopTransactionFeature(null));
    features.add(new UnlockConnectorFeature(this));
  }

  /**
   * Create a client {@link AuthorizeRequest} with required values.
   *
   * @param idTag required, identification token.
   * @return an instance of {@link AuthorizeRequest}.
   * @see AuthorizeRequest
   * @see AuthorizeFeature
   */
  public AuthorizeRequest createAuthorizeRequest(String idTag) {
    return new AuthorizeRequest(idTag);
  }

  /**
   * Create a client {@link BootNotificationRequest} with required values.
   *
   * @param vendor required. Vendor name.
   * @param model required. Charge box model.
   * @return an instance of {@link BootNotificationRequest}
   * @see BootNotificationRequest
   * @see BootNotificationFeature
   */
  public BootNotificationRequest createBootNotificationRequest(String vendor, String model) {
    return new BootNotificationRequest(vendor, model);
  }

  /**
   * Create a client {@link DataTransferRequest} with required values.
   *
   * @param vendorId required. Vendor identification.
   * @return an instance of {@link DataTransferRequest}.
   * @see DataTransferRequest
   * @see DataTransferFeature
   */
  public DataTransferRequest createDataTransferRequest(String vendorId) {
    return new DataTransferRequest(vendorId);
  }

  /**
   * Create a client {@link HeartbeatRequest}.
   *
   * @return an instance of {@link HeartbeatRequest}
   * @see HeartbeatRequest
   * @see HeartbeatFeature
   */
  public HeartbeatRequest createHeartbeatRequest() {
    return new HeartbeatRequest();
  }

  /**
   * Create a client {@link MeterValuesRequest} with one {@link SampledValue} and one {@link
   * MeterValue}
   *
   * @param connectorId required. Identification of connector.
   * @param timestamp required. Time of sample.
   * @param value required. Value of sample.
   * @return an instance of {@link MeterValuesRequest}.
   * @see MeterValuesRequest
   * @see MeterValuesFeature
   */
  public MeterValuesRequest createMeterValuesRequest(
      Integer connectorId, String timestamp, String value) {
    SampledValue sampledValue = new SampledValue(value);
    return createMeterValuesRequest(connectorId, timestamp, sampledValue);
  }

  /**
   * Create a client {@link MeterValuesRequest} with some {@link SampledValue}s and one {@link
   * MeterValue}.
   *
   * @param connectorId required. Identification of connector.
   * @param timestamp required. Time of sample.
   * @param sampledValues required. Params list of {@link SampledValue}s
   * @return an instance of {@link MeterValuesRequest}
   * @see MeterValuesRequest
   * @see MeterValuesFeature
   */
  public MeterValuesRequest createMeterValuesRequest(
      Integer connectorId, String timestamp, SampledValue... sampledValues) {
    MeterValue meterValue = new MeterValue(timestamp, sampledValues);
    return createMeterValuesRequest(connectorId, meterValue);
  }

  /**
   * Create a client {@link MeterValuesRequest} with some {@link MeterValue}s.
   *
   * @param connectorId required. Identification of connector.
   * @param meterValues required. Params list of {@link MeterValue}s
   * @return an instance of {@link MeterValuesRequest}
   * @see MeterValuesRequest
   * @see MeterValuesFeature
   */
  public MeterValuesRequest createMeterValuesRequest(
      Integer connectorId, MeterValue... meterValues) {
    MeterValuesRequest request = new MeterValuesRequest(connectorId);
    request.setMeterValue(meterValues);
    return request;
  }

  /**
   * Create a client {@link StartTransactionRequest} with required values.
   *
   * @param connectorId required. Identification of the connector.
   * @param idTag required. Authorization identification tag.
   * @param meterStart required. The initial value of the meter.
   * @param timestamp required. Time of start.
   * @return an instance of {@link StartTransactionRequest}.
   * @see StartTransactionRequest
   * @see StartTransactionFeature
   */
  public StartTransactionRequest createStartTransactionRequest(
      Integer connectorId, String idTag, Integer meterStart, String timestamp) {
    return new StartTransactionRequest(connectorId, idTag, meterStart, timestamp);
  }

  /**
   * Create a client {@link StatusNotificationRequest} with required values.
   *
   * @param connectorId required. Identification of the connector.
   * @param errorCode required. {@link ChargePointErrorCode} of the connector.
   * @param status required. {@link ChargePointStatus} of the connector.
   * @return an instance of {@link StatusNotificationRequest}.
   * @see StatusNotificationRequest
   * @see StatusNotificationFeature
   */
  public StatusNotificationRequest createStatusNotificationRequest(
      Integer connectorId, ChargePointErrorCode errorCode, ChargePointStatus status) {
    StatusNotificationRequest request =
        new StatusNotificationRequest(connectorId, errorCode, status);
    return request;
  }

  /**
   * Create a client {@link StopTransactionRequest} with required values.
   *
   * @param meterStop required. The final value of the meter.
   * @param timestamp required. Time of stop.
   * @param transactionId required. The identification of the transaction.
   * @return an instance of {@link StopTransactionRequest}.
   */
  public StopTransactionRequest createStopTransactionRequest(
      int meterStop, String timestamp, int transactionId) {
    StopTransactionRequest request =
        new StopTransactionRequest(meterStop, timestamp, transactionId);
    return request;
  }

  @Override
  public ProfileFeature[] getFeatureList() {
    return features.toArray(new ProfileFeature[0]);
  }

  @Override
  public Confirmation handleRequest(UUID sessionIndex, Request request) {
    Confirmation result = null;

    if (request instanceof ChangeAvailabilityRequest) {
      result = eventHandler.handleChangeAvailabilityRequest((ChangeAvailabilityRequest) request);
    } else if (request instanceof ChangeConfigurationRequest) {
      result = eventHandler.handleChangeConfigurationRequest((ChangeConfigurationRequest) request);
    } else if (request instanceof ClearCacheRequest) {
      result = eventHandler.handleClearCacheRequest((ClearCacheRequest) request);
    } else if (request instanceof DataTransferRequest) {
      result = eventHandler.handleDataTransferRequest((DataTransferRequest) request);
    } else if (request instanceof GetConfigurationRequest) {
      result = eventHandler.handleGetConfigurationRequest((GetConfigurationRequest) request);
    } else if (request instanceof RemoteStartTransactionRequest) {
      result =
          eventHandler.handleRemoteStartTransactionRequest((RemoteStartTransactionRequest) request);
    } else if (request instanceof RemoteStopTransactionRequest) {
      result =
          eventHandler.handleRemoteStopTransactionRequest((RemoteStopTransactionRequest) request);
    } else if (request instanceof ResetRequest) {
      result = eventHandler.handleResetRequest((ResetRequest) request);
    } else if (request instanceof UnlockConnectorRequest) {
      result = eventHandler.handleUnlockConnectorRequest((UnlockConnectorRequest) request);
    }

    return result;
  }
}
