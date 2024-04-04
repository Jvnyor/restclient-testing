package com.jvnyor.demorestclient.dtos;

public record CatRequestDTO(String name, String color, Double weight) {
    public CatRequestDTO withWeight(Double weight) {
        return new CatRequestDTO(name, color, weight);
    }
}