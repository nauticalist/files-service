package io.seanapse.clients.jms.services.filesservice.file.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "files")
public class FileInfo {
    @Id
    private String fileId;

    @NotNull
    private String fileName;

    @NotNull
    private String fileUrl;

    @Builder.Default
    private boolean deleted = false;

    private Date createdAt;

    private String createdBy;

    private Date modifiedAt;

    private String modifiedBy;
}
