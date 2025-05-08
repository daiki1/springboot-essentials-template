package com.project.spring_project.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String username;
    private String email;

    //Add extra fields to update
}
