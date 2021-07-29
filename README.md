# Android Chat Tutorial Sample

This repository allows you to check the result after completing each step described in the [Android Chat Tutorial](https://getstream.io/tutorials/android-chat/#kotlin). It contains samples written in both **Kotlin** (_samplekotlin_ module) and **Java** (_samplejava_ module). For more Android Chat examples, see the [Github repo for the SDK](https://github.com/GetStream/stream-chat-android) and the [UI Components sample app](https://github.com/GetStream/stream-chat-android/tree/main/stream-chat-android-ui-components-sample) in it.

> Already all-in on Jetpack Compose? Check out the [tutorial repo of our Compose UI Components](https://github.com/GetStream/compose-chat-tutorial) instead.

The project is pre-configured with a shared [Stream](https://getstream.io) account for testing purposes. You can learn more about Stream Chat [here](https://getstream.io/chat/), and then sign up for an account and obtain your own keys [here](https://getstream.io/chat/trial).

## Quick start

1. Clone the repository
2. Open the project in Android Studio
3. Run the _samplekotlin_ or _samplejava_ configuration
4. Make sure to check the [Details](#details) section below for customizations

## Details

The sample apps consist of two screens:

* `MainActivity`: Shows the list of available channels.
* `ChannelActivity`: Shows the selected channel view, which includes the header, message list, and message input view.

Each module contains multiple `ChannelActivity` implementations, which correspond to the steps of the tutorial. You can easily swap them by changing the `setOnChannelClickListener` located in `MainActivity`:

```kotlin
channelListView.setOnChannelClickListener { channel ->
    // open the channel activity
    startActivity(ChannelActivity.newIntent(this, channel))
}
```

Currently, you can choose from four different `ChannelActivity` implementations:

* `ChannelActivity` - a basic _Message List_ implementation
* `ChannelActivity2` - includes a new _MessageListView_ style and custom attachment type
* `ChannelActivity4` - includes a custom _Typing Header_ component created with the [Low-Level Client](https://github.com/GetStream/stream-chat-android/tree/main/stream-chat-android-client) library
