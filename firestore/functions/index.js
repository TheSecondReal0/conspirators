/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

// The Cloud Functions for Firebase SDK to create Cloud Functions and triggers.
// const {onDocumentCreated} = require("firebase-functions/v2/firestore");

// The Firebase Admin SDK to access Firestore.
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");
const functions = require("firebase-functions/v1");

initializeApp();

exports.createUser = functions.auth.user()
    .onCreate((user) => {
      // The UID of the user.
      const uid = user.uid;

      // The user's email address.
      const email = user.email;

      // The user's display name.
      const displayName = user.displayName;

      // The user's photo URL.
      const photoURL = user.photoURL;

      // The user's phone number.
      const phoneNumber = user.phoneNumber;

      // The user's provider data.
      const providerData = user.providerData;

      const db = getFirestore();
      // Use the user's UID as the document ID
      const usersRef = db.collection("users").doc(uid);

      const userDoc = {
        email: email,
        displayName: displayName,
        photoURL: photoURL,
        phoneNumber: phoneNumber,
        providerData: providerData,
      };

      // eslint-disable-next-line max-len
      logger.info("Creating user document in Firestore", {userDoc}, "with uid", uid);
      return usersRef.set(userDoc);
    });
