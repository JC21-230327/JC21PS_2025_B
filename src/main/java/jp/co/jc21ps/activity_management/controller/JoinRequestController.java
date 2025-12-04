package jp.co.jc21ps.activity_management.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import jp.co.jc21ps.activity_management.dto.JoinRequestDto;
import jp.co.jc21ps.activity_management.dto.JoinRequestSaveDto;
import jp.co.jc21ps.activity_management.dto.SessionDto;
import jp.co.jc21ps.activity_management.form.JoinRequestSaveForm;
import jp.co.jc21ps.activity_management.service.CommonService;
import jp.co.jc21ps.activity_management.service.JoinRequestService;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/joinRequest")

public class JoinRequestController {

    private final JoinRequestService joinRequestService;
    private final MessageSource messageSource;
    private final CommonService commonService;

    // サービスをセット
    public JoinRequestController(JoinRequestService joinRequestService, MessageSource messageSource,
            CommonService commonService) {
        this.joinRequestService = joinRequestService;
        this.messageSource = messageSource;
        this.commonService = commonService;
    }

    @GetMapping
    public ModelAndView getJoinRequestById(HttpSession session, JoinRequestSaveForm paramForm,
            @ModelAttribute("joinOkMessage") String joinOkMessage) {

        ModelAndView mav = new ModelAndView();

        // セッションからuserIdを取得
        SessionDto sessionDto = commonService.getSessionDto(session);
        String userId = sessionDto.getUserId();
        String leaderClubId = sessionDto.getClubId();

        // セッションが切れた場合、エラー画面に遷移
        if (userId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        // formに値をセット
        JoinRequestSaveForm form = new JoinRequestSaveForm();
        form.setUserId(userId);

        try {
            // dtoに値をセット
            JoinRequestDto joinRequestDto = new JoinRequestDto();
            joinRequestDto.setUserId(userId);

            List<JoinRequestDto> joinRequestList = joinRequestService.findRequest(joinRequestDto);
            List<JoinRequestSaveForm> responseForm = new ArrayList<>();

            // formに値をセット
            for (JoinRequestDto dto : joinRequestList) {

                JoinRequestSaveForm saveData = new JoinRequestSaveForm();
                saveData.setClubName(dto.getClubName());
                saveData.setClubDescription(dto.getClubDescription());
                saveData.setClubId(dto.getClubId());

                // responseFormにリストを追加
                responseForm.add(saveData);

            }
            // リダイレクトされてきた登録申請成功のメッセージを、paramFormにセットする
            paramForm.setMessage(joinOkMessage);

            /*
             * TODO ➊ 初期表示情報取得結果に応じて、以下の条件文を完成させる。
             */
            // 部が存在しない場合
            if (joinRequestList.isEmpty()) {
                // メッセージリソースからnotRequestClubMessageを取得
                String notRequestClubMessage = messageSource.getMessage("notRequestClubMessage", null, Locale.getDefault());
                // 取得したメッセージをModelAndViewに追加
                mav.addObject("notRrequestClubMessage", notRequestClubMessage);
                // formから取得したメッセージをModelAndViewに追加
                mav.addObject("message", paramForm.getMessage());
            } else {
                // 部が存在する場合
                // 部情報をModelAndViewに追加
                mav.addObject("joinRequestSaveForm", responseForm);
                // formから取得したメッセージをModelAndViewに追加
                mav.addObject("joinRequestCompleteMessage", paramForm.getMessage());
            }

            mav.addObject("userId", form);
            mav.addObject("leaderClubId", leaderClubId);

            // 部員登録申請画面に遷移
            mav.setViewName("joinRequest");
        } catch (Exception e) {
            // DB接続に失敗した場合、エラー画面に遷移
            e.printStackTrace();
            mav.setViewName("error");
        }
        return mav;

    }

    // インサート処理
    @PostMapping("/save")
    public ModelAndView insertRequestClub(HttpSession session, JoinRequestSaveForm paramForm,
            RedirectAttributes redirectAttributes) {

        ModelAndView mav = new ModelAndView();

        // セッションからuserIdを取得
        SessionDto sessionDto = commonService.getSessionDto(session);
        String userId = sessionDto.getUserId();

        // セッションが切れた場合、エラー画面に遷移
        if (userId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        // dtoに値をセット
        JoinRequestSaveDto joinRequestSaveDto = new JoinRequestSaveDto();
        joinRequestSaveDto.setUserId(userId);
        joinRequestSaveDto.setClubId(paramForm.getClubId());

        try {
            boolean result = joinRequestService.insertJoinRequest(joinRequestSaveDto);

            /*
             * TODO ➋ インサートの成功、失敗に応じて、処理を変更する。
             */
            // 登録成功時（True）
            if (result) {
                // メッセージリソースからjoinRequestCompleteMessageを取得
                String joinRequestCompleteMessage = messageSource.getMessage("joinRequestCompleteMessage", null, Locale.getDefault());
                // 取得したメッセージをformにセット
                paramForm.setMessage(joinRequestCompleteMessage);
                // 取得したメッセージをModelAndViewに追加
                redirectAttributes.addFlashAttribute("joinOkMessage", joinRequestCompleteMessage);
                // /joinRequestにリダイレクト
                mav.setViewName("redirect:/joinRequest");
            } else {
                // 登録失敗時（False）
                // エラー画面に遷移
                mav.setViewName("error");
            }

        } catch (Exception e) {
            // DB接続に失敗した場合、エラー画面に遷移
            // DB接続に失敗した場合、エラー画面に遷移
            mav.setViewName("error");
        }
        return mav;
    }
}

