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

import android.content.Intent
import android.os.Bundle
import android.os.RemoteException
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.cloud.telco.subgraph.api.MobileDataPlanClient
import com.google.android.libraries.cloud.telco.subgraph.api.Subgraph
import com.google.android.libraries.cloud.telco.subgraph.api.SubgraphNotificationIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
  private var mobileDataPlanClient: MobileDataPlanClient? = null
  private var cpid: String? = null
  val ioscope = CoroutineScope(Job() + Dispatchers.IO)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<View>(R.id.api_button).setOnClickListener {
      // Simplified code. The SubgraphNotificationIntent parser will throw
      // IllegalArgumentException. Intents with values are delivered on a fresh Activity
      // onCreate(), or onNewIntent() depending on the activity launchMode setting.
      val i: SubgraphNotificationIntent = getNotification()
      val payloadDelivered =
        String.format(
          "Intent Action: %s, Content-Type: %s, Payload as String: %s",
          i.action(),
          i.contentType(),
          i.payload()
        )
      // Do something with the Third Party Notification intent action, content-type, and
      // payload
      Toast.makeText(applicationContext, payloadDelivered, Toast.LENGTH_LONG).show()

      // Create a MobileDataPlan client with a helper function:
      mobileDataPlanClient =
        i.newMobileDataPlanClient(applicationContext)

      // Launch the request for the CPID:
      getClientSetup(i)
    }
  }

  private fun getClientSetup(i: SubgraphNotificationIntent) {
    ioscope.launch {
      // The remote call getCpid() should run in the background.
      try {
        cpid = mobileDataPlanClient?.cpid
      } catch (ise: IllegalStateException) {
        ise.printStackTrace()
      } catch (re: RemoteException) {
        re.printStackTrace()
      }
    }
  }

  override fun onNewIntent(intent: Intent?) {
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
