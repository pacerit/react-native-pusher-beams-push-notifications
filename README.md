# react-native-pusher-beams-push-notifications

*This package is forked from [b8ne/react-native-pusher-push-notifications](https://github.com/b8ne/react-native-pusher-push-notifications).
The goal is to implement features that were not implemented in the original package.*

Manage pusher interest subscriptions from within React Native JS

More information about Pusher Beams and their Swift library, `push-notifications-swift`, can be found on their [Github repo](https://github.com/pusher/push-notifications-swift).

## Differences from original package
* Implemented *getSubscriptions* method for *iOS*
* Implemented *setSubscriptions* method for *Android*
* Updates deprecated functions in PusherWrapper for *Android*
* Add function that check if Pusher instance for *Android* is set before function call
## Requirements
**This branch is only compatible with React Native >0.60.x**

## Getting started

`$ npm install react-native-pusher-beams-push-notifications --save`

or yarn

`$ yarn add react-native-pusher-beams-push-notifications`

## Automatic installation

React native link will install the pods required for this to work automatically.

### Install via yarn/npm
```
yarn add react-native-pusher-beams-push-notifications
```

### Additional, Manual Steps Required

#### iOS


** DO NOT follow the pusher.com push notification docs that detail modifying the AppDelegate.h/m files! - this package takes care of most of the steps for you**

1. Open ios/PodFile and update `platform :ios, '9.0'` to `platform :ios, '10.0'`
1.  Open `AppDelegate.m` and add:

```
    // Add this at the top of AppDelegate.m
    #import <RNPusherPushNotifications.h>

    // Add the following as a new methods to AppDelegate.m
    - (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
      NSLog(@"Registered for remote with token: %@", deviceToken);
      [[RNPusherPushNotifications alloc] setDeviceToken:deviceToken];
    }

    - (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
      [[RNPusherPushNotifications alloc] handleNotification:userInfo];
    }

    -(void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
      NSLog(@"Remote notification support is unavailable due to error: %@", error.localizedDescription);
    }
```

##### Possible Issues

- `linker command failed with exit code 1 (use -v to see invocation)`
    1. Open ios/YourAppName.xcodeproj in XCode
    2. Right click on Your App Name in the Project Navigator on the left, and click "New File…"
    3. Create a single empty Swift file to the project (make sure that Your App Name target is selected when adding)
    4. when Xcode asks, press Create Bridging Header and do not remove Swift file then. re-run your build.


#### Android

Refer to https://docs.pusher.com/beams/reference/android for up-to-date Pusher Beams installation 
instructions (summarized below):

1. Update `android/build.gradle`

```gradle
buildscript {
    // ...
    dependencies {
        // ...
        // Add this line
        classpath('com.google.gms:google-services:4.3.0')
    }
}    
```

2. Add this to `android/app/build.gradle`:

```gradle

// add to plugins
plugins {
    ...
    id('com.google.gms.google-services')
}

dependencies {
    ...
    implementation 'com.google.firebase:firebase-messaging:20.0.0'
    implementation project(':react-native-pusher-push-notifications')
    implementation 'com.pusher:push-notifications-android:1.4.4'
}
```

3. Update `android/settings.gradle`
```
apply from: file("../node_modules/@react-native-community/cli-platform-android/native_modules.gradle"); applyNativeModulesSettingsGradle(settings)
...
include ':react-native-pusher-push-notifications'
project(':react-native-pusher-push-notifications').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-pusher-push-notifications/android')
...
include ':app'
```

4. Set up `android/app/google-services.json`

    This file is generated via [Google Firebase](https://console.firebase.google.com) console when creating a new app. Setup your app there and download the file.
    
    Pusher Beams requires a FCM secret, this is also found under Cloud Messaging in Google Firebase.

5. Add react-native.config.js to root of react-native directory
```
module.exports = {
    dependencies: {
        "react-native-pusher-push-notifications": {
            platforms: {
                android: null // this skips autolink for android
            }
        }
    }
};

```

6. Add `RNPusherPushNotificationsPackage` to `MainApplication.java`:
```java
import com.b8ne.RNPusherPushNotifications.RNPusherPushNotificationsPackage;
// ...
@Override
    protected List<ReactPackage> getPackages() {
      @SuppressWarnings("UnnecessaryLocalVariable")
      List<ReactPackage> packages = new PackageList(this).getPackages();
      // Packages that cannot be autolinked yet can be added manually here, for example:
      // packages.add(new MyReactNativePackage());
      packages.add(new RNPusherPushNotificationsPackage()); // << Make sure this line is here 
      return packages;
    }
```

#### _suggested, first time_ Clean caches and install all dependencies
```
\\ suggested commands; tested on osx
\\ nuke node_modules
$ rm -rf ./node_modules && yarn install
\\ nuke pods
$ rm -r ~/Library/Developer/Xcode/DerivedData ; cd ./ios && pod deintegrate && pod install ; cd ..
\\ nuke gradle
$ cd ./android && ./gradlew clean && ./gradlew --stop; cd ..
```

## Implementation

Example implementation

```js
import {Platform} from "react-native";
import {PUSHER_BEAMS_INSTANCE_ID} from "react-native-dotenv";
import RNPusherPushNotifications from "react-native-pusher-beams-push-notifications";

// Get your interest
const interests = [
    "debug-test",
    "updates",
];

const init = () => {
    RNPusherPushNotifications.setInstanceId(PUSHER_BEAMS_INSTANCE_ID);

    // Init interests after registration
    RNPusherPushNotifications.on('registered', () => {
       subscribe(interests);
    });

    RNPusherPushNotifications.on("notification", handleNotification);
};

const subscribe = interests => {
    console.log(`Subscribing to "${interests}"`);
    RNPusherPushNotifications.setSubscriptions(
        interests,
        (statusCode, response) => {
            console.error(statusCode, response);
        },
        () => {
            console.log(`CALLBACK: Subscribed to ${interests}`);
        }
    );
};

const handleNotification = notification => {
    console.log(notification);
    if (Platform.OS === "ios") {
        console.log("CALLBACK: handleNotification (ios)");
    } else {
        console.log("CALLBACK: handleNotification (android)");
    }
};
```

# Sample Payload

`POST` to `https://{pusher_instance_id}.pushnotifications.pusher.com/publish_api/v1/instances/{pusher_instance_id}/publishes` with headers:
```headers
    Content-Type: application/json
    Authorization: Bearer {pusher_secret_key}
```
```json
{
  "interests": [
  	"debug-donuts"
  ],
  "apns": {
    "aps": {
    	"alert" : {
	      "title": "iOS Notification",
	      "body": "Hello ios user",
    	},
    "badge": 12
    }
  },
  "fcm": {
    "notification": {
      "title": "Android notification",
      "body": "Hello android user"
    }
  }
}

```

## Increment Badge number

The APS data sent to Pusher Beams and then to Apple have an option `badge`. This will update your apps badge counter to the current number. If you send 1, the badge will show 1. This means you need to handle notification read status in your backend and when pushing update to the current number.

By adding `incrementBadge` you can increment the badge number without having to deal with your backend.

```
{
  aps: {
    data: {
      incrementBadge: true
    }
  }
}
```
