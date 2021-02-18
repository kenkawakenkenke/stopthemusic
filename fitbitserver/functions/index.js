import * as functions from "firebase-functions";
import admin, { storage } from "firebase-admin";
import handleFitbitAsleep from "../src/messenger/messenger.js";

export const fitbitAsleep = functions
    .region('asia-northeast1')
    .https
    .onRequest(
        async (request, response) => {

            if (admin.apps.length === 0) {
                admin.initializeApp();
            }
            const fitbitDeviceKey = request.query.k;
            console.log("request", fitbitDeviceKey);

            const err = await handleFitbitAsleep(admin, fitbitDeviceKey)
                .catch(exception => exception);

            if (err) {
                response.json({
                    success: false,
                    error: err,
                });
            } else {
                response.json({
                    success: true
                });
            }
        });
