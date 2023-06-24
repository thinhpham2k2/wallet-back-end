package com.wallet.service;

import com.google.firebase.messaging.*;
import com.wallet.dto.NoteDTO;
import com.wallet.service.interfaces.IFirebaseMessagingService;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService implements IFirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    @Override
    public String sendNotification(NoteDTO note, String token) throws FirebaseMessagingException {

        Notification notification = Notification
                .builder()
                .setTitle(note.getSubject())
                .setBody(note.getContent())
                .setImage(note.getImage())
                .build();

        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(note.getData())
                .build();

        return firebaseMessaging.send(message);
    }
}
