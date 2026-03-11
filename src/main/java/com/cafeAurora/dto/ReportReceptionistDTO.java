package com.cafeAurora.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportReceptionistDTO {
    private String receptName;
    private String receptEmail;
    private Long totalAttended;
    private Long confirmed;
    private Long rejected;
    private Long completed;
}