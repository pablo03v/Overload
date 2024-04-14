[![Translation status](https://badges.crowdin.net/overload/localized.svg)](https://crowdin.com/project/overload)

# Overload

Overload is an Android (8.0+) application which allows you to capture timespans by hitting a single button. Pauses are created between the timespans automatically. Set goals to archive better results over time or make sure not to do overtime!


## Contributing

Contributions are always welcome!

Just create issues and pull requests in the dev-branch or help [translating](https://crowdin.com/project/overload) on Crowdin :)


## Features

- create and manage categories with colors, emojis and goals
- create time spans
- automatically creates pauses in between
- delete time spans - on-by-one or all-together
- scroll through days with ease
- backup your data
- import backups


## Feedback

Feedback and suggestions are more than welcome! Please reach out by creating an issue :)


## Installation
[<img src="https://github.com/pabloscloud/Overload/assets/93644977/4f38fb5e-afe1-4473-a9b9-81293fb41270"
      alt='Get it on Obtainium'
      height="60">](http://apps.obtainium.imranr.dev/redirect.html?r=obtainium://app/%7B%22id%22%3A%22cloud.pablos.overload%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fpabloscloud%2FOverload%22%2C%22author%22%3A%22github.com%22%2C%22name%22%3A%22Overload%22%7D)
[<img src="https://github.com/pabloscloud/Overload/assets/93644977/29308442-e470-4e30-a999-49d884b83b0d"
      alt='Get it on GitHub'
      height="60">](https://github.com/pabloscloud/Overload/releases/latest)
[<img src="https://github.com/pabloscloud/Overload/assets/93644977/1e47fdfa-a1cc-4754-a9db-050a124d53b8"
      alt='Get it on Google Play'
      height="60">](https://play.google.com/store/apps/details?id=cloud.pablos.overload)

## Authors

- [@pabloscloud](https://github.com/pabloscloud)

### Kudos

#### Translators

- [@mondstern](https://codeberg.org/mondstern)
- [@Vistaus](https://codeberg.org/Vistaus)
- [@0que](https://codeberg.org/0que)

#### Projects

This project is forked from the jetpack compose sample [Reply](https://github.com/android/compose-samples/tree/main/Reply), which is licensed under the Apache License, Version 2.0.

I used portions of the code of the lovely material you style RSS reader [ReadYou](https://github.com/Ashinch/ReadYou) for the color picker which is licensed under GNU GPLv3. Thanks for the [allowed use](https://github.com/Ashinch/ReadYou/discussions/687) [(archive.org)](https://web.archive.org/web/20240414115828/https://github.com/Ashinch/ReadYou/discussions/687).


## Screenshots

<table>
  <tr>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/1.png" alt="Screenshot 1"></td>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/2.png" alt="Screenshot 2"></td>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/3.png" alt="Screenshot 3"></td>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/4.png" alt="Screenshot 4"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/5.png" alt="Screenshot 1"></td>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/6.png" alt="Screenshot 2"></td>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/7.png" alt="Screenshot 3"></td>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/8.png" alt="Screenshot 4"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/9.png" alt="Screenshot 1"></td>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/10.png" alt="Screenshot 2"></td>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/11.png" alt="Screenshot 3"></td>
    <td><img src="https://github.com/pabloscloud/Overload/raw/main/screenshots/12.png" alt="Screenshot 4"></td>
  </tr>
</table>


## FAQ

### How do I import a backup?
Open your files app, choose the backup file, and locate the share icon or text. Tapping this icon will bring up a menu displaying various apps for sharing. From the list, select Overload to initiate the import. Wait until the import is finished, indicated by a completion message.

### Why does Overload rely on the Systems Sharesheet to import a backup?
Overload utilises the Systems Sharesheet for importing backups instead of requesting broad access to all files on your device. This approach avoids the need to seek permissions that could undermine trust in the project. Moreover, Overload's reliance on the Sharesheet ensures that the app only gains access to the specific file it requires, eliminating the necessity for extensive permissions.


### What are ongoing pauses?
By showing the duration between the last item and the current time you can determine how long you stopped or paused working since then. You can plan how much longer your pause is at any given moment as the duration updates in real time.

### Why can't I delete an ongoing pause?
You cannot delete ongoing pauses as they are only there to indicate a pause will be created once you hit start again. If you will not hit start until the next day it will be gone and will not count against your goal. By deleting items that occurred beforehand, you can hide the pause.
Imagine you're using the app to track your work hours. You take a break, and an ongoing pause is created. If you get ill during the day or decide not to work on the day for any other reason, the ongoing pause will vanish without affecting your work-time goal.

### Why does the app annoy me with a popup to adjust the end?
Sometimes we forget stuff. Sometimes we do stuff across days - like sleeping. Nevertheless when an item is still ongoing from yesterday or days before yesterday, you need to set an end or confirm you want to spread the item across multiple days. The popup will be gone once there are no more ongoing items from the past days ;)
