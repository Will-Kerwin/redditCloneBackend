package co.uk.itskerwin.redditClone.repository;

import co.uk.itskerwin.redditClone.model.Post;
import co.uk.itskerwin.redditClone.model.User;
import co.uk.itskerwin.redditClone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    // find by post and user and order by vote id descending
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}