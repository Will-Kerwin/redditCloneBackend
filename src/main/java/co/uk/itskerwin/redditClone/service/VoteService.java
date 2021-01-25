package co.uk.itskerwin.redditClone.service;

import co.uk.itskerwin.redditClone.dto.VoteDto;
import co.uk.itskerwin.redditClone.exception.PostNotFoundException;
import co.uk.itskerwin.redditClone.exception.SpringRedditException;
import co.uk.itskerwin.redditClone.model.Post;
import co.uk.itskerwin.redditClone.model.Vote;
import co.uk.itskerwin.redditClone.repository.PostRepository;
import co.uk.itskerwin.redditClone.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static co.uk.itskerwin.redditClone.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + voteDto.getPostId()));

        // finds previous vote of user
        Optional<Vote> voteByPostAndUser = voteRepository
                .findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());

        // if there is a vote and it is equal to dto
        if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
            throw new SpringRedditException("You have already " + voteDto.getVoteType() + "'d for this post");
        }

        if (UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }

        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post){
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
