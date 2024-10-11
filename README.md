# neXtgen 🌍📱

## [Contributors guide 🔗](https://github.com/Vishwajith-Shettigar/NextGen/blob/master/CONTRIBUTORS.md)



## Overview 🌟
Interact with people within a 100-meter radius! Get real-time locations of nearby folks on Google Maps and chat or have a video call with them. If the conversation wasn’t up to par, you can give them a solid 0-star rating. 😄👎

## Features 🚀
- **Real-time Location:** Spy on the real-time whereabouts of nearby users on Google Maps. 🕵️‍♂️🗺️
- **Chat:** Send some text magic to nearby users. 💬🧙‍♂️
- **Video Call:** Face-to-face chats with nearby users using the power of WebRTC. 📹👥
- **Rating System:** Rate your chat experience. If it was a dud, slap them with a 0-star review. 🌟😢

## Technologies Used 🛠️
- **Android:** The app is tailor-made for Android devices. 🤖📱
- **Firebase:** Powering up authentication, real-time database awesomeness, and storage ninja skills. 🔥🧙‍♀️
- **WebRTC:** Real-time video calling sorcery. Let the calls flow like magic! 🎩🔮
- **Google Maps API:** Pinpointing the exact coordinates of your newfound friends. 📍🗺️
- **Kotlin:** The language of choice for Android wizardry. It’s like casting spells with code! 🧙‍♂️✨

## Screenshots 📸

<table>
  <tr>
    <td><img src="https://github.com/Vishwajith-Shettigar/NextGen/assets/76042077/67e113b9-71f8-4a41-8f12-58f4a70869ea" alt="Real-time Location" style="width: 0;height:600px"/></td>
    <td><img src="https://github.com/Vishwajith-Shettigar/NextGen/assets/76042077/33567a7b-aa5e-4c45-a25b-d5007b9da686" alt="Chat Interface" style="width: 0;height:600px""/></td>
  </tr>
  <tr>
    <td><img src="https://github.com/Vishwajith-Shettigar/NextGen/assets/76042077/e98f0919-9547-461f-8437-0d2a0cd81046" alt="Real-time Location" style="width: 0;height:600px"/></td>
    <td><img src="https://github.com/Vishwajith-Shettigar/NextGen/assets/76042077/5b61abcb-4eb9-4d48-a204-cd1427d321d0" alt="Chat Interface" style="width: 0;height:600px""/></td>
  </tr>
  <tr>
    <td><img src="https://github.com/Vishwajith-Shettigar/NextGen/assets/76042077/493ea38d-8fa4-4596-ade8-937848672dc4" alt="Real-time Location" style="width: 0;height:600px"/></td>
    <td><img src="https://github.com/Vishwajith-Shettigar/NextGen/assets/76042077/278f6e58-65fe-41e2-b6ca-ec496e8410d1" alt="Chat Interface" style="width: 0;height:600px""/></td>
  </tr>
</table>

---

Time to mingle, chat, and rate! Have fun exploring the world around you. 🌍🎉

## Troubleshooting 🤔

If you encounter any issues while using the app, this section will help you find quick solutions to common problems.

### 1. App Crashes on Device Configuration Changes
**Issue:** The app crashes when the device orientation or configuration changes.  
**Solution:**  
- Ensure that `android:configChanges="orientation|screenSize"` is added in the AndroidManifest.xml for the affected activities.
- Consider using ViewModel or handling configuration changes appropriately in the activity lifecycle.

### 2. User's Location is Not Detected
**Issue:** The app is not able to detect the user's location.  
**Solution:**  
- Make sure that location permissions are granted in the device settings.
- If the location is off, prompt the user to enable location services.

### 3. Profile Pictures Still Visible Despite Disabling
**Issue:** Profile pictures are still visible on the map or chat screen, even after setting them to be disabled.  
**Solution:**  
- Check the API response to ensure that the `profilePictureVisible` flag is properly updated.
- Ensure that the visibility logic is correctly handled in the frontend component.

### 4. Red Stroke Not Showing in Default Image on Map
**Issue:** Red stroke does not appear in the default image on the map.  
**Solution:**  
- Check if the image loading logic is correctly implemented in the Map component.
- Ensure that the red stroke is added as an overlay when the map is rendered.

### 5. Online Status Not Working Properly
**Issue:** The user's online status is not updating or displaying correctly.  
**Solution:**  
- Verify that the status updates are being sent from the backend in real-time.
- Ensure that the app is subscribed to the correct event listeners for online status updates.

### 6. Debugging Tips
- Check the logs for any specific error messages or stack traces.
- Try running the app in **debug mode** to identify where the issue occurs.
- If an issue persists, feel free to open a new issue on GitHub or seek help from the community.


## TODO 📝
- [ ] Enhance UX of video call feature.
- [ ] Notification UI.
- [ ] Improve UI/UX.
- [ ] Fix crashing when user swtiches to light/dark mode.

