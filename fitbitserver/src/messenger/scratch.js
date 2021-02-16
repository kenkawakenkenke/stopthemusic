// Scratch script for testing messenger.js.
import { decodeEnv } from "../common/utils/setup_dotenv.js";
import admin from "firebase-admin";
import handleFitbitAsleep from "./messenger.js";

(async () => {
    const fitbitDeviceKey = "kensfitbit";

    // Local scripts need to specify authentication tokens.
    admin.initializeApp({
        credential: admin.credential.cert(decodeEnv("FIRESTORE_SERVICE_KEY")),
    });

    const err = await handleFitbitAsleep(admin, fitbitDeviceKey);
    console.log("err:", err);
})();