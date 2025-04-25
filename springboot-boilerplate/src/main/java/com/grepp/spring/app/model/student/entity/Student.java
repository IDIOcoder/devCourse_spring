package com.grepp.spring.app.model.student.entity;

import com.grepp.spring.infra.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
public class Student extends BaseEntity {

    @Id
    String userId;
    String name;
    String major;
    
    

}
