# Android Chat Tutorial Sample

Android Chat Tutorial Sample repository allows you to check the result after completing each step described in the [Android Chat Tutorial](https://getstream.io/tutorials/android-chat/#kotlin). It contains samples written in both **Kotlin** (_samplekotlin_ module) and **Java** (_samplejava_ module).
For more Android Chat examples, see the [Github repo for UX/Views and Sample app](https://github.com/GetStream/stream-chat-android).

The project is pre-configured with a shared [Stream](https://getstream.io) account for testing purposes. You can learn more about Stream Chat, sign up for an account, and obtain your own keys [Chat](https://getstream.io/chat/).

## Quick start

1. Clone the repository
2. Run _samplekotlin_ or _samplejava_ configuration

## Description
Sample apps consist of two screens:
* _MainActivity_ - responsible for showing list of available channels
* _ChannelActivity*_ - responsible for showing selected channel view which includes: header, message list, and message input view

Each module contains multiple _ChannelActivity_ implementation which corresponds to different Android Chat Tutorial steps.
You can easily swap them by changing `setOnChannelClickListener` located in _MainActivity_:
```kotlin
        channelsView.setOnChannelClickListener { channel ->
            // open the channel activity
            startActivity(ChannelActivity.newIntent(this, channel))
        }
```
Currently, you can choose from four different _ChannelActivity_ implementations:
<!-- TODO: Add links when a new version of Android Tutorial is published -->
* _ChannelActivity_ - corresponds to basic _Message List_ implementation
* _ChannelActivity2_ - includes new _MessageListView_ style and custom attachment type
* _ChannelActivity3_ - includes custom _Channel Header _ component created with [LiveData&Offline](https://github.com/GetStream/stream-chat-android-livedata)
* _ChannelActivity4_ - includes custom _Channel Header_ component created with [Low-Level Client](https://github.com/GetStream/stream-chat-android-client)
