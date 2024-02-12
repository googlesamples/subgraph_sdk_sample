/* Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2022 Google, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  The ASF licenses this
 * file to you under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.samples.quickstart.subgraph_sdk_sample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.libraries.cloud.telco.subgraph.api.DataPlanIdentifier
import com.google.android.libraries.cloud.telco.subgraph.api.MobileDataPlanClient
import com.google.android.libraries.cloud.telco.subgraph.api.Subgraph
import com.google.android.libraries.cloud.telco.subgraph.api.SubgraphNotificationIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
  private var mobileDataPlanClient: MobileDataPlanClient? = null
  private var cpid: String? = null
  private val ioscope = CoroutineScope(Job() + Dispatchers.IO)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Ask the user for permissions to post notifications. Please visit for more information:
    // https://developer.android.com/training/permissions/requesting#request-permission
    val requestPermissionLauncher =
      registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
          // Permission is granted. Continue the action or workflow in your app.
        } else {
          // Explain to the user that the feature is unavailable because the feature requires a
          // permission that the user has denied.
        }
      }

    when {
      ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED -> {
        // You can use the API that requires the POST_NOTIFICATIONS permission.
      }
      ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        Manifest.permission.POST_NOTIFICATIONS,
      ) -> {
        // In an educational UI, explain to the user why your app requires this
        // permission for a specific feature to behave as expected, and what
        // features are disabled if it's declined.
        // showInContextUI()
      }
      else -> {
        // You can directly ask for the permission.
        // The registered ActivityResultCallback gets the result of this request.
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }

    // Main UI for this Activity:
    findViewById<View>(R.id.api_button).setOnClickListener {
      // Simplified code. The SubgraphNotificationIntent parser will throw
      // IllegalArgumentException. Intents with values are delivered on a fresh Activity
      // onCreate(), or onNewIntent() depending on the activity launchMode setting.
      var i: SubgraphNotificationIntent? = null
      try {
        // Show any notifications that have arrived:
        i = getNotification()
        val payloadDelivered =
          String.format(
            "Intent Action: %s, Content-Type: %s, Payload as String: %s",
            i.action(),
            i.contentType(),
            i.payload(),
          )
        // Do something with the Third Party Notification intent action, content-type, and
        // payload
        Toast.makeText(applicationContext, payloadDelivered, Toast.LENGTH_LONG).show()
      } catch (iae: IllegalArgumentException) {
        iae.printStackTrace()
      } catch (ise: IllegalStateException) {
        ise.printStackTrace()
      }

      // Launch the request for the current CPID, using SubgraphNotificationIntent to get the SimID.
      if (i != null) {
        getClientSetup(applicationContext, i)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    // This is required to receive notifications. Be sure to also enable app's notifictions in
    // Android settings to allow notifications.
    mobileDataPlanClient?.resumeNotifications()
  }

  override fun onPause() {
    super.onPause()
    mobileDataPlanClient?.stopNotifications()
  }

  private fun getClientSetup(context: Context, i: SubgraphNotificationIntent) {
    ioscope.launch {
      // The call getCpid() should run in the background. Post the future results to the UI thread.
      // Consider Guava's ListenableFuture for an onSuccess callback, depending on app design.
      try {
        // Create a MobileDataPlan client with the Subgraph Interface. The SIM ID should be real,
        // from a source like the SubgraphNotificationIntent or SubscriptionManager.
        mobileDataPlanClient = Subgraph.newMobileDataPlanClient(context, i.simId())

        var dataPlanIdentifier: DataPlanIdentifier? = mobileDataPlanClient?.cpid
        if (dataPlanIdentifier == null) {
          toastToUiThread(context, "Did not receive a CPID yet.")
          return@launch
        }
        toastToUiThread(
          context,
          "Cpid received: " +
            dataPlanIdentifier.cpid() +
            ", UpdateTime: " +
            dataPlanIdentifier.updateTime(),
        )
      } catch (ise: IllegalStateException) {
        ise.printStackTrace()
      }
    }
  }

  suspend fun toastToUiThread(context: Context, message: String) {
    withContext(Dispatchers.Main.immediate) {
      Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
  }

  override fun onDestroy() {
    super.onDestroy()
    // Cleanup any Subgraph MobileDataPlanClient SDK resources in use.
    mobileDataPlanClient?.close()
  }

  private fun getNotification(): SubgraphNotificationIntent {
    return Subgraph.fromIntent(intent)
  }
}
