# Android Chat Tutorial Sample

This repository allows you to check the result after completing each step described in the [Android Chat Tutorial](https://getstream.io/tutorials/android-chat/#kotlin). It contains samples written in both **Kotlin** (_samplekotlin_ module) and **Java** (_samplejava_ module). For more Android Chat examples, see the [Github repo for UX/Views and its Sample app](https://github.com/GetStream/stream-chat-android).

The project is pre-configured with a shared [Stream](https://getstream.io) account for testing purposes. You can learn more about Stream Chat [here](https://getstream.io/chat/), and then sign up for an account and obtain your own keys [here](https://getstream.io/chat/trial).

## Quick start

1. Clone the repository
2. Run the _samplekotlin_ or _samplejava_ configuration

## Details

The sample apps consist of two screens:

* `MainActivity`: Shows the list of available channels.
* `ChannelActivity`: Shows the selected channel view, which includes the header, message list, and message input view.

Each module contains multiple `ChannelActivity` implementations, which correspond to the steps of the tutorial. You can easily swap them by changing the `setOnChannelClickListener` located in `MainActivity`:

```kotlin
channelsView.setOnChannelClickListener { channel ->
    // open the channel activity
    startActivity(ChannelActivity.newIntent(this, channel))
}
```

Currently, you can choose from four different `ChannelActivity` implementations:
<!-- TODO: Add links when the new version of the Android Tutorial is published -->
* `ChannelActivity` - a basic _Message List_ implementation
* `ChannelActivity2` - includes a new _MessageListView_ style and custom attachment type
* `ChannelActivity3` - includes a custom _Channel Header_ component created with the [LiveData&Offline](https://github.com/GetStream/stream-chat-android-livedata) library
* `ChannelActivity4` - includes a custom _Channel Header_ component created with the [Low-Level Client](https://github.com/GetStream/stream-chat-android-client) library
