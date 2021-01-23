package co.uk.itskerwin.redditClone.repository;

import co.uk.itskerwin.redditClone.model.Comment;
import co.uk.itskerwin.redditClone.model.Post;
import co.uk.itskerwin.redditClone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}