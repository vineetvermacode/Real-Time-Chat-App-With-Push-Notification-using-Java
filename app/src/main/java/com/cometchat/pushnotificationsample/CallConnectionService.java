package com.cometchat.pushnotificationsample;

import static android.telecom.TelecomManager.PRESENTATION_ALLOWED;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;

import android.view.Surface;
import android.widget.Toast;

import com.cometchat.chat.core.Call;
import com.cometchat.pushnotificationsample.helper.ConstantFile;

public class CallConnectionService extends ConnectionService {
    public static CallConnection conn;

    public CallConnectionService() {
        super();
    }

    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Bundle bundle = request.getExtras();
        String sessionID = bundle.getString(ConstantFile.IntentStrings.SESSION_ID);
        String name = bundle.getString(ConstantFile.IntentStrings.NAME);
        String type = bundle.getString(ConstantFile.IntentStrings.TYPE);
        String callType = bundle.getString(ConstantFile.IntentStrings.CALL_TYPE);
        callType = callType.substring(0, 1).toUpperCase() + callType.substring(1);
        String receiverUID = bundle.getString(ConstantFile.IntentStrings.RECEIVER_ID);
        if (receiverUID != null && type != null) {
            Call call = new Call(receiverUID, type, callType);
            call.setSessionId(sessionID);
            conn = new CallConnection(this, call);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
        }
        conn.setCallerDisplayName(name, PRESENTATION_ALLOWED);
        conn.setAddress(Uri.parse(callType + getString(R.string.call)), PRESENTATION_ALLOWED);
        conn.setInitializing();
        conn.setVideoProvider(new Connection.VideoProvider() {
            @Override
            public void onSetCamera(String cameraId) {
            }

            @Override
            public void onSetPreviewSurface(Surface surface) {
            }

            @Override
            public void onSetDisplaySurface(Surface surface) {
            }

            @Override
            public void onSetDeviceOrientation(int rotation) {
            }

            @Override
            public void onSetZoom(float value) {
            }

            @Override
            public void onSendSessionModifyRequest(VideoProfile fromProfile, VideoProfile toProfile) {
            }

            @Override
            public void onSendSessionModifyResponse(VideoProfile responseProfile) {

            }

            @Override
            public void onRequestCameraCapabilities() {
            }

            @Override
            public void onRequestConnectionDataUsage() {

            }

            @Override
            public void onSetPauseImage(Uri uri) {

            }
        });
        conn.setActive();
        return conn;
    }

    @Override
    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
    }

    @Override
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Bundle bundle = request.getExtras();
        String sessionID = bundle.getString(ConstantFile.IntentStrings.SESSION_ID);
        String name = bundle.getString(ConstantFile.IntentStrings.NAME);
        String receiverType = bundle.getString(ConstantFile.IntentStrings.TYPE);
        String callType = bundle.getString(ConstantFile.IntentStrings.CALL_TYPE);
        String receiverUID = bundle.getString(ConstantFile.IntentStrings.RECEIVER_ID);

        if (receiverUID != null) {
            Call call = new Call(receiverUID, receiverType, callType);
            call.setSessionId(sessionID);
            conn = new CallConnection(this, call);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
            }
            conn.setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED);
            conn.setAddress(request.getAddress(), TelecomManager.PRESENTATION_ALLOWED);
            conn.setInitializing();
            conn.setActive();
            return conn;
        } else {
            String phoneNumber = bundle.getString(ConstantFile.IntentStrings.ORIGINAL_NUMBER);
            Toast.makeText(getBaseContext(), R.string.cometchat_tried_calling + phoneNumber, Toast.LENGTH_LONG).show();
            return super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request);
        }
    }
}
