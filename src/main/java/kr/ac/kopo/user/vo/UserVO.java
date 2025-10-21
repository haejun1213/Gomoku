package kr.ac.kopo.user.vo;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Date;

@Data
public class UserVO {
    private Long userId;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
    private String email;

    @Size(min = 8, max = 255, message = "비밀번호는 8자 이상 255자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$",
             message = "비밀번호는 최소 8자 이상이며, 숫자, 소문자, 대문자, 특수문자를 각각 1개 이상 포함해야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다.")
    private String nickname;

    private String profileImage;
    private String isSocialLogin = "N";
    private String socialType;
    private String socialId;
    private String role = "C";
    private String isActive = "Y";
    private Date joinedDate = new Date();
    private Date lastLoginDatetime;
    private String faceEncoding;
}