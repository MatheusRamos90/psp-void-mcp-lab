package com.matheushrs.psp_void_postgres_db.dto;

public record UserRequest(String name, String email, Boolean status, String password) {
}
