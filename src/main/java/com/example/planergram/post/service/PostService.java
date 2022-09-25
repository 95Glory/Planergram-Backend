package com.example.planergram.post.service;

import com.example.planergram.post.DTO.PostDTO;
import com.example.planergram.post.model.Board;
import com.example.planergram.post.model.Post;
import com.example.planergram.post.model.Reply;
import com.example.planergram.post.repository.BoardRepository;
import com.example.planergram.post.repository.PostRepository;
import com.example.planergram.post.repository.ReplyRepository;
import com.example.planergram.postTravel.model.PostAttraction;
import com.example.planergram.postTravel.model.PostRentCar;
import com.example.planergram.postTravel.model.PostStay;
import com.example.planergram.postTravel.model.PostTrain;
import com.example.planergram.postTravel.repository.PostAttractionRepository;
import com.example.planergram.postTravel.repository.PostRentCarRepository;
import com.example.planergram.postTravel.repository.PostStayRepository;
import com.example.planergram.postTravel.repository.PostTrainRepository;
import com.example.planergram.user.model.User;
import com.example.planergram.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostStayRepository postStayRepository;

    @Autowired
    private PostTrainRepository postTrainRepository;

    @Autowired
    private PostAttractionRepository postAttractionRepository;

    @Autowired
    private PostRentCarRepository postRentCarRepository;

    @Autowired
    private ReplyRepository replyRepository;


    // Date 변경을 하는 메서드 save,update,delete
    public PostDTO save(PostDTO postDTO) {
        Long boardId = postDTO.getBoardId();
        postDTO.setBoardName(postDTO.getBoardName());
        Post post = makePost(postDTO);
        post = postRepository.save(post);
        log.info("게시글 작성이 완료되었습니다.");
        return makePostDTO(post);
    }

    public PostDTO update(User user, Long id, PostDTO postDTO) throws Exception {
        Post post = postRepository.getById(id);
        if (!Objects.equals(post.getUser().getUserId(), user.getUserId())) {
            throw new Exception("게시글 작성자가 아닙니다.");
        }
        post.setTitle(postDTO.getTitle());
        post.setContents(postDTO.getContents());
        post.setLikeCount(postDTO.getLikeCount());
        post = postRepository.save(post);
        log.info("게시글 수정이 완료되었습니다.");
        return makePostDTO(post);
    }

    public PostDTO delete(User user, Long id) throws Exception {
        Post post = postRepository.getById(id);
        if (!Objects.equals(post.getUser().getUserId(), user.getUserId())) {
            throw new Exception("게시글 작성자가 아닙니다.");
        }
        log.info("게시글 삭제가 완료되었습니다.");
        return makePostDTO(post);
    }



    //findList
    public List<PostDTO> findAll() {
        List<Post> postList = postRepository.findAll();
        List<PostDTO> postDTOList = makeListPostDTOList(postList);
        log.info("모든 게시글을 조회하였습니다.");
        return postDTOList;
    }

    public List<PostDTO> findByBoard(Long boardId) {
        Board board = boardRepository.getById(boardId);
        List<Post> postList = postRepository.findByBoard(board);
        List<PostDTO> postDTOList = makeListPostDTOList(postList);
        log.info("모든 게시글을 조회하였습니다.");
        return postDTOList;
    }

    public List<PostDTO> findByBoardAndTitleLike(Long boardId,String title) {
        Board board = boardRepository.getById(boardId);
        List<Post> postList = postRepository.findByBoardAndTitleLike(board,"%"+title+"%");
        List<PostDTO> postDTOList = makeListPostDTOList(postList);
        log.info("모든 게시글을 조회하였습니다.");
        return postDTOList;
    }

    public List<PostDTO> findTop5ByOrderByLikeCountDesc() {
        List<Post> postList = postRepository.findTop5ByOrderByLikeCountDesc();
        List<PostDTO> postDTOList = makeListPostDTOList(postList);
        log.info("모든 게시글을 조회하였습니다.");
        return postDTOList;
    }



    //findOne
    public PostDTO findById(Long id) {
        Post post = postRepository.getById(id);
        post.setReadCount(post.getReadCount() + 1);
        post = postRepository.save(post);
        PostDTO postDTO = makeDetailPostDTO(post);
        log.info("게시글을 조회하였습니다.");
        return postDTO;
    }



    // Date 변경을 하는 메서드 save,update,deleted 에 대한 make
    private Post makePost(PostDTO postDTO) {
        Board board = boardRepository.getById(postDTO.getBoardId());
        User user = userRepository.getById(postDTO.getUserId());

        List<PostStay> PostStayList = new ArrayList<>();
        List<PostRentCar> PostRentCarList = new ArrayList<>();
        List<PostTrain> PostTrainList = new ArrayList<>();
        List<PostAttraction> PostAttractionList = new ArrayList<>();
        List<Reply> replyList = new ArrayList<>();

        if (postDTO.getPostStayList() != null) {
            for (Long PostStayId : postDTO.getPostStayList()) {
                PostStayList.add(postStayRepository.getById(PostStayId));
            }
        }

        if (postDTO.getPostRentCarList() != null) {
            for (Long PostRentCarId : postDTO.getPostRentCarList()) {
                PostRentCarList.add(postRentCarRepository.getById(PostRentCarId));
            }
        }

        if (postDTO.getPostTrainList() != null) {
            for (Long PostTrainId : postDTO.getPostTrainList()) {
                PostTrainList.add(postTrainRepository.getById(PostTrainId));
            }
        }

        if (postDTO.getPostAttractionList() != null) {
            for (Long PostAttractionId : postDTO.getPostAttractionList()) {
                PostAttractionList.add(postAttractionRepository.getById(PostAttractionId));
            }
        }

        if (postDTO.getReplyList() != null) {
            for (Long ReplyId : postDTO.getReplyList()) {
                replyList.add(replyRepository.getById(ReplyId));
            }
        }

        return Post.builder()
                .postId(postDTO.getPostId())
                .title(postDTO.getTitle())
                .contents(postDTO.getContents())
                .likeCount(postDTO.getLikeCount())
                .readCount(postDTO.getReadCount())
                .nickname(user.getNickname())
                .boardName(board.getBoardName())
                .board(board)
                .user(user)
                .postAttractionList(PostAttractionList)
                .postRentCarList(PostRentCarList)
                .postStayList(PostStayList)
                .postTrainList(PostTrainList)
                .replyList(replyList)
                .build();
    }

    public PostDTO makePostDTO(Post post) {

        List<Long> PostStayIdList = new ArrayList<>();
        List<Long> PostRentCarIdList = new ArrayList<>();
        List<Long> PostTrainIdList = new ArrayList<>();
        List<Long> PostAttractionIdList = new ArrayList<>();
        List<Long> replyIdList = new ArrayList<>();

        if (post.getPostStayList() != null) {
            for (PostStay postStay : post.getPostStayList()) {
                PostStayIdList.add(postStay.getPostStayId());
            }
        }

        if (post.getPostRentCarList() != null) {
            for (PostRentCar postRentCar : post.getPostRentCarList()) {
                PostRentCarIdList.add(postRentCar.getPostRentCarId());
            }
        }

        if (post.getPostTrainList() != null) {
            for (PostTrain postTrain : post.getPostTrainList()) {
                PostTrainIdList.add(postTrain.getPostTrainId());
            }
        }

        if (post.getPostAttractionList() != null) {
            for (PostAttraction postAttraction : post.getPostAttractionList()) {
                PostAttractionIdList.add(postAttraction.getPostAttractionId());
            }
        }

        if (post.getReplyList() != null) {
            for (Reply reply : post.getReplyList()) {
                replyIdList.add(reply.getReplyId());
            }
        }

        return PostDTO
                .builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .contents(post.getContents())
                .likeCount(post.getLikeCount())
                .readCount(post.getReadCount())
                .nickname(post.getNickname())
                .boardName(post.getBoardName())
                .boardId(post.getBoard().getBoardId())
                .userId(post.getUser().getUserId())
                .postStayList(PostStayIdList)
                .postRentCarList(PostRentCarIdList)
                .postTrainList(PostTrainIdList)
                .postAttractionList(PostAttractionIdList)
                .replyList(replyIdList)
                .build();
    }



    // findList에 대한 make

    public PostDTO makeListPostDTO(Post post) {

        List<Long> replyIdList = new ArrayList<>();

        if (post.getReplyList() != null) {
            for (Reply reply : post.getReplyList()) {
                replyIdList.add(reply.getReplyId());
            }
        }

        return PostDTO
                .builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .contents(post.getContents())
                .likeCount(post.getLikeCount())
                .readCount(post.getReadCount())
                .nickname(post.getNickname())
                .boardName(post.getBoardName())
                .boardId(post.getBoard().getBoardId())
                .userId(post.getUser().getUserId())
                .replyList(replyIdList)
                .build();
    }

    public List<PostDTO> makeListPostDTOList(List<Post> postList) {
        List<PostDTO> postDTOList = new ArrayList<>();

        for (Post post : postList) {
            postDTOList.add(makePostDTO(post));
        }
        return postDTOList;
    }



    // findByid에 대한 make
    public PostDTO makeDetailPostDTO(Post post) {

        List<Reply> replys = new ArrayList<>();
        List<PostRentCar> postRentCars = new ArrayList<>();
        List<PostStay> postStays= new ArrayList<>();
        List<PostTrain> postTrains = new ArrayList<>();
        List<PostAttraction> postAttractions  = new ArrayList<>();

        if (post.getReplyList() != null) {
            for (Reply reply : post.getReplyList()) {
                replys.add(replyRepository.getById(reply.getReplyId()));
            }
        }

        if (post.getPostRentCarList() != null) {
            for (PostRentCar postRentCar : post.getPostRentCarList()) {
                postRentCars.add(postRentCarRepository.getById(postRentCar.getPostRentCarId()));
            }
        }

        if (post.getPostStayList() != null) {
            for (PostStay postStay : post.getPostStayList()) {
                postStays.add(postStayRepository.getById(postStay.getPostStayId()));
            }
        }

        if (post.getPostTrainList() != null) {
            for (PostTrain postTrain : post.getPostTrainList()) {
                postTrains.add(postTrainRepository.getById(postTrain.getPostTrainId()));
            }
        }

        if (post.getPostAttractionList() != null) {
            for (PostAttraction postAttraction : post.getPostAttractionList()) {
                postAttractions.add(postAttractionRepository.getById(postAttraction.getPostAttractionId()));
            }
        }

        return PostDTO
                .builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .contents(post.getContents())
                .likeCount(post.getLikeCount())
                .readCount(post.getReadCount())
                .nickname(post.getNickname())
                .boardName(post.getBoardName())
                .boardId(post.getBoard().getBoardId())
                .userId(post.getUser().getUserId())
                .postStays(postStays)
                .postRentCars(postRentCars)
                .postTrains(postTrains)
                .postAttractions(postAttractions)
                .replys(replys)
                .build();
    }
}

//===============================================================================================
//================================활용 x 차후 Develop때 사용예정 ====================================
//================================================================================================


//    public List<PostDTO> findByDetailAddressAndAddress(String detailAddress, String address) {
//        List<Post> postList = postRepository.findByDetailAddressAndAddress(detailAddress, address);
//        List<PostDTO> postDTOList = makeListPostDTOList(postList);
//        log.info("모든 게시글을 조회하였습니다.");
//        return postDTOList;
//    }
//
//    public List<PostDTO> findByAddress(String address) {
//        List<Post> postList = postRepository.findByAddress(address);
//        List<PostDTO> postDTOList = makeListPostDTOList(postList);
//        log.info("모든 게시글을 조회하였습니다.");
//        return postDTOList;
//    }
//
//    public List<PostDTO> findByTitleLike(String title) {
//        List<Post> postList = postRepository.findByTitleLike("%" + title + "%");
//        List<PostDTO> postDTOList = makeListPostDTOList(postList);
//        log.info("모든 게시글을 조회하였습니다.");
//        return postDTOList;
//    }
