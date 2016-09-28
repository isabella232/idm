/*
 * Copyright 2016 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openid.appauth;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import static net.openid.appauth.AuthorizationResponse.KEY_STATE;
import static net.openid.appauth.DeviceActivationRequest.KEY_ACTIVATION_CODE;
import static net.openid.appauth.AdditionalParamsProcessor.extractAdditionalParams;
import static net.openid.appauth.AuthorizationException.KEY_CODE;
import static net.openid.appauth.Preconditions.checkNotNull;

/**
 * Defines the response of the device registration call.
 */
public class DeviceRegistrationResponse {

    /**
     * The extra string used to store a {@link DeviceRegistrationResponse} in an intent by
     * {@link #toIntent()}.
     */
    public static final String EXTRA_RESPONSE = "com.vmware.idm.DeviceRegistrationResponse";

    static final String KEY_REQUEST = "request";

    /**
     * The registration request associated with this response.
     */
    @NonNull
    public final DeviceRegistrationRequest request;

    /**
     * The authorization code generated by the authorization server.
     */
    @Nullable
    public final String authorizationCode;

    /**
     * The activation code generated by the authorization server.
     */
    @Nullable
    public final String activationCode;

    /**
     * The state as passed in the request.
     */
    @Nullable
    public final String state;

    private DeviceRegistrationResponse(@NonNull DeviceRegistrationRequest request,
                                       @Nullable String authorizationCode,
                                       @Nullable String activationCode,
                                       @Nullable String state) {
        this.request = request;
        this.authorizationCode = authorizationCode;
        this.activationCode = activationCode;
        this.state = state;
    }


    /**
     * Extracts the registration response parameters from the query portion of a redirect URI.
     *
     * @param uri the returned URI
     */
    @NonNull
    public static DeviceRegistrationResponse fromUri(DeviceRegistrationRequest request,
                                                     @NonNull Uri uri) {
        return new DeviceRegistrationResponse(request, uri.getQueryParameter(KEY_CODE),
                uri.getQueryParameter(KEY_ACTIVATION_CODE), uri.getQueryParameter(KEY_STATE));
    }

    /**
     * Produces an intent containing this registration response. This is used to deliver the
     * response to the registered handler after a call to
     * {@link DeviceAuthorizationService#performRegistrationRequest}.
     */
    @NonNull
    public Intent toIntent() {
        Intent data = new Intent();
        data.putExtra(EXTRA_RESPONSE, this.jsonSerializeString());
        return data;
    }

    @NonNull
    private String jsonSerializeString() {
        JSONObject json = new JSONObject();
        JsonUtil.put(json, KEY_REQUEST, request.jsonSerialize());
        JsonUtil.putIfNotNull(json, KEY_CODE, authorizationCode);
        JsonUtil.putIfNotNull(json, KEY_ACTIVATION_CODE, activationCode);
        JsonUtil.putIfNotNull(json, KEY_STATE, state);
        return json.toString();
    }

    /**
     * Extracts a registration response from an intent produced by {@link #toIntent()}. This is
     * used to extract the response from the intent data passed to an activity registered as the
     * handler for {@link DeviceAuthorizationService#performRegistrationRequest}.
     */
    @Nullable
    public static DeviceRegistrationResponse fromIntent(@NonNull Intent dataIntent) {
        checkNotNull(dataIntent, "dataIntent must not be null");
        if (!dataIntent.hasExtra(EXTRA_RESPONSE)) {
            return null;
        }

        try {
            JSONObject jsonResponse = new JSONObject(dataIntent.getStringExtra(EXTRA_RESPONSE));

            if (!jsonResponse.has(KEY_REQUEST)) {
                throw new IllegalArgumentException(
                        "authorization request not provided and not found in JSON");
            }

            DeviceRegistrationRequest request =
                    DeviceRegistrationRequest
                            .jsonDeserialize(jsonResponse.getJSONObject(KEY_REQUEST));

            return new DeviceRegistrationResponse(request, jsonResponse.getString(KEY_CODE),
                    jsonResponse.getString(KEY_ACTIVATION_CODE),
                    JsonUtil.getStringIfDefined(jsonResponse,
                            KEY_STATE));
        } catch (JSONException ex) {
            throw new IllegalArgumentException("Intent contains malformed registration response",
                    ex);
        }
    }

    /**
     * Creates a follow-up request to exchange a received activation code for oauth2 credentials.
     *
     * @param activationEndpoint the activation endpoint
     */
    public DeviceActivationRequest createDeviceActivationRequest(Uri activationEndpoint) {
        if (activationCode == null) {
            throw new IllegalStateException("activationCode not available for activation request");
        }
        return new DeviceActivationRequest.Builder(activationEndpoint,
                request.configuration,
                activationCode).build();
    }

    /**
     * Convert to an original AppAuth {@link AuthorizationResponse}, to be consumed by the AppAuth library.
     *
     * @param clientId the client id
     */
    public AuthorizationResponse toAuthorizationResponse(String clientId) {
        return new AuthorizationResponse.Builder(request.toAuthorizationRequest(clientId))
                .setAuthorizationCode(authorizationCode).build();

    }
}