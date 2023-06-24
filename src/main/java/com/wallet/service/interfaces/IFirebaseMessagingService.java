package com.wallet.service.interfaces;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.wallet.dto.NoteDTO;

public interface IFirebaseMessagingService {

    String sendNotification(NoteDTO note, String token) throws FirebaseMessagingException;
}
