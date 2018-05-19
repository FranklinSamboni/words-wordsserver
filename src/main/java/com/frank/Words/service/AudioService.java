/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frank.Words.service;

import com.frank.Words.data.Speech;
import com.frank.Words.data.Word;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.frank.Words.repository.SpeechRepository;
import com.frank.Words.repository.WordRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author FRANK
 */
@Service
public class AudioService {

    @Autowired
    private SpeechRepository speechRepository;
    
    @Autowired
    private WordRepository wordRepository;

    public String upload(String audioURL, String ext) throws AudioException, FileNotFoundException, IOException {

        RestTemplate restT = new RestTemplate();
        byte[] bytes = restT.getForObject(audioURL, byte[].class);

        File file = File.createTempFile("copia", "." + ext, null);
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();

        String contentType = "audio/" + ext;
        String text = speechToTextIBM(file, contentType, Constants.IMB_ES_MODEL);

        if (!text.isEmpty()) {
            saveWords(text);
        } else {
            throw new AudioException("No fue posible obtener la traducción del audio");
        }
        return text;

    }

    public List<Speech> getSpeeches() throws Exception {
        return speechRepository.findAll();
    }
    
    public List<Object[]> getUsedWords() {
        List<Word> words = wordRepository.findAll();
        List<Object[]> arrObjs = new ArrayList();
        words.forEach((Word word) -> {
            Object[] obj = new Object[2];
            obj[0] = word.getWord();
            obj[1] = word.getRepetitions();
            arrObjs.add(obj);
        });
        return arrObjs;
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

    private void saveWords(String words) {
        Speech speech = new Speech(words);
        speechRepository.insert(speech);
        
        String[] arrWords = words.split(words);
        for(String strWord : arrWords){
            Optional<Word> optional = wordRepository.findById(strWord); 
            Word wr;
            if(optional.isPresent()){
                wr = optional.get();
                wr.setRepetitions(wr.getRepetitions()+1);
                wordRepository.save(wr);
            }
            else{
                wr = new Word(strWord,0);
                wordRepository.insert(wr);
            }
        }
    }


}
