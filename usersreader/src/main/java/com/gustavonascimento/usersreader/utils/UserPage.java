package com.gustavonascimento.usersreader.utils;

import com.gustavonascimento.usersreader.entities.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "UserPage", description = "Paginated response for users")
public class UserPage {
    public List<UserDTO> content;
    public int number;
    public int size;
    public long totalElements;
    public int totalPages;
    public boolean first;
    public boolean last;
}