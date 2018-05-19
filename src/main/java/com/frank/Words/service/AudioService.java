/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frank.Words.service;

import com.frank.Words.util.AudioException;
import com.frank.Words.util.Constants;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import edu.cmu.sphinx.api.SpeechResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author FRANK
 */
@Service
public class AudioService {

    public String load(String audioURL, String ext) throws AudioException, FileNotFoundException, IOException {

        RestTemplate restT = new RestTemplate();
        byte[] bytes = restT.getForObject(audioURL, byte[].class);

        File file = File.createTempFile("copia", "." + ext, null);
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();

        String contentType = "audio/"+ext;
        String text = speechToTextIBM(file, contentType, Constants.IMB_ES_MODEL);

        if (text.isEmpty()) {
            throw new AudioException("No fue posible obtener la traducción del audio");
        }
        return text;

    }

    private String speechToTextIBM(File file, String contentType, String language) {

        SpeechToText speechToText = new SpeechToText();
        speechToText.setUsernameAndPassword(Constants.IBM_USERNAME, Constants.IMB_PASSWORD);

        RecognizeOptions options = new RecognizeOptions.Builder()
                .contentType(contentType)
                .model(language)
                .build();
        SpeechResults result = speechToText.recognize(file, options).execute();

        String speech = "";
        if (!result.getResults().isEmpty()) {
            for (Transcript tp : result.getResults()) {
                if (tp.isFinal()) {
                    speech = tp.getAlternatives().get(0).getTranscript();
                    break;
                }
            }
        }
        return speech;

    }

}
