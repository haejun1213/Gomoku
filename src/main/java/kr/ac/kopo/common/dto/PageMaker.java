package kr.ac.kopo.common.dto;

import lombok.Data;

@Data
public class PageMaker {
    private int totalCount;     // 전체 게시글 수
    private int startPage;      // 화면에 보여질 첫번째 페이지 번호
    private int endPage;        // 화면에 보여질 마지막 페이지 번호
    private boolean prev;       // 이전 버튼 유무
    private boolean next;       // 다음 버튼 유무
    private int displayPageNum = 10; // 화면 하단에 보여줄 페이지의 수
    private Criteria cri;       // 현재 페이지 정보

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        calcData();
    }

    private void calcData() {
        endPage = (int) (Math.ceil(cri.getPage() / (double) displayPageNum) * displayPageNum);
        startPage = (endPage - displayPageNum) + 1;
        int tempEndPage = (int) (Math.ceil(totalCount / (double) cri.getPerPageNum()));
        if (endPage > tempEndPage) {
            endPage = tempEndPage;
        }
        prev = startPage != 1;
        next = endPage * cri.getPerPageNum() < totalCount;
    }
}