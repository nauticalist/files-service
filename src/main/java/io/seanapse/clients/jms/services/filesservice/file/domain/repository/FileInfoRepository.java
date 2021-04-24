package io.seanapse.clients.jms.services.filesservice.file.domain.repository;

import io.seanapse.clients.jms.services.filesservice.file.domain.model.FileInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileInfoRepository extends MongoRepository<FileInfo, String> {
    Optional<FileInfo> findByFileId(String fileId);

    @Query("{'$or':[{id: { $exists: true }}]}")
    List<FileInfo> findFileInfosByDeletedFalseAndPage (final Pageable page);

    @Query(value = "{'$or': [{'fileName': {$regex: '?0', $options: 'i'}}]}")
    List<FileInfo> findByFilterRegex(String filter);
}
