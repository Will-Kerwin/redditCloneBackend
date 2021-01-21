package co.uk.itskerwin.redditClone.mapper;

import co.uk.itskerwin.redditClone.dto.PostRequest;
import co.uk.itskerwin.redditClone.dto.PostResponse;
import co.uk.itskerwin.redditClone.model.Post;
import co.uk.itskerwin.redditClone.model.Subreddit;
import co.uk.itskerwin.redditClone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PostMapper {

    // create post from post request object
    // uses instant now
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "voteCount", constant = "0")
    @Mapping(target = "user", source = "user")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);


    @Mapping(target = "id", source = "postId")
    // remove mappings where source and target are same
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    public abstract PostResponse mapToDto(Post post);


}
