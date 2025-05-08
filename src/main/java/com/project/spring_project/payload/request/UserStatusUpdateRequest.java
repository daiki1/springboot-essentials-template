package com.project.spring_project.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusUpdateRequest {
    private boolean active;
}
