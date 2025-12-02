# BoomField
- A Hex-Based Minesweeper Clone for Java

## Overview
BoomField is a custom-built Minesweeper-style puzzle game featuring a 
hexagonal grid, dynamic difficulty, custom artwork, a variety of sound 
effects, and a clean Java Swing interface. The project demonstrates 
event-driven GUI design, game-state management, object-oriented architecture,
and resource packaging within an executable JAR file.

## Features
- Hexagonal grid using pointy-top orientation
- Randomized mine placement
- Neighbor detection
- Toggling functions (for marking potential mines)
- Timer that does not start until user has selected their first move and 
  stops when the game ends
- Difficulty selection and restart button to allow varied and continuous play
- Display counter for number of mines hidden in current field (when changing 
  difficulty, the new mine count will not be updated until the "Start New 
  Game" button has been selected)
- Custom graphics and tile icons
- Background music
- Sound clips for win, loss, digging, flagging, and menu presses (needed to 
  be converted to 16-bit depths in order to be playable). Gain control has 
  also been used to adjust volumes for individual sound effects
- Fully bundled JAR with image/audio resources

## How to Run
1. Install Java (Version 17 or higher).
2. Download BoomField.jar.
3. Run the game using a Terminal with command "java -jar BoomField.jar" or 
   double-click on .jar file (on supported systems).
4. *All sources & resources can be viewed by renaming BoomField.jar to 
   BoomField.zip*

## How to Play
- Flag all mines & clear the field to win
- Left-click: Dig into a cell
- Right-click: Mark/Unmark cells as potential buried mines
- Refresh button: Starts a new game with selected difficulty
- Difficulty: Select between 27 (Easy), 43 (Medium), or 64 (Hard) mines

## Design Summary
- GUI classes:
  - BackgroundPanel - background artwork
  - BoardPanel - game board/cells/hex-work
  - BoomFieldFrame - JFrame that ties gui together with logic layer
  - InfoPanel - contains difficulty selector, restart button, and displays 
    the timer, number of mines, and flags used
  - MusicPlayer - handles looping background music
  - SoundFX - handles the different sounds for actions and game win/loss
- Logic classes:
  - BoomFieldLogic - main logic layer, board initialization, mine placement, 
    neighbor detection, win/loss detection, behavior for cells
  - MainLauncher - this class contains the main used by the JAR executable 
    to start the game
- Resources - resource path for classloader. Includes:
  - BOOMFIELD.png - main image used for background art
  - Audio directory - contains all wav files used for music and sound effects
  - Icons directory - contains all jpg files used for cell artwork(images,
- Interfaces:
  - GUIToLogic - Implemented by BoomFieldLogic to get info from gui layer
  - LogicToGUI - Implemented by BoomFieldFrame to get info from logic layer

## Issues/Missing Features
Resizing of the game/frame has been disabled due to lack of methods that 
would ensure proper scaling that maintained the aspect ratio of all gui 
elements.

## Credits
- Custom background and tile art made with NightCafe & the MS Paint program.
- Background music: "Space Ambient Cinematic" by DELOSound via 
  PixaBay Music
- Explosion sound by "qubodup" via freesound.org
- Digging in wet course sand (1) by f3bbbo -- https://freesound.
  org/s/651292/ -- License: Creative Commons 0
- Button Clicking 1 by Sheyvan -- https://freesound.org/s/475188/ -- 
  License: Creative Commons 0
- Hit 2.wav by goldenpotatobull -- https://freesound.org/s/468948/ -- 
  License: Creative Commons 0
- retro win jingle by skookaa -- https://freesound.org/s/778891/ -- License: 
  Creative Commons 0
- Game design and programming by Andre DeHerrera