package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "images")
public class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imgId;

    private  String name;
    private  String filePath;
    private  String type;

    @ManyToOne
    @JoinColumn(name = "producctId")
    private Product product;

}
