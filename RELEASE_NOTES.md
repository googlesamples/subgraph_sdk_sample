Subgraph SDK Sample Release Notes
=================================

Version 0.5.4
-------------
- Fixed a bug routing notifications to Activities with the category DEFAULT.
  Updated sample AndroidManifest.xml to indicate mimeType is required if using
  DEFAULT.
- Additional notes on how to ask the user for POST_NOTIFICATIONS permissions.
- Feature was added for pausing and resuming Notifications. This feature is
  still subject to the OS settings value.

Version 0.5.2
-------------
- Internal SDK Intent action rename. Clients should upgrade to continue
  to receive notification messages.

Version 0.5.1
-------------
- SDK now receives Carrier Plan ID updates upon change. New return type for
  getCpid().

Version 0.5.0
-------------
- Add an ability to create a MobileDataPlanClient with an API Call to get
  a server sent Carrier Plan ID (Cpid).