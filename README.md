# Stop the music!
There are 3 parts to this project:
- The fitbit app (figuring out how to share this part)
- The server (fitbitserver)
- The app (StopTheMusicApp)

The fitbit app triggers when you go to sleep, posts its device key to the server, which then notifies the app via Cloud Message, causing the app to stops all music on the device. Simple.

# Setup

You also need two secret files:
- [.env](https://drive.google.com/file/d/13ntB59I7bTyUb2QzRye8hE1EemD5Sgcv/view?usp=sharing): environment variables required for running the server, to place in fitbitserver/.env. If you're cloning this project, take a look at [.env.template](https://github.com/kenkawakenkenke/stopthemusic/blob/main/fitbitserver/.env.template) to build your own environment variables.
- [google-services.json](https://drive.google.com/file/d/1yPG18CL2vKzgr-d9WWJg0p6k87dfzo5o/view?usp=sharing): firebace access token for the Android app, to place in StopTheMusicApp/app/google-services.json. If you're cloning this project, just go to firebase and create your own keys.
