const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

exports.sendNewPostNotification = functions.firestore
    .document('Journal/{postId}')
    .onCreate((snapshot, context) => {
        const message = {
            topic: 'new_posts',
            notification: {
                title: 'New Post',
                body: 'A new post has been added!',
            },
        };

        return admin.messaging().send(message);
    });