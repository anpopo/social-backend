package com.anpopo.social.module.interest;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class InterestService {

    private final InterestRepository interestRepository;

    @PostConstruct
    public void initInterestData() throws IOException {
        if (interestRepository.count() == 0) {
            Resource resource = new ClassPathResource("interest_list.csv");
            List<Interest> interestList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        Interest interest = new Interest();
                        interest.createNewInterest(split[0]);
                        return interest;
                    })
                    .collect(Collectors.toList());

            interestRepository.saveAll(interestList);
        }
    }
}
