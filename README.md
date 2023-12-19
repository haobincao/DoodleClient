## Instructions

**Update:** If gradle does not compile for you, please go to the DoodleDuelJump folder, find the `build.gradle` file, right click it. Click Refresh, then try running the program again. I had to add a dependency to the gradle file for fonts, so Gradle needs to be refreshed.

**Important:** Cross platform work on LibGDX using GitHub was giving me a warning when pushing onto git. 

All _Windows_ users must run `git config --global core.autocrlf true` after initializing their git repository in which they plan to pull changes.
All _Max/Linux_ users must run `git config --global core.autocrlf input` after initializing their git repository in which they plan to pull changes.

Check out the answer in https://stackoverflow.com/questions/5834014/lf-will-be-replaced-by-crlf-in-git-what-is-that-and-is-it-important/5834094#5834094 as to why.

### Steps for first use

Step 0: If you haven't already, follow the instructions for downloading LibGDX: https://libgdx.com/wiki/start/setup. However, this is just to make a new LibGDX game. If you only need to pull and work on the existing project, there should be no need to do this, since the files are already here.

1. Git pull into empty folder
2. Create a new Eclipse Workspace (or you may choose to work on an existing one, but keep in mind that Gradle will add _four_ directories to your project dependencies when you finish setting up.)
3. In the workspace, choose `File -> Import... -> Gradle -> Existing Gradle Project`
4. Choose the `DoodleDuelJump` folder you pulled into
5. Click finish. Gradle should create four directories for you:

* _DoodleDuelJump_ (contains the assets of the game)
* _DoodleGUI-core_ (this is where the core game files will be located)
* _DoodleGUI-desktop_ (this has the `DesktopLauncher` class, which is the class that needs to be run for the desktop game to run)
* _DoodleGUI-android_ (unused; this was made in case we choose to run it as an android app, which we are not)

Finally, To allow for easy running of the program, make a new Run Configuration that automatically launches the `DesktopLauncher` class. Check out the Eclipse section under `Getting it Running --> Desktop` in the link: https://libgdx.com/wiki/start/import-and-running

(When adding the run configurations, add "-XstartOnFirstThread" to VM arugments under Arguments tab. Otherwise, the program might not be able to run on MACOS)

You should now be able to run the game on eclipse.

### Steps for following uses

You should not need to redo all the previous steps for subsequent pulls. Simply pull the files and run your eclipse workspace; your run configurations and gradle files should stay the same. Let me (Dwaipayan) know if it doesn't work.
