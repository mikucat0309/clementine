# Clementine

Minimal one-time Beanfun login app, written in Kotlin.

## Screenshots

<div>

<img src="./images/gama_login.png" width="30%" />
<img src="./images/beanfun_login.png" width="30%" />

</div>

## Highlights

- 💪 Support login via link or QRCodes (NOT recommended[^1]).
- ✅ Simple: Just for login.
- 🚫 Privacy: No Google or other trackings.
- 💖 Open Source: Talk is cheap, show me the code

## Installation

Support Android 13+[^2]

Download APK from [GitHub releases](https://github.com/mikucat0309/clementine/releases)

## Roadmap

- Localization
- Improve QRCode scanner performance[^1], or remove this function

## Non-Goals

- Full replacement of GamaPlay.
- Upload to Google Play Store.
- Support platforms other than Android.

## License

- [App Icon](app/src/main/res/drawable/ic_launcher_foreground.xml) is based
  on [Fluent Emoji](https://github.com/microsoft/fluentui-emoji), licensed under MIT License
- Others are licensed under [AGPLv3 License](LICENSE.txt)

[^1]: App built-in QRCode scanner performs poorly, we recommend using other QRCode scanner and login
via links.
[^2]: Lowest Android version that receiving security updates.
