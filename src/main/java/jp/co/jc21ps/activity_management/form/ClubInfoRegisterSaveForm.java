package jp.co.jc21ps.activity_management.form;

import jakarta.validation.constraints.Size;

public class ClubInfoRegisterSaveForm {

    // leaderClubIb
    private String leaderClubId;

    // 部署名
    private String clubName;

    // 部署説明
    @Size(max = 400, message = "{Size}")
    private String clubDescription;

    public ClubInfoRegisterSaveForm() {

    }

    public String getLeaderClubId() {
        return leaderClubId;
    }

    public void setLeaderClubId(String leaderClubId) {
        this.leaderClubId = leaderClubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubDescription() {
        return clubDescription;
    }

    public void setClubDescription(String clubDescription) {
        this.clubDescription = clubDescription;
    }

}
