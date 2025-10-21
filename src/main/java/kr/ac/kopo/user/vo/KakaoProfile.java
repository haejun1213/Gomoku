package kr.ac.kopo.user.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoProfile {
    private Long id;
    @JsonProperty("connected_at")
    private String connectedAt;
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(String connectedAt) {
        this.connectedAt = connectedAt;
    }

    public KakaoAccount getKakaoAccount() {
        return kakaoAccount;
    }

    public void setKakaoAccount(KakaoAccount kakaoAccount) {
        this.kakaoAccount = kakaoAccount;
    }

    public static class KakaoAccount {
        @JsonProperty("profile_needs_agreement")
        private Boolean profileNeedsAgreement;
        private Profile profile;
        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;
        private String email;
        @JsonProperty("nickname_needs_agreement")
        private Boolean nicknameNeedsAgreement;
        private String nickname;

        // Getter 및 Setter
        public Boolean getProfileNeedsAgreement() {
            return profileNeedsAgreement;
        }

        public void setProfileNeedsAgreement(Boolean profileNeedsAgreement) {
            this.profileNeedsAgreement = profileNeedsAgreement;
        }

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }

        public Boolean getEmailNeedsAgreement() {
            return emailNeedsAgreement;
        }

        public void setEmailNeedsAgreement(Boolean emailNeedsAgreement) {
            this.emailNeedsAgreement = emailNeedsAgreement;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Boolean getNicknameNeedsAgreement() {
            return nicknameNeedsAgreement;
        }

        public void setNicknameNeedsAgreement(Boolean nicknameNeedsAgreement) {
            this.nicknameNeedsAgreement = nicknameNeedsAgreement;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    public static class Profile {
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
        @JsonProperty("is_default_image")
        private Boolean isDefaultImage;

        // Getter 및 Setter
        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getThumbnailImageUrl() {
            return thumbnailImageUrl;
        }

        public void setThumbnailImageUrl(String thumbnailImageUrl) {
            this.thumbnailImageUrl = thumbnailImageUrl;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public Boolean getIsDefaultImage() {
            return isDefaultImage;
        }

        public void setIsDefaultImage(Boolean isDefaultImage) {
            this.isDefaultImage = isDefaultImage;
        }
    }
}