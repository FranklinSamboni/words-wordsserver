/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frank.Words.repository;

import com.frank.Words.data.Word;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 *
 * @author FRANK
 */
public interface WordRepository extends MongoRepository<Word,String> {
    
    @Query("{ 'repetitions' : ?0 }")
    public List<Word> findByRepetitions(int repetitions);
    
}