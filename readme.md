# Music-Bot

After death of groovy and no good bot for replacing it(I 
don't like Hydra), I decided to make a bot of my own
using `JDA` and Java as used in Groovy ~~also groovy used 
Groovy programing language~~.

## Extra information
  - Lavalink was used as audio receiver and player
  - Hikari was used to help with database connection

## Commands
  Currently, these are the available commands with default prefix of `!`
  - Music
    - Join: <span style="opacity: 0.5">Joins your current voice channel</span>
    - Leave: <span style="opacity: 0.5">Leaves your voice channel</span>
    - Play <Song name|url>: <span style="opacity: 0.5">Plays the specified song, resumes the player if paused</span>
    - Playnow <Song name|url>: <span style="opacity: 0.5">Plays a song immediately and overrides the currently playing song</span>
    - Stop: <span style="opacity: 0.5">Stops the song and clears the queue</span>
    - Pause: <span style="opacity: 0.5">Pauses the player</span>
    - Queue: <span style="opacity: 0.5">Shows the queued up songs</span>
    - Skip: <span style="opacity: 0.5">Skips current song</span>
    - Previous: <span style="opacity: 0.5">Returns to previous song</span>
    - Jump \<Track Index>: <span style="opacity: 0.5">Skips to specified index</span>
    - Loop [queue|track|off]: <span style="opacity: 0.5">Loops the queue, track or turns off looping</span>
    - Shuffle: <span style="opacity: 0.5">If on shuffles through the queue</span>
    - Nowplaying: <span style="opacity: 0.5">Shows the currently playing song</span>
  - Playlist
    - Playplaylist \<Playlist Name>: <span style="opacity: 0.5">Plays specified playlist</span>
    - Saveplaylist \<Playlist Name>: <span style="opacity: 0.5">Saves specified playlist</span>
    - Listplaylist \<Playlist Name>: <span style="opacity: 0.5">Shows saved playlists</span>
    - Deleteplaylist \<Playlist Name>: <span style="opacity: 0.5">Deletes specified playlist</span>
  - Settings
    - AddPremium \<User>: <span style="opacity: 0.5">Add user to premium to be able to access premium commands</span>
    - Prefix \<Custom Prefix>: <span style="opacity: 0.5">Changes prefix to the specified prefix</span>
  - Miscellaneous
    - Invite: <span style="opacity: 0.5">An invite link to invite the bot by</span>
    - Ping: <span style="opacity: 0.5">Current latency</span>

## To-do
  - [ ] Indexing through the queue with buttons
  - [ ] Change embed color based on role color
  - [ ] Extracting checks into one global class

## Installation
  1. Clone repo: `git clone https://github.com/Arya-Programmer/JavaMusicBot`
  2. Add `.env` file in the root of the project
  3. In `.env` file initialise three variables: `BOT_TOKEN`, `OWNER_ID`, `PREFIX`
  4. Cross fingers and Run it

## License
Copyright (C) 2021-2021 Arya K. O.

>This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
>  
>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
>
>See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/

[The full license can be found here.](https://github.com/Arya-Programmer/JavaMusicBot/blob/master/LICENSE)