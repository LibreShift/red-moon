## Low Maintenance Mode

Red Moon is currently maintained to the level of "works on my device". Pull Requests are still accepted and if you're interested in becoming a co-maintainer, I'm willing to spend time walking you through the code base. [**Read More**](https://github.com/LibreShift/red-moon/issues/281)

---

<img alt="Red Moon Icon" align="right" height="256" src="https://lut.im/3IqLwsAZWH/piFLRMOgNLWmiqB8.png">

# Red Moon<br/>[![Translation status](https://hosted.weblate.org/widgets/red-moon/-/svg-badge.svg)](https://hosted.weblate.org/engage/red-moon/?utm_source=widget) [![License](https://img.shields.io/badge/license-GPL--3.0%2B-bd0000.svg)](COPYING "License: GPL-3.0-or-later") [![Commits (since latest release)](https://img.shields.io/github/commits-since/LibreShift/red-moon/latest.svg "Commits since latest release")](https://github.com/LibreShift/red-moon/releases/latest)

Blue light may suppress the production of melatonin, the sleep hormone. Red Moon
filters out blue light and dims your screen below the normal minimum, so you can
use your phone comfortably at night.

* Schedule Red Moon to run from sunset to sunrise, or at custom times.
* Use the default color profiles, or set custom color, intensity, and dim levels.
* Automatically pause in apps secured against overlays, or those you choose.
* Quickly start, stop, and switch profiles via notification, tile (Android 7.0+), or widget.

[<img src="https://f-droid.org/badge/get-it-on.png"
      alt="Get it on F-Droid"
      height="80">](https://f-droid.org/repository/browse/?fdid=com.jmstudios.redmoon)
[<img src="art/direct-apk-download.png"
      alt="Direct download"
      height="80">](https://github.com/raatmarien/red-moon/releases)

## Do your part — participate in the community

**Communities keep software projects alive.** Without them, projects usually fade
into obscurity when the primary developer loses interest or becomes busy in
other parts of their life. That's where you come in! You can:

- Read through the [issues] and give a <g-emoji alias="+1" class="emoji" fallback-src="https://assets-cdn.github.com/images/icons/emoji/unicode/1f44d.png" ios-version="6.0">👍</g-emoji> to the ones you care about most.
- Open [new issues] with feedback, feature requests, or bug reports.
- Help translate using [Weblate]. 
- Come chat at [![Matrix](art/badge_matrix.svg)](https://matrix.to/#/#redmoon:matrix.org),
    [![IRC](art/badge_irc.svg)](https://kiwiirc.com/client/irc.freenode.net/#redmoon),
    or [![Gitter](https://img.shields.io/gitter/room/LibreShift/red-moon.svg)](https://gitter.im/LibreShift/red-moon)

<a href="https://hosted.weblate.org/engage/red-moon/?utm_source=widget">
<img src="https://hosted.weblate.org/widgets/red-moon/-/horizontal-auto.svg" alt="Translation status" />
</a>



## Screenshots

Screenshots are slightly out of date.

<img src="https://github.com/LibreShift/red-moon/blob/master/app/src/main/play/en-US/listing/phoneScreenshots/1.png" width="189" height="336" /> <img src="https://github.com/LibreShift/red-moon/blob/master/app/src/main/play/en-US/listing/phoneScreenshots/2.png" width="189" height="336" /> 
<img src="https://github.com/LibreShift/red-moon/blob/master/app/src/main/play/en-US/listing/phoneScreenshots/6.png" width="189" height="336" />
<img src="https://github.com/LibreShift/red-moon/blob/master/app/src/main/play/en-US/listing/phoneScreenshots/7.png" width="189" height="336" />

## Privacy Policy

Red Moon collects data **only when it is necessary for functionality**. These features are all **disabled by default**. If you enable them, the data **never leaves your device**. Specifically:

- If you use the "Sunrise to Sunset" feature, Red Moon will store your location (lattitude & longitude, you can see them in the preference). Used in order to calculate sunrise and sunset times.
- If you use the excluded apps feature, then while Red Moon is running, it will monitor what app is in the foreground and which screen of that app is open (currently it polls once per second). This is checked against the list of excluded apps to determine whether Red Moon should pause, and then is immediately discarded.
    - If you choose to exclude an app, then it's stored to the list of excluded apps, until you remove it.
    - In the debug version of the app, these are logged to the Android system log ("logcat"). These logs are not accessible to other apps on the device. On most versions of android, they're cleared after 1-7 days.

## Development [![Build Status](https://travis-ci.org/LibreShift/red-moon.svg?branch=master)](https://travis-ci.org/LibreShift/red-moon)

### Building

To build the app on GNU+Linux, clone the repository, then, from the root directory, run

```
./gradlew build
```

To install the app on a connected device or running emulator, run

```
./gradlew installDebug
```

### Pull requests

- We're happy to answer questions if you reach out via an issue, the chat room(s), or email.
- If your change makes the UI more complicated, we suggest checking if we're
    interested before you implement it.
- Please keep code and translations in separate PRs.

### Style

**Prioritize legibility over dogmatism.** That said, consistency is nice, so
here's a short list of what I've been doing. 

- 100 characters per line; 80 if you can.
- Indent 4 spaces, or 8 spaces if the previous line ends with `=`.
- `CONSTANTS` and `ENUMS` use all caps, `variableNames` use camelCase.
- Form suggests function: Group and align similar actions, and *don't* align dissimilar ones, even if you could.
- Good comments explain *why* something is done; if you find yourself describing *what* the code does, consider:
    - Refactoring into smaller functions with descriptive names
    - Converting comments to logs. Code that requires comments probably also requires good logs to debug.
- Always use curly braces with `if` (except short val/var one-liners: `val x = if (a) b else c`)

### License

[<img src="https://www.gnu.org/graphics/gplv3-127x51.png"
      align="right"
      alt="GNU GPLv3 Image">](http://www.gnu.org/licenses/gpl-3.0.en.html)

*Red Moon* is licensed under the [GNU General Public License version 3] or (at your option) any later version by [the contributors]. It is a derivative of *[Shades]* by [Chris Nguyen], used under the [MIT License].

All used artwork is released into the public domain. Some of the icons use clip art from [openclipart.org], which are all released in the public domain, namely:

* https://openclipart.org/detail/121903/full-moon
* https://openclipart.org/detail/219211/option-button-symbol-minimal-svg-markup
* https://openclipart.org/detail/20806/wolf-head-howl-1
* https://openclipart.org/detail/213998/nexus-5-flat
* https://openclipart.org/detail/192689/press-button

---

\* Google Play and the Google Play logo are trademarks of Google Inc.

[issues]: https://github.com/raatmarien/red-moon/issues
[new issues]: https://github.com/raatmarien/red-moon/issues/new
[Weblate]: https://hosted.weblate.org/projects/red-moon/strings/
[labels]: https://github.com/LibreShift/red-moon/labels
[Shades]: https://github.com/cngu/shades
[Chris Nguyen]: https://github.com/cngu
[MIT License]: https://github.com/cngu/shades/blob/e240edc1df3e6dd319cd475a739570ff8367d7f8/LICENSE
[GNU General Public License version 3]: https://www.gnu.org/licenses/gpl-3.0.html
[the contributors]: https://github.com/raatmarien/red-moon/graphs/contributors
[openclipart.org]: https://openclipart.org/
