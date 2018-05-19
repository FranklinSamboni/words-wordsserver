/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frank.Words.data;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author FRANK
 */

@Document(collection = "speeches")
public class Speech {
    
    private String speech;

    public Speech(String speech) {
        this.speech = speech;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }
}
