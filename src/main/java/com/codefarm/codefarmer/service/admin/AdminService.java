package com.codefarm.codefarmer.service.admin;

import com.codefarm.codefarmer.domain.board.BoardDTO;
import com.codefarm.codefarmer.entity.admin.Banner;
import com.codefarm.codefarmer.entity.alba.Alba;
import com.codefarm.codefarmer.entity.alba.MemberAlba;
import com.codefarm.codefarmer.entity.board.Reply;
import com.codefarm.codefarmer.entity.member.Member;
import com.codefarm.codefarmer.entity.mentor.Review;
import com.codefarm.codefarmer.entity.program.MemberProgram;
import com.codefarm.codefarmer.entity.program.Program;
import com.codefarm.codefarmer.repository.admin.BannerRepository;
import com.codefarm.codefarmer.repository.alba.AlbaRepository;
import com.codefarm.codefarmer.repository.alba.MemberAlbaRepository;
import com.codefarm.codefarmer.repository.board.BoardCustomRepository;
import com.codefarm.codefarmer.repository.board.BoardRepository;
import com.codefarm.codefarmer.repository.board.ReplyCustomRepository;
import com.codefarm.codefarmer.repository.board.ReplyRepository;
import com.codefarm.codefarmer.repository.member.MemberRepository;
import com.codefarm.codefarmer.repository.mentor.ReviewCustomRepository;
import com.codefarm.codefarmer.repository.mentor.ReviewRepository;
import com.codefarm.codefarmer.repository.program.MemberProgramRepository;
import com.codefarm.codefarmer.repository.program.ProgramCustomRepository;
import com.codefarm.codefarmer.repository.program.ProgramRepository;
import com.codefarm.codefarmer.type.BannerStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
//    배너
    private final BannerRepository bannerRepository;
//    공지
    private final BoardRepository boardRepository;
    private final BoardCustomRepository boardCustomRepository;
//    댓글
    private final ReplyRepository replyRepository;
    private final ReplyCustomRepository replyCustomRepository;
//    후기
    private final ReviewRepository reviewRepository;
    private final ReviewCustomRepository reviewCustomRepository;
//    알바
    private final AlbaRepository albaRepository;
    private final MemberAlbaRepository memberAlbaRepository;
//    프로그램
    private final ProgramRepository programRepository;
    private final MemberProgramRepository memberProgramRepository;
//    멤버
    private final MemberRepository memberRepository;

//    커뮤니티 목록
    public Page<BoardDTO> boardShowAll(Pageable pageable, String keyword, String searchText){
        List<BoardDTO> boards = boardCustomRepository.ShowAllBoard(keyword, searchText);
        final int total = boards.size();
        final int start = (int)pageable.getOffset();
        final int end = (start + pageable.getPageSize()) < total ? (start + pageable.getPageSize()) : total;

        return new PageImpl<>(boards.subList(start, end), pageable, total);
    }

//    커뮤니티 글 개수
    public int countByBoard() { return boardRepository.countByBoard(); }

//    커뮤니티 - 검색했을 때 글 개수
    public int searchCountByBoard(String keyword, String searchText) {
        int total = boardCustomRepository.searchCountByBoard(keyword, searchText);

        return total % 10 == 0 ? (total / 10) : ((total / 10) + 1);
    }

//    댓글 목록
    public Page<Reply> replyShowAll(Pageable pageable, String keyword, String searchText){
        List<Reply> replies = replyCustomRepository.findByNickname(searchText, pageable);
        final int total = replyCustomRepository.countByMemberNickname(searchText);
        final int start = (int)pageable.getOffset();
        final int end = (start + pageable.getPageSize()) < total ? (start + pageable.getPageSize()) : total;

        if (keyword.equals("w")){
            return new PageImpl<>(replies.subList(start, end), pageable, replies.size());
        } else {
            return replyRepository.findByReplyContentContaining(searchText, pageable);
        }
    }
//    작성자로 검색했을 때 개수
    public int countByReplyNickname (String memeberNickname){
        int total = replyCustomRepository.countByMemberNickname(memeberNickname);
        return total%10 == 0 ? (total/10) : ((total/10) + 1);
    }
//    댓글 개수
    public int countByReply(){return replyRepository.countByReply();}

//    후기 목록
    public Page<Review> reviewShowAll(Pageable pageable, String keyword, String searchText){
        List<Review> reviews = reviewCustomRepository.findByNickname(searchText, pageable);
        final int total = reviewCustomRepository.countByMemberNickname(searchText);
        final int start = (int)pageable.getOffset();
        final int end = (start + pageable.getPageSize()) < total ? (start + pageable.getPageSize()) : total;

        if (keyword.equals("w")){
            return new PageImpl<>(reviews.subList(start, end), pageable, reviews.size());
        } else {
            return reviewRepository.findByReviewContentContaining(searchText, pageable);
        }
    }
//    작성자로 검색했을 때 개수
    public int countByReviewNickname (String memeberNickname){
        int total = reviewCustomRepository.countByMemberNickname(memeberNickname);
        return total%10 == 0 ? (total/10) : ((total/10) + 1);
    }

//    댓글 개수
    public int countByReview(){return reviewRepository.countByReview();}

//    알바 목록
    public Page<Alba> albaShowAll(Pageable pageable, String keyword, String searchText){
        List<Alba> albaLists = albaRepository.findByAlbaLikeMemberName(searchText);
        final int total = albaRepository.countByMemberName(searchText);
        final int start = (int)pageable.getOffset();
        final int end = (start + pageable.getPageSize()) < total ? (start + pageable.getPageSize()) : total;

        if (keyword.equals("n")){
            return new PageImpl<>(albaLists.subList(start, end), pageable, albaLists.size());
        } else {
            return albaRepository.findByAlbaTitleContaining(searchText, pageable);
        }
    }

//    알바 - 작성자로 검색했을 때 글 수
    public int countByAlbaNickname (String memeberNickname){
        int total = albaRepository.countByMemberName(memeberNickname);
        return total%10 == 0 ? (total/10) : ((total/10) + 1);
    }

//    알바 글 개수
    public int countByAlba() { return albaRepository.countByAlba(); }

//    알바 신청자 목록
    public Page<MemberAlba> memberAlbaalbaShowAll(Pageable pageable, String keyword, String searchText){
        List<MemberAlba> memberAlbas = memberAlbaRepository.findByMemberAlba(keyword, searchText);
        final int total = memberAlbaRepository.countByMemberAlbaSearch(keyword, searchText);
        final int start = (int)pageable.getOffset();
        final int end = (start + pageable.getPageSize()) < total ? (start + pageable.getPageSize()) : total;

        return new PageImpl<>(memberAlbas.subList(start, end), pageable, memberAlbas.size());
    }

//    알바 신청자 -  검색했을 때 글 수
    public int countByMemberAlbaSearch (String keyword, String searchText){
        int total = memberAlbaRepository.countByMemberAlbaSearch(keyword, searchText);
        return total%10 == 0 ? (total/10) : ((total/10) + 1);
    }

//    알바 신청자 글 수
    public int countByMemberAlba() { return memberAlbaRepository.countByMemberAlba(); }

//    프로그램 목록
    @Transactional(readOnly = true)
    public Page<Program> programShowAll(Pageable pageable, String keyword, String searchText){
        List<Program> programs = programRepository.findByProgramSearch(keyword, searchText);
        final int total = programRepository.countByProgramSearch(keyword, searchText);
        final int start = (int)pageable.getOffset();
        final int end = (start + pageable.getPageSize()) < total ? (start + pageable.getPageSize()) : total;

        return new PageImpl<>(programs.subList(start, end), pageable, programs.size());
    }

//    프로그램 검색했을 떄 개수
    public int countByProgramSearch (String keyword, String searchText){
        int total = programRepository.countByProgramSearch(keyword, searchText);
        return total%10 == 0 ? (total/10) : ((total/10) + 1);
    }

//    프로그램 개수
    public int countByProgram() { return programRepository.countByProgram(); }

//    프로그램 신청자 목록
    @Transactional(readOnly = true)
    public Page<MemberProgram> memberProgramShowAll(Pageable pageable, String keyword, String searchText){
        List<MemberProgram> memberPrograms = memberProgramRepository.findByMemberProgram(keyword, searchText);
        final int total = memberProgramRepository.countByMemeberProgramSearch(keyword, searchText);
        final int start = (int)pageable.getOffset();
        final int end = (start + pageable.getPageSize()) < total ? (start + pageable.getPageSize()) : total;

        return new PageImpl<>(memberPrograms.subList(start, end), pageable, memberPrograms.size());
    }

//    프로그램 신청자 -  검색했을 때 글 수
    public int countByMemberProgramSearch (String keyword, String searchText){
        int total = memberProgramRepository.countByMemeberProgramSearch(keyword, searchText);
        return total%10 == 0 ? (total/10) : ((total/10) + 1);
    }

//    프로그램 신청자 글 수
    public int countByMemberProgram() { return memberProgramRepository.countByMemberProgram(); }


//    배너 목록
    @Transactional(readOnly = true)
    public Page<Banner> bannerShowAll(Pageable pageable, String keyword, String bannerTitle, String bannerContent){
        if (keyword.equals("t")){
            return bannerRepository.findByBannerTitleContaining(bannerTitle, pageable);
        } else if (keyword.equals("c")){
            return bannerRepository.findByBannerInfoContaining(bannerContent, pageable);
        } else {
            return bannerRepository.findByBannerTitleContainingOrBannerInfoContaining(bannerTitle, bannerContent, pageable);
        }
    }

//    배너 디테일
    public Banner bannerShowOne(Long bannerId){
        return bannerRepository.findById(bannerId).get();
    }

//    배너 등록
    public void bannerAdd(Banner banner, String status, String startDate, String endDate) throws DateTimeParseException {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime bannerStartDate = LocalDate.parse(startDate, format).atStartOfDay();
        LocalDateTime bannerEndDate = LocalDate.parse(endDate, format).atStartOfDay();

        banner.setBannerStartDate(bannerStartDate);
        banner.setBannerEndDate(bannerEndDate);

        if (status.equals("statusOk")){
            banner.setBannerStatus(BannerStatus.USING);
        } else if (status.equals("statusNo")) {
            banner.setBannerStatus(BannerStatus.NOT_USING);
        }

        bannerRepository.save(banner);
    }

//    배너 삭제
    public Long bannerDelete(Long bannerId){
        bannerRepository.delete(bannerRepository.findById(bannerId).get());
        return bannerId;
    }

//    배너 글 개수
    public int countByBanner() { return bannerRepository.countByBanner(); }

//    회원 목록
    @Transactional(readOnly = true)
    public Page<Member> memberShowAll(Pageable pageable, String keyword, String searchText){
        if (keyword.equals("nn")){
            return memberRepository.findMemberByMemberNicknameContaining(pageable, searchText);
        } else if (keyword.equals("n")) {
            return memberRepository.findMemberByMemberNameContaining(pageable, searchText);
        } else {
            return memberRepository.findMemberByMemberPhoneContaining(pageable, searchText);
        }
    }

//    회원 삭제
    public void memberRemove(Long memberId) { memberRepository.delete(memberRepository.findById(memberId).get()); }

//    회원 총 수
    public int countByMember() { return memberRepository.countByMember(); }

}
