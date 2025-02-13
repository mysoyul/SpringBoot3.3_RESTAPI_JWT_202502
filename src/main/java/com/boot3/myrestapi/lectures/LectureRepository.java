package com.boot3.myrestapi.lectures;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture,Integer> {
    //custom finder method 선언
    // select lec from Lecture lec where name like '%name%'
    List<Lecture> findByNameContaining(String name);
    // select lec from Lecture lec where basePrice >= basePrice
    List<Lecture> findByBasePriceGreaterThanEqual(int basePrice);
}
