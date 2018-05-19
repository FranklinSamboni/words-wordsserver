/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frank.Words.repository;

import com.frank.Words.data.Speech;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author FRANK
 */
public interface SpeechRepository extends MongoRepository<Speech,String>{
    
}
