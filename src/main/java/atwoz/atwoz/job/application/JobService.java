package atwoz.atwoz.job.application;

import atwoz.atwoz.job.domain.Job;
import atwoz.atwoz.job.domain.JobRepository;
import atwoz.atwoz.job.exception.JobNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job findById(Long id) {
        return jobRepository.findById(id).orElseThrow(() -> new JobNotFoundException());
    }
}
