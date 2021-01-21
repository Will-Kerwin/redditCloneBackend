package co.uk.itskerwin.redditClone.repository;

import co.uk.itskerwin.redditClone.model.Post;
import co.uk.itskerwin.redditClone.model.Subreddit;
import co.uk.itskerwin.redditClone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findBySubreddit(Subreddit subredditId);

    List<Post> findByUser(User user);
}