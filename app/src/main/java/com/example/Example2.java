//package com.example;
//
//import com.example.Example.FirestoreEvent;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.FirestoreOptions;
//import com.google.cloud.firestore.SetOptions;
//import com.google.cloud.functions.BackgroundFunction;
//import com.google.cloud.functions.Context;
//
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class Example implements BackgroundFunction<FirestoreEvent> {
//    private static final Logger logger = Logger.getLogger(Example.class.getName());
//    private static final Firestore FIRESTORE = FirestoreOptions.getDefaultInstance().getService();
//    private final Firestore firestore;
//
//    public Example() {
//        this(FIRESTORE);
//    }
//
//    Example(Firestore firestore) {
//        this.firestore = firestore;
//    }
//
//    @Override
//    public void accept(FirestoreEvent event, Context context) {
//        logger.info("Function triggered by change to: " + event.value.name);
//
//        String newValue = "Time out";
//        Map<String, String> newFields = Map.of("status", newValue);
//
//        String affectedDoc = event.value.name.split("/documents/")[1].replace("\"", "");
//
//        logger.info(String.format("Replacing value: %s --> %s", event.value.name, newValue));
//        try {
//            FIRESTORE.document(affectedDoc).set(newFields, SetOptions.merge()).get();
//        } catch (ExecutionException | InterruptedException e) {
//            logger.log(Level.SEVERE, "Error updating Firestore document: " + e.getMessage(), e);
//        }
//    }
//
//    public static class FirestoreEvent {
//        FirestoreValue value;
//        FirestoreValue oldValue;
//    }
//
//    public static class FirestoreValue {
//        String createTime;
//        String name;
//    }
//}
//
//
////
////<?xml version="1.0" encoding="UTF-8"?>
////<project>
////<modelVersion>4.0.0</modelVersion>
////<groupId>cloudfunctions</groupId>
////<artifactId>firestore-function</artifactId>
////<version>1.0-SNAPSHOT</version>
////
////<properties>
////<maven.compiler.target>11</maven.compiler.target>
////<maven.compiler.source>11</maven.compiler.source>
////</properties>
////
////<!-- Required for Java 11 functions in the inline editor -->
////<build>
////<plugins>
////<plugin>
////<groupId>org.apache.maven.plugins</groupId>
////<artifactId>maven-compiler-plugin</artifactId>
////<version>3.8.1</version>
////<configuration>
////<excludes>
////<exclude>.google/</exclude>
////</excludes>
////</configuration>
////</plugin>
////</plugins>
////</build>
////<dependencies>
////<dependency>
////<groupId>com.google.cloud.functions</groupId>
////<artifactId>functions-framework-api</artifactId>
////<version>1.0.1</version>
////<type>jar</type>
////</dependency>
////<dependency>
////<groupId>com.google.firebase</groupId>
////<artifactId>firebase-admin</artifactId>
////<version>9.0.0</version>
////</dependency>
////</dependencies>
////</project>
////
////Borrow Requests/{Requests}
