package co.uk.itskerwin.redditClone.model;

import com.sun.istack.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


import java.time.Instant;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

// default model structure for our data
// the @Data specifiesss it's a data model
@Data
@Entity
// getters and setters implemented as well via annotation
@Builder
// these helpers do our constructors for us
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    // our primary key
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long postId;

    // name of post with @NotBlank returning an error if not blank
    @NotBlank(message = "Post Name cannot be empty or Null")
    private String postName;

    @Nullable
    private String url;

    @Nullable
    @Lob
    private String description;

    private Integer voteCount;

    // Relational data so specifies the relation in at @ManyToOne and rest is self explanitory
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    private Instant createdDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Subreddit subreddit;
}
