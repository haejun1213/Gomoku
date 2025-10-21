package kr.ac.kopo.user.dto;

import kr.ac.kopo.common.dto.Criteria;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserSearchCriteria extends Criteria {
    private String searchType; // "email" 또는 "nickname"
    private String keyword;    // 검색어
}