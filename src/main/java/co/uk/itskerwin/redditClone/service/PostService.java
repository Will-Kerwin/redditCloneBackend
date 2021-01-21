package co.uk.itskerwin.redditClone.service;

import co.uk.itskerwin.redditClone.dto.PostRequest;
import co.uk.itskerwin.redditClone.dto.PostResponse;
import co.uk.itskerwin.redditClone.exception.PostNotFoundException;
import co.uk.itskerwin.redditClone.exception.SubredditNotFoundException;
import co.uk.itskerwin.redditClone.mapper.PostMapper;
import co.uk.itskerwin.redditClone.model.Post;
import co.uk.itskerwin.redditClone.model.Subreddit;
import co.uk.itskerwin.redditClone.model.User;
import co.uk.itskerwin.redditClone.repository.PostRepository;
import co.uk.itskerwin.redditClone.repository.SubredditRepository;
import co.uk.itskerwin.redditClone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
// transactional because all these use database
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final AuthService authService;
    private final PostMapper postMapper;
    private final UserRepository userRepository;

    // we need subreddit and user so therefore need auth service and subreddit repo
    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));
        postRepository.save(postMapper.map(postRequest, subreddit, authService.getCurrentUser()));
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new PostNotFoundException(id.toString()));
        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(()->new SubredditNotFoundException(subredditId.toString()));
        List<Post> posts = postRepository.findBySubreddit(subreddit);
        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream().map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
