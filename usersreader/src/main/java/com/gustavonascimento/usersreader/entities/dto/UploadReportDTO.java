package com.gustavonascimento.usersreader.entities.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "UploadReport", description = "Summary of the upload operation")
public class UploadReportDTO {

    @Schema(example = "3", description = "Number of records inserted")
    public int inserted;

    @Schema(example = "1", description = "Number of records skipped (e.g., duplicated email)")
    public int skipped;

    @Schema(description = "Processing errors")
    public List<String> errors = new ArrayList<>();
}