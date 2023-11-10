## Political Preparedness

Political Preparedness is the last project of the Android Kotlin Developer nanodegree provided by Udacity. It is the capstone project of the whole course, the last step for achieving the nanodegree. The project is focused on listing the upcoming elections, with the possibility to follow them, and the list of representatives for US political elections.

The project demonstrates the ability to exploit all the aspects faced in the Android Kotlin Developer course, especially

* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Data Binding](https://developer.android.com/topic/libraries/data-binding/) with binding adapters
* [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) with the SafeArgs plugin for parameter passing between fragments

for what concerns the layout and the user navigation, and

* [Retrofit](https://square.github.io/retrofit/)
* [Moshi](https://github.com/square/moshi), together with custom adapters for particular JSON fields
* [Glide](https://bumptech.github.io/glide/) to load and cache images by URL
* [Room](https://developer.android.com/training/data-storage/room)
  
for the development of the source layer, where data is retrieved from external sources.

The project consists of four screens, including a welcome screen, when the user launches the app, two screens containing election data, and one screen containing election representatives data.

Visit the [Wiki](https://github.com/PaoloCaldera/politicalPreparedness/wiki) to see the application screens.


## Getting Started

To clone the repository, use the command
```
$ git clone https://github.com/PaoloCaldera/locationReminder.git
```
or the `Get from VCS` option inside Android Studio by copying the link above.

Before running the application, a further step has to be completed, that is to Create a Google Cloud project and add an API key, following the [Google](https://developers.google.com/maps/documentation/android-sdk/get-api-key) indications.

To retrieve the SHA-1 of the application, which is going to be used both for creating the API key and the authentication methods, execute in the Android Studio terminal the following line:
```
$ keytool -list -v -alias androiddebugkey -keystore <USER_FOLDER>\.android\debug.keystore
```
Then, run the application on an Android device or emulator. The application is compiled with API 33, thus use a device or emulator supporting such API version. For complete usage of the application, be sure that the device or emulator is connected to a Wi-Fi network.


## License

Loading Status is a public project that can be downloaded and modified under the terms and conditions of the [MIT License](LICENSE).
