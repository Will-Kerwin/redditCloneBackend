package co.uk.itskerwin.redditClone.service;

import co.uk.itskerwin.redditClone.dto.SubredditDto;
import co.uk.itskerwin.redditClone.exception.SpringRedditException;
import co.uk.itskerwin.redditClone.mapper.SubredditMapper;
import co.uk.itskerwin.redditClone.model.Subreddit;
import co.uk.itskerwin.redditClone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    // saves to databse
    @Transactional
    public SubredditDto save(SubredditDto subredditDto){
        Subreddit save = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
        subredditDto.setId(save.getId());
        log.info(save.getName());
        return subredditDto;

    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll().stream().map(subredditMapper::mapSubredditToDto).collect(toList());
    }

    // finds by id from database and returns a mapped dto
    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(()-> new SpringRedditException("No subreddit found with this id"));
        return subredditMapper.mapSubredditToDto(subreddit);
    }
}
