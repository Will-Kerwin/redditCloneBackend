package co.uk.itskerwin.redditClone.mapper;

import co.uk.itskerwin.redditClone.dto.SubredditDto;
import co.uk.itskerwin.redditClone.model.Post;
import co.uk.itskerwin.redditClone.model.Subreddit;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

// creates a bean which on build generates getters and setters making sure in our dto we have int of num posts instead
// of list of posts

@Mapper(componentModel = "spring")
public interface SubredditMapper {

    // mappers map to dto and back etc if fields have diff names
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

    // so if look above we can get target="number of posts" using the mapping map posts i.e. the expression above
    // default is becasue its an interface it gives us a set example for all impls
    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    // pretty obvious maps reverse way
    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto);
}
