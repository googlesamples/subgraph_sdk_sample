Subgraph SDK Sample
===================


Introduction
------------

This is the initial Subgraph SDK Sample that will receive an Intent from a
NotifyUser call from the carrier interface. It is still in development.


Getting started
---------------

The Sample uses the google() repository so it can load the current version of
the maven artifact, referenced in the app's build.gradle file.

The small kotlin sample on how to reference and use the api is inside:

java/com/google/samples/quickstart/subgraph_sdk_sample/MainActivity.kt (Kotlin)

Java usage will be very similar.

The carrier will send a NotifyUser RPC, which will direct the message to the SDK
that will then launch the destination_package indicated by the RPC call.

This package should be this following target, or any other valid target:

com.google.samples.quickstart.subgraph_sdk_sample.MainActivity

It will have a data payload that was sent by the NotifyUser RPC. Details on how
to send the RPC will be provided at a later time. Once the activity starts,

  Subgraph.fromIntent(intent)

is a helper function that will parse out the contents for use in the app.

This project uses the Gradle build system. To build this project, use the
`gradlew build` command or use "Import Project" in Android Studio.

For more resources on learning Android development, visit the
[Developer Guides](https://developer.android.com/guide/) at
[developer.android.com](https://developer.android.com).

### Required setup

The subgraph_sdk_sample will have this setup already. Subgraph SDK will be in
the google() repository:

    repositories {
        google()
        // ...
    }

In your app directory's build.gradle, add to the dependencies this line:

dependencies {
  implementation 'com.google.android.libraries.cloud.telco.subgraph:cloud_telco_subgraph:0.0.2'
}

If the app does not already have an OSS plugin to help with Third Party
Licenses generation and display, please go here:

https://developers.google.com/android/guides/opensource

Support
-------

Please report issues with this sample in this project's issues page:
https://github.com/googlesamples/subgraph-sdk-sample/issues

License
-------

```
Copyright 2022 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
```
