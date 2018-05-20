/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frank.Words.controller;

import com.frank.Words.service.AudioService;
import com.frank.Words.util.Constants;
import com.frank.Words.util.ResponseFormatUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author FRANK
 */
@RestController
@RequestMapping("/audio")
public class AudioController {
    
    private static final Log log = LogFactory.getLog("AudioController");

    @Autowired
    private AudioService audioService;

    @GetMapping("/upload")
    public Map<String, Object> upload(@RequestParam String audioURL) {
        try {
            UrlValidator urlValidator = new UrlValidator();
            if (urlValidator.isValid(audioURL)) {
                String ext = getExtension(audioURL);
                if (ext != null && !ext.isEmpty()) {
                    return ResponseFormatUtil.OK(audioService.upload(audioURL,ext));
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
            log.error(e);
            return ResponseFormatUtil.ERROR(e.getMessage());
        }
    }
    
    @GetMapping("/getSpeeches")
    public Map<String, Object> getSpeeches() {
        try{
            return ResponseFormatUtil.OK(audioService.getSpeeches());
        }
        catch(Exception e) {
            return ResponseFormatUtil.ERROR(e.getMessage());
        }
    }
    
    @GetMapping("/getUsedWords")
    public Map<String, Object> getUsedWords() {
        try{
            return ResponseFormatUtil.OK(audioService.getUsedWords());
        }
        catch(Exception e) {
            return ResponseFormatUtil.ERROR(e.getMessage());
        }
    }

    private String getExtension(String url) throws MalformedURLException {
        URL uUrl = new URL(url);
        String ext = FilenameUtils.getExtension(uUrl.getPath());
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
