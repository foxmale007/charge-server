package eu.chargetime.ocpp.feature.profile;

import eu.chargetime.ocpp.model.smartcharging.*;

/*
 * ChargeTime.eu - Java-OCA-OCPP
 *
 * MIT License
 *
 * Copyright (C) 2017 Emil Christopher Solli Melar <emil@iconsultable.no>
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

public interface ClientSmartChargingEventHandler {
  /**
   * Handle a {@link SetChargingProfileRequest} and return a {@link SetChargingProfileConfirmation}.
   *
   * @param request incoming {@link SetChargingProfileRequest} to handle.
   * @return outgoing {@link SetChargingProfileConfirmation} to reply with.
   */
  SetChargingProfileConfirmation handleSetChargingProfileRequest(SetChargingProfileRequest request);

  /**
   * Handle a {@link ClearChargingProfileRequest} and return a {@link
   * ClearChargingProfileConfirmation}.
   *
   * @param request incoming {@link ClearChargingProfileRequest} to handle.
   * @return outgoing {@link ClearChargingProfileConfirmation} to reply with.
   */
  ClearChargingProfileConfirmation handleClearChargingProfileRequest(
      ClearChargingProfileRequest request);

  /**
   * Handle a {@link GetCompositeScheduleRequest} and return a {@link
   * GetCompositeScheduleConfirmation}.
   *
   * @param request incoming {@link GetCompositeScheduleRequest} to handle.
   * @return outgoing {@link GetCompositeScheduleConfirmation} to reply with.
   */
  GetCompositeScheduleConfirmation handleGetCompositeScheduleRequest(
      GetCompositeScheduleRequest request);
}
