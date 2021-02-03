package co.uk.itskerwin.redditClone.mapper;

import co.uk.itskerwin.redditClone.dto.PostRequest;
import co.uk.itskerwin.redditClone.dto.PostResponse;
import co.uk.itskerwin.redditClone.model.*;
import co.uk.itskerwin.redditClone.repository.CommentRepository;
import co.uk.itskerwin.redditClone.repository.VoteRepository;
import co.uk.itskerwin.redditClone.service.AuthService;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static co.uk.itskerwin.redditClone.model.VoteType.DOWNVOTE;
import static co.uk.itskerwin.redditClone.model.VoteType.UPVOTE;

// changed to abstract class because added 4 new fields to dto so need deps to fill
@Mapper(componentModel = "spring")
public abstract class PostMapper {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthService authService;

    // create post from post request object
    // uses instant now
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "user", source = "user")
    // added with vote impl
    @Mapping(target = "voteCount", constant = "0")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);


    @Mapping(target = "id", source = "postId")
    // remove mappings where source and target are same
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    // added since vote impl
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    @Mapping(target = "upVote", expression = "java(isPostUpVoted(post))")
    @Mapping(target = "downVote", expression = "java(isPostDownVoted(post))")
    public abstract PostResponse mapToDto(Post post);

    // gets amount of comments
    Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }

    // gets coloqialized time
    String getDuration(Post post) {
        return TimeAgo.Companion.using(post.getCreatedDate().toEpochMilli());
    }

    boolean isPostUpVoted(Post post) {
        return checkVoteType(post, UPVOTE);
    }

    boolean isPostDownVoted(Post post) {
        return checkVoteType(post, DOWNVOTE);
    }

    private boolean checkVoteType(Post post, VoteType voteType) {
        if (authService.isLoggedIn()) {
            Optional<Vote> voteForPostByUser = voteRepository
                    .findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType)).isPresent();
        }
        return false;
    }
}
