package com.code10.libsys;

import com.example.Example.FirestoreEvent;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import java.util.logging.Logger;
import java.util.Locale;
import java.util.Map;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.SetOptions;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class Example implements BackgroundFunction<FirestoreEvent> {
    private static final Logger logger = Logger.getLogger(Example.class.getName());
    private static final Firestore FIRESTORE = FirestoreOptions.getDefaultInstance().getService();
    private final Firestore firestore;

    public Example() {
        this(FIRESTORE);
    }

    Example(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void accept(FirestoreEvent event, Context context) {
        logger.info("Function triggered by change to: " + event.value.name);

        String newValue = "new"
        Map<String, String> newFields = Map.of("Status", newValue);

        String affectedDoc = event.value.name.split("/documents/")[1].replace("\"", "");

        logger.info(String.format("Replacing value: %s --> %s", event.value.name, newValue));
        try {
            FIRESTORE.document(affectedDoc).set(newFields, SetOptions.merge()).get();
        } catch (ExecutionException | InterruptedException e) {
            logger.log(Level.SEVERE, "Error updating Firestore document: " + e.getMessage(), e);
        }
    }