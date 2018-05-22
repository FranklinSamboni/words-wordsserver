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
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import edu.cmu.sphinx.api.SpeechResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.frank.Words.repository.SpeechRepository;
import com.frank.Words.repository.WordRepository;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

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

    public String upload(String audioURL, String ext) throws AudioException, FileNotFoundException, ConnectException, IOException {

        RestTemplate restT = new RestTemplate();
        byte[] bytes = restT.getForObject(audioURL, byte[].class);

        String text = speechToTextIBM(bytes, ext);
        if (!text.isEmpty()) {
            saveWords(text);
        } else {
            text = speechToTextSPHINX(bytes);
            if (!text.isEmpty()) {
                saveWords(text);
            } else {
                throw new AudioException("No fue posible obtener la traducción del audio");
            }
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

    private String speechToTextIBM(byte[] bytes, String ext) throws IOException {

        File file = File.createTempFile("src/main/resources/static/audio/copia", "." + ext, null);
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();

        String contentType = "audio/" + ext;

        SpeechToText speechToText = new SpeechToText();
        speechToText.setUsernameAndPassword(Constants.IBM_USERNAME, Constants.IMB_PASSWORD);

        RecognizeOptions options = new RecognizeOptions.Builder()
                .contentType(contentType)
                .model(Constants.IMB_ES_MODEL)
                .build();
        SpeechResults result = speechToText.recognize(file, options).execute();

        String speech = "";
        if (!result.getResults().isEmpty()) {
            for (Transcript tp : result.getResults()) {
                if (tp.isFinal()) {
                    speech = speech + " " + tp.getAlternatives().get(0).getTranscript();
                }
            }
        }
        return speech;
    }

    private String speechToTextSPHINX(byte[] bytes) throws IOException {
        Configuration configuration = new Configuration();

        configuration.setAcousticModelPath("src/main/resources/cmusphinx-es-5.2/model_parameters/voxforge_es_sphinx.cd_ptm_4000");
        configuration.setDictionaryPath("src/main/resources/cmusphinx-es-5.2/etc/voxforge_es_sphinx.dic");
        configuration.setLanguageModelPath("src/main/resources/cmusphinx-es-5.2/etc/es-20k.lm");

        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
        InputStream stream = new ByteArrayInputStream(bytes);

        recognizer.startRecognition(stream);
        SpeechResult result;
        String speech = "";
        while ((result = recognizer.getResult()) != null) {
            speech = speech + " " + result.getHypothesis();
        }
        recognizer.stopRecognition();
        return speech;
    }

    private void saveWords(String words) {
        Speech speech = new Speech(words);
        speechRepository.insert(speech);

        String[] arrWords = words.trim().split(" ");
        //LinkedHashSet<String> wordsNotRepeated = new LinkedHashSet<>(Arrays.asList(arrWords));
        
        //Crea un Map con las palabras  y el numero de veces que esta se repitieron en el texto
        Map<String, Integer> repetitions = new HashMap<>();
        for (String strWord : arrWords) {
            Integer count = repetitions.get(strWord) != null ? repetitions.get(strWord) : 0;
            repetitions.put(strWord, count+1);
        }
        
        //Busca en la base de datos las palabras del texto que ya existen y actualiza su informacion
        Iterable<Word> dbWords = wordRepository.findAllById(repetitions.keySet());
        for (Word w : dbWords) {
            Integer count = repetitions.get(w.getWord()) != null ? repetitions.get(w.getWord()) : 0;
            repetitions.put(w.getWord(), w.getRepetitions()+count);
        }    
        
        //Se crea la lista a guardar
        List<Word> wordsToSave = new ArrayList();
        repetitions.entrySet().stream().forEach((w)->{
            wordsToSave.add(new Word(w.getKey(),w.getValue()));
        });
        wordRepository.saveAll(wordsToSave);
    }

}
