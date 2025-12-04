package jp.co.jc21ps.activity_management.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import jp.co.jc21ps.activity_management.entity.JoinRequestEntity;
import jp.co.jc21ps.activity_management.entity.JoinRequestSaveEntity;

@Repository
public class JoinRequestRepository {
    private final JdbcTemplate jdbcTemplate;

    public JoinRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 初期画面表示
    public List<JoinRequestEntity> getJoinRequestById(JoinRequestEntity paramEntity) {
        /*
         * TODO ➊ 初期表示情報を取得するSQLを完成させる。
         * mst_clubから、user_idでtrn_join_requestに存在せず、trn_club_memberにも存在しない部を取得
         */
        String sql = """
                    SELECT 
                        club.club_id,
                        club.club_name,
                        club.club_description
                    FROM 
                        mst_club club
                    WHERE 
                        NOT EXISTS (
                            SELECT 1 
                            FROM trn_join_request request
                            WHERE request.club_id = club.club_id
                            AND request.user_id = ?
                        )
                        AND NOT EXISTS (
                            SELECT 1 
                            FROM trn_club_member member
                            WHERE member.club_id = club.club_id
                            AND member.user_id = ?
                        )
                    ORDER BY club.club_id
                """;

        List<JoinRequestEntity> responseEntity = new ArrayList<>();
        try {
            List<Map<String, Object>> joinRequestList = jdbcTemplate.queryForList(sql, paramEntity.getUserId(),
                    paramEntity.getUserId());

            // リストが空だった場合
            if (joinRequestList.isEmpty()) {
                return responseEntity;
            }

            for (Map<String, Object> joinRequest : joinRequestList) {

                // entityに値をセットする
                JoinRequestEntity joinData = new JoinRequestEntity();
                joinData.setClubName((String) joinRequest.get("club_name"));
                joinData.setClubDescription((String) joinRequest.get("club_description"));
                joinData.setClubId((String) joinRequest.get("club_id"));
                responseEntity.add(joinData);

            }
        } catch (Exception e) {
            // エラーが発生した場合はログを出力して例外を再スロー
            e.printStackTrace();
            throw e;
        }

        return responseEntity;
    }

    // 申請処理
    public void insertClub(JoinRequestSaveEntity paramEntity) {
        /*
         * TODO ➋ 申請者の情報をインサートするSQLを完成させる。
         * trn_join_requestテーブルにuser_id, club_id, leader_flgを登録
         */
        String sql = """
                    INSERT INTO trn_join_request (
                        club_id,
                        user_id,
                        leader_flg
                    ) VALUES (
                        ?,
                        ?,
                        ?
                    )
                """;

        // entityから値をゲットする
        Object[] paramList = {
                paramEntity.getClubId(),
                paramEntity.getUserId(),
                paramEntity.isLeaderFlg() ? 1 : 0
        };

        jdbcTemplate.update(sql, paramList);
    }
}
