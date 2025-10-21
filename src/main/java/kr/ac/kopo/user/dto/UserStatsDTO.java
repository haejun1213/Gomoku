package kr.ac.kopo.user.dto;

import lombok.Data;

@Data
public class UserStatsDTO {
    private int totalGames;
    private int wins;
    private int losses;
    private int draws;
    private double winRate; // 승률 (%)
}