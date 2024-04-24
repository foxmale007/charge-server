package eu.chargetime.ocpp.feature.profile;
/*
   ChargeTime.eu - Java-OCA-OCPP

   MIT License

   Copyright (C) 2016-2018 Thomas Volden <tv@chargetime.eu>
   Copyright (C) 2018 Mikhail Kladkevich <kladmv@ecp-share.com>
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

import eu.chargetime.ocpp.feature.CancelReservationFeature;
import eu.chargetime.ocpp.feature.Feature;
import eu.chargetime.ocpp.feature.ProfileFeature;
import eu.chargetime.ocpp.feature.ReserveNowFeature;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.reservation.CancelReservationRequest;
import eu.chargetime.ocpp.model.reservation.ReserveNowRequest;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;

public class ServerReservationProfile implements Profile {

  private HashSet<Feature> features;

  public ServerReservationProfile() {
    features = new HashSet<>();
    features.add(new ReserveNowFeature(null));
    features.add(new CancelReservationFeature(null));
  }

  @Override
  public ProfileFeature[] getFeatureList() {
    return features.toArray(new ProfileFeature[0]);
  }

  @Override
  public Confirmation handleRequest(UUID sessionIndex, Request request) {
    return null;
  }

  /**
   * Create a client {@link CancelReservationRequest} with required values.
   *
   * @param reservationId Integer, id of the reservation
   * @return an instance of {@link CancelReservationRequest}
   * @see CancelReservationRequest
   * @see CancelReservationFeature
   */
  CancelReservationRequest createCancelReservationRequest(Integer reservationId) {
    return new CancelReservationRequest(reservationId);
  }

  /**
   * Create a client {@link ReserveNowRequest} with required values.
   *
   * @param connectorId Integer, the destination connectorId
   * @param expiryDate ZonedDateTime, end of reservation
   * @param idTag String, the identifier
   * @param reservationId Integer, id of reservation
   * @return an instance of {@link ReserveNowRequest}
   * @see ReserveNowRequest
   * @see ReserveNowFeature
   */
  ReserveNowRequest createReserveNowRequest(
      Integer connectorId,
      ZonedDateTime expiryDate,
      String idTag,
      Integer reservationId,
      String parentIdTag) {
    ReserveNowRequest request =
        new ReserveNowRequest(connectorId, expiryDate, idTag, reservationId);
    request.setParentIdTag(parentIdTag);

    return request;
  }
}
