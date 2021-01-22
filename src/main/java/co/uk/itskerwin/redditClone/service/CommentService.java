package co.uk.itskerwin.redditClone.service;

import co.uk.itskerwin.redditClone.dto.CommentsDto;
import co.uk.itskerwin.redditClone.exception.PostNotFoundException;
import co.uk.itskerwin.redditClone.mapper.CommentMapper;
import co.uk.itskerwin.redditClone.model.Comment;
import co.uk.itskerwin.redditClone.model.Post;
import co.uk.itskerwin.redditClone.repository.CommentRepository;
import co.uk.itskerwin.redditClone.repository.PostRepository;
import co.uk.itskerwin.redditClone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Transactional
    public void save(CommentsDto commentsDto){
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(
                        () -> new PostNotFoundException(commentsDto.getPostId().toString())
                );
        Comment comment = commentMapper.map(commentsDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

    }
}
