import moment from "moment-timezone";

async function fetchTokenForDevice(admin, fitbitDeviceKey) {
    const devices = await admin.firestore().collection("devices")
        .where("fitbitDeviceKey", "==", fitbitDeviceKey)
        .limit(1)
        .get();
    let tokens = [];
    devices.forEach(d => tokens.push(d.data().cloudMessageToken));
    return tokens.length === 0 ? undefined : tokens[0];
}

async function notifyApp(admin, messageToken) {
    let payload = {
        // Currently the data doesn't matter; the app doesn't do anything with it.
        data: {
            blah: "123",
        },
    };
    const messageResponse = await admin.messaging().sendToDevice([messageToken], payload);
    // TODO: can there be error states?
    return true;
}

// Returns: error.
export default async function handleFitbitAsleep(admin, fitbitDeviceKey) {
    const messageToken = await fetchTokenForDevice(admin, fitbitDeviceKey);
    if (!messageToken) {
        return "unknown device key";
    }

    // Message the app.
    const notifySuccess = await notifyApp(admin, messageToken);

    // Log in activations table.
    await admin.firestore().collection("activations").doc().set(
        {
            t: moment().format(),
            fitbitDeviceKey,
            success: notifySuccess,
        }
    );
    return undefined;
}
