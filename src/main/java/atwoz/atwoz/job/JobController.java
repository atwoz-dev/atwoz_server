package atwoz.atwoz.job;

import atwoz.atwoz.common.response.BaseResponse;
import atwoz.atwoz.job.query.JobView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/job")
public class JobController {

    @GetMapping
    public ResponseEntity<BaseResponse<List<JobView>>> getAll() {
        return null;
    }
}
