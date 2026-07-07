package com.ecommerce.order_service.dto;


import lombok.Data;


@Data
public class UserResponseDTO {

    private Long id;

    private String nom;

    private String prenom;

    private String email;

}