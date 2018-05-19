/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frank.Words.controller;

import com.frank.Words.data.AudioURL;
import com.frank.Words.service.AudioService;
import com.frank.Words.util.AudioException;
import com.frank.Words.util.Constants;
import com.frank.Words.util.ResponseFormatUtil;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author FRANK
 */
@RestController
@RequestMapping("/audio")
public class AudioController {

    private String path = "src/main/resources/Recording.m4a";
    private String samplePath = "src/main/resources/test.wav";
    private String samplePath2 = "test.wav";

    @Autowired
    private AudioService audioService;

    @GetMapping("/loadAudio")
    public Map<String, Object> loadAudio() throws IOException {

        try {
            /*File file = new ClassPathResource(path).getFile();
            
            InputStream in = new FileInputStream(file);
            int read = 0;
            byte[] buffer = new byte[4096];
            
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }*/

            FileOutputStream out = new FileOutputStream("otros.m4a");
            Path paths = Paths.get(samplePath);
            byte[] data = Files.readAllBytes(paths);

            out.write(data);
            out.close();

            Map<String, Object> obj = new HashMap<>();
            obj.put("Algo", "Algo");

            /*File file = new ClassPathResource(samplePath).getFile();
            InputStream stream = new FileInputStream(file);*/
            final String uri = "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize";
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            Map map = new HashMap<String, String>();
            map.put("Content-Type", "audio/wav");
            headers.setAll(map);

            HttpEntity<byte[]> request = new HttpEntity<byte[]>(data, headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("2b1815a3-27ad-4240-9163-33112515301d", "TZ6ABuLeQ67X"));
            String result = restTemplate.postForObject(uri, request, String.class);

            obj.put("res", result);

            /*            Configuration configuration = new Configuration();

            configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
            configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
            configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

            configuration.setAcousticModelPath("src/main/resources/cmusphinx-es-5.2/model_parameters/voxforge_es_sphinx.cd_ptm_4000");
            configuration.setDictionaryPath("src/main/resources/cmusphinx-es-5.2/etc/voxforge_es_sphinx.dic");
            configuration.setLanguageModelPath("src/main/resources/cmusphinx-es-5.2/etc/es-20k.lm");
            StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
            File file = new ClassPathResource(samplePath2).getFile();
            InputStream stream = new FileInputStream(file);

            recognizer.startRecognition(stream);
            SpeechResult result;
            while ((result = recognizer.getResult()) != null) {
                System.out.format("Hypothesis: %s\n", result.getHypothesis());
            }
            recognizer.stopRecognition();*/
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @GetMapping("/load")
    public Map<String, Object> load(@RequestParam String audioURL) throws IOException {
        try {
            UrlValidator urlValidator = new UrlValidator();
            if (urlValidator.isValid(audioURL)) {
                String ext = getExtension(audioURL);
                if (ext != null && !ext.isEmpty()) {
                    return ResponseFormatUtil.OK(audioService.load(audioURL,ext));
                } else {
                    String msg = "Formatos de Audio soportados: "
                            + Constants.EXT_WAV + ", " + Constants.EXT_WEBM + ", " + Constants.EXT_PCM + ", "
                            + Constants.EXT_BASIC + ", " + Constants.EXT_FLAC + ", " + Constants.EXT_MULAW + ", "
                            + Constants.EXT_MP3 + ", " + Constants.EXT_MPEG + ", " + Constants.EXT_RAW;
                    return ResponseFormatUtil.ERROR(msg);
                }
            } else {
                return ResponseFormatUtil.ERROR("URL incorrecta.");
            }
        } catch (Exception e) {
            return ResponseFormatUtil.ERROR(e.getMessage());
        }
    }

    private String getExtension(String url) {
        String ext = FilenameUtils.getExtension(url);
        if (ext != null) {
            if (ext.equals(Constants.EXT_WAV)
                    || ext.equals(Constants.EXT_WEBM)
                    || ext.equals(Constants.EXT_PCM)
                    || ext.equals(Constants.EXT_BASIC)
                    || ext.equals(Constants.EXT_FLAC)
                    || ext.equals(Constants.EXT_MULAW)
                    || ext.equals(Constants.EXT_MP3)
                    || ext.equals(Constants.EXT_MPEG)
                    || ext.equals(Constants.EXT_RAW)) {
                return ext;
            }

        }
        return null;
    }
}
