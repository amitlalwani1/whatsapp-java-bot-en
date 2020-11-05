package com.example.whatsappbot.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Base64Help {

    static public String getPDFtring() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/pdf.txt")));
    }

    static  public String getMP3String() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/mp3.txt")));
    }

    static public String getJPEG() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/jpeg.txt")));
    }

    static public String getDOC() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/doc.txt")));
    }

}