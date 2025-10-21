package kr.ac.kopo.common.dto;

import lombok.Data;

@Data
public class Criteria {
    private int page;       // 현재 페이지 번호
    private int perPageNum; // 한 페이지당 보여줄 게시글의 수

    public Criteria() {
        this.page = 1;          // 기본값: 1페이지
        this.perPageNum = 10;   // 기본값: 한 페이지에 10개
    }

    // 쿼리에서 사용할 시작 인덱스를 계산 (예: 1페이지는 0, 2페이지는 10)
    public int getPageStart() {
        return (this.page - 1) * perPageNum;
    }
}